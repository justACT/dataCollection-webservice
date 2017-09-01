package com.dataCollection.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiangrchen on 8/9/17.
 */
public class JobStatus {
    /**
     * STARTED: running during 8 hours
     * PENDING: never run, just be saved in job queue
     * BLOCKED; running but > 8hours
     * FAILED: failed due to some reasons. such as path exception, mr failed.
     * SUCCESS: execute data collection successfully
     * ABANDONED: user choose to abandoned
     */
    public enum Status{
        STARTED,
        PENDING,
        BLOCKED,
        FAILED,
        SUCCESS,
        ABANDONED,
    }

    /**
     * SUCCESS FAILED ABANDONED is closed Status
     * @param Status
     * @return
     */
    public static boolean isClosed(Status Status){
        if (Status.equals(Status.SUCCESS) || Status.equals(Status.FAILED) || Status.equals(Status.ABANDONED)){
            return true;
        }
        return false;
    }

    public static List<Status> getUnclosedStatusList(){
        List<Status> list=new ArrayList<>();
        for (Status status:Status.values()){
            if (isClosed(status)){
                continue;
            }else {
                list.add(status);
            }
        }
        return list;
    }
}
