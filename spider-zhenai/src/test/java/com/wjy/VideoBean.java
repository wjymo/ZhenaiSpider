package com.wjy;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class VideoBean {
    private String inputVideoPath;
    private String outputVideoPath;
    private String startTime;
    private String length;
}
