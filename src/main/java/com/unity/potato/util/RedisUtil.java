package com.unity.potato.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, String> template;

    @Autowired
    private RedisTemplate<String, LocalDateTime> dateTypeTemplate;

    public void add(String key, String value){
        template.opsForValue().set(key, value);
    }

    public void add(String key, LocalDateTime dateTime){
        dateTypeTemplate.opsForValue().set(key, dateTime);
    }

    public void add(String key, String value, long timeoutMinutes){
        template.opsForValue().set(key, value, timeoutMinutes, TimeUnit.MINUTES);
    }

    public String getValue(String key){
        return template.opsForValue().get(key);
    }

    public void delete(String key){
        template.opsForValue().getOperations().delete(key);
    }

    public boolean existData(String key){
        return Boolean.TRUE.equals(template.hasKey(key));
    }

    public Long increment(String key) {
        return template.opsForValue().increment(key);
    }

    public void addWithExpireDay(String key, String value, long expireDay) {
        template.opsForValue().set(key, value, expireDay, TimeUnit.DAYS);
    }

    public void addWithExpireMin(String key, String value, long expireMin) {
        template.opsForValue().set(key, value, expireMin, TimeUnit.MINUTES);
    }

}
