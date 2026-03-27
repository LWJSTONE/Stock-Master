package com.graduation.inventory.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.graduation.inventory.common.exception.ServiceException;
import com.graduation.inventory.common.utils.StringUtils;
import com.graduation.inventory.system.entity.SysMenu;
import com.graduation.inventory.system.entity.vo.MenuTreeVo;
import com.graduation.inventory.system.entity.vo.RouterMeta;
import com.graduation.inventory.system.entity.vo.RouterVo;
import com.graduation.inventory.system.mapper.SysMenuMapper;
import com.graduation.inventory.system.service.SysMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务实现
 * 
 * @author graduation
 * @version 1.0.0
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private static final Logger log = LoggerFactory.getLogger(SysMenuServiceImpl.class);

    /**
     * 菜单根节点ID
     */
    private static final Long ROOT_MENU_ID = 0L;

    @Autowired
    private SysMenuMapper menuMapper;

    /**
     * 查询菜单列表
     *
     * @param menu 菜单查询条件
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();

        // 根据菜单名称模糊查询
        if (StringUtils.isNotBlank(menu.getMenuName())) {
            queryWrapper.like(SysMenu::getMenuName, menu.getMenuName());
        }
        // 根据状态精确查询
        if (StringUtils.isNotBlank(menu.getStatus())) {
            queryWrapper.eq(SysMenu::getStatus, menu.getStatus());
        }
        // 根据可见性查询
        if (StringUtils.isNotBlank(menu.getVisible())) {
            queryWrapper.eq(SysMenu::getVisible, menu.getVisible());
        }
        // 按父ID和排序号排序
        queryWrapper.orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum);

        return menuMapper.selectList(queryWrapper);
    }

    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenusByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return menuMapper.selectMenusByUserId(userId);
    }

    /**
     * 根据ID查询菜单
     *
     * @param menuId 菜单ID
     * @return 菜单对象
     */
    @Override
    public SysMenu selectMenuById(Long menuId) {
        if (menuId == null) {
            return null;
        }
        return menuMapper.selectById(menuId);
    }

    /**
     * 构建菜单树
     *
     * @param menus 菜单列表
     * @return 菜单树
     */
    @Override
    public List<MenuTreeVo> buildMenuTree(List<SysMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            return new ArrayList<>();
        }

        // 转换为MenuTreeVo列表
        List<MenuTreeVo> menuTreeVoList = menus.stream().map(menu -> {
            MenuTreeVo vo = new MenuTreeVo();
            BeanUtils.copyProperties(menu, vo);
            return vo;
        }).collect(Collectors.toList());

        // 构建树形结构
        return buildTree(menuTreeVoList, ROOT_MENU_ID);
    }

    /**
     * 构建路由树
     *
     * @param menus 菜单列表
     * @return 路由树
     */
    @Override
    public List<RouterVo> buildRouterTree(List<SysMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            return new ArrayList<>();
        }

        // 过滤出顶级菜单（父ID为0）
        List<SysMenu> rootMenus = menus.stream()
                .filter(menu -> ROOT_MENU_ID.equals(menu.getParentId()))
                .collect(Collectors.toList());

        // 构建路由树
        List<RouterVo> routers = new ArrayList<>();
        for (SysMenu menu : rootMenus) {
            RouterVo router = buildRouter(menu, menus);
            routers.add(router);
        }

        return routers;
    }

    /**
     * 新增菜单
     *
     * @param menu 菜单信息
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertMenu(SysMenu menu) {
        // 校验菜单名称唯一性
        if (!checkMenuNameUnique(menu)) {
            throw new ServiceException("同一级别下菜单名称已存在");
        }

        // 默认状态为正常
        if (StringUtils.isBlank(menu.getStatus())) {
            menu.setStatus("0");
        }
        // 默认显示
        if (StringUtils.isBlank(menu.getVisible())) {
            menu.setVisible("0");
        }

        int result = menuMapper.insert(menu);
        log.info("新增菜单成功, 菜单名称: {}, 菜单ID: {}", menu.getMenuName(), menu.getId());

        return result;
    }

    /**
     * 修改菜单
     *
     * @param menu 菜单信息
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateMenu(SysMenu menu) {
        if (menu.getId() == null) {
            throw new ServiceException("菜单ID不能为空");
        }

        // 校验菜单名称唯一性（同级下）
        if (StringUtils.isNotBlank(menu.getMenuName())) {
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysMenu::getMenuName, menu.getMenuName());
            queryWrapper.eq(SysMenu::getParentId, menu.getParentId() != null ? menu.getParentId() : ROOT_MENU_ID);
            queryWrapper.ne(SysMenu::getId, menu.getId());
            if (menuMapper.selectCount(queryWrapper) > 0) {
                throw new ServiceException("同一级别下菜单名称已存在");
            }
        }

        // 不能将父菜单设置为自己或自己的子菜单
        if (menu.getParentId() != null && menu.getParentId().equals(menu.getId())) {
            throw new ServiceException("上级菜单不能选择自己");
        }

        int result = menuMapper.updateById(menu);
        log.info("修改菜单成功, 菜单ID: {}", menu.getId());

        return result;
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteMenuById(Long menuId) {
        if (menuId == null) {
            throw new ServiceException("菜单ID不能为空");
        }

        // 检查是否存在子菜单
        if (hasChildByMenuId(menuId)) {
            throw new ServiceException("存在子菜单，不允许删除");
        }

        int result = menuMapper.deleteById(menuId);
        log.info("删除菜单成功, 菜单ID: {}", menuId);

        return result;
    }

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果（true唯一 false不唯一）
     */
    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        if (StringUtils.isBlank(menu.getMenuName())) {
            return false;
        }

        Long parentId = menu.getParentId() != null ? menu.getParentId() : ROOT_MENU_ID;

        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getMenuName, menu.getMenuName());
        queryWrapper.eq(SysMenu::getParentId, parentId);

        SysMenu existingMenu = menuMapper.selectOne(queryWrapper);
        if (existingMenu == null) {
            return true;
        }
        // 修改时，排除自己
        if (menu.getId() != null && existingMenu.getId().equals(menu.getId())) {
            return true;
        }

        return false;
    }

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果（true存在 false不存在）
     */
    @Override
    public boolean hasChildByMenuId(Long menuId) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getParentId, menuId);
        return menuMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 构建树形结构
     *
     * @param menuTreeVoList 菜单树VO列表
     * @param parentId       父ID
     * @return 树形结构
     */
    private List<MenuTreeVo> buildTree(List<MenuTreeVo> menuTreeVoList, Long parentId) {
        List<MenuTreeVo> returnList = new ArrayList<>();
        for (Iterator<MenuTreeVo> iterator = menuTreeVoList.iterator(); iterator.hasNext(); ) {
            MenuTreeVo vo = iterator.next();
            if (parentId.equals(vo.getParentId())) {
                recursionFn(menuTreeVoList, vo);
                returnList.add(vo);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list 菜单列表
     * @param vo   当前菜单
     */
    private void recursionFn(List<MenuTreeVo> list, MenuTreeVo vo) {
        List<MenuTreeVo> childList = getChildList(list, vo);
        vo.setChildren(childList);
        for (MenuTreeVo child : childList) {
            if (hasChild(list, child)) {
                recursionFn(list, child);
            }
        }
    }

    /**
     * 得到子节点列表
     *
     * @param list 菜单列表
     * @param vo   当前菜单
     * @return 子节点列表
     */
    private List<MenuTreeVo> getChildList(List<MenuTreeVo> list, MenuTreeVo vo) {
        List<MenuTreeVo> childList = new ArrayList<>();
        for (MenuTreeVo item : list) {
            if (vo.getId().equals(item.getParentId())) {
                childList.add(item);
            }
        }
        return childList;
    }

    /**
     * 判断是否有子节点
     *
     * @param list 菜单列表
     * @param vo   当前菜单
     * @return 是否有子节点
     */
    private boolean hasChild(List<MenuTreeVo> list, MenuTreeVo vo) {
        return getChildList(list, vo).size() > 0;
    }

    /**
     * 构建路由对象
     *
     * @param menu  当前菜单
     * @param menus 所有菜单
     * @return 路由对象
     */
    private RouterVo buildRouter(SysMenu menu, List<SysMenu> menus) {
        RouterVo router = new RouterVo();
        router.setName(menu.getMenuName());
        router.setPath(menu.getPath());
        router.setComponent(menu.getComponent());

        // 构建元信息
        RouterMeta meta = new RouterMeta(menu.getMenuName(), menu.getIcon());
        router.setMeta(meta);

        // 获取子菜单
        List<SysMenu> childMenus = menus.stream()
                .filter(m -> menu.getId().equals(m.getParentId()))
                .collect(Collectors.toList());

        if (!childMenus.isEmpty()) {
            List<RouterVo> children = new ArrayList<>();
            for (SysMenu child : childMenus) {
                RouterVo childRouter = buildRouter(child, menus);
                children.add(childRouter);
            }
            router.setChildren(children);
        }

        return router;
    }
}
