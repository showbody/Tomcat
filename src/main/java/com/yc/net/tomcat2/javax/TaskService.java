package com.yc.net.tomcat2.javax;


import com.yc.net.tomcat2.javax.servlet.YcServletContext;
import com.yc.net.tomcat2.javax.servlet.http.YcHttpServletRequest;
import com.yc.net.tomcat2.javax.servlet.http.YcHttpServletResponse;
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
//            YcHttpServletRequest request = new YcHttpServletRequest(this.s,this.iis);
//            YcHttpServletResponse response = new YcHttpServletResponse(request,this.oos);
//            response.send();

            YcHttpServletRequest request = new YcHttpServletRequest(this.s,this.iis);
            YcHttpServletResponse response = new YcHttpServletResponse(request,this.oos);


            //根据request 中的URI来判断是什么资源
            Processor processor = null;
            //requestURI  /wowotaun/hello    -contextPath  /wowotuan
            int contentPathLength = request.getContextPath().length();
            String uri = request.getRequestURI().substring(contentPathLength);
            if (YcServletContext.servletClass.containsKey(uri)){
                //这是的动态请求
                processor = new DynamicProcessor();
            }else {
                //这是静态请求
                processor = new StaticProcessor();
            }
            processor.process(request,response);
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
