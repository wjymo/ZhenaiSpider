package com.wjy.entity;

import lombok.Data;

import java.util.List;

@Data
public class ZhenaiResult {
    List<ZhenaiRequest> requests;
    List<UserProfile> items;
}
