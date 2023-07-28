package com.yc.net.tomcat2.javax;

import com.yc.net.tomcat2.javax.servlet.YcServletRequest;
import com.yc.net.tomcat2.javax.servlet.YcServletResponse;

public class StaticProcessor implements Processor {
    @Override
    public void process(YcServletRequest request, YcServletResponse response) {
        response.send();
    }
}
