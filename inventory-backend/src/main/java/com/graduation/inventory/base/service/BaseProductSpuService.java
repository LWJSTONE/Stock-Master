package com.graduation.inventory.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.inventory.base.entity.BaseProductSpu;
import com.graduation.inventory.base.entity.BaseProductSku;

import java.util.List;

/**
 * 商品SPU服务接口
 *
 * @author graduation
 * @version 1.0.0
 */
public interface BaseProductSpuService extends IService<BaseProductSpu> {

    /**
     * 分页查询SPU列表
     *
     * @param page     分页对象
     * @param spuCode  SPU编码
     * @param spuName  SPU名称
     * @param categoryId 分类ID
     * @param status   状态
     * @return 分页结果
     */
    Page<BaseProductSpu> selectSpuPage(Page<BaseProductSpu> page, String spuCode, String spuName, Long categoryId, Integer status);

    /**
     * 根据ID查询SPU
     *
     * @param id SPU ID
     * @return SPU对象
     */
    BaseProductSpu selectSpuById(Long id);

    /**
     * 新增SPU
     *
     * @param spu SPU信息
     * @return 是否成功
     */
    boolean insertSpu(BaseProductSpu spu);

    /**
     * 修改SPU
     *
     * @param spu SPU信息
     * @return 是否成功
     */
    boolean updateSpu(BaseProductSpu spu);

    /**
     * 删除SPU
     *
     * @param id SPU ID
     * @return 是否成功
     */
    boolean deleteSpuById(Long id);

    /**
     * 批量删除SPU
     *
     * @param ids SPU ID列表
     * @return 是否成功
     */
    boolean deleteSpuByIds(List<Long> ids);

    /**
     * 获取SPU及其SKU列表
     *
     * @param id SPU ID
     * @return SPU对象（包含SKU列表）
     */
    BaseProductSpu selectSpuWithSkuList(Long id);

    /**
     * 校验SPU编码是否唯一
     *
     * @param spu SPU信息
     * @return 是否唯一
     */
    boolean checkSpuCodeUnique(BaseProductSpu spu);
}
