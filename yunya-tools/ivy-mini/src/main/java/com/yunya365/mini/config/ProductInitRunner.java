//package com.yunya365.mini.config;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.google.common.base.Joiner;
//import com.yunya.framework.redis.util.RedisUtils;
//import com.yunya365.mini.service.IProductService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//
//import static java.util.stream.Collectors.*;
//
///**
// * @description:
// * @author: xy
// * @date 2022/3/10 10:09
// **/
//
//@Order(1)
//@Component
//@Slf4j
//public class ProductInitRunner implements ApplicationRunner {
//
//    @Resource
//    private RedisUtils redisUtils;
//    @Resource
//    private IProductService productService;
//    @Resource(name = "threadExecutor")
//    private ExecutorService threadExecutor;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        LambdaQueryWrapper<City> wrapper = Wrappers.lambdaQuery(City.class)
//                .select(City::getAreaName, City::getLongitude, City::getLatitude);
//        List<City> list = cityMapper.selectList(wrapper);
//        Map<String, String> values = list.stream()
//                .collect(toMap(City::getAreaName, t -> Joiner.on(",")
//                        .join(t.getLongitude(), t.getLatitude())));
//        threadExecutor.execute(() -> {
//            try {
//                redisUtils.hmSet(CITY_LIST_KEY, values);
//            } catch (Exception e) {
//                log.error("城市经纬度列表初始化失败，size：{}", values.size());
//            }
//        });
//    }
//}
