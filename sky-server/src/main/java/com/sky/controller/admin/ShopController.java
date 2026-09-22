package com.sky.controller.admin;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){
        log.info("修改店铺状态为：{}", status==1?"营业中":"打烊中");
        redisTemplate.opsForValue().set("shopStatus", String.valueOf(status));
        return Result.success();
    }

    @GetMapping("/status")
    public Result<Integer> getStatus(){
        log.info("获取店铺状态");
        String shopStatus = (String) redisTemplate.opsForValue().get("shopStatus");
        return Result.success(shopStatus == null ? 1 : Integer.parseInt(shopStatus));
    }
    
    
}
