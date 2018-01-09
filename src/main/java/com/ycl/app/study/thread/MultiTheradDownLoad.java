package com.ycl.app.study.thread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * Encode:UTF-8
 * 
 * Author:zhiming.xu
 * 
 * 多线程的断点下载程序，根据输入的url和指定线程数，来完成断点续传功能。
 * 
 * 每个线程支负责某一小段的数据下载；再通过RandomAccessFile完成数据的整合。
 */
public class MultiTheradDownLoad {

    private String filepath = null;

    private String filename = null;

    public String getFilename() {
        return filename;
    }

    private String tmpfilename = null;

    private int threadNum = 0;

    private CountDownLatch latch = null;//设置一个计数器，代码内主要用来完成对缓存文件的删除

    private long fileLength = 0l;

    private long threadLength = 0l;

    private long[] startPos;//保留每个线程下载数据的起始位置。

    private long[] endPos;//保留每个线程下载数据的截止位置。

    private boolean bool = false;

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public boolean isBool() {
        return bool;
    }

    private URL url = null;

    public URL getUrl() {
        return url;
    }

    //有参构造函数，先构造需要的数据
    public MultiTheradDownLoad(String filepath, int threadNum) {
        this.filepath = filepath;
        this.threadNum = threadNum;
        startPos = new long[this.threadNum];
        endPos = new long[this.threadNum];
        latch = new CountDownLatch(this.threadNum);
    }

    /*
     * 组织断点续传功能的方法
     */
    public void downloadPart() {
        File file = null;
        File tmpfile = null;
        HttpURLConnection httpcon = null;

        //在请求url内获取文件资源的名称；此处没考虑文件名为空的情况，此种情况可能需使用UUID来生成一个唯一数来代表文件名。
        filename = filepath.substring(filepath.lastIndexOf('/') + 1,
            filepath.contains("?") ? filepath.lastIndexOf('?')
                    : filepath.length());
        tmpfilename = filename + "_tmp";
        try {
            url = new URL(filepath);
            httpcon = (HttpURLConnection) url.openConnection();

            setHeader(httpcon);
            fileLength = httpcon.getContentLengthLong();//获取请求资源的总长度。

            file = new File(filename);
            tmpfile = new File(tmpfilename);

            threadLength = fileLength / threadNum;//每个线程需下载的资源大小。
            System.out.println("fileName: " + filename + " ," + "fileLength= "
                    + fileLength + " the threadLength= " + threadLength);

            if (file.exists() && file.length() == fileLength) {
                System.out
                        .println("the file you want to download has exited!!");
                return;
            } else {
                setBreakPoint(startPos, endPos, tmpfile);
                ExecutorService exec = Executors.newCachedThreadPool();
                for (int i = 0; i < threadNum; i++) {
                    exec.execute(new DownLoadThread(startPos[i], endPos[i],
                            this, i, tmpfile, latch));
                }
                latch.await();//当你的计数器减为0之前，会在此处一直阻塞。
                exec.shutdown();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /*
     * 断点设置方法，当有临时文件时，直接在临时文件中读取上次下载中断时的断点位置。没有临时文件，即第一次下载时，重新设置断点。
     * 
     * rantmpfile.seek()跳转到一个位置的目的是为了让各个断点存储的位置尽量分开。
     * 
     * 这是实现断点续传的重要基础。
     */
    private void setBreakPoint(long[] startPos, long[] endPos, File tmpfile) {
        RandomAccessFile rantmpfile = null;
        try {
            if (tmpfile.exists()) {
                System.out.println("the download has continued!!");
                rantmpfile = new RandomAccessFile(tmpfile, "rw");
                for (int i = 0; i < threadNum; i++) {
                    rantmpfile.seek(8 * i + 8);
                    startPos[i] = rantmpfile.readLong();

                    rantmpfile.seek(8 * (i + 1000) + 16);
                    endPos[i] = rantmpfile.readLong();

                    System.out.println("the Array content in the exit file: ");
                    System.out.println("thre thread" + (i + 1) + " startPos:"
                            + startPos[i] + ", endPos: " + endPos[i]);
                }
            } else {
                System.out.println("the tmpfile is not available!!");
                rantmpfile = new RandomAccessFile(tmpfile, "rw");

                //最后一个线程的截止位置大小为请求资源的大小
                for (int i = 0; i < threadNum; i++) {
                    startPos[i] = threadLength * i;
                    if (i == threadNum - 1) {
                        endPos[i] = fileLength;
                    } else {
                        endPos[i] = threadLength * (i + 1) - 1;
                    }

                    rantmpfile.seek(8 * i + 8);
                    rantmpfile.writeLong(startPos[i]);

                    rantmpfile.seek(8 * (i + 1000) + 16);
                    rantmpfile.writeLong(endPos[i]);

                    System.out.println("the Array content: ");
                    System.out.println("thre thread" + (i + 1) + " startPos:"
                            + startPos[i] + ", endPos: " + endPos[i]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rantmpfile != null) {
                    rantmpfile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * 为一个HttpURLConnection模拟请求头，伪装成一个浏览器发出的请求
     */
    public static void setHeader(HttpURLConnection con) {
        con.setRequestProperty("User-Agent",
            "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.3) Gecko/2008092510 Ubuntu/8.04 (hardy) Firefox/3.0.3");
        con.setRequestProperty("Accept-Language", "en-us,en;q=0.7,zh-cn;q=0.3");
        con.setRequestProperty("Accept-Encoding", "aa");
        con.setRequestProperty("Accept-Charset",
            "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        con.setRequestProperty("Keep-Alive", "300");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("If-Modified-Since",
            "Fri, 02 Jan 2009 17:00:05 GMT");
        con.setRequestProperty("If-None-Match", "\"1261d8-4290-df64d224\"");
        con.setRequestProperty("Cache-Control", "max-age=0");
        con.setRequestProperty("Referer",
            "http://www.skycn.com/soft/14857.html");
    }

}
