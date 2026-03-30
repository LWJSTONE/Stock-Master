package com.graduation.inventory.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.inventory.base.entity.BaseCategory;
import com.graduation.inventory.base.entity.BaseProductSku;
import com.graduation.inventory.base.entity.BaseProductSpu;
import com.graduation.inventory.base.mapper.BaseCategoryMapper;
import com.graduation.inventory.base.mapper.BaseProductSkuMapper;
import com.graduation.inventory.base.mapper.BaseProductSpuMapper;
import com.graduation.inventory.base.service.BaseProductSpuService;
import com.graduation.inventory.common.exception.ServiceException;
import com.graduation.inventory.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品SPU服务实现
 *
 * @author graduation
 * @version 1.0.0
 */
@Service
public class BaseProductSpuServiceImpl extends ServiceImpl<BaseProductSpuMapper, BaseProductSpu> implements BaseProductSpuService {

    private static final Logger log = LoggerFactory.getLogger(BaseProductSpuServiceImpl.class);

    @Autowired
    private BaseProductSpuMapper spuMapper;

    @Autowired
    private BaseProductSkuMapper skuMapper;
    
    @Autowired
    private BaseCategoryMapper categoryMapper;

    /**
     * 分页查询SPU列表
     */
    @Override
    public Page<BaseProductSpu> selectSpuPage(Page<BaseProductSpu> page, String spuCode, String spuName, Long categoryId, Integer status) {
        LambdaQueryWrapper<BaseProductSpu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(spuCode), BaseProductSpu::getSpuCode, spuCode);
        queryWrapper.like(StringUtils.isNotBlank(spuName), BaseProductSpu::getSpuName, spuName);
        queryWrapper.eq(categoryId != null, BaseProductSpu::getCategoryId, categoryId);
        queryWrapper.eq(status != null, BaseProductSpu::getStatus, status);
        queryWrapper.eq(BaseProductSpu::getIsDeleted, 0);
        queryWrapper.orderByDesc(BaseProductSpu::getCreateTime);
        Page<BaseProductSpu> result = spuMapper.selectPage(page, queryWrapper);
        
        // 填充分类名称和品牌名称
        for (BaseProductSpu spu : result.getRecords()) {
            if (spu.getCategoryId() != null) {
                BaseCategory category = categoryMapper.selectById(spu.getCategoryId());
                if (category != null) {
                    spu.setCategoryName(category.getCategoryName());
                }
            }
            // 品牌名称直接使用品牌字段
            spu.setBrandName(spu.getBrand());
        }
        
        return result;
    }

    /**
     * 根据ID查询SPU
     */
    @Override
    public BaseProductSpu selectSpuById(Long id) {
        if (id == null) {
            return null;
        }
        return spuMapper.selectById(id);
    }

    /**
     * 新增SPU
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertSpu(BaseProductSpu spu) {
        if (!checkSpuCodeUnique(spu)) {
            throw new ServiceException("SPU编码已存在");
        }
        if (spu.getStatus() == null) {
            spu.setStatus(1);
        }
        spu.setIsDeleted(0);
        return spuMapper.insert(spu) > 0;
    }

    /**
     * 修改SPU
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSpu(BaseProductSpu spu) {
        if (spu.getId() == null) {
            throw new ServiceException("SPU ID不能为空");
        }
        if (StringUtils.isNotBlank(spu.getSpuCode()) && !checkSpuCodeUnique(spu)) {
            throw new ServiceException("SPU编码已存在");
        }
        return spuMapper.updateById(spu) > 0;
    }

    /**
     * 删除SPU
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSpuById(Long id) {
        if (id == null) {
            throw new ServiceException("SPU ID不能为空");
        }
        // 检查是否存在SKU
        LambdaQueryWrapper<BaseProductSku> skuWrapper = new LambdaQueryWrapper<>();
        skuWrapper.eq(BaseProductSku::getSpuId, id);
        skuWrapper.eq(BaseProductSku::getIsDeleted, 0);
        if (skuMapper.selectCount(skuWrapper) > 0) {
            throw new ServiceException("存在关联的SKU，不允许删除");
        }
        // 逻辑删除
        BaseProductSpu spu = new BaseProductSpu();
        spu.setId(id);
        spu.setIsDeleted(1);
        return spuMapper.updateById(spu) > 0;
    }

    /**
     * 批量删除SPU
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSpuByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("SPU ID列表不能为空");
        }
        for (Long id : ids) {
            deleteSpuById(id);
        }
        return true;
    }

    /**
     * 获取SPU及其SKU列表
     */
    @Override
    public BaseProductSpu selectSpuWithSkuList(Long id) {
        BaseProductSpu spu = selectSpuById(id);
        if (spu != null) {
            List<BaseProductSku> skuList = selectSkuListBySpuId(id);
            spu.setSkuList(skuList);
        }
        return spu;
    }

    /**
     * 校验SPU编码是否唯一
     */
    @Override
    public boolean checkSpuCodeUnique(BaseProductSpu spu) {
        if (StringUtils.isBlank(spu.getSpuCode())) {
            return false;
        }
        LambdaQueryWrapper<BaseProductSpu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseProductSpu::getSpuCode, spu.getSpuCode());
        queryWrapper.eq(BaseProductSpu::getIsDeleted, 0);
        BaseProductSpu existingSpu = spuMapper.selectOne(queryWrapper);
        if (existingSpu == null) {
            return true;
        }
        return spu.getId() != null && existingSpu.getId().equals(spu.getId());
    }

    /**
     * 根据SPU ID查询SKU列表
     */
    private List<BaseProductSku> selectSkuListBySpuId(Long spuId) {
        LambdaQueryWrapper<BaseProductSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseProductSku::getSpuId, spuId);
        queryWrapper.eq(BaseProductSku::getIsDeleted, 0);
        return skuMapper.selectList(queryWrapper);
    }
}
