package com.graduation.inventory.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.business.entity.BusStockCheck;
import com.graduation.inventory.common.domain.PageResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 盘点服务接口
 *
 * @author graduation
 * @version 1.0.0
 */
public interface StockCheckService {

    /**
     * 分页查询盘点列表
     *
     * @param page       分页对象
     * @param checkNo    盘点单号
     * @param warehouseId 仓库ID
     * @param checkStatus 盘点状态
     * @return 分页结果
     */
    PageResult<Map<String, Object>> list(Page<Map<String, Object>> page, String checkNo, Long warehouseId, Integer checkStatus);

    /**
     * 查询盘点详情
     *
     * @param id 盘点ID
     * @return 盘点详情
     */
    Map<String, Object> getInfo(Long id);

    /**
     * 新增盘点单（查询系统库存）
     *
     * @param stockCheck  盘点主表
     * @param skuIds      要盘点的SKU ID列表
     * @return 是否成功
     */
    boolean add(BusStockCheck stockCheck, List<Long> skuIds);

    /**
     * 提交实盘数量
     *
     * @param checkId   盘点ID
     * @param skuId     SKU ID
     * @param actualQty 实盘数量
     * @param remark    备注
     * @return 是否成功
     */
    boolean submitItem(Long checkId, Long skuId, BigDecimal actualQty, String remark);

    /**
     * 确认盘点（差异处理：盘盈入库、盘亏出库）
     *
     * @param id 盘点ID
     * @return 是否成功
     */
    boolean confirm(Long id);
}
