package com.wjy;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TimeBean {
    private Integer hour;
    private Integer minute;
    private Double second;
}
