package com.dataCollection.util;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by xiangrchen on 8/15/17.
 */

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DataCollectionMessage {
    //success
    START_DATACOLLECTION_SUCCESS("start data collection success"),

    //failed
    PATH_IS_EXIST("path is aleardy exist."),
    PATH_IS_NULL("path is null."),
    PATH_FORMAT_IS_ERROR("path format is error."),
    EMAIL_IS_NULL("email is null."),
    EMAIL_FORMAT_IS_ERROR("email format is error.");

    private final String description;

    DataCollectionMessage(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "DataCollectionMessage{" +
                "description='" + description + '\'' +
                '}';
    }
}
