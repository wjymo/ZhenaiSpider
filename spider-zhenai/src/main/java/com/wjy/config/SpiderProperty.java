package com.wjy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;


@Data
@Component
@ConfigurationProperties(prefix = "wjy.config")
public class SpiderProperty implements Serializable {
    private static final long serialVersionUID = -671205722088924600L;
    private HttpClientConfig httpClientConfig;
    private CommonConfig commonConfig;
    private XpathConfig xpathConfig;
    private RegexConfig regexConfig;

    @Data
    public static class HttpClientConfig implements Serializable {
        private static final long serialVersionUID = 1071003476119428443L;
        private String maxTotal;
        private String defaultMaxPerRoute;
        private String connectTimeout;
        private String connectionRequestTimeout;
        private String socketTimeout;
    }

    @Data
    public static class RegexConfig implements Serializable{
        private static final long serialVersionUID = -4879880525118217866L;
        private String cityRegex;
        private String userRegex;
        private String nextPageRegex;
        private String userImgRegex;


    }

    @Data
    public static class CommonConfig implements Serializable{
        private static final long serialVersionUID = 2199260796334371205L;
        private String threadNum;
    }

    @Data
    public static class XpathConfig implements Serializable{
        private static final long serialVersionUID = 5761342494945075586L;
        //页面内容
        private String content;
        //总播放量

        private String allnumber;
        //每日播放增量

        private String daynumber;
        //评论数

        private String commentnumber;
        //收藏数

        private String collectnumber;
        //赞

        private String supportnumber;
        //踩

        private String againstnumber;
    }
}

