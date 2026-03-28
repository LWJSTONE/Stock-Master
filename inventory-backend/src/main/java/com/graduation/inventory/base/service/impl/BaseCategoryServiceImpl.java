package com.graduation.inventory.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.inventory.base.entity.BaseCategory;
import com.graduation.inventory.base.mapper.BaseCategoryMapper;
import com.graduation.inventory.base.service.BaseCategoryService;
import com.graduation.inventory.common.exception.ServiceException;
import com.graduation.inventory.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 商品分类服务实现
 *
 * @author graduation
 * @version 1.0.0
 */
@Service
public class BaseCategoryServiceImpl extends ServiceImpl<BaseCategoryMapper, BaseCategory> implements BaseCategoryService {

    private static final Long ROOT_PARENT_ID = 0L;

    @Autowired
    private BaseCategoryMapper categoryMapper;

    /**
     * 分页查询分类列表
     */
    @Override
    public Page<BaseCategory> selectCategoryPage(Page<BaseCategory> page, String categoryCode, String categoryName, Integer status) {
        LambdaQueryWrapper<BaseCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(categoryCode), BaseCategory::getCategoryCode, categoryCode);
        queryWrapper.like(StringUtils.isNotBlank(categoryName), BaseCategory::getCategoryName, categoryName);
        queryWrapper.eq(status != null, BaseCategory::getStatus, status);
        queryWrapper.eq(BaseCategory::getIsDeleted, 0);
        queryWrapper.orderByAsc(BaseCategory::getParentId, BaseCategory::getOrderNum);
        return categoryMapper.selectPage(page, queryWrapper);
    }

    /**
     * 查询分类列表（不分页）
     */
    @Override
    public List<BaseCategory> selectCategoryList(BaseCategory category) {
        LambdaQueryWrapper<BaseCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(category.getCategoryCode()), BaseCategory::getCategoryCode, category.getCategoryCode());
        queryWrapper.like(StringUtils.isNotBlank(category.getCategoryName()), BaseCategory::getCategoryName, category.getCategoryName());
        queryWrapper.eq(category.getStatus() != null, BaseCategory::getStatus, category.getStatus());
        queryWrapper.eq(BaseCategory::getIsDeleted, 0);
        queryWrapper.orderByAsc(BaseCategory::getParentId, BaseCategory::getOrderNum);
        return categoryMapper.selectList(queryWrapper);
    }

    /**
     * 根据ID查询分类
     */
    @Override
    public BaseCategory selectCategoryById(Long id) {
        if (id == null) {
            return null;
        }
        return categoryMapper.selectById(id);
    }

    /**
     * 新增分类
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertCategory(BaseCategory category) {
        if (!checkCategoryCodeUnique(category)) {
            throw new ServiceException("分类编码已存在");
        }
        if (category.getParentId() == null) {
            category.setParentId(ROOT_PARENT_ID);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (category.getOrderNum() == null) {
            category.setOrderNum(0);
        }
        category.setIsDeleted(0);
        return categoryMapper.insert(category) > 0;
    }

    /**
     * 修改分类
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategory(BaseCategory category) {
        if (category.getId() == null) {
            throw new ServiceException("分类ID不能为空");
        }
        if (StringUtils.isNotBlank(category.getCategoryCode()) && !checkCategoryCodeUnique(category)) {
            throw new ServiceException("分类编码已存在");
        }
        if (category.getId().equals(category.getParentId())) {
            throw new ServiceException("父分类不能选择自己");
        }
        return categoryMapper.updateById(category) > 0;
    }

    /**
     * 删除分类
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategoryById(Long id) {
        if (id == null) {
            throw new ServiceException("分类ID不能为空");
        }
        if (hasChildById(id)) {
            throw new ServiceException("存在子分类，不允许删除");
        }
        // 逻辑删除
        BaseCategory category = new BaseCategory();
        category.setId(id);
        category.setIsDeleted(1);
        return categoryMapper.updateById(category) > 0;
    }

    /**
     * 批量删除分类
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategoryByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("分类ID列表不能为空");
        }
        for (Long id : ids) {
            deleteCategoryById(id);
        }
        return true;
    }

    /**
     * 构建分类树
     */
    @Override
    public List<BaseCategory> buildCategoryTree(List<BaseCategory> categories) {
        if (categories == null || categories.isEmpty()) {
            return new ArrayList<>();
        }
        return buildTree(categories, ROOT_PARENT_ID);
    }

    /**
     * 获取分类树
     */
    @Override
    public List<BaseCategory> getCategoryTree() {
        List<BaseCategory> categories = selectCategoryList(new BaseCategory());
        return buildCategoryTree(categories);
    }

    /**
     * 是否存在子节点
     */
    @Override
    public boolean hasChildById(Long id) {
        LambdaQueryWrapper<BaseCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseCategory::getParentId, id);
        queryWrapper.eq(BaseCategory::getIsDeleted, 0);
        return categoryMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 校验分类编码是否唯一
     */
    @Override
    public boolean checkCategoryCodeUnique(BaseCategory category) {
        if (StringUtils.isBlank(category.getCategoryCode())) {
            return false;
        }
        LambdaQueryWrapper<BaseCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseCategory::getCategoryCode, category.getCategoryCode());
        queryWrapper.eq(BaseCategory::getIsDeleted, 0);
        BaseCategory existingCategory = categoryMapper.selectOne(queryWrapper);
        if (existingCategory == null) {
            return true;
        }
        return category.getId() != null && existingCategory.getId().equals(category.getId());
    }

    /**
     * 构建树形结构
     */
    private List<BaseCategory> buildTree(List<BaseCategory> categories, Long parentId) {
        List<BaseCategory> returnList = new ArrayList<>();
        for (Iterator<BaseCategory> iterator = categories.iterator(); iterator.hasNext(); ) {
            BaseCategory category = iterator.next();
            if (parentId.equals(category.getParentId())) {
                recursionFn(categories, category);
                returnList.add(category);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<BaseCategory> list, BaseCategory category) {
        List<BaseCategory> childList = getChildList(list, category);
        category.setChildren(childList);
        for (BaseCategory child : childList) {
            if (hasChild(list, child)) {
                recursionFn(list, child);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<BaseCategory> getChildList(List<BaseCategory> list, BaseCategory category) {
        List<BaseCategory> childList = new ArrayList<>();
        for (BaseCategory item : list) {
            if (category.getId().equals(item.getParentId())) {
                childList.add(item);
            }
        }
        return childList;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<BaseCategory> list, BaseCategory category) {
        return getChildList(list, category).size() > 0;
    }
}
