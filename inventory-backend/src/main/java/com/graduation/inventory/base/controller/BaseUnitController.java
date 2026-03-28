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
 * 单位控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "单位管理")
@RestController
@RequestMapping("/base/unit")
@RequiredArgsConstructor
public class BaseUnitController {

    /**
     * 获取单位列表
     */
    @ApiOperation("获取单位列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        // 返回预定义的单位列表
        List<Map<String, Object>> units = new ArrayList<>();
        
        Map<String, Object> unit1 = new HashMap<>();
        unit1.put("value", "件");
        unit1.put("label", "件");
        units.add(unit1);
        
        Map<String, Object> unit2 = new HashMap<>();
        unit2.put("value", "台");
        unit2.put("label", "台");
        units.add(unit2);
        
        Map<String, Object> unit3 = new HashMap<>();
        unit3.put("value", "个");
        unit3.put("label", "个");
        units.add(unit3);
        
        Map<String, Object> unit4 = new HashMap<>();
        unit4.put("value", "箱");
        unit4.put("label", "箱");
        units.add(unit4);
        
        Map<String, Object> unit5 = new HashMap<>();
        unit5.put("value", "套");
        unit5.put("label", "套");
        units.add(unit5);
        
        Map<String, Object> unit6 = new HashMap<>();
        unit6.put("value", "瓶");
        unit6.put("label", "瓶");
        units.add(unit6);
        
        Map<String, Object> unit7 = new HashMap<>();
        unit7.put("value", "kg");
        unit7.put("label", "千克");
        units.add(unit7);
        
        Map<String, Object> unit8 = new HashMap<>();
        unit8.put("value", "m");
        unit8.put("label", "米");
        units.add(unit8);
        
        return Result.success(units);
    }
}
