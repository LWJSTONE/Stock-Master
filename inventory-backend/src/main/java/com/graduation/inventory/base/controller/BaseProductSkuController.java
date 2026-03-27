package com.graduation.inventory.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.base.entity.BaseProductSku;
import com.graduation.inventory.base.service.BaseProductSkuService;
import com.graduation.inventory.common.annotation.Log;
import com.graduation.inventory.common.domain.PageResult;
import com.graduation.inventory.common.domain.Result;
import com.graduation.inventory.common.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品SKU控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "SKU管理")
@RestController
@RequestMapping("/base/sku")
@RequiredArgsConstructor
public class BaseProductSkuController {

    private final BaseProductSkuService skuService;

    /**
     * 分页查询SKU列表
     */
    @ApiOperation("分页查询SKU列表")
    @PreAuthorize("@ss.hasPermi('base:product:list')")
    @GetMapping("/list")
    public Result<PageResult<BaseProductSku>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("SKU编码") @RequestParam(required = false) String skuCode,
            @ApiParam("SKU名称") @RequestParam(required = false) String skuName,
            @ApiParam("SPU ID") @RequestParam(required = false) Long spuId) {
        Page<BaseProductSku> page = new Page<>(pageNum, pageSize);
        Page<BaseProductSku> result = skuService.selectSkuPage(page, skuCode, skuName, spuId);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    /**
     * 获取SKU详情
     */
    @ApiOperation("获取SKU详情")
    @PreAuthorize("@ss.hasPermi('base:product:query')")
    @GetMapping("/{id}")
    public Result<BaseProductSku> getInfo(@ApiParam("SKU ID") @PathVariable Long id) {
        return Result.success(skuService.selectSkuById(id));
    }

    /**
     * 新增SKU
     */
    @ApiOperation("新增SKU")
    @PreAuthorize("@ss.hasPermi('base:product:add')")
    @Log(title = "SKU管理", action = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody BaseProductSku sku) {
        return skuService.insertSku(sku) ? Result.success() : Result.error("新增SKU失败");
    }

    /**
     * 批量生成SKU（笛卡尔积）
     */
    @ApiOperation("批量生成SKU")
    @PreAuthorize("@ss.hasPermi('base:product:add')")
    @Log(title = "SKU管理", action = BusinessType.INSERT)
    @PostMapping("/batchGenerate/{spuId}")
    public Result<Void> batchGenerate(
            @ApiParam("SPU ID") @PathVariable Long spuId,
            @RequestBody Map<String, List<String>> specLists) {
        return skuService.batchGenerateSku(spuId, specLists) ? Result.success() : Result.error("批量生成SKU失败");
    }

    /**
     * 修改SKU
     */
    @ApiOperation("修改SKU")
    @PreAuthorize("@ss.hasPermi('base:product:edit')")
    @Log(title = "SKU管理", action = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody BaseProductSku sku) {
        return skuService.updateSku(sku) ? Result.success() : Result.error("修改SKU失败");
    }

    /**
     * 删除SKU
     */
    @ApiOperation("删除SKU")
    @PreAuthorize("@ss.hasPermi('base:product:remove')")
    @Log(title = "SKU管理", action = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> remove(@ApiParam("SKU ID") @PathVariable Long id) {
        return skuService.deleteSkuById(id) ? Result.success() : Result.error("删除SKU失败");
    }

    /**
     * 批量删除SKU
     */
    @ApiOperation("批量删除SKU")
    @PreAuthorize("@ss.hasPermi('base:product:remove')")
    @Log(title = "SKU管理", action = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public Result<Void> removeBatch(@RequestBody List<Long> ids) {
        return skuService.deleteSkuByIds(ids) ? Result.success() : Result.error("批量删除SKU失败");
    }

    /**
     * 校验SKU编码是否唯一
     */
    @ApiOperation("校验SKU编码是否唯一")
    @GetMapping("/checkSkuCodeUnique")
    public Result<Boolean> checkSkuCodeUnique(
            @ApiParam("SKU编码") @RequestParam String skuCode,
            @ApiParam("SKU ID") @RequestParam(required = false) Long id) {
        BaseProductSku sku = new BaseProductSku();
        sku.setSkuCode(skuCode);
        sku.setId(id);
        return Result.success(skuService.checkSkuCodeUnique(sku));
    }
}
