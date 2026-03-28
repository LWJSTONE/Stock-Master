package com.graduation.inventory.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.inventory.base.entity.BaseSupplier;
import com.graduation.inventory.base.mapper.BaseSupplierMapper;
import com.graduation.inventory.base.service.BaseSupplierService;
import com.graduation.inventory.common.exception.ServiceException;
import com.graduation.inventory.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 供应商服务实现
 *
 * @author graduation
 * @version 1.0.0
 */
@Service
public class BaseSupplierServiceImpl extends ServiceImpl<BaseSupplierMapper, BaseSupplier> implements BaseSupplierService {

    @Autowired
    private BaseSupplierMapper supplierMapper;

    /**
     * 分页查询供应商列表
     */
    @Override
    public Page<BaseSupplier> selectSupplierPage(Page<BaseSupplier> page, String supCode, String supName) {
        LambdaQueryWrapper<BaseSupplier> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(supCode), BaseSupplier::getSupCode, supCode);
        queryWrapper.like(StringUtils.isNotBlank(supName), BaseSupplier::getSupName, supName);
        queryWrapper.eq(BaseSupplier::getIsDeleted, 0);
        queryWrapper.orderByDesc(BaseSupplier::getCreateTime);
        return supplierMapper.selectPage(page, queryWrapper);
    }

    /**
     * 根据ID查询供应商
     */
    @Override
    public BaseSupplier selectSupplierById(Long id) {
        if (id == null) {
            return null;
        }
        return supplierMapper.selectById(id);
    }

    /**
     * 新增供应商
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertSupplier(BaseSupplier supplier) {
        if (!checkSupCodeUnique(supplier)) {
            throw new ServiceException("供应商编码已存在");
        }
        supplier.setIsDeleted(0);
        return supplierMapper.insert(supplier) > 0;
    }

    /**
     * 修改供应商
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSupplier(BaseSupplier supplier) {
        if (supplier.getId() == null) {
            throw new ServiceException("供应商ID不能为空");
        }
        if (StringUtils.isNotBlank(supplier.getSupCode()) && !checkSupCodeUnique(supplier)) {
            throw new ServiceException("供应商编码已存在");
        }
        return supplierMapper.updateById(supplier) > 0;
    }

    /**
     * 删除供应商
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSupplierById(Long id) {
        if (id == null) {
            throw new ServiceException("供应商ID不能为空");
        }
        // 逻辑删除
        BaseSupplier supplier = new BaseSupplier();
        supplier.setId(id);
        supplier.setIsDeleted(1);
        return supplierMapper.updateById(supplier) > 0;
    }

    /**
     * 批量删除供应商
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSupplierByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("供应商ID列表不能为空");
        }
        for (Long id : ids) {
            deleteSupplierById(id);
        }
        return true;
    }

    /**
     * 查询所有供应商
     */
    @Override
    public List<BaseSupplier> selectAllSupplier() {
        LambdaQueryWrapper<BaseSupplier> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseSupplier::getIsDeleted, 0);
        return supplierMapper.selectList(queryWrapper);
    }

    /**
     * 校验供应商编码是否唯一
     */
    @Override
    public boolean checkSupCodeUnique(BaseSupplier supplier) {
        if (StringUtils.isBlank(supplier.getSupCode())) {
            return false;
        }
        LambdaQueryWrapper<BaseSupplier> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseSupplier::getSupCode, supplier.getSupCode());
        queryWrapper.eq(BaseSupplier::getIsDeleted, 0);
        BaseSupplier existingSupplier = supplierMapper.selectOne(queryWrapper);
        if (existingSupplier == null) {
            return true;
        }
        return supplier.getId() != null && existingSupplier.getId().equals(supplier.getId());
    }
}
