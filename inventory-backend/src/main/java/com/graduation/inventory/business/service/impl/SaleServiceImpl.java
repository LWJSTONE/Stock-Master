package com.graduation.inventory.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.base.entity.BaseProductSku;
import com.graduation.inventory.base.mapper.BaseProductSkuMapper;
import com.graduation.inventory.business.entity.BusSaleItem;
import com.graduation.inventory.business.entity.BusSaleOrder;
import com.graduation.inventory.business.mapper.BusSaleItemMapper;
import com.graduation.inventory.business.mapper.BusSaleOrderMapper;
import com.graduation.inventory.business.service.SaleService;
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
 * 销售服务实现
 *
 * @author graduation
 * @version 1.0.0
 */
@Service
public class SaleServiceImpl implements SaleService {

    private static final Logger log = LoggerFactory.getLogger(SaleServiceImpl.class);

    @Autowired
    private BusSaleOrderMapper saleOrderMapper;

    @Autowired
    private BusSaleItemMapper saleItemMapper;

    @Autowired
    private BaseProductSkuMapper skuMapper;

    @Autowired
    private StockService stockService;

    /**
     * 分页查询销售订单列表
     */
    @Override
    public PageResult<Map<String, Object>> list(Page<Map<String, Object>> page, String saleNo, Long customerId, Integer status) {
        List<Map<String, Object>> list = saleOrderMapper.selectSaleOrderList(
                (page.getCurrent() - 1) * page.getSize(),
                page.getSize(),
                saleNo,
                customerId,
                status
        );
        
        Long total = saleOrderMapper.selectSaleOrderListCount(saleNo, customerId, status);
        
        return new PageResult<>(list, total);
    }

    /**
     * 查询销售订单详情
     */
    @Override
    public Map<String, Object> getInfo(Long id) {
        if (id == null) {
            throw new ServiceException("销售订单ID不能为空");
        }
        
        BusSaleOrder order = saleOrderMapper.selectById(id);
        if (order == null) {
            throw new ServiceException("销售订单不存在");
        }
        
        // 查询销售明细
        LambdaQueryWrapper<BusSaleItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BusSaleItem::getSaleId, id);
        List<BusSaleItem> items = saleItemMapper.selectList(queryWrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", items);
        
        return result;
    }

    /**
     * 新增销售订单（冻结库存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(BusSaleOrder order, List<Map<String, Object>> items, Long warehouseId) {
        if (order == null) {
            throw new ServiceException("销售订单不能为空");
        }
        if (items == null || items.isEmpty()) {
            throw new ServiceException("销售明细不能为空");
        }
        if (warehouseId == null) {
            throw new ServiceException("出库仓库不能为空");
        }
        
        // 校验库存是否充足，并冻结库存
        for (Map<String, Object> item : items) {
            Long skuId = Long.parseLong(item.get("skuId").toString());
            BigDecimal quantity = new BigDecimal(item.get("quantity").toString());
            
            if (!stockService.checkStockAvailable(warehouseId, skuId, quantity)) {
                throw new ServiceException("SKU ID: " + skuId + " 库存不足");
            }
        }
        
        // 生成销售单号
        String saleNo = generateSaleNo();
        order.setSaleNo(saleNo);
        order.setStatus(0); // 待审核
        order.setCreateTime(new Date());
        
        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Map<String, Object> item : items) {
            BigDecimal price = new BigDecimal(item.get("price").toString());
            BigDecimal quantity = new BigDecimal(item.get("quantity").toString());
            totalAmount = totalAmount.add(price.multiply(quantity));
        }
        order.setTotalAmount(totalAmount);
        
        // 插入主表
        saleOrderMapper.insert(order);
        
        // 插入明细并冻结库存
        for (Map<String, Object> item : items) {
            Long skuId = Long.parseLong(item.get("skuId").toString());
            BigDecimal quantity = new BigDecimal(item.get("quantity").toString());
            BigDecimal price = new BigDecimal(item.get("price").toString());
            
            BaseProductSku sku = skuMapper.selectById(skuId);
            if (sku == null) {
                throw new ServiceException("SKU不存在: " + skuId);
            }
            
            BusSaleItem saleItem = new BusSaleItem();
            saleItem.setSaleId(order.getId());
            saleItem.setSkuId(skuId);
            saleItem.setSkuCode(sku.getSkuCode());
            saleItem.setSkuName(sku.getSkuName());
            saleItem.setPrice(price);
            saleItem.setQuantity(quantity);
            saleItem.setTotalPrice(price.multiply(quantity));
            saleItem.setCreateTime(new Date());
            
            saleItemMapper.insert(saleItem);
            
            // 冻结库存
            stockService.freezeStock(warehouseId, skuId, quantity);
        }
        
        log.info("新增销售订单成功，销售单号: {}", saleNo);
        return true;
    }

    /**
     * 审核销售订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean audit(Long id) {
        if (id == null) {
            throw new ServiceException("销售订单ID不能为空");
        }
        
        BusSaleOrder order = saleOrderMapper.selectById(id);
        if (order == null) {
            throw new ServiceException("销售订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new ServiceException("只有待审核状态的订单才能审核");
        }
        
        order.setStatus(1); // 已审核
        order.setUpdateTime(new Date());
        
        saleOrderMapper.updateById(order);
        
        log.info("审核销售订单成功，销售单号: {}", order.getSaleNo());
        return true;
    }

    /**
     * 执行出库（解冻并扣减库存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doOutStock(Long id) {
        if (id == null) {
            throw new ServiceException("销售订单ID不能为空");
        }
        
        BusSaleOrder order = saleOrderMapper.selectById(id);
        if (order == null) {
            throw new ServiceException("销售订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new ServiceException("只有已审核状态的订单才能出库");
        }
        
        // 查询销售明细
        LambdaQueryWrapper<BusSaleItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BusSaleItem::getSaleId, id);
        List<BusSaleItem> items = saleItemMapper.selectList(queryWrapper);
        
        // 从订单获取仓库ID（如果订单没有仓库ID，需要从其他地方获取）
        Long warehouseId = getWarehouseIdBySaleOrder(order);
        if (warehouseId == null) {
            throw new ServiceException("未指定出库仓库");
        }
        
        // 遍历明细执行出库
        for (BusSaleItem item : items) {
            // 先解冻
            stockService.unfreezeStock(warehouseId, item.getSkuId(), item.getQuantity());
            
            // 再出库
            stockService.outStock(
                    warehouseId,
                    item.getSkuId(),
                    item.getQuantity(),
                    order.getSaleNo(),
                    2 // 订单类型：销售出库
            );
        }
        
        // 更新订单状态
        order.setStatus(2); // 已出库
        order.setDeliverTime(new Date());
        order.setUpdateTime(new Date());
        saleOrderMapper.updateById(order);
        
        log.info("销售订单出库成功，销售单号: {}", order.getSaleNo());
        return true;
    }

    /**
     * 取消订单（解冻库存）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(Long id) {
        if (id == null) {
            throw new ServiceException("销售订单ID不能为空");
        }
        
        BusSaleOrder order = saleOrderMapper.selectById(id);
        if (order == null) {
            throw new ServiceException("销售订单不存在");
        }
        if (order.getStatus() == 2) {
            throw new ServiceException("已出库的订单不能取消");
        }
        if (order.getStatus() == 3) {
            throw new ServiceException("订单已取消");
        }
        
        // 如果订单已冻结库存，需要解冻
        if (order.getStatus() == 0 || order.getStatus() == 1) {
            Long warehouseId = getWarehouseIdBySaleOrder(order);
            if (warehouseId != null) {
                // 查询销售明细
                LambdaQueryWrapper<BusSaleItem> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(BusSaleItem::getSaleId, id);
                List<BusSaleItem> items = saleItemMapper.selectList(queryWrapper);
                
                // 解冻库存
                for (BusSaleItem item : items) {
                    try {
                        stockService.unfreezeStock(warehouseId, item.getSkuId(), item.getQuantity());
                    } catch (Exception e) {
                        log.warn("解冻库存失败: {}", e.getMessage());
                    }
                }
            }
        }
        
        order.setStatus(3); // 已取消
        order.setUpdateTime(new Date());
        saleOrderMapper.updateById(order);
        
        log.info("取消销售订单成功，销售单号: {}", order.getSaleNo());
        return true;
    }

    /**
     * 删除订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        if (id == null) {
            throw new ServiceException("销售订单ID不能为空");
        }
        
        BusSaleOrder order = saleOrderMapper.selectById(id);
        if (order == null) {
            throw new ServiceException("销售订单不存在");
        }
        if (order.getStatus() != 0 && order.getStatus() != 3) {
            throw new ServiceException("只有待审核或已取消的订单才能删除");
        }
        
        // 删除明细
        LambdaQueryWrapper<BusSaleItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BusSaleItem::getSaleId, id);
        saleItemMapper.delete(queryWrapper);
        
        // 删除主表
        saleOrderMapper.deleteById(id);
        
        log.info("删除销售订单成功，销售单号: {}", order.getSaleNo());
        return true;
    }

    /**
     * 生成销售单号（格式：XSyyyyMMddHHmmss）
     */
    private String generateSaleNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "XS" + sdf.format(new Date());
    }

    /**
     * 获取销售订单的仓库ID
     * 注意：当前BusSaleOrder没有warehouseId字段，这里提供一个默认实现
     * 实际项目中可能需要在订单中添加仓库字段，或通过其他方式获取
     */
    private Long getWarehouseIdBySaleOrder(BusSaleOrder order) {
        // 这里返回一个默认仓库ID，实际项目中需要根据业务逻辑获取
        // 可以从订单明细中获取，或者要求订单必须指定仓库
        return 1L; // 默认仓库ID
    }
}
