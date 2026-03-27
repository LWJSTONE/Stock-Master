package com.graduation.inventory.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.inventory.base.entity.BaseCategory;

import java.util.List;

/**
 * 商品分类服务接口
 *
 * @author graduation
 * @version 1.0.0
 */
public interface BaseCategoryService extends IService<BaseCategory> {

    /**
     * 分页查询分类列表
     *
     * @param page         分页对象
     * @param categoryCode 分类编码
     * @param categoryName 分类名称
     * @param status       状态
     * @return 分页结果
     */
    Page<BaseCategory> selectCategoryPage(Page<BaseCategory> page, String categoryCode, String categoryName, Integer status);

    /**
     * 查询分类列表（不分页）
     *
     * @param category 分类查询条件
     * @return 分类列表
     */
    List<BaseCategory> selectCategoryList(BaseCategory category);

    /**
     * 根据ID查询分类
     *
     * @param id 分类ID
     * @return 分类对象
     */
    BaseCategory selectCategoryById(Long id);

    /**
     * 新增分类
     *
     * @param category 分类信息
     * @return 是否成功
     */
    boolean insertCategory(BaseCategory category);

    /**
     * 修改分类
     *
     * @param category 分类信息
     * @return 是否成功
     */
    boolean updateCategory(BaseCategory category);

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 是否成功
     */
    boolean deleteCategoryById(Long id);

    /**
     * 批量删除分类
     *
     * @param ids 分类ID列表
     * @return 是否成功
     */
    boolean deleteCategoryByIds(List<Long> ids);

    /**
     * 构建分类树
     *
     * @param categories 分类列表
     * @return 分类树
     */
    List<BaseCategory> buildCategoryTree(List<BaseCategory> categories);

    /**
     * 获取分类树
     *
     * @return 分类树
     */
    List<BaseCategory> getCategoryTree();

    /**
     * 是否存在子节点
     *
     * @param id 分类ID
     * @return 是否存在
     */
    boolean hasChildById(Long id);

    /**
     * 校验分类编码是否唯一
     *
     * @param category 分类信息
     * @return 是否唯一
     */
    boolean checkCategoryCodeUnique(BaseCategory category);
}
