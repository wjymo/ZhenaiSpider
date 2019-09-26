package com.wjy.service;

import com.alibaba.fastjson.JSON;
import com.wjy.entity.ZhenaiRequest;
import com.wjy.util.ProtostuffUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.util.function.Function;

@Slf4j
@Component
public class RedisService {
//    @Autowired
//    private RedisTemplate redisTemplate;

    @Autowired
    private JedisPool jedisPool;

    private Jedis getJedis(){
        return jedisPool.getResource();
    }
    /**
     * 队列key
     */
    public static String QUEUE_KEY = "spider.queue.zhenai";
    /**
     * set的key，用于去重
     */
    public static String SET_KEY = "spider.set.zhenai";
    public static String IP_KEY = "proxy.ip";

    public void pushRequest(ZhenaiRequest request) {
        execute(jedis -> {
            String url = request.getUrl();
            Boolean sismember = jedis.sismember(SET_KEY, url);
            if(!sismember){
                byte[] serialize = serialize(request);
                jedis.lpush(QUEUE_KEY.getBytes(),serialize);
                jedis.sadd(SET_KEY, url);
            }
            return null;
        });

//        Jedis jedis=null;
//        try {
//            jedis= getJedis();
//            String url = request.getUrl();
//            Boolean sismember = jedis.sismember(SET_KEY, url);
//            if(!sismember){
//                byte[] serialize = serialize(request);
//                jedis.lpush(QUEUE_KEY.getBytes(),serialize);
//                jedis.sadd(SET_KEY, url);
//            }
//        } finally {
//            // 必须要释放Redis连接
//            if (jedis != null) {
//                jedis.close();
//            }
//        }
    }

    public ZhenaiRequest popRequest() {
        return execute(jedis ->{
            byte[] bytes = jedis.rpop(QUEUE_KEY.getBytes());
            if(bytes!=null) {
                ZhenaiRequest zhenaiRequest = (ZhenaiRequest) deserizlize(bytes);
                String url = zhenaiRequest.getUrl();
                jedis.srem(SET_KEY,url);
                return zhenaiRequest;
            }
            return null;
        });
//        Jedis jedis=null;
//        try {
//            jedis= getJedis();
//            byte[] bytes = jedis.rpop(QUEUE_KEY.getBytes());
//            if(bytes!=null) {
//                ZhenaiRequest zhenaiRequest = (ZhenaiRequest) deserizlize(bytes);
//                String url = zhenaiRequest.getUrl();
//                jedis.srem(SET_KEY,url);
//                return zhenaiRequest;
//            }
//        }finally {
//            if (jedis != null) {
//                jedis.close();
//            }
//        }
//        return null;
    }
    public Long sadd(String key,String ...value){
        return execute(jedis -> {
            Long sadd = jedis.sadd(key, value);
            return sadd;
        });
    }
    public String srandmember(String key){
        return execute(jedis -> {
            String srandmember = jedis.srandmember(key);
            return srandmember;
        });
    }

    public Long sdel(String key,String value){
        return execute(jedis -> {
            Long srem = jedis.srem(key, value);
            return srem;
        });
    }

    private static byte[] serialize(Object object) {
        ObjectOutputStream objectOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            byte[] getByte = byteArrayOutputStream.toByteArray();
            return getByte;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object deserizlize(byte[] binaryByte) {
        ObjectInputStream objectInputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;
        byteArrayInputStream = new ByteArrayInputStream(binaryByte);
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object obj = objectInputStream.readObject();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private <T> T execute(Function<Jedis,T> fun){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return fun.apply(jedis);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
//    public void pushRequest(ZhenaiRequest request) {
//        String url = request.getUrl();
////        byte[] serialize = ProtostuffUtil.serialize(request);
//        Boolean member = redisTemplate.boundSetOps(SET_KEY).isMember(url);
//        //当set中不存在此url时，才往队列中加入reuqest
//        if(!member){
////            String s = JSON.toJSONString(request);
//            redisTemplate.boundListOps(QUEUE_KEY).leftPush(request);
//        }
//    }


//    public ZhenaiRequest popRequest() {
////        byte[] bytes = (byte[]) redisTemplate.boundListOps(QUEUE_KEY).rightPop();
////        if(bytes!=null) {
////            ZhenaiRequest zhenaiRequest = ProtostuffUtil.deserialize(bytes, ZhenaiRequest.class);
////            String url = zhenaiRequest.getUrl();
////            redisTemplate.boundSetOps(SET_KEY).remove(url);
////            return zhenaiRequest;
////        }
////        return null;
//        Object o = redisTemplate.boundListOps(QUEUE_KEY).rightPop();
//        if(o!=null && o instanceof ZhenaiRequest) {
//            ZhenaiRequest zhenaiRequest = (ZhenaiRequest)o ;
//            String url = zhenaiRequest.getUrl();
//            redisTemplate.boundSetOps(SET_KEY).remove(url);
//            return zhenaiRequest;
//        }
//        return null;
//    }
}
