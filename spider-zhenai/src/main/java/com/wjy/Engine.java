package com.wjy;

import com.wjy.entity.UserProfile;
import com.wjy.entity.ZhenaiResult;
import com.wjy.entity.ZhenaiRequest;
import com.wjy.exception.ZhenaiException;
import com.wjy.service.*;
import com.wjy.service.fetcher.HttpClientFetcher;
import com.wjy.service.parser.Parser;
import com.wjy.service.store.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class Engine {
    @Autowired
    private RedisService redisService;

    /*********************************************************************************************************/
    @Autowired
    private HttpClientFetcher httpClientFetcher;
    @Autowired
    private StoreService storeService;

    //固定线程池
    private ExecutorService newFixedTaskThreadPool = Executors.newFixedThreadPool(4);

    private List<UserProfile> userProfileList = new ArrayList<>();


    public void run(ZhenaiRequest... seeds) throws InterruptedException {
        for (ZhenaiRequest seed : seeds) {
            redisService.pushRequest(seed);
        }
        while (true) {
            ZhenaiRequest zhenaiRequest = redisService.popRequest();
            if (zhenaiRequest == null) {
                log.info("队列中的url解析完毕，请等待！");
                TimeUnit.SECONDS.sleep(5);
            } else {
                String url = zhenaiRequest.getUrl();
                Parser parser = zhenaiRequest.getParser();
                if (StringUtils.isNotEmpty(url)) {
                    newFixedTaskThreadPool.execute(() -> {
                        if (parser != null) {
                            String content = null;
                            try {
                                content = httpClientFetcher.fetch(url);
                            } catch (ZhenaiException e) {
                                log.error("url：{}抓取失败,将：{}重新放入队列",url,zhenaiRequest);
                                redisService.pushRequest(zhenaiRequest);
                            }
                            if(StringUtils.isNotEmpty(content)){
                                ZhenaiResult zhenaiResult = parser.parse(content);
                                List<ZhenaiRequest> requests = zhenaiResult.getRequests();
                                if(requests!=null){
                                    requests.forEach(request -> {
                                        redisService.pushRequest(request);
                                    });
                                }else {
                                    log.warn("zhenaiResult有，但requests为null，对应url：{}",url);
                                }
                                List<UserProfile> items = zhenaiResult.getItems();
                                if (items != null && !items.isEmpty()) {
                                    userProfileList.addAll(items);
                                    if (userProfileList.size() >= 50) {
                                        //异步批量插入数据
                                        storeService.stoeUsers(userProfileList);
                                    }
                                }
                                try {
                                    TimeUnit.SECONDS.sleep(2);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                        } else {
                            log.warn("url:{}对应parser为空！！", url);
                        }
                    });
                } else {
                    log.warn("{}: 对应url为空", parser);
                }
            }
        }
    }

    public void addProxy(){
        new Thread(()->{
//            while (true){
                String content = httpClientFetcher.fetch("https://www.xicidaili.com/nn");
                Document document = Jsoup.parse(content);
                Element element = document.getElementById("ip_list");
                Elements trs = element.getElementsByTag("tr");
                List<String> ipList=new ArrayList<>();
                for(int i=1;i<trs.size();i++){
                    Element tr = trs.get(i);
                    Elements tds = tr.getElementsByTag("td");
                    String host = tds.get(1).text();
                    String port = tds.get(2).text();
                    String host_port = host.concat("_").concat(port);
                    ipList.add(host_port);
                    if(ipList.size()>5){
                        String[] strings = new String[ipList.size()];
                        String[] array = ipList.toArray(strings);
                        redisService.sadd(RedisService.IP_KEY, array);
                        ipList.clear();
                    }
                }
                System.out.println("ip池一轮添加完毕");
                try {
                    TimeUnit.SECONDS.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//            }
        }).start();
    }

}
