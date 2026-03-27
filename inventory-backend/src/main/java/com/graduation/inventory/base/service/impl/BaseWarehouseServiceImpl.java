package com.graduation.inventory.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.inventory.base.entity.BaseWarehouse;
import com.graduation.inventory.base.mapper.BaseWarehouseMapper;
import com.graduation.inventory.base.service.BaseWarehouseService;
import com.graduation.inventory.common.exception.ServiceException;
import com.graduation.inventory.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 仓库服务实现
 *
 * @author graduation
 * @version 1.0.0
 */
@Service
public class BaseWarehouseServiceImpl extends ServiceImpl<BaseWarehouseMapper, BaseWarehouse> implements BaseWarehouseService {

    @Autowired
    private BaseWarehouseMapper warehouseMapper;

    /**
     * 分页查询仓库列表
     */
    @Override
    public Page<BaseWarehouse> selectWarehousePage(Page<BaseWarehouse> page, String whCode, String whName, Integer status) {
        LambdaQueryWrapper<BaseWarehouse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(whCode), BaseWarehouse::getWhCode, whCode);
        queryWrapper.like(StringUtils.isNotBlank(whName), BaseWarehouse::getWhName, whName);
        queryWrapper.eq(status != null, BaseWarehouse::getStatus, status);
        queryWrapper.eq(BaseWarehouse::getIsDeleted, 0);
        queryWrapper.orderByDesc(BaseWarehouse::getCreateTime);
        return warehouseMapper.selectPage(page, queryWrapper);
    }

    /**
     * 根据ID查询仓库
     */
    @Override
    public BaseWarehouse selectWarehouseById(Long id) {
        if (id == null) {
            return null;
        }
        return warehouseMapper.selectById(id);
    }

    /**
     * 新增仓库
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertWarehouse(BaseWarehouse warehouse) {
        if (!checkWhCodeUnique(warehouse)) {
            throw new ServiceException("仓库编码已存在");
        }
        if (warehouse.getStatus() == null) {
            warehouse.setStatus(1);
        }
        warehouse.setIsDeleted(0);
        return warehouseMapper.insert(warehouse) > 0;
    }

    /**
     * 修改仓库
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWarehouse(BaseWarehouse warehouse) {
        if (warehouse.getId() == null) {
            throw new ServiceException("仓库ID不能为空");
        }
        if (StringUtils.isNotBlank(warehouse.getWhCode()) && !checkWhCodeUnique(warehouse)) {
            throw new ServiceException("仓库编码已存在");
        }
        return warehouseMapper.updateById(warehouse) > 0;
    }

    /**
     * 删除仓库
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteWarehouseById(Long id) {
        if (id == null) {
            throw new ServiceException("仓库ID不能为空");
        }
        // 逻辑删除
        BaseWarehouse warehouse = new BaseWarehouse();
        warehouse.setId(id);
        warehouse.setIsDeleted(1);
        return warehouseMapper.updateById(warehouse) > 0;
    }

    /**
     * 批量删除仓库
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteWarehouseByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("仓库ID列表不能为空");
        }
        for (Long id : ids) {
            deleteWarehouseById(id);
        }
        return true;
    }

    /**
     * 查询所有启用的仓库
     */
    @Override
    public List<BaseWarehouse> selectAllEnabledWarehouse() {
        LambdaQueryWrapper<BaseWarehouse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseWarehouse::getStatus, 1);
        queryWrapper.eq(BaseWarehouse::getIsDeleted, 0);
        return warehouseMapper.selectList(queryWrapper);
    }

    /**
     * 校验仓库编码是否唯一
     */
    @Override
    public boolean checkWhCodeUnique(BaseWarehouse warehouse) {
        if (StringUtils.isBlank(warehouse.getWhCode())) {
            return false;
        }
        LambdaQueryWrapper<BaseWarehouse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseWarehouse::getWhCode, warehouse.getWhCode());
        queryWrapper.eq(BaseWarehouse::getIsDeleted, 0);
        BaseWarehouse existingWarehouse = warehouseMapper.selectOne(queryWrapper);
        if (existingWarehouse == null) {
            return true;
        }
        return warehouse.getId() != null && existingWarehouse.getId().equals(warehouse.getId());
    }
}
