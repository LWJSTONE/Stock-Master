package com.graduation.inventory.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.base.entity.BaseProductSku;
import com.graduation.inventory.base.entity.BaseWarehouse;
import com.graduation.inventory.base.mapper.BaseProductSkuMapper;
import com.graduation.inventory.base.mapper.BaseWarehouseMapper;
import com.graduation.inventory.business.entity.StockMain;
import com.graduation.inventory.business.entity.StockRecord;
import com.graduation.inventory.business.mapper.StockMainMapper;
import com.graduation.inventory.business.mapper.StockRecordMapper;
import com.graduation.inventory.business.service.StockService;
import com.graduation.inventory.common.domain.PageResult;
import com.graduation.inventory.common.exception.ServiceException;
import com.graduation.inventory.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 库存服务实现
 *
 * @author graduation
 * @version 1.0.0
 */
@Service
public class StockServiceImpl implements StockService {

    private static final Logger log = LoggerFactory.getLogger(StockServiceImpl.class);

    @Autowired
    private StockMainMapper stockMainMapper;

    @Autowired
    private StockRecordMapper stockRecordMapper;

    @Autowired
    private BaseProductSkuMapper skuMapper;

    @Autowired
    private BaseWarehouseMapper warehouseMapper;

    /**
     * 分页查询实时库存
     */
    @Override
    public PageResult<Map<String, Object>> getStockList(Page<Map<String, Object>> page, Long warehouseId, String skuCode, String skuName) {
        // 构建查询条件，联表查询库存信息
        List<Map<String, Object>> list = stockMainMapper.selectStockList(
                (page.getCurrent() - 1) * page.getSize(),
                page.getSize(),
                warehouseId,
                skuCode,
                skuName
        );
        
        // 查询总数
        Long total = stockMainMapper.selectStockListCount(warehouseId, skuCode, skuName);
        
        return new PageResult<>(list, total);
    }

    /**
     * 查询库存详情（含仓库、SKU信息）
     */
    @Override
    public Map<String, Object> getStockDetail(Long warehouseId, Long skuId) {
        if (warehouseId == null || skuId == null) {
            throw new ServiceException("仓库ID和SKU ID不能为空");
        }
        
        StockMain stockMain = getStockByWarehouseAndSku(warehouseId, skuId);
        if (stockMain == null) {
            throw new ServiceException("库存记录不存在");
        }
        
        BaseProductSku sku = skuMapper.selectById(skuId);
        BaseWarehouse warehouse = warehouseMapper.selectById(warehouseId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("stock", stockMain);
        result.put("sku", sku);
        result.put("warehouse", warehouse);
        
        return result;
    }

    /**
     * 入库操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void inStock(Long warehouseId, Long skuId, BigDecimal qty, String batchNo, String orderNo, Integer orderType) {
        if (warehouseId == null || skuId == null || qty == null) {
            throw new ServiceException("入库参数不完整");
        }
        if (qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("入库数量必须大于0");
        }
        
        // 检查仓库是否存在
        BaseWarehouse warehouse = warehouseMapper.selectById(warehouseId);
        if (warehouse == null) {
            throw new ServiceException("仓库不存在");
        }
        
        // 检查SKU是否存在
        BaseProductSku sku = skuMapper.selectById(skuId);
        if (sku == null) {
            throw new ServiceException("SKU不存在");
        }
        
        // 查询库存记录
        StockMain stockMain = getStockByWarehouseAndSku(warehouseId, skuId);
        BigDecimal beforeQty = BigDecimal.ZERO;
        
        if (stockMain == null) {
            // 新增库存记录
            stockMain = new StockMain();
            stockMain.setWarehouseId(warehouseId);
            stockMain.setSkuId(skuId);
            stockMain.setQuantity(qty);
            stockMain.setFrozenQty(BigDecimal.ZERO);
            stockMain.setBatchNo(batchNo);
            stockMainMapper.insert(stockMain);
            beforeQty = BigDecimal.ZERO;
        } else {
            // 更新库存
            beforeQty = stockMain.getQuantity();
            stockMain.setQuantity(beforeQty.add(qty));
            if (StringUtils.isNotBlank(batchNo)) {
                stockMain.setBatchNo(batchNo);
            }
            stockMainMapper.updateById(stockMain);
        }
        
        // 记录库存流水
        StockRecord record = new StockRecord();
        record.setOrderNo(orderNo);
        record.setOrderType(orderType);
        record.setWarehouseId(warehouseId);
        record.setSkuId(skuId);
        record.setChangeQty(qty);
        record.setBeforeQty(beforeQty);
        record.setAfterQty(stockMain.getQuantity());
        record.setBatchNo(batchNo);
        record.setCreateTime(new Date());
        stockRecordMapper.insert(record);
        
        log.info("入库成功，仓库ID: {}, SKU ID: {}, 数量: {}, 订单号: {}", warehouseId, skuId, qty, orderNo);
    }

    /**
     * 出库操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void outStock(Long warehouseId, Long skuId, BigDecimal qty, String orderNo, Integer orderType) {
        if (warehouseId == null || skuId == null || qty == null) {
            throw new ServiceException("出库参数不完整");
        }
        if (qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("出库数量必须大于0");
        }
        
        // 检查库存是否充足
        StockMain stockMain = getStockByWarehouseAndSku(warehouseId, skuId);
        if (stockMain == null) {
            throw new ServiceException("库存不足");
        }
        
        BigDecimal beforeQty = stockMain.getQuantity();
        if (beforeQty.compareTo(qty) < 0) {
            throw new ServiceException("库存不足，当前库存: " + beforeQty);
        }
        
        // 扣减库存
        stockMain.setQuantity(beforeQty.subtract(qty));
        stockMainMapper.updateById(stockMain);
        
        // 记录库存流水（出库为负数）
        StockRecord record = new StockRecord();
        record.setOrderNo(orderNo);
        record.setOrderType(orderType);
        record.setWarehouseId(warehouseId);
        record.setSkuId(skuId);
        record.setChangeQty(qty.negate());
        record.setBeforeQty(beforeQty);
        record.setAfterQty(stockMain.getQuantity());
        record.setCreateTime(new Date());
        stockRecordMapper.insert(record);
        
        log.info("出库成功，仓库ID: {}, SKU ID: {}, 数量: {}, 订单号: {}", warehouseId, skuId, qty, orderNo);
    }

    /**
     * 冻结库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void freezeStock(Long warehouseId, Long skuId, BigDecimal qty) {
        if (warehouseId == null || skuId == null || qty == null) {
            throw new ServiceException("冻结参数不完整");
        }
        if (qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("冻结数量必须大于0");
        }
        
        StockMain stockMain = getStockByWarehouseAndSku(warehouseId, skuId);
        if (stockMain == null) {
            throw new ServiceException("库存不存在");
        }
        
        // 检查可用库存是否充足
        BigDecimal availableQty = stockMain.getQuantity().subtract(
                stockMain.getFrozenQty() != null ? stockMain.getFrozenQty() : BigDecimal.ZERO
        );
        if (availableQty.compareTo(qty) < 0) {
            throw new ServiceException("可用库存不足，当前可用库存: " + availableQty);
        }
        
        // 增加冻结库存
        BigDecimal frozenQty = stockMain.getFrozenQty() != null ? stockMain.getFrozenQty() : BigDecimal.ZERO;
        stockMain.setFrozenQty(frozenQty.add(qty));
        stockMainMapper.updateById(stockMain);
        
        log.info("冻结库存成功，仓库ID: {}, SKU ID: {}, 数量: {}", warehouseId, skuId, qty);
    }

    /**
     * 解冻库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfreezeStock(Long warehouseId, Long skuId, BigDecimal qty) {
        if (warehouseId == null || skuId == null || qty == null) {
            throw new ServiceException("解冻参数不完整");
        }
        if (qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("解冻数量必须大于0");
        }
        
        StockMain stockMain = getStockByWarehouseAndSku(warehouseId, skuId);
        if (stockMain == null) {
            throw new ServiceException("库存不存在");
        }
        
        BigDecimal frozenQty = stockMain.getFrozenQty() != null ? stockMain.getFrozenQty() : BigDecimal.ZERO;
        if (frozenQty.compareTo(qty) < 0) {
            throw new ServiceException("冻结库存不足，当前冻结库存: " + frozenQty);
        }
        
        // 减少冻结库存
        stockMain.setFrozenQty(frozenQty.subtract(qty));
        stockMainMapper.updateById(stockMain);
        
        log.info("解冻库存成功，仓库ID: {}, SKU ID: {}, 数量: {}", warehouseId, skuId, qty);
    }

    /**
     * 分页查询库存流水
     */
    @Override
    public PageResult<Map<String, Object>> getStockRecords(Page<Map<String, Object>> page, Long warehouseId, Long skuId, Integer orderType, String orderNo) {
        List<Map<String, Object>> list = stockRecordMapper.selectStockRecordList(
                (page.getCurrent() - 1) * page.getSize(),
                page.getSize(),
                warehouseId,
                skuId,
                orderType,
                orderNo
        );
        
        Long total = stockRecordMapper.selectStockRecordListCount(warehouseId, skuId, orderType, orderNo);
        
        return new PageResult<>(list, total);
    }

    /**
     * 检查库存预警
     */
    @Override
    public List<Map<String, Object>> checkStockWarning() {
        // 查询库存低于安全库存的商品
        return stockMainMapper.selectStockWarningList();
    }

    /**
     * 根据仓库ID和SKU ID查询库存
     */
    @Override
    public StockMain getStockByWarehouseAndSku(Long warehouseId, Long skuId) {
        LambdaQueryWrapper<StockMain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StockMain::getWarehouseId, warehouseId);
        queryWrapper.eq(StockMain::getSkuId, skuId);
        return stockMainMapper.selectOne(queryWrapper);
    }

    /**
     * 检查库存是否充足
     */
    @Override
    public boolean checkStockAvailable(Long warehouseId, Long skuId, BigDecimal qty) {
        StockMain stockMain = getStockByWarehouseAndSku(warehouseId, skuId);
        if (stockMain == null) {
            return false;
        }
        BigDecimal availableQty = stockMain.getQuantity().subtract(
                stockMain.getFrozenQty() != null ? stockMain.getFrozenQty() : BigDecimal.ZERO
        );
        return availableQty.compareTo(qty) >= 0;
    }
}
