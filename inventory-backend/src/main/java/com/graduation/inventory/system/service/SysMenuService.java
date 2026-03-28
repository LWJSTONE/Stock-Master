package com.graduation.inventory.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.graduation.inventory.system.entity.SysMenu;
import com.graduation.inventory.system.entity.vo.MenuTreeVo;
import com.graduation.inventory.system.entity.vo.RouterVo;

import java.util.List;

/**
 * 菜单服务接口
 * 
 * @author graduation
 * @version 1.0.0
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 查询菜单列表
     *
     * @param menu 菜单查询条件
     * @return 菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenusByUserId(Long userId);

    /**
     * 根据ID查询菜单
     *
     * @param menuId 菜单ID
     * @return 菜单对象
     */
    SysMenu selectMenuById(Long menuId);

    /**
     * 构建菜单树
     *
     * @param menus 菜单列表
     * @return 菜单树
     */
    List<MenuTreeVo> buildMenuTree(List<SysMenu> menus);

    /**
     * 构建路由树
     *
     * @param menus 菜单列表
     * @return 路由树
     */
    List<RouterVo> buildRouterTree(List<SysMenu> menus);

    /**
     * 新增菜单
     *
     * @param menu 菜单信息
     * @return 影响行数
     */
    int insertMenu(SysMenu menu);

    /**
     * 修改菜单
     *
     * @param menu 菜单信息
     * @return 影响行数
     */
    int updateMenu(SysMenu menu);

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 影响行数
     */
    int deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果（true唯一 false不唯一）
     */
    boolean checkMenuNameUnique(SysMenu menu);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果（true存在 false不存在）
     */
    boolean hasChildByMenuId(Long menuId);
}
