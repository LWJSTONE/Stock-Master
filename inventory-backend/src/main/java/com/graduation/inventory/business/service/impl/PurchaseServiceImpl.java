package com.graduation.inventory.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.base.entity.BaseProductSku;
import com.graduation.inventory.base.mapper.BaseProductSkuMapper;
import com.graduation.inventory.business.entity.BusPurchaseItem;
import com.graduation.inventory.business.entity.BusPurchaseOrder;
import com.graduation.inventory.business.mapper.BusPurchaseItemMapper;
import com.graduation.inventory.business.mapper.BusPurchaseOrderMapper;
import com.graduation.inventory.business.service.PurchaseService;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 采购服务实现
 *
 * @author graduation
 * @version 1.0.0
 */
@Service
public class PurchaseServiceImpl implements PurchaseService {

    private static final Logger log = LoggerFactory.getLogger(PurchaseServiceImpl.class);

    @Autowired
    private BusPurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private BusPurchaseItemMapper purchaseItemMapper;

    @Autowired
    private BaseProductSkuMapper skuMapper;

    @Autowired
    private StockService stockService;

    /**
     * 分页查询采购订单列表
     */
    @Override
    public PageResult<Map<String, Object>> list(Page<Map<String, Object>> page, String purchaseNo, Long supplierId, Integer status) {
        List<Map<String, Object>> list = purchaseOrderMapper.selectPurchaseOrderList(
                (page.getCurrent() - 1) * page.getSize(),
                page.getSize(),
                purchaseNo,
                supplierId,
                status
        );
        
        Long total = purchaseOrderMapper.selectPurchaseOrderListCount(purchaseNo, supplierId, status);
        
        return new PageResult<>(list, total);
    }

    /**
     * 查询采购订单详情（含明细）
     */
    @Override
    public Map<String, Object> getInfo(Long id) {
        if (id == null) {
            throw new ServiceException("采购订单ID不能为空");
        }
        
        BusPurchaseOrder order = purchaseOrderMapper.selectById(id);
        if (order == null) {
            throw new ServiceException("采购订单不存在");
        }
        
        // 查询采购明细
        LambdaQueryWrapper<BusPurchaseItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BusPurchaseItem::getPurchaseId, id);
        List<BusPurchaseItem> items = purchaseItemMapper.selectList(queryWrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", items);
        
        return result;
    }

    /**
     * 新增采购订单（状态0待审核）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(BusPurchaseOrder order, List<Map<String, Object>> items) {
        if (order == null) {
            throw new ServiceException("采购订单不能为空");
        }
        if (items == null || items.isEmpty()) {
            throw new ServiceException("采购明细不能为空");
        }
        
        // 生成采购单号
        String purchaseNo = generatePurchaseNo();
        order.setPurchaseNo(purchaseNo);
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
        purchaseOrderMapper.insert(order);
        
        // 插入明细
        for (Map<String, Object> item : items) {
            Long skuId = Long.parseLong(item.get("skuId").toString());
            BaseProductSku sku = skuMapper.selectById(skuId);
            if (sku == null) {
                throw new ServiceException("SKU不存在: " + skuId);
            }
            
            BusPurchaseItem purchaseItem = new BusPurchaseItem();
            purchaseItem.setPurchaseId(order.getId());
            purchaseItem.setSkuId(skuId);
            purchaseItem.setSkuCode(sku.getSkuCode());
            purchaseItem.setSkuName(sku.getSkuName());
            purchaseItem.setPrice(new BigDecimal(item.get("price").toString()));
            purchaseItem.setQuantity(new BigDecimal(item.get("quantity").toString()));
            purchaseItem.setTotalPrice(purchaseItem.getPrice().multiply(purchaseItem.getQuantity()));
            purchaseItem.setCreateTime(new Date());
            
            purchaseItemMapper.insert(purchaseItem);
        }
        
        log.info("新增采购订单成功，采购单号: {}", purchaseNo);
        return true;
    }

    /**
     * 审核采购订单（状态1已审核）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean audit(Long id, Long auditBy) {
        if (id == null) {
            throw new ServiceException("采购订单ID不能为空");
        }
        
        BusPurchaseOrder order = purchaseOrderMapper.selectById(id);
        if (order == null) {
            throw new ServiceException("采购订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new ServiceException("只有待审核状态的订单才能审核");
        }
        
        order.setStatus(1); // 已审核
        order.setAuditTime(new Date());
        order.setAuditBy(auditBy);
        order.setUpdateTime(new Date());
        
        purchaseOrderMapper.updateById(order);
        
        log.info("审核采购订单成功，采购单号: {}", order.getPurchaseNo());
        return true;
    }

    /**
     * 执行入库（状态2已入库，调用StockService.inStock）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doInStock(Long id, Long warehouseId) {
        if (id == null || warehouseId == null) {
            throw new ServiceException("采购订单ID和仓库ID不能为空");
        }
        
        BusPurchaseOrder order = purchaseOrderMapper.selectById(id);
        if (order == null) {
            throw new ServiceException("采购订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new ServiceException("只有已审核状态的订单才能入库");
        }
        
        // 查询采购明细
        LambdaQueryWrapper<BusPurchaseItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BusPurchaseItem::getPurchaseId, id);
        List<BusPurchaseItem> items = purchaseItemMapper.selectList(queryWrapper);
        
        // 遍历明细执行入库
        for (BusPurchaseItem item : items) {
            stockService.inStock(
                    warehouseId,
                    item.getSkuId(),
                    item.getQuantity(),
                    null, // 批次号
                    order.getPurchaseNo(),
                    1 // 订单类型：采购入库
            );
        }
        
        // 更新订单状态
        order.setStatus(2); // 已入库
        order.setUpdateTime(new Date());
        purchaseOrderMapper.updateById(order);
        
        log.info("采购订单入库成功，采购单号: {}, 仓库ID: {}", order.getPurchaseNo(), warehouseId);
        return true;
    }

    /**
     * 取消订单（状态3已取消）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancel(Long id) {
        if (id == null) {
            throw new ServiceException("采购订单ID不能为空");
        }
        
        BusPurchaseOrder order = purchaseOrderMapper.selectById(id);
        if (order == null) {
            throw new ServiceException("采购订单不存在");
        }
        if (order.getStatus() == 2) {
            throw new ServiceException("已入库的订单不能取消");
        }
        if (order.getStatus() == 3) {
            throw new ServiceException("订单已取消");
        }
        
        order.setStatus(3); // 已取消
        order.setUpdateTime(new Date());
        purchaseOrderMapper.updateById(order);
        
        log.info("取消采购订单成功，采购单号: {}", order.getPurchaseNo());
        return true;
    }

    /**
     * 删除订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        if (id == null) {
            throw new ServiceException("采购订单ID不能为空");
        }
        
        BusPurchaseOrder order = purchaseOrderMapper.selectById(id);
        if (order == null) {
            throw new ServiceException("采购订单不存在");
        }
        if (order.getStatus() != 0 && order.getStatus() != 3) {
            throw new ServiceException("只有待审核或已取消的订单才能删除");
        }
        
        // 删除明细
        LambdaQueryWrapper<BusPurchaseItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BusPurchaseItem::getPurchaseId, id);
        purchaseItemMapper.delete(queryWrapper);
        
        // 删除主表
        purchaseOrderMapper.deleteById(id);
        
        log.info("删除采购订单成功，采购单号: {}", order.getPurchaseNo());
        return true;
    }

    /**
     * 生成采购单号（格式：CGyyyyMMddHHmmss）
     */
    private String generatePurchaseNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "CG" + sdf.format(new Date());
    }
}
