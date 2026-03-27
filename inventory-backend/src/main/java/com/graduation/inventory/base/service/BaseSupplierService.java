package com.graduation.inventory.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.inventory.base.entity.BaseSupplier;

import java.util.List;

/**
 * 供应商服务接口
 *
 * @author graduation
 * @version 1.0.0
 */
public interface BaseSupplierService extends IService<BaseSupplier> {

    /**
     * 分页查询供应商列表
     *
     * @param page     分页对象
     * @param supCode  供应商编码
     * @param supName  供应商名称
     * @return 分页结果
     */
    Page<BaseSupplier> selectSupplierPage(Page<BaseSupplier> page, String supCode, String supName);

    /**
     * 根据ID查询供应商
     *
     * @param id 供应商ID
     * @return 供应商对象
     */
    BaseSupplier selectSupplierById(Long id);

    /**
     * 新增供应商
     *
     * @param supplier 供应商信息
     * @return 是否成功
     */
    boolean insertSupplier(BaseSupplier supplier);

    /**
     * 修改供应商
     *
     * @param supplier 供应商信息
     * @return 是否成功
     */
    boolean updateSupplier(BaseSupplier supplier);

    /**
     * 删除供应商
     *
     * @param id 供应商ID
     * @return 是否成功
     */
    boolean deleteSupplierById(Long id);

    /**
     * 批量删除供应商
     *
     * @param ids 供应商ID列表
     * @return 是否成功
     */
    boolean deleteSupplierByIds(List<Long> ids);

    /**
     * 查询所有供应商
     *
     * @return 供应商列表
     */
    List<BaseSupplier> selectAllSupplier();

    /**
     * 校验供应商编码是否唯一
     *
     * @param supplier 供应商信息
     * @return 是否唯一
     */
    boolean checkSupCodeUnique(BaseSupplier supplier);
}
