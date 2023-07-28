package com.yc.net.tomcat1;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
