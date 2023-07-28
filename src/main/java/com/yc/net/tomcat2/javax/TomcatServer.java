package com.yc.net.tomcat2.javax;

import com.yc.net.tomcat2.javax.servlet.YcServletContext;
import com.yc.net.tomcat2.javax.servlet.YcWebServlet;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

public class TomcatServer {
    static Logger logger =  Logger.getLogger(TomcatServer.class);
    public static void main(String[] args) {
        logger.debug("程序开始了...");
        TomcatServer ts = new TomcatServer();
        int port = ts.parsePortFromXml();
        logger.debug("服务器配置端口为:"+port);
        ts.startServer(port);
    }

    private void startServer(int port){
        boolean flag = true;


        String packgeName = "com.yc";
        String packagePath = packgeName.replaceAll("\\.","/");
        //服务器启动时，扫描它所有的classes， 查找@YcWbServlet的classes，存到map中
        try {
            //jvm类加载器
            Enumeration<URL> files = Thread.currentThread().getContextClassLoader().getResources(packagePath);
            while (files.hasMoreElements()) {
                URL url = files.nextElement();
                logger.info("正在扫描的包路径为:" + url.getFile());
                //查找此包下的文件
                findPackgeClasses(url.getFile(), packgeName);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        try(ServerSocket ss = new ServerSocket(port)){
            logger.debug("服务器启动成功，配置端口为:"+port);
            while (flag){
                Socket s = ss.accept();
                logger.debug("客户端:"+s.getRemoteSocketAddress()+"连接上了服务器");
                TaskService task = new TaskService(s);
                Thread t = new Thread(task);
                t.start();
            }
        }catch (Exception ex){
            ex.printStackTrace();
            logger.debug("服务器套接字创建失败...");
        }
    }


    //packagePath:com/yc        packgeNamecom.yc
    private void findPackgeClasses(String packagePath,  String packgeName) {
        //容错
        if (packagePath.startsWith("/")){
            packagePath = packagePath.substring(1);

        }
        //取这个路径下所有的字节码文件
        File file = new File(packagePath);
        File[] classFiles = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getName().endsWith(".class")||pathname.isDirectory()){
                    return true;
                } else{
                    return false;
                }
            }
        });
//        System.out.println(classFiles);
        if (classFiles!=null&&classFiles.length>0){
            for (File cf:classFiles){
                if (cf.isDirectory()){
                    findPackgeClasses(cf.getAbsolutePath(),packgeName+"."+cf.getName());
                }else {
                    //是字节码文件，则利用类加载器加载这个class文件
                    URLClassLoader uc= new URLClassLoader(new URL[]{});
                    try {
                        Class cls = uc.loadClass(packgeName + "." + cf.getName().replaceAll(".class", ""));
//                        logger.info("加载了一个类:" + cls.getName());
                        if (cls.isAnnotationPresent(YcWebServlet.class)) {
                            logger.info("加载了一个类:"+cls.getName());
                            //通过注解的values()方法取出url地址  存到ServletContext.servletClass这个map中
                            YcWebServlet anno = (YcWebServlet)cls.getAnnotation(YcWebServlet.class);
                            String url =anno.value();
                            YcServletContext.servletClass.put(url,cls);
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private int parsePortFromXml(){
        int port = 8080;
        String serverXmlPath = System.getProperty("user.dir")+ File.separator+"conf"+File.separator+"server.xml";

        try(InputStream iis = new FileInputStream(serverXmlPath);) {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.parse(iis);
            NodeList nl = doc.getElementsByTagName("Connector");
            for (int i = 0; i < nl.getLength(); i++) {
                Element node = (Element) nl.item(i);
                port = Integer.parseInt(node.getAttribute("port"));
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return port;
    }
}
