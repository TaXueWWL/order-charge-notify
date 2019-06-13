package com.snowalker.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * 上游收单网关启动器
 * @author snowalker
 */
@SpringBootApplication
@ImportResource({"classpath*:META-INF/applicationContext-bean.xml"})
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
