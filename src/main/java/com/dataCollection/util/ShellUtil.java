package com.dataCollection.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by xiangrchen on 8/23/17.
 */
public class ShellUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShellUtil.class);

    public static String executeCommand(String command){
        StringBuffer output=new StringBuffer();
        Process p;
        try {
            p=Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line=reader.readLine())!=null){
                output.append(line+"\n");
            }
        } catch (Exception e) {
            LOGGER.error("exception when execute command "+command+":"+e);
        }
        return output.toString();
    }

}
