package com.wjy.service.fetcher;

import com.wjy.exception.ZhenaiException;
import com.wjy.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.UnknownHostException;

@Slf4j
@Component
public class HttpClientFetcher {
    private final static String USER_AGENT="Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
    @Autowired
    private BeanFactory beanFactory;
    @Autowired
    private RequestConfig requestConfig;
    @Autowired
    private HttpClientBuilder httpClientBuilder;
    @Autowired
    private RedisService redisService;

    public String fetch(String url) {
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("User-Agent",USER_AGENT);
        CloseableHttpResponse response = null;
        String host_port= redisService.srandmember(RedisService.IP_KEY);
        HttpHost httpHost=null;
        if(StringUtils.isNotEmpty(host_port)){
            String[] s = host_port.split("_");
            String host=s[0];
            String port=s[1];
            httpHost=new HttpHost(host,Integer.valueOf(port));
        }
        try {
            // 执行请求
            if(httpHost==null){
                response =  getHttpClient().execute(httpGet);
            }else {
                response = httpClientBuilder.setProxy(httpHost).build().execute(httpGet);
            }
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (HttpHostConnectException |ConnectTimeoutException | UnknownHostException e) {
            //如果当前ip不可用，从动态代理ip库里面删除
            redisService.sdel(RedisService.IP_KEY,host_port);
            log.error("url：{}抓取失败",url,e);
            throw new ZhenaiException("url：{}抓取失败");
        } catch (ClientProtocolException e) {
            log.error("【ClientProtocolException】url：{}抓取失败",url,e);
        } catch (IOException e) {
            log.error("【IOException】url：{}抓取失败",url,e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public String fetchWithProxy(String url, HttpHost httpHost){
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("User-Agent",USER_AGENT);
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            CloseableHttpClient httpClient = httpClientBuilder.setProxy(httpHost).build();
            response =  httpClient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private CloseableHttpClient getHttpClient(){
        return  beanFactory.getBean(CloseableHttpClient.class);
    }
}
