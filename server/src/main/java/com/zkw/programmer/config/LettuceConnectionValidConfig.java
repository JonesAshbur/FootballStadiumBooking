package com.zkw.programmer.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Component;


@Component
public class LettuceConnectionValidConfig implements InitializingBean {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 启动项目时候进行Lettuce连接校验
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if(redisConnectionFactory instanceof LettuceConnectionFactory){
            LettuceConnectionFactory c=(LettuceConnectionFactory)redisConnectionFactory;
            c.setValidateConnection(true);
        }
    }
}
