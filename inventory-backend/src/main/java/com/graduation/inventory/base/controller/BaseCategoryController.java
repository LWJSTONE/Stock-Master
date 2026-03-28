package com.graduation.inventory.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.base.entity.BaseCategory;
import com.graduation.inventory.base.service.BaseCategoryService;
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
 * 商品分类控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "商品分类管理")
@RestController
@RequestMapping("/base/category")
@RequiredArgsConstructor
public class BaseCategoryController {

    private final BaseCategoryService categoryService;

    /**
     * 分页查询分类列表
     */
    @ApiOperation("分页查询分类列表")
    @PreAuthorize("@ss.hasPermi('base:product:list')")
    @GetMapping("/list")
    public Result<PageResult<BaseCategory>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("分类编码") @RequestParam(required = false) String categoryCode,
            @ApiParam("分类名称") @RequestParam(required = false) String categoryName,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        Page<BaseCategory> page = new Page<>(pageNum, pageSize);
        Page<BaseCategory> result = categoryService.selectCategoryPage(page, categoryCode, categoryName, status);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    /**
     * 获取分类树
     */
    @ApiOperation("获取分类树")
    @GetMapping("/tree")
    public Result<List<BaseCategory>> tree() {
        return Result.success(categoryService.getCategoryTree());
    }

    /**
     * 获取分类详情
     */
    @ApiOperation("获取分类详情")
    @PreAuthorize("@ss.hasPermi('base:product:query')")
    @GetMapping("/{id}")
    public Result<BaseCategory> getInfo(@ApiParam("分类ID") @PathVariable Long id) {
        return Result.success(categoryService.selectCategoryById(id));
    }

    /**
     * 新增分类
     */
    @ApiOperation("新增分类")
    @PreAuthorize("@ss.hasPermi('base:product:add')")
    @Log(title = "分类管理", action = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody BaseCategory category) {
        return categoryService.insertCategory(category) ? Result.success() : Result.error("新增分类失败");
    }

    /**
     * 修改分类
     */
    @ApiOperation("修改分类")
    @PreAuthorize("@ss.hasPermi('base:product:edit')")
    @Log(title = "分类管理", action = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody BaseCategory category) {
        return categoryService.updateCategory(category) ? Result.success() : Result.error("修改分类失败");
    }

    /**
     * 删除分类
     */
    @ApiOperation("删除分类")
    @PreAuthorize("@ss.hasPermi('base:product:remove')")
    @Log(title = "分类管理", action = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> remove(@ApiParam("分类ID") @PathVariable Long id) {
        if (categoryService.hasChildById(id)) {
            return Result.error("存在子分类，不允许删除");
        }
        return categoryService.deleteCategoryById(id) ? Result.success() : Result.error("删除分类失败");
    }

    /**
     * 批量删除分类
     */
    @ApiOperation("批量删除分类")
    @PreAuthorize("@ss.hasPermi('base:product:remove')")
    @Log(title = "分类管理", action = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public Result<Void> removeBatch(@RequestBody List<Long> ids) {
        return categoryService.deleteCategoryByIds(ids) ? Result.success() : Result.error("批量删除分类失败");
    }

    /**
     * 校验分类编码是否唯一
     */
    @ApiOperation("校验分类编码是否唯一")
    @GetMapping("/checkCategoryCodeUnique")
    public Result<Boolean> checkCategoryCodeUnique(
            @ApiParam("分类编码") @RequestParam String categoryCode,
            @ApiParam("分类ID") @RequestParam(required = false) Long id) {
        BaseCategory category = new BaseCategory();
        category.setCategoryCode(categoryCode);
        category.setId(id);
        return Result.success(categoryService.checkCategoryCodeUnique(category));
    }
}
