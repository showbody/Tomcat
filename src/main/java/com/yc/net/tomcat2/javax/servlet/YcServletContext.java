package com.yc.net.tomcat2.javax.servlet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;




public class YcServletContext {
    public static Map<String,Class> servletClass = new ConcurrentHashMap<String, Class>();
    /**应用程序上下文类
     * <String,Class>
    * url地址  servlet的字节码路径
    * requestURI  再利用反射实例化一个对象
    */


    public static Map<String,YcServlet> servletInstance = new ConcurrentHashMap<String, YcServlet>();

    /**
     * 每个servlet都是单例，当第一次访问这个servlet时，创建后保存到这个map中
     */

}
