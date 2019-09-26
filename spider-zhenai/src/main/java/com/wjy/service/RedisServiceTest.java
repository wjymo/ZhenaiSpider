package com.wjy.service;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.wjy.entity.ZhenaiRequest;
import com.wjy.entity.ZhenaiResult;
import com.wjy.service.parser.Parser;
import com.wjy.service.parser.ParserImpl;
import com.wjy.service.parser.UserListParser;
import com.wjy.util.ProtostuffUtil;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.*;

@Component
public class RedisServiceTest {
    @Autowired
    private RedisTemplate redisTemplate;

    //    public static void main(String[] args) {
//        Jedis jedis=null;
//        try {
////            RuntimeSchema<Parser> schema=RuntimeSchema.createFrom(Parser.class);
//
//            JedisPool jedisPool=new JedisPool(new GenericObjectPoolConfig(),"wang-108",6379,6000,"gggyvw");
//            jedis= jedisPool.getResource();
//            String key="test.key";
////            ZhenaiRequest zhenaiRequest=new ZhenaiRequest();
//            ParserImpl parser=new ParserImpl() {
//                @Override
//                public ZhenaiResult parse(String content) {
//                    return new UserListParser().parseUserList(content,"xxx");
//                }
//            };
////            zhenaiRequest.setParser(parser);
//            byte[] bytes = ProtostuffUtil.serialize(parser);
////            byte[] bytes = ProtostuffIOUtil.toByteArray(parser, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
//            String set = jedis.set(key.getBytes(), bytes);
//
//            byte[] bytes1 = jedis.get(key.getBytes());
//            if(bytes1!=null){
////                ParserImpl parser1 = schema.newMessage();
////                ProtostuffIOUtil.mergeFrom(bytes1,parser1,schema);
//                ProtostuffUtil.deserialize(bytes1, PageImpl.class);
//                System.out.println(1);
//            }
//        }finally {
//            if(jedis!=null){
//                jedis.close();
//            }
//        }
//
//    }
    public static void main(String[] args) {
        Jedis jedis = null;
        try {
            JedisPool jedisPool = new JedisPool(new GenericObjectPoolConfig(), "wang-108", 6379, 6000, "gggyvw");
            jedis = jedisPool.getResource();
            String key = "test.key2";
            Parser parser = new Parser() {
                @Override
                public ZhenaiResult parse(String content) {
                    ZhenaiResult zhenaiResult = new ZhenaiResult();
                    System.out.println(content);
                    return zhenaiResult;
                }
            };
            byte[] serialize = serialize(parser);
            jedis.set(key.getBytes(),serialize);

            byte[] bytes = jedis.get(key.getBytes());
            Object deserizlize = deserizlize(bytes);
            Parser deserizlize1 = (Parser) deserizlize;
            deserizlize1.parse("henren2");
            System.out.println(1);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
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


    /**
     * 队列key
     */
    public static String QUEUE_KEY = "spider.queue.zhenai";
    /**
     * set的key，用于去重
     */
    public static String SET_KEY = "spider.set.zhenai";

    public void pushRequest(ZhenaiRequest request) {
        String url = request.getUrl();
//        byte[] serialize = ProtostuffUtil.serialize(request);
        Boolean member = redisTemplate.boundSetOps(SET_KEY).isMember(url);
        //当set中不存在此url时，才往队列中加入reuqest
        if (!member) {
//            String s = JSON.toJSONString(request);
            redisTemplate.boundListOps(QUEUE_KEY).leftPush(request);
        }
    }


    public ZhenaiRequest popRequest() {
//        byte[] bytes = (byte[]) redisTemplate.boundListOps(QUEUE_KEY).rightPop();
//        if(bytes!=null) {
//            ZhenaiRequest zhenaiRequest = ProtostuffUtil.deserialize(bytes, ZhenaiRequest.class);
//            String url = zhenaiRequest.getUrl();
//            redisTemplate.boundSetOps(SET_KEY).remove(url);
//            return zhenaiRequest;
//        }
//        return null;
        Object o = redisTemplate.boundListOps(QUEUE_KEY).rightPop();
        if (o != null && o instanceof ZhenaiRequest) {
            ZhenaiRequest zhenaiRequest = (ZhenaiRequest) o;
            String url = zhenaiRequest.getUrl();
            redisTemplate.boundSetOps(SET_KEY).remove(url);
            return zhenaiRequest;
        }
        return null;
    }
}
