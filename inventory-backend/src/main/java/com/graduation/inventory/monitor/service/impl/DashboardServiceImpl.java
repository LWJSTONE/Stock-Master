package com.graduation.inventory.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduation.inventory.base.entity.BaseCategory;
import com.graduation.inventory.base.entity.BaseProductSku;
import com.graduation.inventory.base.entity.BaseProductSpu;
import com.graduation.inventory.base.mapper.BaseCategoryMapper;
import com.graduation.inventory.base.mapper.BaseProductSkuMapper;
import com.graduation.inventory.base.mapper.BaseProductSpuMapper;
import com.graduation.inventory.business.entity.BusPurchaseOrder;
import com.graduation.inventory.business.entity.BusSaleItem;
import com.graduation.inventory.business.entity.BusSaleOrder;
import com.graduation.inventory.business.entity.StockMain;
import com.graduation.inventory.business.entity.StockRecord;
import com.graduation.inventory.business.mapper.BusPurchaseOrderMapper;
import com.graduation.inventory.business.mapper.BusSaleItemMapper;
import com.graduation.inventory.business.mapper.BusSaleOrderMapper;
import com.graduation.inventory.business.mapper.StockMainMapper;
import com.graduation.inventory.business.mapper.StockRecordMapper;
import com.graduation.inventory.monitor.entity.vo.CategoryRatioVo;
import com.graduation.inventory.monitor.entity.vo.OverviewVo;
import com.graduation.inventory.monitor.entity.vo.StockWarningVo;
import com.graduation.inventory.monitor.entity.vo.TopProductVo;
import com.graduation.inventory.monitor.entity.vo.TrendVo;
import com.graduation.inventory.monitor.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 大屏数据服务实现类
 *
 * @author graduation
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final StockMainMapper stockMainMapper;
    private final StockRecordMapper stockRecordMapper;
    private final BusPurchaseOrderMapper purchaseOrderMapper;
    private final BusSaleOrderMapper saleOrderMapper;
    private final BusSaleItemMapper saleItemMapper;
    private final BaseProductSkuMapper skuMapper;
    private final BaseProductSpuMapper spuMapper;
    private final BaseCategoryMapper categoryMapper;

    @Override
    public OverviewVo getOverview() {
        OverviewVo overview = new OverviewVo();
        
        try {
            // 1. 统计总库存
            LambdaQueryWrapper<StockMain> stockWrapper = new LambdaQueryWrapper<>();
            List<StockMain> stockList = stockMainMapper.selectList(stockWrapper);
            BigDecimal totalStock = BigDecimal.ZERO;
            if (stockList != null && !stockList.isEmpty()) {
                totalStock = stockList.stream()
                        .map(StockMain::getQuantity)
                        .filter(q -> q != null)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            overview.setTotalStock(totalStock);
            log.debug("总库存: {}", totalStock);
            
            // 2. 统计SKU数量（MyBatis-Plus的@TableLogic会自动处理逻辑删除）
            LambdaQueryWrapper<BaseProductSku> skuWrapper = new LambdaQueryWrapper<>();
            Long totalSku = skuMapper.selectCount(skuWrapper);
            overview.setTotalSku(totalSku != null ? totalSku.intValue() : 0);
            log.debug("SKU数量: {}", totalSku);
            
            // 3. 统计待审核采购单数量
            LambdaQueryWrapper<BusPurchaseOrder> purchaseWrapper = new LambdaQueryWrapper<>();
            purchaseWrapper.eq(BusPurchaseOrder::getStatus, 0);
            Long pendingPurchase = purchaseOrderMapper.selectCount(purchaseWrapper);
            overview.setPendingPurchase(pendingPurchase != null ? pendingPurchase.intValue() : 0);
            log.debug("待审核采购单: {}", pendingPurchase);
            
            // 4. 统计待审核销售单数量
            LambdaQueryWrapper<BusSaleOrder> saleWrapper = new LambdaQueryWrapper<>();
            saleWrapper.eq(BusSaleOrder::getStatus, 0);
            Long pendingSale = saleOrderMapper.selectCount(saleWrapper);
            overview.setPendingSale(pendingSale != null ? pendingSale.intValue() : 0);
            log.debug("待审核销售单: {}", pendingSale);
            
            // 5. 设置待审核订单总数
            int totalPending = (pendingPurchase != null ? pendingPurchase.intValue() : 0) 
                             + (pendingSale != null ? pendingSale.intValue() : 0);
            overview.setPendingOrders(totalPending);
            
            // 6. 统计今日入库
            LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            Date startDate = Date.from(todayStart.atZone(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(todayEnd.atZone(ZoneId.systemDefault()).toInstant());
            
            LambdaQueryWrapper<StockRecord> inboundWrapper = new LambdaQueryWrapper<>();
            inboundWrapper.eq(StockRecord::getOrderType, 1) // 采购入库
                    .between(StockRecord::getCreateTime, startDate, endDate);
            List<StockRecord> inboundRecords = stockRecordMapper.selectList(inboundWrapper);
            BigDecimal todayInbound = BigDecimal.ZERO;
            if (inboundRecords != null && !inboundRecords.isEmpty()) {
                todayInbound = inboundRecords.stream()
                        .map(StockRecord::getChangeQty)
                        .filter(q -> q != null)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            overview.setTodayInbound(todayInbound);
            log.debug("今日入库: {}", todayInbound);
            
            // 7. 统计今日出库
            LambdaQueryWrapper<StockRecord> outboundWrapper = new LambdaQueryWrapper<>();
            outboundWrapper.eq(StockRecord::getOrderType, 2) // 销售出库
                    .between(StockRecord::getCreateTime, startDate, endDate);
            List<StockRecord> outboundRecords = stockRecordMapper.selectList(outboundWrapper);
            BigDecimal todayOutbound = BigDecimal.ZERO;
            if (outboundRecords != null && !outboundRecords.isEmpty()) {
                todayOutbound = outboundRecords.stream()
                        .map(StockRecord::getChangeQty)
                        .filter(q -> q != null)
                        .map(BigDecimal::abs)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            overview.setTodayOutbound(todayOutbound);
            log.debug("今日出库: {}", todayOutbound);
            
            log.info("Dashboard概览数据: totalStock={}, totalSku={}, pendingOrders={}, todayInbound={}, todayOutbound={}", 
                    totalStock, totalSku, totalPending, todayInbound, todayOutbound);
        } catch (Exception e) {
            // 发生异常时记录日志并返回默认值
            log.error("获取Dashboard概览数据失败: {}", e.getMessage(), e);
            overview.setTotalStock(BigDecimal.ZERO);
            overview.setTotalSku(0);
            overview.setPendingPurchase(0);
            overview.setPendingSale(0);
            overview.setPendingOrders(0);
            overview.setTodayInbound(BigDecimal.ZERO);
            overview.setTodayOutbound(BigDecimal.ZERO);
        }
        
        return overview;
    }

    @Override
    public List<TrendVo> getInboundOutboundTrend() {
        List<TrendVo> trendList = new ArrayList<>();
        
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd");
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
            
            // 获取近7天的日期
            LocalDate today = LocalDate.now();
            Map<String, BigDecimal> inboundMap = new HashMap<>();
            Map<String, BigDecimal> outboundMap = new HashMap<>();
            
            // 初始化7天的数据
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                String dateStr = date.format(dateFormatter);
                inboundMap.put(dateStr, BigDecimal.ZERO);
                outboundMap.put(dateStr, BigDecimal.ZERO);
            }
            
            // 查询近7天的入库记录
            LocalDate startDate = today.minusDays(6);
            Date startDateTime = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDateTime = Date.from(today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
            
            LambdaQueryWrapper<StockRecord> inboundWrapper = new LambdaQueryWrapper<>();
            inboundWrapper.eq(StockRecord::getOrderType, 1) // 采购入库
                    .between(StockRecord::getCreateTime, startDateTime, endDateTime);
            List<StockRecord> inboundRecords = stockRecordMapper.selectList(inboundWrapper);
            
            // 按日期分组统计入库
            if (inboundRecords != null) {
                for (StockRecord record : inboundRecords) {
                    if (record.getCreateTime() != null && record.getChangeQty() != null) {
                        String dateStr = sdf.format(record.getCreateTime());
                        if (inboundMap.containsKey(dateStr)) {
                            inboundMap.put(dateStr, inboundMap.get(dateStr).add(record.getChangeQty()));
                        }
                    }
                }
            }
            
            // 查询近7天的出库记录
            LambdaQueryWrapper<StockRecord> outboundWrapper = new LambdaQueryWrapper<>();
            outboundWrapper.eq(StockRecord::getOrderType, 2) // 销售出库
                    .between(StockRecord::getCreateTime, startDateTime, endDateTime);
            List<StockRecord> outboundRecords = stockRecordMapper.selectList(outboundWrapper);
            
            // 按日期分组统计出库
            if (outboundRecords != null) {
                for (StockRecord record : outboundRecords) {
                    if (record.getCreateTime() != null && record.getChangeQty() != null) {
                        String dateStr = sdf.format(record.getCreateTime());
                        if (outboundMap.containsKey(dateStr)) {
                            outboundMap.put(dateStr, outboundMap.get(dateStr).add(record.getChangeQty().abs()));
                        }
                    }
                }
            }
            
            // 构建结果列表
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                String dateStr = date.format(dateFormatter);
                TrendVo trend = new TrendVo();
                trend.setDate(dateStr);
                trend.setInbound(inboundMap.get(dateStr));
                trend.setOutbound(outboundMap.get(dateStr));
                trendList.add(trend);
            }
        } catch (Exception e) {
            // 发生异常时返回空列表
        }
        
        return trendList;
    }

    @Override
    public List<CategoryRatioVo> getCategoryStockRatio() {
        List<CategoryRatioVo> ratioList = new ArrayList<>();
        
        try {
            // 1. 获取所有库存数据，按SKU分组汇总
            LambdaQueryWrapper<StockMain> stockWrapper = new LambdaQueryWrapper<>();
            List<StockMain> stockList = stockMainMapper.selectList(stockWrapper);
            
            if (stockList == null || stockList.isEmpty()) {
                return ratioList;
            }
            
            // 按SKU ID分组统计库存
            Map<Long, BigDecimal> skuStockMap = new HashMap<>();
            for (StockMain stock : stockList) {
                Long skuId = stock.getSkuId();
                BigDecimal quantity = stock.getQuantity();
                if (skuId != null && quantity != null) {
                    skuStockMap.merge(skuId, quantity, BigDecimal::add);
                }
            }
            
            // 2. 获取SKU与分类的对应关系
            Map<Long, Long> skuCategoryMap = new HashMap<>();
            Map<Long, String> categoryNameMap = new HashMap<>();
            
            // 获取所有分类
            List<BaseCategory> categories = categoryMapper.selectList(
                    new LambdaQueryWrapper<BaseCategory>().eq(BaseCategory::getIsDeleted, 0));
            if (categories != null) {
                for (BaseCategory category : categories) {
                    categoryNameMap.put(category.getId(), category.getCategoryName());
                }
            }
            
            // 获取所有SPU
            List<BaseProductSpu> spuList = spuMapper.selectList(
                    new LambdaQueryWrapper<BaseProductSpu>().eq(BaseProductSpu::getIsDeleted, 0));
            Map<Long, Long> spuCategoryMap = new HashMap<>();
            if (spuList != null) {
                for (BaseProductSpu spu : spuList) {
                    if (spu.getCategoryId() != null) {
                        spuCategoryMap.put(spu.getId(), spu.getCategoryId());
                    }
                }
            }
            
            // 获取所有SKU
            List<BaseProductSku> skuList = skuMapper.selectList(
                    new LambdaQueryWrapper<BaseProductSku>().eq(BaseProductSku::getIsDeleted, 0));
            if (skuList != null) {
                for (BaseProductSku sku : skuList) {
                    Long spuId = sku.getSpuId();
                    if (spuId != null && spuCategoryMap.containsKey(spuId)) {
                        skuCategoryMap.put(sku.getId(), spuCategoryMap.get(spuId));
                    }
                }
            }
            
            // 3. 按分类汇总库存
            Map<Long, BigDecimal> categoryStockMap = new HashMap<>();
            BigDecimal totalStock = BigDecimal.ZERO;
            
            for (Map.Entry<Long, BigDecimal> entry : skuStockMap.entrySet()) {
                Long skuId = entry.getKey();
                BigDecimal quantity = entry.getValue();
                Long categoryId = skuCategoryMap.get(skuId);
                if (categoryId != null) {
                    categoryStockMap.merge(categoryId, quantity, BigDecimal::add);
                    totalStock = totalStock.add(quantity);
                }
            }
            
            // 4. 计算占比
            for (Map.Entry<Long, BigDecimal> entry : categoryStockMap.entrySet()) {
                Long categoryId = entry.getKey();
                BigDecimal quantity = entry.getValue();
                String categoryName = categoryNameMap.get(categoryId);
                
                if (categoryName != null) {
                    CategoryRatioVo ratio = new CategoryRatioVo();
                    ratio.setCategoryName(categoryName);
                    ratio.setQuantity(quantity);
                    
                    if (totalStock.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal ratioValue = quantity.multiply(new BigDecimal("100"))
                                .divide(totalStock, 2, RoundingMode.HALF_UP);
                        ratio.setRatio(ratioValue);
                    } else {
                        ratio.setRatio(BigDecimal.ZERO);
                    }
                    ratioList.add(ratio);
                }
            }
            
            // 按库存数量降序排序
            ratioList.sort((a, b) -> b.getQuantity().compareTo(a.getQuantity()));
        } catch (Exception e) {
            // 发生异常时返回空列表
        }
        
        return ratioList;
    }

    @Override
    public List<StockWarningVo> getStockWarningList() {
        List<StockWarningVo> warningList = new ArrayList<>();
        
        try {
            // 1. 获取所有SKU的安全库存设置
            LambdaQueryWrapper<BaseProductSku> skuWrapper = new LambdaQueryWrapper<>();
            skuWrapper.eq(BaseProductSku::getIsDeleted, 0)
                    .isNotNull(BaseProductSku::getSafetyStock);
            List<BaseProductSku> skuList = skuMapper.selectList(skuWrapper);
            
            if (skuList == null || skuList.isEmpty()) {
                return warningList;
            }
            
            // 2. 获取库存数据
            LambdaQueryWrapper<StockMain> stockWrapper = new LambdaQueryWrapper<>();
            List<StockMain> stockList = stockMainMapper.selectList(stockWrapper);
            
            // 按SKU ID分组统计当前库存
            Map<Long, BigDecimal> skuStockMap = new HashMap<>();
            if (stockList != null) {
                for (StockMain stock : stockList) {
                    Long skuId = stock.getSkuId();
                    BigDecimal quantity = stock.getQuantity();
                    if (skuId != null && quantity != null) {
                        skuStockMap.merge(skuId, quantity, BigDecimal::add);
                    }
                }
            }
            
            // 3. 检查库存预警
            for (BaseProductSku sku : skuList) {
                Integer safetyStock = sku.getSafetyStock();
                if (safetyStock == null || safetyStock <= 0) {
                    continue;
                }
                
                BigDecimal currentStock = skuStockMap.getOrDefault(sku.getId(), BigDecimal.ZERO);
                BigDecimal safetyStockDecimal = new BigDecimal(safetyStock);
                
                // 库存不足预警：当前库存 < 安全库存
                if (currentStock.compareTo(safetyStockDecimal) < 0) {
                    StockWarningVo warning = new StockWarningVo();
                    warning.setSkuId(sku.getId());
                    warning.setSkuCode(sku.getSkuCode());
                    warning.setSkuName(sku.getSkuName());
                    warning.setCurrentStock(currentStock);
                    warning.setSafetyStock(safetyStock);
                    warning.setWarningType(1); // 库存不足
                    warningList.add(warning);
                }
                // 库存过量预警：当前库存 > 安全库存的3倍
                else if (currentStock.compareTo(safetyStockDecimal.multiply(new BigDecimal("3"))) > 0) {
                    StockWarningVo warning = new StockWarningVo();
                    warning.setSkuId(sku.getId());
                    warning.setSkuCode(sku.getSkuCode());
                    warning.setSkuName(sku.getSkuName());
                    warning.setCurrentStock(currentStock);
                    warning.setSafetyStock(safetyStock);
                    warning.setWarningType(2); // 库存过量
                    warningList.add(warning);
                }
            }
            
            // 按预警类型和库存数量排序
            warningList.sort((a, b) -> {
                int typeCompare = a.getWarningType().compareTo(b.getWarningType());
                if (typeCompare != 0) {
                    return typeCompare;
                }
                return a.getCurrentStock().compareTo(b.getCurrentStock());
            });
        } catch (Exception e) {
            // 发生异常时返回空列表
        }
        
        return warningList;
    }

    @Override
    public List<TopProductVo> getTopProducts() {
        List<TopProductVo> topList = new ArrayList<>();
        
        try {
            // 获取已完成的销售订单（状态为2已出库或更高）
            LambdaQueryWrapper<BusSaleOrder> orderWrapper = new LambdaQueryWrapper<>();
            orderWrapper.in(BusSaleOrder::getStatus, 2, 3); // 2已出库 3已取消（部分可能已完成）
            List<BusSaleOrder> saleOrders = saleOrderMapper.selectList(orderWrapper);
            
            if (saleOrders == null || saleOrders.isEmpty()) {
                return topList;
            }
            
            // 获取销售订单ID列表
            List<Long> saleIds = saleOrders.stream()
                    .map(BusSaleOrder::getId)
                    .collect(Collectors.toList());
            
            // 查询销售明细
            LambdaQueryWrapper<BusSaleItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.in(BusSaleItem::getSaleId, saleIds);
            List<BusSaleItem> saleItems = saleItemMapper.selectList(itemWrapper);
            
            if (saleItems == null || saleItems.isEmpty()) {
                return topList;
            }
            
            // 按SKU分组统计销售数量和金额
            Map<String, BigDecimal> quantityMap = new HashMap<>();
            Map<String, BigDecimal> amountMap = new HashMap<>();
            
            for (BusSaleItem item : saleItems) {
                String skuName = item.getSkuName();
                BigDecimal quantity = item.getQuantity();
                BigDecimal totalPrice = item.getTotalPrice();
                
                if (skuName != null) {
                    quantityMap.merge(skuName, quantity != null ? quantity : BigDecimal.ZERO, BigDecimal::add);
                    amountMap.merge(skuName, totalPrice != null ? totalPrice : BigDecimal.ZERO, BigDecimal::add);
                }
            }
            
            // 构建结果并按销售金额排序取TOP10
            topList = quantityMap.entrySet().stream()
                    .map(entry -> {
                        TopProductVo vo = new TopProductVo();
                        vo.setSkuName(entry.getKey());
                        vo.setQuantity(entry.getValue());
                        vo.setAmount(amountMap.getOrDefault(entry.getKey(), BigDecimal.ZERO));
                        return vo;
                    })
                    .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
                    .limit(10)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // 发生异常时返回空列表
        }
        
        return topList;
    }
}
