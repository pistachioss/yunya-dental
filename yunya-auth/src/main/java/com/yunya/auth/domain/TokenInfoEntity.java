package com.yunya.auth.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: token信息
 * @author: LHB
 * @create: 2021-01-06 10:30
 **/
@Data
public class TokenInfoEntity implements Serializable {
    private List<String> loginDevices = new ArrayList<>();

    public void add(String value) {
        this.loginDevices.add(value);
    }

    public boolean hasElement(String value) {
        return this.loginDevices.contains(value);
    }
}
