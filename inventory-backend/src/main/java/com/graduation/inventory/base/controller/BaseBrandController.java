package com.graduation.inventory.base.controller;

import com.graduation.inventory.common.domain.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 品牌控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "品牌管理")
@RestController
@RequestMapping("/base/brand")
@RequiredArgsConstructor
public class BaseBrandController {

    /**
     * 获取品牌列表
     */
    @ApiOperation("获取品牌列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        // 返回预定义的品牌列表
        List<Map<String, Object>> brands = new ArrayList<>();
        
        Map<String, Object> brand1 = new HashMap<>();
        brand1.put("id", 1);
        brand1.put("name", "华为");
        brand1.put("code", "HUAWEI");
        brands.add(brand1);
        
        Map<String, Object> brand2 = new HashMap<>();
        brand2.put("id", 2);
        brand2.put("name", "小米");
        brand2.put("code", "XIAOMI");
        brands.add(brand2);
        
        Map<String, Object> brand3 = new HashMap<>();
        brand3.put("id", 3);
        brand3.put("name", "苹果");
        brand3.put("code", "APPLE");
        brands.add(brand3);
        
        Map<String, Object> brand4 = new HashMap<>();
        brand4.put("id", 4);
        brand4.put("name", "联想");
        brand4.put("code", "LENOVO");
        brands.add(brand4);
        
        Map<String, Object> brand5 = new HashMap<>();
        brand5.put("id", 5);
        brand5.put("name", "戴尔");
        brand5.put("code", "DELL");
        brands.add(brand5);
        
        Map<String, Object> brand6 = new HashMap<>();
        brand6.put("id", 6);
        brand6.put("name", "惠普");
        brand6.put("code", "HP");
        brands.add(brand6);
        
        return Result.success(brands);
    }

    /**
     * 获取品牌选项（下拉选择用）
     */
    @ApiOperation("获取品牌选项")
    @GetMapping("/options")
    public Result<List<Map<String, Object>>> options() {
        return list();
    }
}
