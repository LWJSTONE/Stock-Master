package com.graduation.inventory.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.inventory.base.entity.BaseProductSku;

import java.util.List;
import java.util.Map;

/**
 * 商品SKU服务接口
 *
 * @author graduation
 * @version 1.0.0
 */
public interface BaseProductSkuService extends IService<BaseProductSku> {

    /**
     * 分页查询SKU列表
     *
     * @param page    分页对象
     * @param skuCode SKU编码
     * @param skuName SKU名称
     * @param spuId   SPU ID
     * @return 分页结果
     */
    Page<BaseProductSku> selectSkuPage(Page<BaseProductSku> page, String skuCode, String skuName, Long spuId);

    /**
     * 根据ID查询SKU
     *
     * @param id SKU ID
     * @return SKU对象
     */
    BaseProductSku selectSkuById(Long id);

    /**
     * 新增SKU
     *
     * @param sku SKU信息
     * @return 是否成功
     */
    boolean insertSku(BaseProductSku sku);

    /**
     * 批量生成SKU（笛卡尔积）
     *
     * @param spuId     SPU ID
     * @param specLists 规格列表 Map<规格名, 规格值列表>
     * @return 是否成功
     */
    boolean batchGenerateSku(Long spuId, Map<String, List<String>> specLists);

    /**
     * 修改SKU
     *
     * @param sku SKU信息
     * @return 是否成功
     */
    boolean updateSku(BaseProductSku sku);

    /**
     * 删除SKU
     *
     * @param id SKU ID
     * @return 是否成功
     */
    boolean deleteSkuById(Long id);

    /**
     * 批量删除SKU
     *
     * @param ids SKU ID列表
     * @return 是否成功
     */
    boolean deleteSkuByIds(List<Long> ids);

    /**
     * 根据SPU ID查询SKU列表
     *
     * @param spuId SPU ID
     * @return SKU列表
     */
    List<BaseProductSku> selectSkuListBySpuId(Long spuId);

    /**
     * 校验SKU编码是否唯一
     *
     * @param sku SKU信息
     * @return 是否唯一
     */
    boolean checkSkuCodeUnique(BaseProductSku sku);
}
