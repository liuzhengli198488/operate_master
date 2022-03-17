//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gys.common.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RedisManager {
    @Autowired
    private RedisTemplate redisTemplate;

    public RedisManager() {
    }
    public Boolean setNx(String key, Object value, long second) {
        try{
            this.redisTemplate.opsForValue().setIfAbsent(key, value);
            this.redisTemplate.expire(key, second, TimeUnit.SECONDS);
        }catch (Exception e){
            return false;
        }
        return true;
    }
    public void set(String key, Object value) {
        ValueOperations<String, Object> operation = this.redisTemplate.opsForValue();
        operation.set(key, value);
    }

    public void set(String key, Object value, Long second) {
        ValueOperations<String, Object> operation = this.redisTemplate.opsForValue();
        operation.set(key, value, second, TimeUnit.SECONDS);
    }

    public Object get(String key) {
        ValueOperations<String, Object> operation = this.redisTemplate.opsForValue();
        return operation.get(key);
    }

    public void setHash(String key, Map map) {
        HashOperations operation = this.redisTemplate.opsForHash();
        operation.putAll(key, map);
    }

    public void setHash(String key, Map map, Long hour) {
        HashOperations operation = this.redisTemplate.opsForHash();
        operation.putAll(key, map);
        this.redisTemplate.expire(key, hour, TimeUnit.HOURS);
    }

    public Object getHash(String key) {
        HashOperations operation = this.redisTemplate.opsForHash();
        return operation.entries(key);
    }

    public void delete(String key) {
        this.redisTemplate.delete(key);
    }

    public Boolean hasKey(String key) {
        return this.redisTemplate.hasKey(key);
    }

    
    public Long incrBy(String key, long step) {
        return redisTemplate.opsForValue().increment(key, step);
    }




}
