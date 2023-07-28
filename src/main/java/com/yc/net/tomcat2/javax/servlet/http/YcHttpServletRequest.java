package com.yc.net.tomcat2.javax.servlet.http;

import com.yc.net.tomcat2.javax.servlet.YcServletRequest;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

public class YcHttpServletRequest implements YcServletRequest {
    private Logger logger = Logger.getLogger(YcHttpServletRequest.class);
    private InputStream iis;
    private Socket s;
    private String method;
    private String requestURL;
    private String requestURI;
    private String contextPath;
    private String queryString;
    private Map<String,String[]> parameterMap = new ConcurrentHashMap<>();
    private String scheme;
    private String protocol;

    private String realPath;

    public String getRequestURL() {
        return requestURL;
    }

    public String getQueryString() {
        return queryString;
    }

    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    public String getScheme() {
        return scheme;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getMethod() {
        return this.method;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getRealPath() {
        return realPath;
    }


    public YcHttpServletRequest(Socket s,InputStream iis ){
        this.s = s;
        this.iis = iis;
        this.parseRequest();
    }

    //解析方法
    private void parseRequest(){
        String requestInfoString = readFormInputStream();
        if (requestInfoString==null||"".equals(requestInfoString.trim())){
            throw new RuntimeException("读取输入流异常");
        }
        parseRequestInfoString(requestInfoString);
    }

    private void parseRequestInfoString(String requestInfoString) {
        StringTokenizer st = new StringTokenizer(requestInfoString);
        this.method = st.nextToken();
        this.requestURI = st.nextToken();
        //requestURI要考虑地址栏参数
        int questionIndex = this.requestURI.lastIndexOf("?");
        if (questionIndex>=0){
            //有？即有地址栏参数 参数存queryString
            this.queryString = this.requestURI.substring(questionIndex+1);
            this.requestURI = this.requestURI.substring(0,questionIndex);
        }
        //第三部分 协议版本  HTTP/1.1
        this.protocol = st.nextToken();
        //HTTP
        this.scheme = this.protocol.substring(0,this.protocol.indexOf("/"));
        //requestURI ：   /1442342f/index.html
        //www.baidu       GET/
        //contextPath:   /1442342f
        //               /
        int slash2Index = this.requestURI.indexOf("/",1);
        if (slash2Index>=0){
            this.contextPath = this.requestURI.substring(0,slash2Index);
        }else {
            this.contextPath = this.requestURI;
        }

        //requestURL:URL统一资源定位符  http//:ip:端口/requestURL
        this.requestURL = this.scheme+"://"+s.getLocalSocketAddress()+this.requestURI;

        //参数的处理：/1442342f/index.html?uname=a&pwd=b
        //从queryString中取出参数
        if (this.queryString!=null&&this.queryString.length()>0){
            String[]ps = this.queryString.split("&");
            for (String s:ps){
                String [] params = s.split("=");
                this.parameterMap.put(params[0],params[1].split(","));
            }
        }
        this.realPath = System.getProperty("user.dir")+ File.separator+"webapps";

    }

    private String readFormInputStream() {
        int length = -1;
        StringBuffer sb =null;
        byte[]bs = new byte[300*1024];
        try {
            length = this.iis.read(bs, 0, bs.length);
            sb = new StringBuffer();
            for (int i = 0; i < length; i++) {
                sb.append( (char)bs[i]);
            }
        }catch (Exception ex){
            logger.error("读取请求失败");
            ex.printStackTrace();
        }
        return sb.toString();
    }

    public String[] getParamterValues(String name){
        if (parameterMap==null||parameterMap.size()<=0){
            return null;
        }
        String[] values = this.parameterMap.get(name);
        return values;
    }

    public  String getParamter(String name){
        String[] values = getParamterValues(name);
        if (values==null||values.length<=0){
            return null;
        }
        return values[0];
    }
}
