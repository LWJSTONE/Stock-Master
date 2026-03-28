package com.graduation.inventory.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.inventory.base.entity.BaseCustomer;
import com.graduation.inventory.base.mapper.BaseCustomerMapper;
import com.graduation.inventory.base.service.BaseCustomerService;
import com.graduation.inventory.common.exception.ServiceException;
import com.graduation.inventory.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 客户服务实现
 *
 * @author graduation
 * @version 1.0.0
 */
@Service
public class BaseCustomerServiceImpl extends ServiceImpl<BaseCustomerMapper, BaseCustomer> implements BaseCustomerService {

    @Autowired
    private BaseCustomerMapper customerMapper;

    /**
     * 分页查询客户列表
     */
    @Override
    public Page<BaseCustomer> selectCustomerPage(Page<BaseCustomer> page, String custCode, String custName) {
        LambdaQueryWrapper<BaseCustomer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(custCode), BaseCustomer::getCustCode, custCode);
        queryWrapper.like(StringUtils.isNotBlank(custName), BaseCustomer::getCustName, custName);
        queryWrapper.eq(BaseCustomer::getIsDeleted, 0);
        queryWrapper.orderByDesc(BaseCustomer::getCreateTime);
        return customerMapper.selectPage(page, queryWrapper);
    }

    /**
     * 根据ID查询客户
     */
    @Override
    public BaseCustomer selectCustomerById(Long id) {
        if (id == null) {
            return null;
        }
        return customerMapper.selectById(id);
    }

    /**
     * 新增客户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertCustomer(BaseCustomer customer) {
        if (!checkCustCodeUnique(customer)) {
            throw new ServiceException("客户编码已存在");
        }
        customer.setIsDeleted(0);
        return customerMapper.insert(customer) > 0;
    }

    /**
     * 修改客户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCustomer(BaseCustomer customer) {
        if (customer.getId() == null) {
            throw new ServiceException("客户ID不能为空");
        }
        if (StringUtils.isNotBlank(customer.getCustCode()) && !checkCustCodeUnique(customer)) {
            throw new ServiceException("客户编码已存在");
        }
        return customerMapper.updateById(customer) > 0;
    }

    /**
     * 删除客户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCustomerById(Long id) {
        if (id == null) {
            throw new ServiceException("客户ID不能为空");
        }
        // 逻辑删除
        BaseCustomer customer = new BaseCustomer();
        customer.setId(id);
        customer.setIsDeleted(1);
        return customerMapper.updateById(customer) > 0;
    }

    /**
     * 批量删除客户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCustomerByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("客户ID列表不能为空");
        }
        for (Long id : ids) {
            deleteCustomerById(id);
        }
        return true;
    }

    /**
     * 查询所有客户
     */
    @Override
    public List<BaseCustomer> selectAllCustomer() {
        LambdaQueryWrapper<BaseCustomer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseCustomer::getIsDeleted, 0);
        return customerMapper.selectList(queryWrapper);
    }

    /**
     * 校验客户编码是否唯一
     */
    @Override
    public boolean checkCustCodeUnique(BaseCustomer customer) {
        if (StringUtils.isBlank(customer.getCustCode())) {
            return false;
        }
        LambdaQueryWrapper<BaseCustomer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseCustomer::getCustCode, customer.getCustCode());
        queryWrapper.eq(BaseCustomer::getIsDeleted, 0);
        BaseCustomer existingCustomer = customerMapper.selectOne(queryWrapper);
        if (existingCustomer == null) {
            return true;
        }
        return customer.getId() != null && existingCustomer.getId().equals(customer.getId());
    }
}
