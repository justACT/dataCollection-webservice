package com.dataCollection.entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by xiangrchen on 8/9/17.
 */
@Entity
public class PipelineJob extends AuditableEntity {
    @Lob
    @Column(length = 1048576)  //2^20=1048576
    String paths;

    @Enumerated(EnumType.STRING)
    JobStatus.Status status;

    @Lob
    @Column(length = 1048576)  //2^20=1048576
    String emails;

    /**
     * priority=0~4, default value is 2
     */
    int priority=2;

    Timestamp startExecuteTimestamp;

    @Lob
    @Column(length = 1048576)  //2^20=1048576
    String filterCriteria;

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    public JobStatus.Status getStatus() {
        return status;
    }

    public void setStatus(JobStatus.Status status) {
        this.status = status;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getFilterCriteria() {
        return filterCriteria;
    }

    public Timestamp getStartExecuteTimestamp() {
        return startExecuteTimestamp;
    }

    public void setStartExecuteTimestamp(Timestamp startExecuteTimestamp) {
        this.startExecuteTimestamp = startExecuteTimestamp;
    }

    public void setFilterCriteria(String filterCriteria) {
        this.filterCriteria = filterCriteria;
    }

    public PipelineJob() {
    }

    public PipelineJob(String paths, JobStatus.Status status, String emails, String filterCriteria) {
        this.paths = paths;
        this.status = status;
        this.emails = emails;
        this.filterCriteria = filterCriteria;
    }
}
