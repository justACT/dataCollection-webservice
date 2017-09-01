package com.dataCollection.util;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by xiangrchen on 8/14/17.
 */
public class FSUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FSUtil.class);
    public enum FSType{
        LOCAL,
        HDFS
    }

    public enum ResultFile{
        _SUCCESS,
        _FAILED,
        _LOG
    }

    /**
     * getFileSystem by dataPath.
     */
    public static FileSystem getHDFSFileSystem(String hdfsPath) {
        if(StringUtils.isEmpty(hdfsPath)){
            return null;
        }
        URI path=getUri(hdfsPath);
        Configuration conf=new Configuration();
        FileSystem fileSystem= null;
        try {
            fileSystem = FileSystem.get(path,conf);
        } catch (IOException e) {
            LOGGER.error("cannot get "+path+" hdfsFilesystem."+e.getMessage());
        }
        return fileSystem;
    }

    /**
     * get URI by path.
     */
    public static URI getUri(String path){
        URI dataUri;
        try{
            dataUri=new URI(path);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(
                    "The specified path is not a valid URI: " + path + ", "
                            + e.getMessage());
        }
        String schema=dataUri.getScheme();
        if(schema==null){
            LOGGER.debug("no scheme specified, local file");
            File file=new File(path);
            return file.toURI();
        }else {
            return dataUri;
        }
    }

    /**
     * list all sub dir of a dir
     *
     * @param dir
     * @return
     * @throws IOException
     */
    public static List<String> listSubDir(String dir) throws IOException {
        FileSystem fileSystem=getHDFSFileSystem(dir);
        Path path=new Path(dir);
        if(fileSystem.isFile(path)){
            return new ArrayList<String>(0);
        }

        List<String> fileList=new ArrayList<String>();
        FileStatus[] statuses=fileSystem.listStatus(path);
        for (FileStatus fileStatus:statuses){
            if (fileStatus.isDirectory()){
                fileList.add(fileStatus.getPath().toString());
            }
        }
        return fileList;

    }

    /**
     * get all file status of a dir.
     *
     * @param dir
     * @return
     * @throws IOException
     */
    public static List<FileStatus> listFileStatus(String dir) throws IOException {
        FileSystem fileSystem=getHDFSFileSystem(dir);
        Path path=new Path(dir);
        if(fileSystem.isFile(path)){
            return null;
        }
        List<FileStatus> fileStatusList=new ArrayList<FileStatus>();
        FileStatus[] statuses=fileSystem.listStatus(path);
        for (FileStatus fileStatus:statuses){
            if(!fileStatus.isDirectory()){
                fileStatusList.add(fileStatus);
            }
        }
        return fileStatusList;
    }

    /**
     * touch file
     *
     * @param filePath
     * @throws IOException
     */
    public static void touch(String filePath) throws IOException {
        FileSystem fs=getHDFSFileSystem(filePath);
        Path path=new Path(filePath);
        FileStatus st;
        if(fs.exists(path)){
            st=fs.getFileStatus(path);
            if(st.isDirectory()) {
                throw new IOException(filePath+" is a directory");
            }else if(st.getLen()!=0){
                throw new IOException(filePath + " must be a zero-length file");
            }
        }
        FSDataOutputStream out=null;
        try{
            out=fs.create(path);
        }finally {
            if(out!=null){
                out.close();
            }
        }

    }

    /**
     * keep all path ends with '/'
     */
    public static String normalizePath(String path){
        if(null==path){
            return null;
        }
        String tmp=path.trim();
        if(path.endsWith("/")){
            return tmp;
        }else {
            return tmp+"/";
        }
    }


    public static FSType getFSType(String url){
        if(url.startsWith("local:")){
            return FSType.LOCAL;
        }
        else if(url.startsWith("hdfs:")){
            return FSType.HDFS;
        }
        return null;
    }



    public static boolean isFileExist(String path) throws IOException {
        if (getFSType(path).equals(FSType.LOCAL)){
            String localPath=path.substring(path.indexOf(":")+1);
            File file=new File(localPath);
            if (file.exists()){
                return true;
            }
        }
        else if (getFSType(path).equals(FSType.HDFS)){
            FileSystem hdfsFileSystem=getHDFSFileSystem(path);
            Path hdfsPath=new Path(path);
            if (hdfsFileSystem.isFile(hdfsPath) || hdfsFileSystem.isDirectory(hdfsPath)){
                return true;
            }
        }
        return false;
    }

}
