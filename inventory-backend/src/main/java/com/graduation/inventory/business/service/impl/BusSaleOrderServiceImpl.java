package com.graduation.inventory.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.inventory.business.entity.BusSaleItem;
import com.graduation.inventory.business.entity.BusSaleOrder;
import com.graduation.inventory.business.entity.dto.AuditDto;
import com.graduation.inventory.business.entity.dto.SaleItemDto;
import com.graduation.inventory.business.entity.dto.SaleOrderDto;
import com.graduation.inventory.business.mapper.BusSaleItemMapper;
import com.graduation.inventory.business.mapper.BusSaleOrderMapper;
import com.graduation.inventory.business.service.BusSaleOrderService;
import com.graduation.inventory.common.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 销售订单服务实现类
 *
 * @author graduation
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class BusSaleOrderServiceImpl extends ServiceImpl<BusSaleOrderMapper, BusSaleOrder> implements BusSaleOrderService {

    private final BusSaleItemMapper saleItemMapper;

    @Override
    public Page<BusSaleOrder> selectSalePage(Page<BusSaleOrder> page, String saleNo, Long customerId, Integer status) {
        LambdaQueryWrapper<BusSaleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(saleNo), BusSaleOrder::getSaleNo, saleNo);
        wrapper.eq(customerId != null, BusSaleOrder::getCustomerId, customerId);
        wrapper.eq(status != null, BusSaleOrder::getStatus, status);
        wrapper.orderByDesc(BusSaleOrder::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public BusSaleOrder selectSaleById(Long saleId) {
        BusSaleOrder order = baseMapper.selectById(saleId);
        if (order != null) {
            // 查询明细
            LambdaQueryWrapper<BusSaleItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BusSaleItem::getSaleId, saleId);
            List<BusSaleItem> items = saleItemMapper.selectList(wrapper);
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertSale(SaleOrderDto dto) {
        // 生成销售单号
        String saleNo = "SO" + System.currentTimeMillis();
        
        BusSaleOrder order = new BusSaleOrder();
        order.setSaleNo(saleNo);
        order.setCustomerId(dto.getCustomerId());
        order.setStatus(0); // 待审核
        order.setPayStatus(0); // 未支付
        order.setRemark(dto.getRemark());
        
        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (SaleItemDto item : dto.getItems()) {
            totalAmount = totalAmount.add(item.getPrice().multiply(item.getQuantity()));
        }
        order.setTotalAmount(totalAmount);
        
        // 保存主表
        baseMapper.insert(order);
        
        // 保存明细
        for (SaleItemDto itemDto : dto.getItems()) {
            BusSaleItem item = new BusSaleItem();
            BeanUtils.copyProperties(itemDto, item);
            item.setSaleId(order.getId());
            item.setTotalPrice(itemDto.getPrice().multiply(itemDto.getQuantity()));
            item.setCreateTime(new Date());
            item.setUpdateTime(new Date());
            saleItemMapper.insert(item);
        }
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditSale(AuditDto dto) {
        BusSaleOrder order = baseMapper.selectById(dto.getId());
        if (order == null) {
            throw new ServiceException("销售订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new ServiceException("只有待审核状态的订单才能审核");
        }
        
        order.setStatus(dto.getStatus() == 1 ? 1 : 3); // 1通过，3驳回取消
        baseMapper.updateById(order);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean outstock(Long saleId) {
        BusSaleOrder order = baseMapper.selectById(saleId);
        if (order == null) {
            throw new ServiceException("销售订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new ServiceException("只有已审核状态的订单才能出库");
        }
        
        // TODO: 执行出库逻辑，更新库存
        order.setStatus(2); // 已出库
        order.setDeliverTime(new Date());
        baseMapper.updateById(order);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelSale(Long saleId) {
        BusSaleOrder order = baseMapper.selectById(saleId);
        if (order == null) {
            throw new ServiceException("销售订单不存在");
        }
        if (order.getStatus() == 2) {
            throw new ServiceException("已出库的订单不能取消");
        }
        
        order.setStatus(3); // 已取消
        baseMapper.updateById(order);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSaleByIds(Long[] saleIds) {
        for (Long saleId : saleIds) {
            BusSaleOrder order = baseMapper.selectById(saleId);
            if (order != null && order.getStatus() != 3) {
                throw new ServiceException("只能删除已取消的订单");
            }
            // 删除明细
            LambdaQueryWrapper<BusSaleItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BusSaleItem::getSaleId, saleId);
            saleItemMapper.delete(wrapper);
            // 删除主表
            baseMapper.deleteById(saleId);
        }
        return true;
    }
}
