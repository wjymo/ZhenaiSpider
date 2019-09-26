package com.wjy.service.parser;

import com.wjy.config.SpiderProperty;
import com.wjy.entity.UserProfile;
import com.wjy.entity.ZhenaiResult;
import com.wjy.entity.ZhenaiRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.rmi.runtime.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class UserListParser implements Serializable {
    private static final long serialVersionUID = 6139896974190203996L;
    @Autowired
    private SpiderProperty spiderProperty;
    @Autowired
    private UserParser userParser;

    public ZhenaiResult parseUserList(String content, String cityName) {
        //解析每一个用户
        String userRegex = spiderProperty.getRegexConfig().getUserRegex();
        Pattern pattern = Pattern.compile(userRegex);
        Matcher matcher = pattern.matcher(content);
        List<ZhenaiRequest> spiderRequests=new ArrayList<>();
        while (matcher.find()){
            UserProfile userProfile=new UserProfile();
            ZhenaiRequest spiderRequest1=new ZhenaiRequest();
            String userUrl = matcher.group(1);
            //获取userId
            String userId = userUrl.substring(userUrl.lastIndexOf("/") + 1);
            spiderRequest1.setUrl(userUrl);
            String username = matcher.group(2);
            userProfile.setUserId(userId);
            userProfile.setUsername(username);
            log.info("在用户列表获取到用户信息，名字：{}，url：{}，用户id：{}",username,userUrl,userId);
            userProfile.setCity(cityName);
            Parser parser1=(content2)->userParser.parseUser(content2,userProfile);
            spiderRequest1.setParser(parser1);
            spiderRequests.add(spiderRequest1);
        }

        //解析到下一页
        String nextPageRegex = spiderProperty.getRegexConfig().getNextPageRegex();
        if(StringUtils.isNotEmpty(nextPageRegex)){
            pattern = Pattern.compile(nextPageRegex);
            matcher = pattern.matcher(content);
            while (matcher.find()){
                ZhenaiRequest spiderRequest2=new ZhenaiRequest();
                String nextPageUrl = matcher.group(1);
                log.info("在用户列表获取到用户下一页列表url：{}",nextPageUrl);
                spiderRequest2.setUrl(nextPageUrl);
                Parser parser2=(content2)->this.parseUserList(content2,cityName);
                spiderRequest2.setParser(parser2);
                spiderRequests.add(spiderRequest2);
            }
        }

        ZhenaiResult zhenaiResult=new ZhenaiResult();
        zhenaiResult.setRequests(spiderRequests);
        return zhenaiResult;
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        FileOutputStream fileOutputStream=new FileOutputStream("D:\\xx3.txt");
//        ObjectOutputStream obj = new ObjectOutputStream(fileOutputStream);
//        obj.writeObject(new Parser() {
//            private static final long serialVersionUID = -2697734410409458860L;
//            @Override
//            public ZhenaiResult parse(String content) {
//                return new UserListParser().parseUserList(content,"xxx2");
//            }
//        });
//        obj.writeObject((Parser)(content)->new UserListParser().parseUserList(content,"xxx2"));
//        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream("D:\\xx3.txt")));
//        Parser parser = (Parser)ois.readObject();
        Pattern compile = Pattern.compile(null);

        System.out.println(1);
    }
}
