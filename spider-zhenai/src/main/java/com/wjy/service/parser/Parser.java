package com.wjy.service.parser;

import com.wjy.entity.ZhenaiResult;

import java.io.Serializable;

public interface Parser extends Serializable {
    ZhenaiResult parse(String content);
}
