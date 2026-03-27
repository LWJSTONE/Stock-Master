package com.graduation.inventory.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.graduation.inventory.common.utils.ServletUtil;
import com.graduation.inventory.common.utils.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MyBatis-Plus自动填充处理器
 *
 * @author graduation
 * @version 1.0.0
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    private static final Logger log = LoggerFactory.getLogger(MyMetaObjectHandler.class);

    /**
     * 插入时自动填充
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");

        // 填充创建时间
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());

        // 填充更新时间
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());

        // 填充创建人
        String createBy = getCurrentUsername();
        if (StringUtils.isNotBlank(createBy)) {
            this.strictInsertFill(metaObject, "createBy", String.class, createBy);
        }

        // 填充更新人
        if (StringUtils.isNotBlank(createBy)) {
            this.strictInsertFill(metaObject, "updateBy", String.class, createBy);
        }
    }

    /**
     * 更新时自动填充
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");

        // 填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());

        // 填充更新人
        String updateBy = getCurrentUsername();
        if (StringUtils.isNotBlank(updateBy)) {
            this.strictUpdateFill(metaObject, "updateBy", String.class, updateBy);
        }
    }

    /**
     * 获取当前用户名
     * 从Security上下文或Session中获取
     *
     * @return 当前用户名
     */
    private String getCurrentUsername() {
        try {
            // 尝试从Spring Security上下文获取用户名
            // 由于项目暂时排除了Security自动配置，这里先从Session获取
            // 后续配置Security后可以改为从SecurityContextHolder获取

            // 从Session获取用户信息
            // Object user = ServletUtil.getSession().getAttribute("user");
            // if (user instanceof UserDetails) {
            //     return ((UserDetails) user).getUsername();
            // }
            // if (user instanceof String) {
            //     return (String) user;
            // }

            // 暂时返回系统用户
            return "system";
        } catch (Exception e) {
            log.warn("获取当前用户名失败: {}", e.getMessage());
            return "system";
        }
    }
}
