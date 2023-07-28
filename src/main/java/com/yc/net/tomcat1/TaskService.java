package com.yc.net.tomcat1;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TaskService implements Runnable {
    private Logger logger = Logger.getLogger(TaskService.class);

    private Socket s;
    private InputStream iis;
    private OutputStream oos;
    private boolean flag = true;

    public TaskService(Socket s) {
        this.s = s;
        try {
            this.iis = this.s.getInputStream();
            this.oos = this.s.getOutputStream();
        }catch (Exception ex){
            logger.debug("socket获取流异常");
            ex.printStackTrace();
            flag = false;
        }
    }

    @Override
    public void run() {
        if (this.flag){
            YcHttpServletRequest request = new YcHttpServletRequest(this.s,this.iis);
            YcHttpServletResponse response = new YcHttpServletResponse(request,this.oos);
            response.send();
        }
        try {
            this.iis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
