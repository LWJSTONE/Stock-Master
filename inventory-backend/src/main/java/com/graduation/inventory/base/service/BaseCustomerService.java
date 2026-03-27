package com.graduation.inventory.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.inventory.base.entity.BaseCustomer;

import java.util.List;

/**
 * 客户服务接口
 *
 * @author graduation
 * @version 1.0.0
 */
public interface BaseCustomerService extends IService<BaseCustomer> {

    /**
     * 分页查询客户列表
     *
     * @param page      分页对象
     * @param custCode  客户编码
     * @param custName  客户名称
     * @return 分页结果
     */
    Page<BaseCustomer> selectCustomerPage(Page<BaseCustomer> page, String custCode, String custName);

    /**
     * 根据ID查询客户
     *
     * @param id 客户ID
     * @return 客户对象
     */
    BaseCustomer selectCustomerById(Long id);

    /**
     * 新增客户
     *
     * @param customer 客户信息
     * @return 是否成功
     */
    boolean insertCustomer(BaseCustomer customer);

    /**
     * 修改客户
     *
     * @param customer 客户信息
     * @return 是否成功
     */
    boolean updateCustomer(BaseCustomer customer);

    /**
     * 删除客户
     *
     * @param id 客户ID
     * @return 是否成功
     */
    boolean deleteCustomerById(Long id);

    /**
     * 批量删除客户
     *
     * @param ids 客户ID列表
     * @return 是否成功
     */
    boolean deleteCustomerByIds(List<Long> ids);

    /**
     * 查询所有客户
     *
     * @return 客户列表
     */
    List<BaseCustomer> selectAllCustomer();

    /**
     * 校验客户编码是否唯一
     *
     * @param customer 客户信息
     * @return 是否唯一
     */
    boolean checkCustCodeUnique(BaseCustomer customer);
}
