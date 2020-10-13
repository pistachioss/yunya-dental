package com.yunya.middletable.controller;

import com.yunya.middletable.service.ShardingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @Autowired
    ShardingService shardingService;

    @RequestMapping("/")
    public String test(){
        shardingService.test();
        return "success";
    }
}
