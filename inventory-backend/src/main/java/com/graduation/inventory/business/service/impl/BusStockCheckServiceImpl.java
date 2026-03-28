package com.graduation.inventory.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.inventory.base.entity.BaseProductSku;
import com.graduation.inventory.base.entity.BaseWarehouse;
import com.graduation.inventory.base.mapper.BaseProductSkuMapper;
import com.graduation.inventory.base.mapper.BaseWarehouseMapper;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 库存盘点服务实现类
 *
 * @author graduation
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BusStockCheckServiceImpl extends ServiceImpl<BusStockCheckMapper, BusStockCheck> implements BusStockCheckService {

    private final BusStockCheckItemMapper checkItemMapper;
    private final StockMainMapper stockMainMapper;
    private final BaseWarehouseMapper warehouseMapper;
    private final BaseProductSkuMapper skuMapper;

    @Override
    public Page<BusStockCheck> selectCheckPage(Page<BusStockCheck> page, String checkNo, Long warehouseId, Integer checkStatus) {
        LambdaQueryWrapper<BusStockCheck> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(checkNo), BusStockCheck::getCheckNo, checkNo);
        wrapper.eq(warehouseId != null, BusStockCheck::getWarehouseId, warehouseId);
        wrapper.eq(checkStatus != null, BusStockCheck::getCheckStatus, checkStatus);
        wrapper.orderByDesc(BusStockCheck::getCreateTime);
        Page<BusStockCheck> result = baseMapper.selectPage(page, wrapper);
        
        // 填充仓库名称和统计信息
        for (BusStockCheck check : result.getRecords()) {
            fillCheckInfo(check);
        }
        
        return result;
    }
    
    /**
     * 填充盘点单信息（仓库名称、商品数量、差异数量）
     */
    private void fillCheckInfo(BusStockCheck check) {
        // 填充仓库名称
        if (check.getWarehouseId() != null) {
            BaseWarehouse warehouse = warehouseMapper.selectById(check.getWarehouseId());
            if (warehouse != null) {
                check.setWarehouseName(warehouse.getWhName());
            }
        }
        
        // 查询盘点明细统计
        if (check.getId() != null) {
            LambdaQueryWrapper<BusStockCheckItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(BusStockCheckItem::getCheckId, check.getId());
            List<BusStockCheckItem> items = checkItemMapper.selectList(itemWrapper);
            
            // 填充SKU信息
            for (BusStockCheckItem item : items) {
                fillItemSkuInfo(item);
            }
            
            check.setProductCount(items.size());
            check.setItems(items);
            
            // 计算差异数量和金额
            int diffCount = 0;
            BigDecimal diffAmount = BigDecimal.ZERO;
            for (BusStockCheckItem item : items) {
                if (item.getDiffQty() != null) {
                    diffCount += item.getDiffQty().intValue();
                }
            }
            check.setDifferenceCount(diffCount);
            check.setDifferenceAmount(diffAmount);
        }
    }
    
    /**
     * 填充盘点明细的SKU信息
     */
    private void fillItemSkuInfo(BusStockCheckItem item) {
        if (item.getSkuId() != null) {
            BaseProductSku sku = skuMapper.selectById(item.getSkuId());
            if (sku != null) {
                item.setSkuCode(sku.getSkuCode());
                item.setSkuName(sku.getSkuName());
                item.setSpecValues(sku.getSpecInfo());
                item.setPrice(sku.getSalePrice());
            }
        }
    }

    @Override
    public BusStockCheck selectCheckById(Long checkId) {
        BusStockCheck check = baseMapper.selectById(checkId);
        if (check != null) {
            // 查询明细并设置到check对象
            LambdaQueryWrapper<BusStockCheckItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BusStockCheckItem::getCheckId, checkId);
            List<BusStockCheckItem> items = checkItemMapper.selectList(wrapper);
            
            // 填充SKU信息
            for (BusStockCheckItem item : items) {
                fillItemSkuInfo(item);
            }
            
            check.setItems(items != null ? items : new ArrayList<>());
            
            // 填充仓库名称
            fillCheckInfo(check);
        }
        return check;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertCheck(StockCheckDto dto) {
        try {
            log.info("开始创建盘点单，warehouseId={}, skuIds={}", dto.getWarehouseId(), dto.getSkuIds());
            
            // 生成盘点单号
            String checkNo = "CK" + System.currentTimeMillis();
            
            BusStockCheck check = new BusStockCheck();
            check.setCheckNo(checkNo);
            check.setWarehouseId(dto.getWarehouseId());
            check.setCheckStatus(0); // 盘点中
            check.setCreateTime(new Date());
            check.setUpdateTime(new Date());
            
            // 保存主表
            int result = baseMapper.insert(check);
            if (result <= 0) {
                log.error("保存盘点主表失败");
                throw new ServiceException("创建盘点单失败");
            }
            log.info("盘点主表创建成功，checkId={}, checkNo={}", check.getId(), checkNo);
            
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
                log.info("创建盘点明细{}条", dto.getSkuIds().size());
            }
            
            return true;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建盘点单失败: {}", e.getMessage(), e);
            throw new ServiceException("创建盘点单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitActualQty(StockCheckDto dto) {
        // 批量更新盘点明细
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (BusStockCheckItem item : dto.getItems()) {
                BusStockCheckItem existItem = checkItemMapper.selectById(item.getId());
                if (existItem == null) {
                    continue;
                }
                
                BusStockCheck check = baseMapper.selectById(existItem.getCheckId());
                if (check.getCheckStatus() != 0) {
                    throw new ServiceException("盘点已完成，不能再提交");
                }
                
                existItem.setActualQty(item.getActualQty());
                existItem.setDiffQty(item.getActualQty().subtract(existItem.getSystemQty()));
                existItem.setUpdateTime(new Date());
                checkItemMapper.updateById(existItem);
            }
            return true;
        }
        
        // 单个更新
        if (dto.getItemId() != null) {
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
        }
        
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
        
        // 检查是否已录入实盘数量
        LambdaQueryWrapper<BusStockCheckItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(BusStockCheckItem::getCheckId, checkId);
        List<BusStockCheckItem> items = checkItemMapper.selectList(itemWrapper);
        for (BusStockCheckItem item : items) {
            if (item.getActualQty() == null) {
                throw new ServiceException("请先录入所有商品的实盘数量");
            }
        }
        
        // 根据盘点结果调整库存
        for (BusStockCheckItem item : items) {
            if (item.getDiffQty() != null && item.getDiffQty().compareTo(BigDecimal.ZERO) != 0) {
                // 查询当前库存
                LambdaQueryWrapper<StockMain> stockWrapper = new LambdaQueryWrapper<>();
                stockWrapper.eq(StockMain::getWarehouseId, check.getWarehouseId());
                stockWrapper.eq(StockMain::getSkuId, item.getSkuId());
                StockMain stockMain = stockMainMapper.selectOne(stockWrapper);
                
                if (stockMain != null) {
                    // 调整库存
                    stockMain.setQuantity(item.getActualQty());
                    stockMain.setUpdateTime(new Date());
                    stockMainMapper.updateById(stockMain);
                }
            }
        }
        
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
