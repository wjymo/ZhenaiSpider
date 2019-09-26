package com.wjy.service.parser;

import com.wjy.config.SpiderProperty;
import com.wjy.entity.UserProfile;
import com.wjy.entity.ZhenaiResult;
import com.wjy.entity.ZhenaiRequest;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class UserParser implements Serializable {
    private static final long serialVersionUID = 4142159356881618672L;
    @Autowired
    private SpiderProperty spiderProperty;

    public ZhenaiResult parseUser(String content, UserProfile userProfile) {
        //构建一个user
        //获取用户头像
        String userImgRegex = spiderProperty.getRegexConfig().getUserImgRegex();
        Pattern pattern = Pattern.compile(userImgRegex);
        Matcher matcher = pattern.matcher(content);
        List<UserProfile> items = new ArrayList<>();
        while (matcher.find()) {
            String img = matcher.group(0);
            userProfile.setUserImg(img);
        }

        //获取用户其它属性
        Document document = Jsoup.parse(content);
        Elements elements = document.getElementsByClass("m-content-box");
        Element signature = elements.get(0);
        Element userDetail = elements.get(1);
        Element interests = elements.get(2);
        Element criteria = elements.get(3);
        String signatureStr = signature.getAllElements().get(0).child(0).text();
        userProfile.setSignatureStr(signatureStr);
        //用户基础信息
        Elements purpleElements = userDetail.getElementsByClass("purple");
        if (purpleElements.size() >= 9) {
            String maritalStatus = purpleElements.get(0).text();
            userProfile.setMaritalStatus(maritalStatus);
            String age = purpleElements.get(1).text();
            userProfile.setAge(age);
            String constellation = purpleElements.get(2).text();
            userProfile.setConstellation(constellation);
            String height = purpleElements.get(3).text();
            userProfile.setHeight(height);
            String weight = purpleElements.get(4).text();
            userProfile.setWeight(weight);
            String workplace = purpleElements.get(5).text();
            userProfile.setWorkplace(workplace);
            String income = purpleElements.get(6).text();
            userProfile.setIncome(income);
            String job = purpleElements.get(7).text();
            userProfile.setJob(job);
            String education = purpleElements.get(8).text();
            userProfile.setEducation(education);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                String maritalStatus = purpleElements.get(0).text();
                stringBuilder.append(maritalStatus).append("||");
                String age = purpleElements.get(1).text();
                stringBuilder.append(age).append("||");
                String constellation = purpleElements.get(2).text();
                stringBuilder.append(constellation).append("||");
                String height = purpleElements.get(3).text();
                stringBuilder.append(height).append("||");
                String weight = purpleElements.get(4).text();
                stringBuilder.append(weight).append("||");
                String workplace = purpleElements.get(5).text();
                stringBuilder.append(workplace).append("||");
                String income = purpleElements.get(6).text();
                stringBuilder.append(income).append("||");
                String job = purpleElements.get(7).text();
                stringBuilder.append(job).append("||");
                String education = purpleElements.get(8).text();
                stringBuilder.append(education);
            } catch (IndexOutOfBoundsException e) {
                log.error("用户：{}的信息不全，其信息为：{}", userProfile.getUserId(), stringBuilder.toString(), e);
            }
            userProfile.setBaseContent(stringBuilder.toString());
        }

        //用户扩展信息
        Elements pinkElements = userDetail.getElementsByClass("pink");
        if (pinkElements != null) {
            if (pinkElements.size() >= 10) {
                String nation = pinkElements.get(0).text();
                userProfile.setNation(nation);
                String birthplace = pinkElements.get(1).text();
                userProfile.setBirthplace(birthplace);
                String bodyType = pinkElements.get(2).text();
                userProfile.setBodyType(bodyType);
                String smoke = pinkElements.get(3).text();
                userProfile.setSmoke(smoke);
                String drink = pinkElements.get(4).text();
                userProfile.setDrink(drink);
                String house = pinkElements.get(5).text();
                userProfile.setHouse(house);
                String car = pinkElements.get(6).text();
                userProfile.setCar(car);
                String children = pinkElements.get(7).text();
                userProfile.setChildren(children);
                String toHaveChildren = pinkElements.get(8).text();
                userProfile.setToHaveChildren(toHaveChildren);
                String marriedTime = pinkElements.get(9).text();
                userProfile.setMarriedTime(marriedTime);
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                try {
                    String nation = pinkElements.get(0).text();
                    stringBuilder.append(nation).append("||");
                    String birthplace = pinkElements.get(1).text();
                    stringBuilder.append(birthplace).append("||");
                    String bodyType = pinkElements.get(2).text();
                    stringBuilder.append(bodyType).append("||");
                    String smoke = pinkElements.get(3).text();
                    stringBuilder.append(smoke).append("||");
                    String drink = pinkElements.get(4).text();
                    stringBuilder.append(drink).append("||");
                    String house = pinkElements.get(5).text();
                    stringBuilder.append(house).append("||");
                    String car = pinkElements.get(6).text();
                    stringBuilder.append(car).append("||");
                    String children = pinkElements.get(7).text();
                    stringBuilder.append(children).append("||");
                    String toHaveChildren = pinkElements.get(8).text();
                    stringBuilder.append(toHaveChildren).append("||");
                    String marriedTime = pinkElements.get(9).text();
                    stringBuilder.append(marriedTime);
                } catch (IndexOutOfBoundsException e) {
                    log.error("用户：{}的信息不全，其信息为：{}", userProfile.getUserId(), stringBuilder.toString(), e);
                }
                userProfile.setExtraContent(stringBuilder.toString());
            }
        }


        //用户兴趣
        Elements questionElements = interests.getElementsByClass("question");
        Elements answerElements = interests.getElementsByClass("answer");
        Map<String, String> interestsMap = new HashMap<String, String>();
        for (int i = 0; i < questionElements.size(); i++) {
            String text = answerElements.get(i).text();
            interestsMap.put(questionElements.get(i).text(), text);
        }
        userProfile.setInterests(interestsMap.toString());

        //择偶条件
        Elements criteriaElements = criteria.getElementsByClass("m-btn");
        if (criteriaElements != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < criteriaElements.size(); i++) {
                String text = criteriaElements.get(i).text();
                stringBuilder.append(text).append("||");
            }
            userProfile.setCriteriaContent(stringBuilder.toString());
//            if (criteriaElements.size() >= 10) {
//                String criteriaAge = criteriaElements.get(0).text();
//                userProfile.setCriteriaAge(criteriaAge);
//                String criteriaHeight = criteriaElements.get(1).text();
//                userProfile.setCriteriaHeight(criteriaHeight);
//                String criteriaWorkplace = criteriaElements.get(2).text();
//                userProfile.setCriteriaWorkplace(criteriaWorkplace);
//                String criteriaIncome = criteriaElements.get(3).text();
//                userProfile.setCriteriaIncome(criteriaIncome);
//                String criteriaEducation = criteriaElements.get(4).text();
//                userProfile.setCriteriaEducation(criteriaEducation);
//                String criteriamaritalStatus = criteriaElements.get(5).text();
//                userProfile.setCriteriamaritalStatus(criteriamaritalStatus);
//                String criteriaBodyType = criteriaElements.get(6).text();
//                userProfile.setCriteriaBodyType(criteriaBodyType);
//                String criteriaDrink = criteriaElements.get(7).text();
//                userProfile.setCriteriaDrink(criteriaDrink);
//                String criteriaSmoke = criteriaElements.get(8).text();
//                userProfile.setCriteriaSmoke(criteriaSmoke);
//                String criteriaChild = criteriaElements.get(9).text();
//                userProfile.setCriteriaChild(criteriaChild);
//            } else {
//                StringBuilder stringBuilder = new StringBuilder();
//                try {
//                    String criteriaAge = criteriaElements.get(0).text();
//                    stringBuilder.append(criteriaAge).append("||");
//                    String criteriaHeight = criteriaElements.get(1).text();
//                    stringBuilder.append(criteriaHeight).append("||");
//                    String criteriaWorkplace = criteriaElements.get(2).text();
//                    stringBuilder.append(criteriaWorkplace).append("||");
//                    String criteriaIncome = criteriaElements.get(3).text();
//                    stringBuilder.append(criteriaIncome).append("||");
//                    String criteriaEducation = criteriaElements.get(4).text();
//                    stringBuilder.append(criteriaEducation).append("||");
//                    String criteriamaritalStatus = criteriaElements.get(5).text();
//                    stringBuilder.append(criteriamaritalStatus).append("||");
//                    String criteriaBodyType = criteriaElements.get(6).text();
//                    stringBuilder.append(criteriaBodyType).append("||");
//                    String criteriaDrink = criteriaElements.get(7).text();
//                    stringBuilder.append(criteriaDrink).append("||");
//                    String criteriaSmoke = criteriaElements.get(8).text();
//                    stringBuilder.append(criteriaSmoke).append("||");
//                    String criteriaChild = criteriaElements.get(9).text();
//                    stringBuilder.append(criteriaChild);
//                } catch (IndexOutOfBoundsException e) {
//                    log.error("用户：{}的信息不全，其信息为：{}", userProfile.getUserId(), stringBuilder.toString(), e);
//                }
//                userProfile.setCriteriaContent(stringBuilder.toString());
//            }
        }

        /*String criteriaAge = criteriaElements.get(0).text();
        userProfile.setCriteriaAge(criteriaAge);
        String criteriaWorkplace = criteriaElements.get(1).text();
        userProfile.setCriteriaWorkplace(criteriaWorkplace);
        String criteriaIncome = criteriaElements.get(2).text();
        userProfile.setCriteriaIncome(criteriaIncome);*/

        log.info("在用户页面获取到用户详细信息：{}", userProfile);

        items.add(userProfile);
        ZhenaiResult zhenaiResult = new ZhenaiResult();
        zhenaiResult.setItems(items);

        return zhenaiResult;
    }
}
