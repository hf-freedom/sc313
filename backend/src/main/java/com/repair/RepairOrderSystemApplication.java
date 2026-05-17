package com.repair;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RepairOrderSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(RepairOrderSystemApplication.class, args);
        System.out.println("维修师傅抢单系统启动成功！端口: 8002");
    }
}
