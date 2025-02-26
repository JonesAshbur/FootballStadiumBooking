package com.zkw.programmer.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class LettuceConnectionValidTask {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    // 每两秒执行任务
    @Scheduled(cron="0/2 * * * * ?")
    public void task() {
        if(redisConnectionFactory instanceof LettuceConnectionFactory){
            LettuceConnectionFactory c=(LettuceConnectionFactory)redisConnectionFactory;
            c.validateConnection();
        }
    }

}
