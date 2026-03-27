package com.graduation.inventory.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.base.entity.BaseProductSku;
import com.graduation.inventory.base.mapper.BaseProductSkuMapper;
import com.graduation.inventory.business.entity.BusStockCheck;
import com.graduation.inventory.business.entity.BusStockCheckItem;
import com.graduation.inventory.business.mapper.BusStockCheckItemMapper;
import com.graduation.inventory.business.mapper.BusStockCheckMapper;
import com.graduation.inventory.business.service.StockCheckService;
import com.graduation.inventory.business.service.StockService;
import com.graduation.inventory.common.domain.PageResult;
import com.graduation.inventory.common.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 盘点服务实现
 *
 * @author graduation
 * @version 1.0.0
 */
@Service
public class StockCheckServiceImpl implements StockCheckService {

    private static final Logger log = LoggerFactory.getLogger(StockCheckServiceImpl.class);

    @Autowired
    private BusStockCheckMapper stockCheckMapper;

    @Autowired
    private BusStockCheckItemMapper stockCheckItemMapper;

    @Autowired
    private BaseProductSkuMapper skuMapper;

    @Autowired
    private StockService stockService;

    /**
     * 分页查询盘点列表
     */
    @Override
    public PageResult<Map<String, Object>> list(Page<Map<String, Object>> page, String checkNo, Long warehouseId, Integer checkStatus) {
        List<Map<String, Object>> list = stockCheckMapper.selectStockCheckList(
                (page.getCurrent() - 1) * page.getSize(),
                page.getSize(),
                checkNo,
                warehouseId,
                checkStatus
        );
        
        Long total = stockCheckMapper.selectStockCheckListCount(checkNo, warehouseId, checkStatus);
        
        return new PageResult<>(list, total);
    }

    /**
     * 查询盘点详情
     */
    @Override
    public Map<String, Object> getInfo(Long id) {
        if (id == null) {
            throw new ServiceException("盘点ID不能为空");
        }
        
        BusStockCheck stockCheck = stockCheckMapper.selectById(id);
        if (stockCheck == null) {
            throw new ServiceException("盘点记录不存在");
        }
        
        // 查询盘点明细
        LambdaQueryWrapper<BusStockCheckItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BusStockCheckItem::getCheckId, id);
        List<BusStockCheckItem> items = stockCheckItemMapper.selectList(queryWrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("stockCheck", stockCheck);
        result.put("items", items);
        
        return result;
    }

    /**
     * 新增盘点单（查询系统库存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(BusStockCheck stockCheck, List<Long> skuIds) {
        if (stockCheck == null) {
            throw new ServiceException("盘点信息不能为空");
        }
        if (stockCheck.getWarehouseId() == null) {
            throw new ServiceException("仓库ID不能为空");
        }
        if (skuIds == null || skuIds.isEmpty()) {
            throw new ServiceException("盘点商品不能为空");
        }
        
        // 生成盘点单号
        String checkNo = generateCheckNo();
        stockCheck.setCheckNo(checkNo);
        stockCheck.setCheckStatus(0); // 盘点中
        stockCheck.setCreateTime(new Date());
        
        // 插入盘点主表
        stockCheckMapper.insert(stockCheck);
        
        // 插入盘点明细
        for (Long skuId : skuIds) {
            BaseProductSku sku = skuMapper.selectById(skuId);
            if (sku == null) {
                throw new ServiceException("SKU不存在: " + skuId);
            }
            
            // 查询系统库存
            StockMain stockMain = stockService.getStockByWarehouseAndSku(stockCheck.getWarehouseId(), skuId);
            BigDecimal systemQty = BigDecimal.ZERO;
            if (stockMain != null && stockMain.getQuantity() != null) {
                systemQty = stockMain.getQuantity();
            }
            
            BusStockCheckItem checkItem = new BusStockCheckItem();
            checkItem.setCheckId(stockCheck.getId());
            checkItem.setSkuId(skuId);
            checkItem.setSystemQty(systemQty);
            checkItem.setActualQty(null); // 实盘数量待提交
            checkItem.setDiffQty(null); // 差异数量待计算
            checkItem.setCreateTime(new Date());
            
            stockCheckItemMapper.insert(checkItem);
        }
        
        log.info("新增盘点单成功，盘点单号: {}", checkNo);
        return true;
    }

    /**
     * 提交实盘数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitItem(Long checkId, Long skuId, BigDecimal actualQty, String remark) {
        if (checkId == null || skuId == null || actualQty == null) {
            throw new ServiceException("参数不完整");
        }
        
        // 查询盘点单
        BusStockCheck stockCheck = stockCheckMapper.selectById(checkId);
        if (stockCheck == null) {
            throw new ServiceException("盘点记录不存在");
        }
        if (stockCheck.getCheckStatus() != 0) {
            throw new ServiceException("盘点已结束，无法提交");
        }
        
        // 查询盘点明细
        LambdaQueryWrapper<BusStockCheckItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BusStockCheckItem::getCheckId, checkId);
        queryWrapper.eq(BusStockCheckItem::getSkuId, skuId);
        BusStockCheckItem checkItem = stockCheckItemMapper.selectOne(queryWrapper);
        
        if (checkItem == null) {
            throw new ServiceException("盘点明细不存在");
        }
        
        // 计算差异
        BigDecimal diffQty = actualQty.subtract(checkItem.getSystemQty());
        
        // 更新盘点明细
        checkItem.setActualQty(actualQty);
        checkItem.setDiffQty(diffQty);
        checkItem.setRemark(remark);
        checkItem.setUpdateTime(new Date());
        stockCheckItemMapper.updateById(checkItem);
        
        log.info("提交盘点明细成功，盘点单号: {}, SKU ID: {}, 实盘数量: {}", stockCheck.getCheckNo(), skuId, actualQty);
        return true;
    }

    /**
     * 确认盘点（差异处理：盘盈入库、盘亏出库）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirm(Long id) {
        if (id == null) {
            throw new ServiceException("盘点ID不能为空");
        }
        
        BusStockCheck stockCheck = stockCheckMapper.selectById(id);
        if (stockCheck == null) {
            throw new ServiceException("盘点记录不存在");
        }
        if (stockCheck.getCheckStatus() != 0) {
            throw new ServiceException("盘点已结束");
        }
        
        // 查询所有盘点明细
        LambdaQueryWrapper<BusStockCheckItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BusStockCheckItem::getCheckId, id);
        List<BusStockCheckItem> items = stockCheckItemMapper.selectList(queryWrapper);
        
        // 检查是否所有明细都已提交实盘数量
        for (BusStockCheckItem item : items) {
            if (item.getActualQty() == null) {
                throw new ServiceException("存在未提交实盘数量的商品，请先完成盘点");
            }
        }
        
        // 处理差异
        for (BusStockCheckItem item : items) {
            BigDecimal diffQty = item.getDiffQty();
            if (diffQty == null || diffQty.compareTo(BigDecimal.ZERO) == 0) {
                continue; // 无差异，跳过
            }
            
            if (diffQty.compareTo(BigDecimal.ZERO) > 0) {
                // 盘盈：入库
                stockService.inStock(
                        stockCheck.getWarehouseId(),
                        item.getSkuId(),
                        diffQty,
                        null,
                        stockCheck.getCheckNo(),
                        5 // 订单类型：盘盈入库
                );
                log.info("盘盈入库，SKU ID: {}, 数量: {}", item.getSkuId(), diffQty);
            } else {
                // 盘亏：出库
                stockService.outStock(
                        stockCheck.getWarehouseId(),
                        item.getSkuId(),
                        diffQty.negate(), // 转为正数
                        stockCheck.getCheckNo(),
                        6 // 订单类型：盘亏出库
                );
                log.info("盘亏出库，SKU ID: {}, 数量: {}", item.getSkuId(), diffQty.negate());
            }
        }
        
        // 更新盘点状态
        stockCheck.setCheckStatus(1); // 完成
        stockCheck.setCheckTime(new Date());
        stockCheck.setUpdateTime(new Date());
        stockCheckMapper.updateById(stockCheck);
        
        log.info("确认盘点成功，盘点单号: {}", stockCheck.getCheckNo());
        return true;
    }

    /**
     * 生成盘点单号（格式：PDyyyyMMddHHmmss）
     */
    private String generateCheckNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "PD" + sdf.format(new Date());
    }
}
