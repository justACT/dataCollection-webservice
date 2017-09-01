package com.dataCollection.entity;

import java.util.List;

/**
 * Created by xiangrchen on 8/14/17.
 */
public class PipelineRequest {
    String filterCriteria;
    List<String> pathList;
    List<String> emailList;

    public String getFilterCriteria() {
        return filterCriteria;
    }

    public void setFilterCriteria(String filterCriteria) {
        this.filterCriteria = filterCriteria;
    }

    public List<String> getPathList() {
        return pathList;
    }

    public void setPathList(List<String> pathList) {
        this.pathList = pathList;
    }

    public List<String> getEmailList() {
        return emailList;
    }

    public void setEmailList(List<String> emailList) {
        this.emailList = emailList;
    }

    public PipelineRequest() {
    }

    public PipelineRequest(String filterCriteria, List<String> pathList, List<String> emailList) {
        this.filterCriteria = filterCriteria;
        this.pathList = pathList;
        this.emailList = emailList;
    }
}
