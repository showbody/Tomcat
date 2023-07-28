package com.yc.net.tomcat2.javax.servlet.http;

import com.yc.net.tomcat2.javax.servlet.YcServletResponse;

import java.io.*;

public class YcHttpServletResponse  implements YcServletResponse {
    private YcHttpServletRequest request;
    private OutputStream oos;


    public YcHttpServletResponse(YcHttpServletRequest request, OutputStream oos){
        this.request = request;
        this.oos = oos;
    }
    @Override
    public void send(){
        String uri = this.request.getRequestURI();//com.yc.wowotuan/index.html
        String realPath = this.request.getRealPath();//服务器路径

        File f = new File(realPath,uri);
        byte[] fileContent = null;
        String responseProtocol = null;
        if (!f.exists()){
            //文件不存在
            fileContent = readFile(new File(realPath,"/404.html"));
            responseProtocol = get404(fileContent);
        }else {
            //文件存在 则读取回2xx
            fileContent = readFile(new File(realPath,uri));
            responseProtocol = get200(fileContent);

        }
        try {
            oos.write(responseProtocol.getBytes());
            oos.flush();
            oos.write(fileContent);
            oos.flush();
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if (oos!=null){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public OutputStream getOutputStream() {
        return oos;
    }

    @Override
    public PrintWriter getWriter() {
        return new PrintWriter(this.oos);
    }

    private String get200(byte[] fileContent) {
        String protocol200 = "";
        //先取出请求的资源的类型
        String uri = this.request.getRequestURI();//   uri:/com.yc.wowotuan/index.html
        int index = uri.lastIndexOf(".");
        if (index>=0){
            index = index+1;
        }
        String fileExtension = uri.substring(index);
        if ("JPG".equalsIgnoreCase(fileExtension)){
            protocol200 = "HTTP/1.1 200 ok\r\nContent-Type: image/jpeg\r\nContent-Length:"+fileContent.length+"\r\n\r\n";
        }else  if ("css".equalsIgnoreCase(fileExtension)){
            protocol200 = "HTTP/1.1 200 ok\r\nContent-Type: text/css\r\nContent-Length:"+fileContent.length+"\r\n\r\n";
        }else  if ("js".equalsIgnoreCase(fileExtension)){
            protocol200 = "HTTP/1.1 200 ok\r\nContent-Type: text/js\r\nContent-Length:"+fileContent.length+"\r\n\r\n";
        }else  if ("gif".equalsIgnoreCase(fileExtension)){
            protocol200 = "HTTP/1.1 200 ok\r\nContent-Type: image/gif\r\nContent-Length:"+fileContent.length+"\r\n\r\n";
        }else  if ("png".equalsIgnoreCase(fileExtension)) {
            protocol200 = "HTTP/1.1 200 ok\r\nContent-Type: image/png\r\nContent-Length:" + fileContent.length + "\r\n\r\n";
        }else  if ("swf".equalsIgnoreCase(fileExtension)) {
            protocol200 = "HTTP/1.1 200 ok\r\nContent-Type: application/x-shockwave-flash\r\nContent-Length:" + fileContent.length + "\r\n\r\n";
        }else {
            protocol200 = "HTTP/1.1 200 ok\r\nContent-Type: text/html\r\nContent-Length:"+fileContent.length+"\r\n\r\n";
        }
        return protocol200;
    }

    private String get404(byte[] fileContent) {
        String protocol404 = "HTTP/1.1 404 Not Found\r\nContent-Type: text/html; charset=utf-8\r\nContent-Length: "+fileContent.length+"\r\n";
        protocol404+="Server: kitty server\r\n\r\n";
        return protocol404;
    }

    private byte[] readFile(File file) {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] bs = new byte[100 * 1024];
            int length = -1;
            while ((length = fis.read(bs, 0, bs.length)) != -1) {
                boas.write(bs, 0, length);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if (fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return boas.toByteArray();
    }
}
