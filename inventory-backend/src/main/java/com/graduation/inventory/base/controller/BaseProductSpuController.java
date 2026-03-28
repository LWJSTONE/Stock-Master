package com.graduation.inventory.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.base.entity.BaseProductSku;
import com.graduation.inventory.base.entity.BaseProductSpu;
import com.graduation.inventory.base.service.BaseProductSkuService;
import com.graduation.inventory.base.service.BaseProductSpuService;
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

/**
 * 商品SPU控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "商品管理")
@RestController
@RequestMapping("/base/product")
@RequiredArgsConstructor
public class BaseProductSpuController {

    private final BaseProductSpuService spuService;
    private final BaseProductSkuService skuService;

    /**
     * 分页查询商品列表
     */
    @ApiOperation("分页查询商品列表")
    @PreAuthorize("@ss.hasPermi('base:product:list')")
    @GetMapping("/list")
    public Result<PageResult<BaseProductSpu>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("SPU编码") @RequestParam(required = false) String spuCode,
            @ApiParam("SPU名称") @RequestParam(required = false) String spuName,
            @ApiParam("分类ID") @RequestParam(required = false) Long categoryId,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<BaseProductSpu> page = new Page<>(pageNum, pageSize);
        Page<BaseProductSpu> result = spuService.selectSpuPage(page, spuCode, spuName, categoryId, status);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    /**
     * 获取商品详情
     */
    @ApiOperation("获取商品详情")
    @PreAuthorize("@ss.hasPermi('base:product:query')")
    @GetMapping("/{id}")
    public Result<BaseProductSpu> getInfo(@ApiParam("商品ID") @PathVariable Long id) {
        return Result.success(spuService.selectSpuById(id));
    }

    /**
     * 获取商品及其SKU列表
     */
    @ApiOperation("获取商品及其SKU列表")
    @PreAuthorize("@ss.hasPermi('base:product:query')")
    @GetMapping("/{id}/sku")
    public Result<BaseProductSpu> getSpuWithSku(@ApiParam("商品ID") @PathVariable Long id) {
        return Result.success(spuService.selectSpuWithSkuList(id));
    }

    /**
     * 获取商品的SKU列表
     */
    @ApiOperation("获取商品的SKU列表")
    @PreAuthorize("@ss.hasPermi('base:product:query')")
    @GetMapping("/{id}/skuList")
    public Result<List<BaseProductSku>> getSkuList(@ApiParam("商品ID") @PathVariable Long id) {
        return Result.success(skuService.selectSkuListBySpuId(id));
    }

    /**
     * 新增商品
     */
    @ApiOperation("新增商品")
    @PreAuthorize("@ss.hasPermi('base:product:add')")
    @Log(title = "商品管理", action = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody BaseProductSpu spu) {
        return spuService.insertSpu(spu) ? Result.success() : Result.error("新增商品失败");
    }

    /**
     * 修改商品
     */
    @ApiOperation("修改商品")
    @PreAuthorize("@ss.hasPermi('base:product:edit')")
    @Log(title = "商品管理", action = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody BaseProductSpu spu) {
        return spuService.updateSpu(spu) ? Result.success() : Result.error("修改商品失败");
    }

    /**
     * 删除商品
     */
    @ApiOperation("删除商品")
    @PreAuthorize("@ss.hasPermi('base:product:remove')")
    @Log(title = "商品管理", action = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> remove(@ApiParam("商品ID") @PathVariable Long id) {
        return spuService.deleteSpuById(id) ? Result.success() : Result.error("删除商品失败");
    }

    /**
     * 批量删除商品
     */
    @ApiOperation("批量删除商品")
    @PreAuthorize("@ss.hasPermi('base:product:remove')")
    @Log(title = "商品管理", action = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public Result<Void> removeBatch(@RequestBody List<Long> ids) {
        return spuService.deleteSpuByIds(ids) ? Result.success() : Result.error("批量删除商品失败");
    }

    /**
     * 校验商品编码是否唯一
     */
    @ApiOperation("校验商品编码是否唯一")
    @GetMapping("/checkSpuCodeUnique")
    public Result<Boolean> checkSpuCodeUnique(
            @ApiParam("商品编码") @RequestParam String spuCode,
            @ApiParam("商品ID") @RequestParam(required = false) Long id) {
        BaseProductSpu spu = new BaseProductSpu();
        spu.setSpuCode(spuCode);
        spu.setId(id);
        return Result.success(spuService.checkSpuCodeUnique(spu));
    }
}
