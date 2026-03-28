package com.graduation.inventory.system.controller;

import com.graduation.inventory.common.annotation.Log;
import com.graduation.inventory.common.domain.Result;
import com.graduation.inventory.common.enums.BusinessType;
import com.graduation.inventory.system.entity.SysMenu;
import com.graduation.inventory.system.entity.vo.MenuTreeVo;
import com.graduation.inventory.system.service.SysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单控制器
 *
 * @author graduation
 * @version 1.0.0
 */
@Api(tags = "菜单管理")
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    /**
     * 查询菜单列表
     *
     * @param menuName 菜单名称
     * @param status   状态
     * @return 菜单列表
     */
    @ApiOperation("查询菜单列表")
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public Result<List<SysMenu>> list(
            @ApiParam("菜单名称") @RequestParam(required = false) String menuName,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        SysMenu menu = new SysMenu();
        menu.setMenuName(menuName);
        menu.setStatus(status);
        List<SysMenu> menus = sysMenuService.selectMenuList(menu);
        return Result.success(menus);
    }

    /**
     * 获取菜单详情
     *
     * @param menuId 菜单ID
     * @return 菜单详情
     */
    @ApiOperation("获取菜单详情")
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping("/{menuId}")
    public Result<SysMenu> getInfo(@ApiParam("菜单ID") @PathVariable Long menuId) {
        return Result.success(sysMenuService.getById(menuId));
    }

    /**
     * 获取菜单树
     *
     * @return 菜单树
     */
    @ApiOperation("获取菜单树")
    @GetMapping("/tree")
    public Result<List<MenuTreeVo>> tree() {
        List<SysMenu> menus = sysMenuService.selectMenuList(new SysMenu());
        List<MenuTreeVo> tree = sysMenuService.buildMenuTree(menus);
        return Result.success(tree);
    }

    /**
     * 新增菜单
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @ApiOperation("新增菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @Log(title = "菜单管理", action = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody SysMenu menu) {
        if (!sysMenuService.checkMenuNameUnique(menu)) {
            return Result.error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        return sysMenuService.save(menu) ? Result.success() : Result.error("新增菜单失败");
    }

    /**
     * 修改菜单
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @ApiOperation("修改菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "菜单管理", action = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody SysMenu menu) {
        if (!sysMenuService.checkMenuNameUnique(menu)) {
            return Result.error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        if (menu.getId().equals(menu.getParentId())) {
            return Result.error("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        return sysMenuService.updateById(menu) ? Result.success() : Result.error("修改菜单失败");
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @ApiOperation("删除菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @Log(title = "菜单管理", action = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public Result<Void> remove(@ApiParam("菜单ID") @PathVariable Long menuId) {
        if (sysMenuService.hasChildByMenuId(menuId)) {
            return Result.error("存在子菜单,不允许删除");
        }
        return sysMenuService.removeById(menuId) ? Result.success() : Result.error("删除菜单失败");
    }
}
