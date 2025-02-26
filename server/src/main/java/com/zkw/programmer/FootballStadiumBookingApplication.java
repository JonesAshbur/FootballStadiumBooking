package com.zkw.programmer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动类
 */
@SpringBootApplication
@MapperScan("com.zkw.programmer.dao")
public class FootballStadiumBookingApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(FootballStadiumBookingApplication.class, args);
    }
}
