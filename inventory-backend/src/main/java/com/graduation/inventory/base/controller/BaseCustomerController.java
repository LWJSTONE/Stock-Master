package com.graduation.inventory.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.graduation.inventory.base.entity.BaseCustomer;
import com.graduation.inventory.base.service.BaseCustomerService;
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
 * 客户控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "客户管理")
@RestController
@RequestMapping("/base/customer")
@RequiredArgsConstructor
public class BaseCustomerController {

    private final BaseCustomerService customerService;

    /**
     * 分页查询客户列表
     */
    @ApiOperation("分页查询客户列表")
    @PreAuthorize("@ss.hasPermi('base:customer:list')")
    @GetMapping("/list")
    public Result<PageResult<BaseCustomer>> list(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("客户编码") @RequestParam(required = false) String custCode,
            @ApiParam("客户名称") @RequestParam(required = false) String custName) {
        Page<BaseCustomer> page = new Page<>(pageNum, pageSize);
        Page<BaseCustomer> result = customerService.selectCustomerPage(page, custCode, custName);
        return Result.success(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    /**
     * 获取客户详情
     */
    @ApiOperation("获取客户详情")
    @PreAuthorize("@ss.hasPermi('base:customer:query')")
    @GetMapping("/{id}")
    public Result<BaseCustomer> getInfo(@ApiParam("客户ID") @PathVariable Long id) {
        return Result.success(customerService.selectCustomerById(id));
    }

    /**
     * 查询所有客户
     */
    @ApiOperation("查询所有客户")
    @GetMapping("/listAll")
    public Result<List<BaseCustomer>> listAll() {
        return Result.success(customerService.selectAllCustomer());
    }

    /**
     * 新增客户
     */
    @ApiOperation("新增客户")
    @PreAuthorize("@ss.hasPermi('base:customer:add')")
    @Log(title = "客户管理", action = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody BaseCustomer customer) {
        return customerService.insertCustomer(customer) ? Result.success() : Result.error("新增客户失败");
    }

    /**
     * 修改客户
     */
    @ApiOperation("修改客户")
    @PreAuthorize("@ss.hasPermi('base:customer:edit')")
    @Log(title = "客户管理", action = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody BaseCustomer customer) {
        return customerService.updateCustomer(customer) ? Result.success() : Result.error("修改客户失败");
    }

    /**
     * 删除客户
     */
    @ApiOperation("删除客户")
    @PreAuthorize("@ss.hasPermi('base:customer:remove')")
    @Log(title = "客户管理", action = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> remove(@ApiParam("客户ID") @PathVariable Long id) {
        return customerService.deleteCustomerById(id) ? Result.success() : Result.error("删除客户失败");
    }

    /**
     * 批量删除客户
     */
    @ApiOperation("批量删除客户")
    @PreAuthorize("@ss.hasPermi('base:customer:remove')")
    @Log(title = "客户管理", action = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public Result<Void> removeBatch(@RequestBody List<Long> ids) {
        return customerService.deleteCustomerByIds(ids) ? Result.success() : Result.error("批量删除客户失败");
    }

    /**
     * 校验客户编码是否唯一
     */
    @ApiOperation("校验客户编码是否唯一")
    @GetMapping("/checkCustCodeUnique")
    public Result<Boolean> checkCustCodeUnique(
            @ApiParam("客户编码") @RequestParam String custCode,
            @ApiParam("客户ID") @RequestParam(required = false) Long id) {
        BaseCustomer customer = new BaseCustomer();
        customer.setCustCode(custCode);
        customer.setId(id);
        return Result.success(customerService.checkCustCodeUnique(customer));
    }
}
