package com.graduation.inventory.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.inventory.business.entity.BusStockCheck;
import com.graduation.inventory.business.entity.BusStockCheckItem;
import com.graduation.inventory.business.entity.StockMain;
import com.graduation.inventory.business.entity.dto.StockCheckDto;
import com.graduation.inventory.business.mapper.BusStockCheckItemMapper;
import com.graduation.inventory.business.mapper.BusStockCheckMapper;
import com.graduation.inventory.business.mapper.StockMainMapper;
import com.graduation.inventory.business.service.BusStockCheckService;
import com.graduation.inventory.common.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 库存盘点服务实现类
 *
 * @author graduation
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class BusStockCheckServiceImpl extends ServiceImpl<BusStockCheckMapper, BusStockCheck> implements BusStockCheckService {

    private final BusStockCheckItemMapper checkItemMapper;
    private final StockMainMapper stockMainMapper;

    @Override
    public Page<BusStockCheck> selectCheckPage(Page<BusStockCheck> page, String checkNo, Long warehouseId, Integer checkStatus) {
        LambdaQueryWrapper<BusStockCheck> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(checkNo), BusStockCheck::getCheckNo, checkNo);
        wrapper.eq(warehouseId != null, BusStockCheck::getWarehouseId, warehouseId);
        wrapper.eq(checkStatus != null, BusStockCheck::getCheckStatus, checkStatus);
        wrapper.orderByDesc(BusStockCheck::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public BusStockCheck selectCheckById(Long checkId) {
        BusStockCheck check = baseMapper.selectById(checkId);
        if (check != null) {
            // 查询明细
            LambdaQueryWrapper<BusStockCheckItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BusStockCheckItem::getCheckId, checkId);
            List<BusStockCheckItem> items = checkItemMapper.selectList(wrapper);
        }
        return check;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertCheck(StockCheckDto dto) {
        // 生成盘点单号
        String checkNo = "CK" + System.currentTimeMillis();
        
        BusStockCheck check = new BusStockCheck();
        check.setCheckNo(checkNo);
        check.setWarehouseId(dto.getWarehouseId());
        check.setCheckStatus(0); // 盘点中
        check.setCreateTime(new Date());
        check.setUpdateTime(new Date());
        
        // 保存主表
        baseMapper.insert(check);
        
        // 生成盘点明细
        if (dto.getSkuIds() != null && !dto.getSkuIds().isEmpty()) {
            for (Long skuId : dto.getSkuIds()) {
                // 查询当前库存
                LambdaQueryWrapper<StockMain> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(StockMain::getWarehouseId, dto.getWarehouseId());
                wrapper.eq(StockMain::getSkuId, skuId);
                StockMain stockMain = stockMainMapper.selectOne(wrapper);
                
                BusStockCheckItem item = new BusStockCheckItem();
                item.setCheckId(check.getId());
                item.setSkuId(skuId);
                item.setSystemQty(stockMain != null ? stockMain.getQuantity() : BigDecimal.ZERO);
                item.setActualQty(BigDecimal.ZERO);
                item.setDiffQty(BigDecimal.ZERO);
                item.setCreateTime(new Date());
                item.setUpdateTime(new Date());
                checkItemMapper.insert(item);
            }
        }
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitActualQty(StockCheckDto dto) {
        BusStockCheckItem item = checkItemMapper.selectById(dto.getItemId());
        if (item == null) {
            throw new ServiceException("盘点明细不存在");
        }
        
        BusStockCheck check = baseMapper.selectById(item.getCheckId());
        if (check.getCheckStatus() != 0) {
            throw new ServiceException("盘点已完成，不能再提交");
        }
        
        item.setActualQty(dto.getActualQty());
        item.setDiffQty(dto.getActualQty().subtract(item.getSystemQty()));
        item.setRemark(dto.getRemark());
        item.setUpdateTime(new Date());
        checkItemMapper.updateById(item);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmCheck(Long checkId) {
        BusStockCheck check = baseMapper.selectById(checkId);
        if (check == null) {
            throw new ServiceException("盘点单不存在");
        }
        if (check.getCheckStatus() != 0) {
            throw new ServiceException("盘点已完成");
        }
        
        // TODO: 根据盘点结果调整库存
        
        check.setCheckStatus(1); // 完成
        check.setCheckTime(new Date());
        check.setUpdateTime(new Date());
        baseMapper.updateById(check);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCheckByIds(Long[] checkIds) {
        for (Long checkId : checkIds) {
            BusStockCheck check = baseMapper.selectById(checkId);
            if (check != null && check.getCheckStatus() != 0) {
                throw new ServiceException("只能删除盘点中的盘点单");
            }
            // 删除明细
            LambdaQueryWrapper<BusStockCheckItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BusStockCheckItem::getCheckId, checkId);
            checkItemMapper.delete(wrapper);
            // 删除主表
            baseMapper.deleteById(checkId);
        }
        return true;
    }
}
