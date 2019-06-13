package com.snowalker.notify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * 通知服务启动器
 * @author snowalker
 */
@SpringBootApplication
@ImportResource({"classpath*:META-INF/applicationContext-bean.xml"})
public class NotifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotifyApplication.class, args);
    }

}
