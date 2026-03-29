package com.graduation.inventory.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.inventory.business.entity.BusPurchaseItem;
import com.graduation.inventory.business.entity.BusPurchaseOrder;
import com.graduation.inventory.business.entity.dto.AuditDto;
import com.graduation.inventory.business.entity.dto.PurchaseItemDto;
import com.graduation.inventory.business.entity.dto.PurchaseOrderDto;
import com.graduation.inventory.business.mapper.BusPurchaseItemMapper;
import com.graduation.inventory.business.mapper.BusPurchaseOrderMapper;
import com.graduation.inventory.business.service.BusPurchaseOrderService;
import com.graduation.inventory.common.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 采购订单服务实现类
 *
 * @author graduation
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class BusPurchaseOrderServiceImpl extends ServiceImpl<BusPurchaseOrderMapper, BusPurchaseOrder> implements BusPurchaseOrderService {

    private final BusPurchaseItemMapper purchaseItemMapper;

    @Override
    public Page<BusPurchaseOrder> selectPurchasePage(Page<BusPurchaseOrder> page, String purchaseNo, Long supplierId, Integer status) {
        LambdaQueryWrapper<BusPurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(purchaseNo), BusPurchaseOrder::getPurchaseNo, purchaseNo);
        wrapper.eq(supplierId != null, BusPurchaseOrder::getSupplierId, supplierId);
        wrapper.eq(status != null, BusPurchaseOrder::getStatus, status);
        wrapper.orderByDesc(BusPurchaseOrder::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public BusPurchaseOrder selectPurchaseById(Long purchaseId) {
        BusPurchaseOrder order = baseMapper.selectById(purchaseId);
        if (order != null) {
            // 查询明细
            LambdaQueryWrapper<BusPurchaseItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BusPurchaseItem::getPurchaseId, purchaseId);
            List<BusPurchaseItem> items = purchaseItemMapper.selectList(wrapper);
            order.setItems(items);
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertPurchase(PurchaseOrderDto dto) {
        // 生成采购单号
        String purchaseNo = "PO" + System.currentTimeMillis();
        
        BusPurchaseOrder order = new BusPurchaseOrder();
        order.setPurchaseNo(purchaseNo);
        order.setSupplierId(dto.getSupplierId());
        order.setStatus(0); // 待审核
        order.setRemark(dto.getRemark());
        
        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseItemDto item : dto.getItems()) {
            totalAmount = totalAmount.add(item.getPrice().multiply(item.getQuantity()));
        }
        order.setTotalAmount(totalAmount);
        
        // 保存主表
        baseMapper.insert(order);
        
        // 保存明细
        for (PurchaseItemDto itemDto : dto.getItems()) {
            BusPurchaseItem item = new BusPurchaseItem();
            BeanUtils.copyProperties(itemDto, item);
            item.setPurchaseId(order.getId());
            item.setTotalPrice(itemDto.getPrice().multiply(itemDto.getQuantity()));
            item.setCreateTime(new Date());
            item.setUpdateTime(new Date());
            purchaseItemMapper.insert(item);
        }
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePurchase(PurchaseOrderDto dto) {
        BusPurchaseOrder order = baseMapper.selectById(dto.getId());
        if (order == null) {
            throw new ServiceException("采购订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new ServiceException("只有待审核状态的订单才能修改");
        }
        
        // 更新主表
        order.setSupplierId(dto.getSupplierId());
        order.setRemark(dto.getRemark());
        
        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseItemDto item : dto.getItems()) {
            totalAmount = totalAmount.add(item.getPrice().multiply(item.getQuantity()));
        }
        order.setTotalAmount(totalAmount);
        
        baseMapper.updateById(order);
        
        // 删除原明细
        LambdaQueryWrapper<BusPurchaseItem> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(BusPurchaseItem::getPurchaseId, dto.getId());
        purchaseItemMapper.delete(deleteWrapper);
        
        // 重新插入明细
        for (PurchaseItemDto itemDto : dto.getItems()) {
            BusPurchaseItem item = new BusPurchaseItem();
            BeanUtils.copyProperties(itemDto, item);
            item.setPurchaseId(order.getId());
            item.setTotalPrice(itemDto.getPrice().multiply(itemDto.getQuantity()));
            item.setCreateTime(new Date());
            item.setUpdateTime(new Date());
            purchaseItemMapper.insert(item);
        }
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditPurchase(AuditDto dto) {
        BusPurchaseOrder order = baseMapper.selectById(dto.getId());
        if (order == null) {
            throw new ServiceException("采购订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new ServiceException("只有待审核状态的订单才能审核");
        }
        
        order.setStatus(dto.getStatus() == 1 ? 1 : 3); // 1通过，3驳回取消
        order.setAuditTime(new Date());
        baseMapper.updateById(order);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean instock(Long purchaseId, Long warehouseId) {
        BusPurchaseOrder order = baseMapper.selectById(purchaseId);
        if (order == null) {
            throw new ServiceException("采购订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new ServiceException("只有已审核状态的订单才能入库");
        }
        
        // TODO: 执行入库逻辑，更新库存
        order.setStatus(2); // 已入库
        baseMapper.updateById(order);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelPurchase(Long purchaseId) {
        BusPurchaseOrder order = baseMapper.selectById(purchaseId);
        if (order == null) {
            throw new ServiceException("采购订单不存在");
        }
        if (order.getStatus() == 2) {
            throw new ServiceException("已入库的订单不能取消");
        }
        
        order.setStatus(3); // 已取消
        baseMapper.updateById(order);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePurchaseByIds(Long[] purchaseIds) {
        for (Long purchaseId : purchaseIds) {
            BusPurchaseOrder order = baseMapper.selectById(purchaseId);
            if (order != null && order.getStatus() != 3) {
                throw new ServiceException("只能删除已取消的订单");
            }
            // 删除明细
            LambdaQueryWrapper<BusPurchaseItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BusPurchaseItem::getPurchaseId, purchaseId);
            purchaseItemMapper.delete(wrapper);
            // 删除主表
            baseMapper.deleteById(purchaseId);
        }
        return true;
    }
}
