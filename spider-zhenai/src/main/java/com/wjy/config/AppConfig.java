package com.wjy.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class AppConfig {
    @Autowired
    private SpiderProperty spiderProperty;

    @Bean
    public PoolingHttpClientConnectionManager httpClientConnectionManager() {
        PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager();
        httpClientConnectionManager.setMaxTotal(Integer.valueOf(spiderProperty.getHttpClientConfig().getMaxTotal()));
        httpClientConnectionManager.setDefaultMaxPerRoute(Integer.valueOf(spiderProperty.getHttpClientConfig().getDefaultMaxPerRoute()));
        return httpClientConnectionManager;
    }

    @Bean
    public HttpClientBuilder httpClientBuilder() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(httpClientConnectionManager());
        return httpClientBuilder;
    }

    @Bean
    @Scope("prototype")
    public CloseableHttpClient httpClient(HttpClientBuilder httpClientBuilder) {
        CloseableHttpClient httpClient = httpClientBuilder.build();
        return httpClient;
    }

    @Bean
    public RequestConfig requestConfig() {
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectTimeout(Integer.valueOf(spiderProperty.getHttpClientConfig().getConnectTimeout()));
        builder.setConnectionRequestTimeout(Integer.valueOf(spiderProperty.getHttpClientConfig().getConnectionRequestTimeout()));
        builder.setSocketTimeout(Integer.valueOf(spiderProperty.getHttpClientConfig().getSocketTimeout()));
        return builder.build();
    }


//    @Bean
//    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//        // 使用Jackson2JsonRedisSerialize 替换默认序列化
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//
//        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
//
//        // 设置value的序列化规则和 key的序列化规则
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
//        redisTemplate.afterPropertiesSet();
//        return redisTemplate;
//    }

    @Bean
    public JedisPool jedisPool(){
        JedisPool jedisPool = new JedisPool(new GenericObjectPoolConfig(), "wang-108", 6379, 6000, "gggyvw");
        return jedisPool;
    }
}
