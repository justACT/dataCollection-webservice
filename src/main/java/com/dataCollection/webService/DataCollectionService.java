package com.dataCollection.webService;

import com.dataCollection.entity.PipelineRequest;
import com.dataCollection.util.DataCollectionMessage;

import java.io.IOException;
import java.util.List;

/**
 * Created by xiangrchen on 8/15/17.
 */
public interface DataCollectionService {
     DataCollectionMessage validateParam(PipelineRequest pipelineRequest);

     boolean isPathListExist(List<String> pathList) throws IOException;

     DataCollectionMessage addJob(PipelineRequest pipelineRequest);

}
