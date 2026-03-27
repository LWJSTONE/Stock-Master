package com.graduation.inventory.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.inventory.base.entity.BaseWarehouse;

import java.util.List;

/**
 * 仓库服务接口
 *
 * @author graduation
 * @version 1.0.0
 */
public interface BaseWarehouseService extends IService<BaseWarehouse> {

    /**
     * 分页查询仓库列表
     *
     * @param page    分页对象
     * @param whCode  仓库编码
     * @param whName  仓库名称
     * @param status  状态
     * @return 分页结果
     */
    Page<BaseWarehouse> selectWarehousePage(Page<BaseWarehouse> page, String whCode, String whName, Integer status);

    /**
     * 根据ID查询仓库
     *
     * @param id 仓库ID
     * @return 仓库对象
     */
    BaseWarehouse selectWarehouseById(Long id);

    /**
     * 新增仓库
     *
     * @param warehouse 仓库信息
     * @return 是否成功
     */
    boolean insertWarehouse(BaseWarehouse warehouse);

    /**
     * 修改仓库
     *
     * @param warehouse 仓库信息
     * @return 是否成功
     */
    boolean updateWarehouse(BaseWarehouse warehouse);

    /**
     * 删除仓库
     *
     * @param id 仓库ID
     * @return 是否成功
     */
    boolean deleteWarehouseById(Long id);

    /**
     * 批量删除仓库
     *
     * @param ids 仓库ID列表
     * @return 是否成功
     */
    boolean deleteWarehouseByIds(List<Long> ids);

    /**
     * 查询所有启用的仓库
     *
     * @return 仓库列表
     */
    List<BaseWarehouse> selectAllEnabledWarehouse();

    /**
     * 校验仓库编码是否唯一
     *
     * @param warehouse 仓库信息
     * @return 是否唯一
     */
    boolean checkWhCodeUnique(BaseWarehouse warehouse);
}
