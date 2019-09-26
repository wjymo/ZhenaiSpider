package com.wjy.entity;

import lombok.*;

import java.io.Serializable;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserProfile implements Serializable {
    private static final long serialVersionUID = -4346661717793939815L;
    private Integer id;


    private String signatureStr;
    /**
     * 基础信息
     */
    private String userId;
    private String city;
    private String username;
    private String userImg;
    private String age;
    private String maritalStatus;
    private String constellation;
    private String height;
    private String weight;
    private String workplace;
    private String income;
    private String job;
    private String education;

    private String baseContent;
//    private Boolean baseAllSet;

    /**
     * 问答（兴趣爱好）
     */
    private String interests;



    /**
     * 额外信息
     */
    private String nation ;
    private String birthplace ;
    private String bodyType ;
    private String smoke ;
    private String drink ;
    private String house ;
    private String car ;
    private String children ;
    private String toHaveChildren ;
    private String marriedTime ;

    private String extraContent;
//    private Boolean extraAllSet;

    /**
     * 择偶条件，爬取的数据是乱序的
     */
//    private String criteriaAge;
//    private String criteriaWorkplace;
//    private String criteriaIncome;
//    private String criteriaHeight ;
//    private String criteriaEducation ;
//    private String criteriamaritalStatus ;
//    private String criteriaBodyType ;
//    private String criteriaDrink ;
//    private String criteriaSmoke ;
//    private String criteriaChild ;

    private String criteriaContent;
}
