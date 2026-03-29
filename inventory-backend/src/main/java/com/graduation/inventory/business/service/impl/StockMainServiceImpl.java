package com.graduation.inventory.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.inventory.base.entity.BaseProductSku;
import com.graduation.inventory.base.mapper.BaseProductSkuMapper;
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
import java.util.stream.Collectors;

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
    private final BaseProductSkuMapper skuMapper;

    @Override
    public Page<StockMain> selectStockPage(Page<StockMain> page, Long warehouseId, String skuCode, String skuName) {
        LambdaQueryWrapper<StockMain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(warehouseId != null, StockMain::getWarehouseId, warehouseId);
        // 注意：skuCode和skuName需要关联查询，这里简化处理
        // 实际项目中可能需要自定义SQL进行关联查询
        return baseMapper.selectPage(page, wrapper);
    }
    
    /**
     * 分页查询库存列表（包含关联信息）
     */
    @Override
    public Map<String, Object> selectStockListWithInfo(Integer pageNum, Integer pageSize, Long warehouseId, String keyword) {
        long offset = (long) (pageNum - 1) * pageSize;
        String skuCode = null;
        String skuName = null;
        
        // 如果有关键词，同时搜索SKU编码和名称
        if (StringUtils.hasText(keyword)) {
            skuCode = keyword;
            skuName = keyword;
        }
        
        List<Map<String, Object>> list = baseMapper.selectStockList(offset, pageSize, warehouseId, skuCode, skuName);
        Long total = baseMapper.selectStockListCount(warehouseId, skuCode, skuName);
        
        // 转换字段名以匹配前端
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map<String, Object> item : list) {
            Map<String, Object> newItem = new HashMap<>();
            newItem.put("id", item.get("id"));
            newItem.put("warehouseId", item.get("warehouse_id"));
            newItem.put("warehouseName", item.get("wh_name"));
            newItem.put("skuId", item.get("sku_id"));
            newItem.put("skuCode", item.get("sku_code"));
            newItem.put("skuName", item.get("sku_name"));
            newItem.put("availableQuantity", item.get("quantity"));
            newItem.put("lockedQuantity", item.get("frozen_qty"));
            newItem.put("totalQuantity", item.get("quantity"));
            newItem.put("batchNo", item.get("batch_no"));
            newItem.put("location", item.get("position"));
            newItem.put("minQuantity", item.get("safety_stock"));
            resultList.add(newItem);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("rows", resultList);
        result.put("total", total);
        return result;
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
        Page<StockRecord> result = stockRecordMapper.selectPage(page, wrapper);
        
        // 填充SKU信息
        List<StockRecord> records = result.getRecords();
        if (!records.isEmpty()) {
            List<Long> skuIds = records.stream()
                    .map(StockRecord::getSkuId)
                    .filter(id -> id != null)
                    .distinct()
                    .collect(Collectors.toList());
            
            if (!skuIds.isEmpty()) {
                // 批量查询SKU信息
                List<BaseProductSku> skuList = skuMapper.selectBatchIds(skuIds);
                Map<Long, BaseProductSku> skuMap = skuList.stream()
                        .collect(Collectors.toMap(BaseProductSku::getId, sku -> sku));
                
                // 填充信息
                for (StockRecord record : records) {
                    BaseProductSku sku = skuMap.get(record.getSkuId());
                    if (sku != null) {
                        record.setSkuCode(sku.getSkuCode());
                        record.setSkuName(sku.getSkuName());
                    }
                }
            }
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> selectWarningList(Long warehouseId) {
        // 查询库存预警列表
        return baseMapper.selectStockWarningList();
    }
}
