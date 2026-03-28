package com.graduation.inventory.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.inventory.base.entity.BaseProductSku;
import com.graduation.inventory.base.mapper.BaseProductSkuMapper;
import com.graduation.inventory.base.service.BaseProductSkuService;
import com.graduation.inventory.common.exception.ServiceException;
import com.graduation.inventory.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商品SKU服务实现
 *
 * @author graduation
 * @version 1.0.0
 */
@Service
public class BaseProductSkuServiceImpl extends ServiceImpl<BaseProductSkuMapper, BaseProductSku> implements BaseProductSkuService {

    private static final Logger log = LoggerFactory.getLogger(BaseProductSkuServiceImpl.class);

    @Autowired
    private BaseProductSkuMapper skuMapper;

    /**
     * 分页查询SKU列表
     */
    @Override
    public Page<BaseProductSku> selectSkuPage(Page<BaseProductSku> page, String skuCode, String skuName, Long spuId) {
        LambdaQueryWrapper<BaseProductSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(skuCode), BaseProductSku::getSkuCode, skuCode);
        queryWrapper.like(StringUtils.isNotBlank(skuName), BaseProductSku::getSkuName, skuName);
        queryWrapper.eq(spuId != null, BaseProductSku::getSpuId, spuId);
        queryWrapper.eq(BaseProductSku::getIsDeleted, 0);
        queryWrapper.orderByDesc(BaseProductSku::getCreateTime);
        return skuMapper.selectPage(page, queryWrapper);
    }

    /**
     * 根据ID查询SKU
     */
    @Override
    public BaseProductSku selectSkuById(Long id) {
        if (id == null) {
            return null;
        }
        return skuMapper.selectById(id);
    }

    /**
     * 新增SKU
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertSku(BaseProductSku sku) {
        if (!checkSkuCodeUnique(sku)) {
            throw new ServiceException("SKU编码已存在");
        }
        if (sku.getSalePrice() == null) {
            sku.setSalePrice(BigDecimal.ZERO);
        }
        if (sku.getCostPrice() == null) {
            sku.setCostPrice(BigDecimal.ZERO);
        }
        if (sku.getSafetyStock() == null) {
            sku.setSafetyStock(0);
        }
        sku.setIsDeleted(0);
        return skuMapper.insert(sku) > 0;
    }

    /**
     * 批量生成SKU（笛卡尔积）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchGenerateSku(Long spuId, Map<String, List<String>> specLists) {
        if (spuId == null) {
            throw new ServiceException("SPU ID不能为空");
        }
        if (specLists == null || specLists.isEmpty()) {
            throw new ServiceException("规格列表不能为空");
        }

        // 生成笛卡尔积
        List<Map<String, String>> combinations = generateCombinations(specLists);
        
        List<BaseProductSku> skuList = new ArrayList<>();
        int index = 1;
        for (Map<String, String> combination : combinations) {
            BaseProductSku sku = new BaseProductSku();
            sku.setSpuId(spuId);
            
            // 生成SKU编码（SPU_ID_序号）
            sku.setSkuCode("SKU_" + spuId + "_" + index);
            
            // 生成SKU名称（规格值拼接）
            String skuName = String.join("_", combination.values());
            sku.setSkuName(skuName);
            
            // 将规格信息转为JSON字符串
            sku.setSpecInfo(toJsonString(combination));
            
            sku.setSalePrice(BigDecimal.ZERO);
            sku.setCostPrice(BigDecimal.ZERO);
            sku.setSafetyStock(0);
            sku.setIsDeleted(0);
            
            skuList.add(sku);
            index++;
        }

        // 批量插入
        for (BaseProductSku sku : skuList) {
            skuMapper.insert(sku);
        }
        
        log.info("批量生成SKU成功，SPU ID: {}, 数量: {}", spuId, skuList.size());
        return true;
    }

    /**
     * 修改SKU
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSku(BaseProductSku sku) {
        if (sku.getId() == null) {
            throw new ServiceException("SKU ID不能为空");
        }
        if (StringUtils.isNotBlank(sku.getSkuCode()) && !checkSkuCodeUnique(sku)) {
            throw new ServiceException("SKU编码已存在");
        }
        return skuMapper.updateById(sku) > 0;
    }

    /**
     * 删除SKU
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSkuById(Long id) {
        if (id == null) {
            throw new ServiceException("SKU ID不能为空");
        }
        // 逻辑删除
        BaseProductSku sku = new BaseProductSku();
        sku.setId(id);
        sku.setIsDeleted(1);
        return skuMapper.updateById(sku) > 0;
    }

    /**
     * 批量删除SKU
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSkuByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException("SKU ID列表不能为空");
        }
        for (Long id : ids) {
            deleteSkuById(id);
        }
        return true;
    }

    /**
     * 根据SPU ID查询SKU列表
     */
    @Override
    public List<BaseProductSku> selectSkuListBySpuId(Long spuId) {
        LambdaQueryWrapper<BaseProductSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseProductSku::getSpuId, spuId);
        queryWrapper.eq(BaseProductSku::getIsDeleted, 0);
        return skuMapper.selectList(queryWrapper);
    }

    /**
     * 校验SKU编码是否唯一
     */
    @Override
    public boolean checkSkuCodeUnique(BaseProductSku sku) {
        if (StringUtils.isBlank(sku.getSkuCode())) {
            return false;
        }
        LambdaQueryWrapper<BaseProductSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseProductSku::getSkuCode, sku.getSkuCode());
        queryWrapper.eq(BaseProductSku::getIsDeleted, 0);
        BaseProductSku existingSku = skuMapper.selectOne(queryWrapper);
        if (existingSku == null) {
            return true;
        }
        return sku.getId() != null && existingSku.getId().equals(sku.getId());
    }

    /**
     * 生成笛卡尔积组合
     */
    private List<Map<String, String>> generateCombinations(Map<String, List<String>> specLists) {
        List<Map<String, String>> result = new ArrayList<>();
        List<String> keys = new ArrayList<>(specLists.keySet());
        
        if (keys.isEmpty()) {
            return result;
        }

        // 递归生成所有组合
        generateCombinationsRecursive(specLists, keys, 0, new java.util.LinkedHashMap<>(), result);
        return result;
    }

    /**
     * 递归生成组合
     */
    private void generateCombinationsRecursive(Map<String, List<String>> specLists, List<String> keys, 
                                               int index, Map<String, String> current, List<Map<String, String>> result) {
        if (index == keys.size()) {
            result.add(new java.util.LinkedHashMap<>(current));
            return;
        }

        String key = keys.get(index);
        List<String> values = specLists.get(key);
        for (String value : values) {
            current.put(key, value);
            generateCombinationsRecursive(specLists, keys, index + 1, current, result);
            current.remove(key);
        }
    }

    /**
     * 转为JSON字符串
     */
    private String toJsonString(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            sb.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
