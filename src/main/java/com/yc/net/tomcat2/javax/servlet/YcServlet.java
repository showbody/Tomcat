package com.yc.net.tomcat2.javax.servlet;



//服务器小程序  接口
public interface YcServlet {

    //初始化方法：在生命周期中，是在构造方法后调用一次
    public void init();

    //销毁方法
    public void destroy();

    //每次请求都会调用service
    public void service(YcServletRequest request, YcServletResponse response);



}
