package com.wjy;

import com.alibaba.fastjson.JSON;
import com.wjy.entity.ZhenaiRequest;
import com.wjy.entity.ZhenaiResult;
import com.wjy.service.RedisService;
import com.wjy.service.parser.Parser;
import com.wjy.service.parser.UserListParser;
import com.wjy.util.ProtostuffUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.NONE)
public class TestRedis implements Serializable {
    private static final long serialVersionUID = 9012772162888154031L;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserListParser userListParser;
    @Test
    public void testPopBytes(){

        RedisSerializer defaultSerializer =redisTemplate.getDefaultSerializer();

        ZhenaiRequest request=new ZhenaiRequest();
        request.setUrl("xxx");
        Parser parser=(content)->userListParser.parseUserList(content,"xxxx");
        request.setParser(parser);
//        byte[] serialize = ProtostuffUtil.serialize(request);
//        String s = JSON.toJSONString(request);
//        byte[] serialize = defaultSerializer.serialize(request);
        redisTemplate.boundListOps(RedisService.QUEUE_KEY).leftPush(request);

//        ZhenaiRequest spiderRequest = redisService.popRequest();
        Object o = redisTemplate.boundListOps(RedisService.QUEUE_KEY).rightPop();
        System.out.println(1);
    }

    @Test
    public void testDBNum(){
        LettuceConnectionFactory connectionFactory = (LettuceConnectionFactory)redisTemplate.getConnectionFactory();
        int database = connectionFactory.getDatabase();
        String hostName = connectionFactory.getHostName();
        int port = connectionFactory.getPort();
        String clientName = connectionFactory.getClientName();
        System.out.println(1);
    }

    @Test
    public void testSer() throws IOException, ClassNotFoundException {
        FileOutputStream fileOutputStream=new FileOutputStream("D:\\xx2.txt");
        ObjectOutputStream obj = new ObjectOutputStream(fileOutputStream);
        obj.writeObject(new Parser() {
            private static final long serialVersionUID = -2697734410409458860L;

            @Override
            public ZhenaiResult parse(String content) {
                return null;
            }
        });

//        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream("D:\\xx.txt")));
//        Parser parser = (Parser)ois.readObject();
//        System.out.println(1);
    }

    @Test
    public void testSet(){
        Long sadd = redisService.sadd("huyao", "niub", "niua");
        System.out.println(1);
    }


}
