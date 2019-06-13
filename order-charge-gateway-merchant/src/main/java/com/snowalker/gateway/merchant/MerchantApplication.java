package com.snowalker.gateway.merchant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * 渠道收单网关启动器
 * @author snowalker
 */
@SpringBootApplication
@ImportResource({"classpath*:META-INF/applicationContext-bean.xml"})
public class MerchantApplication {

    public static void main(String[] args) {
        SpringApplication.run(MerchantApplication.class, args);
    }

}
