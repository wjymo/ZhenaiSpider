package com.wjy.service.parser;

import com.wjy.config.SpiderProperty;
import com.wjy.entity.ZhenaiResult;
import com.wjy.entity.ZhenaiRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CityListParser implements Parser, Serializable {

    private static final long serialVersionUID = 6961892584429537718L;
    @Autowired
    private SpiderProperty spiderProperty;
    @Autowired
    private UserListParser userListParser;

    @Override
    public ZhenaiResult parse(String content) {
        String cityRegex = spiderProperty.getRegexConfig().getCityRegex();
        Pattern pattern = Pattern.compile(cityRegex);
        Matcher matcher = pattern.matcher(content);
//        PageResult pageResult = parseItem(userListParser, matcher);
        List<ZhenaiRequest> spiderRequests=new ArrayList<>();
//        List<PageItem> pageItems=new ArrayList<>();
        while(matcher.find()){
            ZhenaiRequest spiderRequest=new ZhenaiRequest();
            String cityUrl = matcher.group(1);
            spiderRequest.setUrl(cityUrl);


            String cityName = matcher.group(2);
//            Parser parser = new Parser() {
//                private static final long serialVersionUID = -3757336258612270891L;
//                @Override
//                public ZhenaiResult parse(String content) {
//                    return userListParser.parseUserList(content,cityName );
//                }
//            };
            Parser parser=(content2)->userListParser.parseUserList(content2,cityName );
            spiderRequest.setParser(parser);
            spiderRequests.add(spiderRequest);

//            PageItem pageItem=new PageItem();
//            pageItem.setCity(cityName);
//            pageItems.add(pageItem);
            System.out.println("在城市列表页获取到相关信息： "+cityUrl+" : "+cityName);
        }
        ZhenaiResult pageResult=new ZhenaiResult();
        pageResult.setRequests(spiderRequests);
//        pageResult.setPageItems(pageItems);
        return pageResult;
    }




}
