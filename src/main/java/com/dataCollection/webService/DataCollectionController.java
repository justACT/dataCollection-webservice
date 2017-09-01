package com.dataCollection.webService;

import com.dataCollection.entity.PipelineRequest;
import com.dataCollection.util.DataCollectionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Created by xiangrchen on 8/9/17.
 */
@RestController
@RequestMapping("/dataCollection")
public class DataCollectionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataCollectionController.class);

    @Autowired
    DataCollectionService dataCollectionService;

    @RequestMapping(value = "/version",method = RequestMethod.GET)
    public String getVersion(){
        return "1.0.1";
    }

    @RequestMapping(value = "",method = RequestMethod.POST)
    public DataCollectionMessage startDataCollection(@RequestBody PipelineRequest pipelineRequest) throws IOException {
        System.out.println(pipelineRequest);
        // validate format
        if (dataCollectionService.validateParam(pipelineRequest)!=null){
            return dataCollectionService.validateParam(pipelineRequest);
        }

        // check path and decide whether pipeline job will be started
        if (dataCollectionService.isPathListExist(pipelineRequest.getPathList())){
            return DataCollectionMessage.PATH_IS_EXIST;
        }else {
            dataCollectionService.addJob(pipelineRequest);
        }
        return DataCollectionMessage.START_DATACOLLECTION_SUCCESS;
    }
}
