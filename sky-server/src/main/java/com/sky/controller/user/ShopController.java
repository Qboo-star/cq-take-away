package com.sky.controller.user;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;


    @GetMapping("/status")
    public Result<Integer> getStatus(){
        log.info("获取店铺状态");
        String shopStatus = (String) redisTemplate.opsForValue().get("shopStatus");
        return Result.success(shopStatus == null ? 1 : Integer.parseInt(shopStatus));
    }


}
