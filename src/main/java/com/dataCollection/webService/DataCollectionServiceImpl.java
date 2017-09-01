package com.dataCollection.webService;

import com.dataCollection.entity.JobStatus;
import com.dataCollection.entity.PipelineJob;
import com.dataCollection.entity.PipelineRequest;
import com.dataCollection.repo.PipelineJobRepo;
import com.dataCollection.util.*;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by xiangrchen on 8/15/17.
 */
@Service
public class DataCollectionServiceImpl implements  DataCollectionService{
    private static final Logger LOGGER = LoggerFactory.getLogger(DataCollectionServiceImpl.class);

    @Autowired
    PipelineJobRepo pipelineJobRepo;

    @Autowired
    EmailUtil emailUtil;

    @Value("${job.alertDime.in.milliseconds}")
    long alertTime;

    @Override
    public boolean isPathListExist(List<String> pathList) throws IOException {
        for (String path:pathList){
            if (FSUtil.isFileExist(path)){
                return true;
            }
        }
        return false;
    }

    @Override
    public DataCollectionMessage validateParam(PipelineRequest pipelineRequest){
        // validate emails
        if (pipelineRequest.getEmailList()==null){
            return DataCollectionMessage.EMAIL_IS_NULL;
        }
        for (String email:pipelineRequest.getEmailList()){
            if (!RegexValidateUtil.checkEmail(email)){
                return DataCollectionMessage.EMAIL_FORMAT_IS_ERROR;
            }
        }

        // validate paths
        if (pipelineRequest.getPathList()==null){
            return DataCollectionMessage.PATH_IS_NULL;
        }
        for (String path:pipelineRequest.getPathList()){
            if (!RegexValidateUtil.checkPath(path)){
                return DataCollectionMessage.PATH_FORMAT_IS_ERROR;
            }
        }

        return null;

    }

    @Override
    public DataCollectionMessage addJob(PipelineRequest pipelineRequest) {
        genOnePendingJob(pipelineRequest);


        return DataCollectionMessage.START_DATACOLLECTION_SUCCESS;
    }

    public void genOnePendingJob(PipelineRequest pipelineRequest){
        //init a job, Status is PENDING
        PipelineJob pipelineJob=new PipelineJob();
        pipelineJob.setPaths(BasicUtil.toJson(pipelineRequest.getPathList()));
        pipelineJob.setFilterCriteria(pipelineRequest.getFilterCriteria());
        pipelineJob.setEmails(BasicUtil.toJson(pipelineRequest.getEmailList()));
        pipelineJob.setStatus(JobStatus.Status.PENDING);
        pipelineJobRepo.save(pipelineJob);
    }

    //shell
    public void executeShell(PipelineJob pipelineJob){
        pipelineJob.setStatus(JobStatus.Status.STARTED);
        pipelineJob.setStartExecuteTimestamp(new Timestamp(System.currentTimeMillis()));
        pipelineJobRepo.save(pipelineJob);

        System.out.println(ShellUtil.executeCommand("host -t a google.com"));

        // shell(pipelineJob.getFilterCriteria())
    }

    /**
     * scheduler1: pick out
     * pick out a job in pending Status while no other is in started or blocked Status.
     * 1. pick out a job
     * 2. update its Status
     * 3. execute shell
     * 4. send starting remind email
     */
    @Scheduled(fixedRateString = "${pickOutExecutedJob.fixedDelay.in.milliseconds}")
    public void pickOutExecutedJob() throws IOException, MessagingException {
//        System.out.println(ShellUtil.executeCommand("host -t a google.com"));
        List<JobStatus.Status> stateList=new ArrayList<>();
        stateList.add(JobStatus.Status.STARTED);
        stateList.add(JobStatus.Status.BLOCKED);
        List<PipelineJob> startedOrBlockedJobList=pipelineJobRepo.findByStatusIn(stateList);
        if (startedOrBlockedJobList.size()!=0){
            LOGGER.info("there are some job is in STARTED or BLOCKED status.");
            return;
        }

        // pick out a job in pending Status, whose priority is highest and create timestamp is pioneer.
        List<PipelineJob> pendingJobList=pipelineJobRepo.findByStatus(JobStatus.Status.PENDING);
        if (pendingJobList.size()==0){
            return;
        }
        sortJobsBypriorityAndTS(pendingJobList);
        PipelineJob pickedExecutedJob=pendingJobList.get(0);
        executeShell(pickedExecutedJob);
        TypeReference<List<String>> type=new TypeReference<List<String>>(){};
        emailUtil.sendEmail(BasicUtil.toEntity(pickedExecutedJob.getEmails(),type), "start job","start job."+pickedExecutedJob);
        LOGGER.info("start job., send email");

        LOGGER.info("pickOutExecutedJob: "+System.currentTimeMillis());
    }

    public void sortJobsBypriorityAndTS(List<PipelineJob> pendingJobList){
        Collections.sort(pendingJobList, new Comparator<PipelineJob>() {
            @Override
            public int compare(PipelineJob o1, PipelineJob o2) {
                if (o1.getPriority()==o2.getPriority()){
                    return o1.getCreateTimestamp().compareTo(o2.getCreateTimestamp());
                }else {
                    return o1.getPriority()>=o2.getPriority()?-1:1;
                }
            }
        });
    }


    /**
     * scheduler2: check job result
     * check job result
     * 1. check
     */
    @Scheduled(fixedRateString = "${checkJobResult.fixedDelay.in.milliseconds}")
    public void checkJobResult() throws IOException, MessagingException {
        LOGGER.info("checkJobResult: "+System.currentTimeMillis());
        List<PipelineJob> monitorPipelineJobList=findUnclosedPipelineJobStatusList();
        if (monitorPipelineJobList==null || monitorPipelineJobList.size()==0){
            LOGGER.warn("this is no unclosed job.");
            return;
        }
        for (PipelineJob pipelineJob:monitorPipelineJobList){
            // startTime
            TypeReference<List<String>> type=new TypeReference<List<String>>() {};
            if (JobStatus.Status.STARTED.equals(pipelineJob.getStatus())) {
                // if one job is in started Status more than alertTime, set its Status blocked.
                if ((System.currentTimeMillis() - pipelineJob.getStartExecuteTimestamp().getTime()) > alertTime) {
                    pipelineJobRepo.save(pipelineJob);
                    //send alert enmail
                    emailUtil.sendEmail(BasicUtil.toEntity(pipelineJob.getEmails(),type), "alert email","running time is over alert-time. "+pipelineJob);
                    LOGGER.info(pipelineJob+" execute successfully, send email");
                }
            }
            // check whether _SUCCESS file is generated in this job's paths.
            List<String> pathList=BasicUtil.toEntity(pipelineJob.getPaths(),type);
            for (String path:pathList){

                if (FSUtil.isFileExist(path+ File.separator+ FSUtil.ResultFile._SUCCESS)){
                    // send success email
                      emailUtil.sendEmail(BasicUtil.toEntity(pipelineJob.getEmails(),type), "start job","start job. "+pipelineJob);
                      LOGGER.info(pipelineJob+" execute successfully, send email");

                    pipelineJob.setEndTimestamp(new Timestamp(System.currentTimeMillis()));
                    pipelineJobRepo.save(pipelineJob);
                }
            }
        }

    }

    public List<PipelineJob> findUnclosedPipelineJobStatusList(){
        List<JobStatus.Status> StatusList=JobStatus.getUnclosedStatusList();
        return pipelineJobRepo.findByStatusIn(StatusList);
    }



}
