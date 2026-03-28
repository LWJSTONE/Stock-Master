package com.graduation.inventory.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.inventory.business.entity.BusStockCheck;
import com.graduation.inventory.business.entity.dto.StockCheckDto;

/**
 * 库存盘点服务接口
 *
 * @author graduation
 * @version 1.0.0
 */
public interface BusStockCheckService extends IService<BusStockCheck> {

    /**
     * 分页查询盘点列表
     *
     * @param page         分页对象
     * @param checkNo      盘点单号
     * @param warehouseId  仓库ID
     * @param checkStatus  盘点状态
     * @return 分页结果
     */
    Page<BusStockCheck> selectCheckPage(Page<BusStockCheck> page, String checkNo, Long warehouseId, Integer checkStatus);

    /**
     * 根据ID查询盘点详情
     *
     * @param checkId 盘点ID
     * @return 盘点详情
     */
    BusStockCheck selectCheckById(Long checkId);

    /**
     * 新增盘点单
     *
     * @param dto 盘点DTO
     * @return 是否成功
     */
    boolean insertCheck(StockCheckDto dto);

    /**
     * 提交实盘数量
     *
     * @param dto 盘点DTO
     * @return 是否成功
     */
    boolean submitActualQty(StockCheckDto dto);

    /**
     * 确认盘点
     *
     * @param checkId 盘点ID
     * @return 是否成功
     */
    boolean confirmCheck(Long checkId);

    /**
     * 批量删除盘点单
     *
     * @param checkIds 盘点ID数组
     * @return 是否成功
     */
    boolean deleteCheckByIds(Long[] checkIds);
}
