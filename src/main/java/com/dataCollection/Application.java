package com.dataCollection;

import com.dataCollection.repo.PipelineJobRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by xiangrchen on 8/9/17.
 */
@SpringBootApplication
@EnableScheduling
public class Application implements CommandLineRunner{
    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }

    @Autowired
    PipelineJobRepo pipelineJobRepo;

    @Override
    public void run(String... strings) throws Exception {
//        ArrayList<String> pathList=new ArrayList<>();
//        pathList.add("hdfs://localhost/user/a");
//        pathList.add("local://user/a");
//
//        ArrayList<String> emails=new ArrayList<>();
////        emails.add("aaa@qq.com");
//        emails.add("xiangrchen@ebay.com");
////        emails.add("xiaoryang@ebay.com");
//        PipelineJob pipelineJob=new PipelineJob(BasicUtil.toJson(pathList), JobState.State.PENDING, BasicUtil.toJson(emails),"ssf");
//        pipelineJobRepo.save(pipelineJob);
//        System.out.println("======================================");
//        for (PipelineJob pj:pipelineJobRepo.findAll()) {
//            System.out.println(BasicUtil.toJson(pj));
//        }
//        System.out.println("======================================");

//        System.out.println(BasicUtil.isFileExist("/Users/xiangrchen/Desktop/cxr_files/temp/tempdata/test/"));

    }
}
