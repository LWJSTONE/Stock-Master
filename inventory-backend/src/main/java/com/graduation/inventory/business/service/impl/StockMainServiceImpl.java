package com.graduation.inventory.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.inventory.business.entity.StockMain;
import com.graduation.inventory.business.entity.StockRecord;
import com.graduation.inventory.business.mapper.StockMainMapper;
import com.graduation.inventory.business.mapper.StockRecordMapper;
import com.graduation.inventory.business.service.StockMainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 库存服务实现类
 *
 * @author graduation
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class StockMainServiceImpl extends ServiceImpl<StockMainMapper, StockMain> implements StockMainService {

    private final StockRecordMapper stockRecordMapper;

    @Override
    public Page<StockMain> selectStockPage(Page<StockMain> page, Long warehouseId, String skuCode, String skuName) {
        LambdaQueryWrapper<StockMain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(warehouseId != null, StockMain::getWarehouseId, warehouseId);
        // 注意：skuCode和skuName需要关联查询，这里简化处理
        // 实际项目中可能需要自定义SQL进行关联查询
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public StockMain selectStockById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public Page<StockRecord> selectRecordPage(Page<StockRecord> page, Long warehouseId, String orderNo, Integer orderType) {
        LambdaQueryWrapper<StockRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(warehouseId != null, StockRecord::getWarehouseId, warehouseId);
        wrapper.eq(StringUtils.hasText(orderNo), StockRecord::getOrderNo, orderNo);
        wrapper.eq(orderType != null, StockRecord::getOrderType, orderType);
        wrapper.orderByDesc(StockRecord::getCreateTime);
        return stockRecordMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Map<String, Object>> selectWarningList(Long warehouseId) {
        // 查询库存预警列表，这里简化处理
        // 实际项目中需要根据商品的安全库存进行比较
        List<Map<String, Object>> warningList = new ArrayList<>();
        // 可以在这里添加查询库存低于安全库存的逻辑
        return warningList;
    }
}
