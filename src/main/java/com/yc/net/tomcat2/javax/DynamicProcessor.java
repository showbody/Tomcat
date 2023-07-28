package com.yc.net.tomcat2.javax;

import com.yc.net.tomcat2.javax.servlet.YcServlet;
import com.yc.net.tomcat2.javax.servlet.YcServletContext;
import com.yc.net.tomcat2.javax.servlet.YcServletRequest;
import com.yc.net.tomcat2.javax.servlet.YcServletResponse;
import com.yc.net.tomcat2.javax.servlet.http.YcHttpServletRequest;

import java.io.PrintWriter;

public class DynamicProcessor implements Processor {
    @Override
    public void process(YcServletRequest request, YcServletResponse response) {
        //request中的参数已经解析好了
        //1.从request中取出requestURI(/hello,到ServletContext的map中去取class.
        String uri = ((YcHttpServletRequest) request).getRequestURI();

        int contentPathLength = ((YcHttpServletRequest)request).getContextPath().length();
        uri =uri.substring(contentPathLength);
        YcServlet servlet = null;

        try {
            //2.为了保证单例，先看另一个map中是否已经有这个class的实例 a.如果有，说明是第二次调用，则直接取，再调用service()
            if (YcServletContext.servletInstance.containsKey(uri)) {
                servlet = YcServletContext.servletInstance.get(uri);
            } else {
                //b.如果没有，则说明此servlet是第一次调用 先利用反射创建servlet（调用servlet的无参构造方法
                //存到另一个map，再调用init()->service
                Class clz = YcServletContext.servletClass.get(uri);
                Object obj = clz.newInstance();//调用此Servlet的构造方法
                if (obj instanceof YcServlet) {
                    servlet = (YcServlet) obj;
                    servlet.init();
                    YcServletContext.servletInstance.put(uri, servlet);
                }
            }
            //此service就是客户端要访问的servlet
            servlet.service(request, response);//YcServlet.service->根据method调用doXxx()
        }catch (Exception ex) {
            String bodyEntity = ex.toString();
            String protocol = get500(bodyEntity);
            //以输出流返回到客户端
            PrintWriter out = response.getWriter();
            out.println(protocol);
            out.println(bodyEntity);
            out.flush();
        }
    }

    private String get500(String bodyEntity) {
        String protocol500 = "HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/html; charset=utf-8\r\nContent-length: "+bodyEntity.getBytes().length+"\r\n\r\n";
        return protocol500;

    }
}
