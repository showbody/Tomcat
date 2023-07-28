package com.yc.net.tomcat2.javax.servlet.http;

import com.yc.net.tomcat2.javax.servlet.YcServlet;
import com.yc.net.tomcat2.javax.servlet.YcServletRequest;
import com.yc.net.tomcat2.javax.servlet.YcServletResponse;

public  abstract class YcHttpServlet implements YcServlet {
    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    protected void doPost(YcHttpServletRequest req, YcHttpServletResponse rep){

    }
    protected void doGet(YcHttpServletRequest req, YcHttpServletResponse rep){

    }
    protected void doHead(YcHttpServletRequest req, YcHttpServletResponse rep){

    }
    protected void doTrace(YcHttpServletRequest req,YcHttpServletResponse rep){

    }
    protected void doOption(YcHttpServletRequest req, YcHttpServletResponse rep){

    }


    //模板设计模式  ：规范httpServlet中的各方法的调用顺序
    //在service中判断 method是什么  再调用对应的doXxx的方法
    @Override
    public void service(YcServletRequest request, YcServletResponse response) {
        //从request中取出method(http协议特有)
        String method = ((YcHttpServletRequest)request).getMethod();
        if ("get".equalsIgnoreCase(method)){
            doGet((YcHttpServletRequest)request,(YcHttpServletResponse)response);
        }else if ("post".equalsIgnoreCase(method)){
            doPost((YcHttpServletRequest)request,(YcHttpServletResponse)response);
        }else if ("head".equalsIgnoreCase(method)){
            doHead((YcHttpServletRequest)request,(YcHttpServletResponse)response);
        }else if ("trace".equalsIgnoreCase(method)){
            doTrace((YcHttpServletRequest)request,(YcHttpServletResponse)response);
        }else if ("option".equalsIgnoreCase(method)){
            doOption((YcHttpServletRequest)request,(YcHttpServletResponse)response);
        }else {

        }
    }

    public  void service(YcHttpServletRequest request,YcHttpServletResponse response){
        service(request,response);
    }
}
