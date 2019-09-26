package com.wjy;

import com.wjy.entity.ZhenaiRequest;
import com.wjy.service.parser.CityListParser;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;


@MapperScan("com.wjy.dao")
@EnableAsync
@SpringBootApplication
public class SpiderExecApplication {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(SpiderExecApplication.class, args);
        Engine engine = context.getBean(Engine.class);
        CityListParser cityListParser = context.getBean(CityListParser.class);
        ZhenaiRequest zhenaiRequest=new ZhenaiRequest();
        zhenaiRequest.setParser(cityListParser);
        zhenaiRequest.setUrl("http://www.zhenai.com/zhenghun");
        engine.addProxy();
        engine.run(zhenaiRequest);
//        context.close();
    }
}
