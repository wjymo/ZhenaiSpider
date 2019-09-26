package com.wjy.entity;

import com.wjy.service.parser.Parser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

//@AllArgsConstructor
//@NoArgsConstructor
@ToString
@Data
public class ZhenaiRequest implements Serializable {
    private static final long serialVersionUID = 2647762075919564006L;
    private String url;
    private Parser parser;


}
