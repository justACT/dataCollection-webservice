package com.dataCollection.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xiangrchen on 8/23/17.
 */
public class RegexValidateUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegexValidateUtil.class);

    /**
     * check email
     * @param email
     * @return
     */
    public static boolean checkEmail(String email){
        boolean flag=false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex=Pattern.compile(check);
            Matcher matcher=regex.matcher(email);
            flag=matcher.matches();
        }catch (Exception e){
            flag=false;
        }
        return flag;
    }

    /**
     * check path
     * @param path
     * @return
     */
    public static boolean checkPath(String path){
        path=path.trim();
        return (path.startsWith("local://") || path.startsWith("hdfs://"));
    }


}
