package com.yc.wowotuan;

import com.yc.net.tomcat2.javax.servlet.YcWebServlet;
import com.yc.net.tomcat2.javax.servlet.http.YcHttpServlet;
import com.yc.net.tomcat2.javax.servlet.http.YcHttpServletRequest;
import com.yc.net.tomcat2.javax.servlet.http.YcHttpServletResponse;

import java.io.PrintWriter;


//生命周期  ：构造方法->  init()->service()->doGet()/doPost()
//                            service()->doGet()/doPost()
//servlet 单例
@YcWebServlet("/hello")
public class HelloServlet extends YcHttpServlet {
    public HelloServlet(){
        System.out.println("构造方法...");
    }
    @Override
    public void init() {
        System.out.println("初始化方法...");
    }

    @Override
    protected void doGet(YcHttpServletRequest req, YcHttpServletResponse resp) {

//        System.out.println("hello world");
        String result = "hello world";
        PrintWriter out = resp.getWriter();
        out.print("HTTP/1.1 200 ok\r\nContent-Type: text/html; charset=utf-8\r\nContent-Length: "+result.getBytes().length+"\r\n\r\n");
        out.print(result);
        out.flush();
    }
}
