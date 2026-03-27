package com.graduation.inventory.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.inventory.business.entity.StockMain;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 实时库存Mapper接口
 *
 * @author graduation
 * @version 1.0.0
 */
@Mapper
public interface StockMainMapper extends BaseMapper<StockMain> {

    /**
     * 分页查询库存列表（联表查询）
     */
    @Select("<script>" +
            "SELECT s.id, s.warehouse_id, s.sku_id, s.quantity, s.frozen_qty, s.batch_no, s.position, " +
            "w.wh_name, k.sku_code, k.sku_name, k.safety_stock " +
            "FROM stock_main s " +
            "LEFT JOIN base_warehouse w ON s.warehouse_id = w.id " +
            "LEFT JOIN base_product_sku k ON s.sku_id = k.id " +
            "WHERE 1=1 " +
            "<if test='warehouseId != null'> AND s.warehouse_id = #{warehouseId} </if>" +
            "<if test='skuCode != null and skuCode != \"\"'> AND k.sku_code LIKE CONCAT('%', #{skuCode}, '%') </if>" +
            "<if test='skuName != null and skuName != \"\"'> AND k.sku_name LIKE CONCAT('%', #{skuName}, '%') </if>" +
            "ORDER BY s.update_time DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Map<String, Object>> selectStockList(@Param("offset") long offset, @Param("limit") long limit,
                                               @Param("warehouseId") Long warehouseId,
                                               @Param("skuCode") String skuCode, @Param("skuName") String skuName);

    /**
     * 查询库存列表总数
     */
    @Select("<script>" +
            "SELECT COUNT(1) FROM stock_main s " +
            "LEFT JOIN base_product_sku k ON s.sku_id = k.id " +
            "WHERE 1=1 " +
            "<if test='warehouseId != null'> AND s.warehouse_id = #{warehouseId} </if>" +
            "<if test='skuCode != null and skuCode != \"\"'> AND k.sku_code LIKE CONCAT('%', #{skuCode}, '%') </if>" +
            "<if test='skuName != null and skuName != \"\"'> AND k.sku_name LIKE CONCAT('%', #{skuName}, '%') </if>" +
            "</script>")
    Long selectStockListCount(@Param("warehouseId") Long warehouseId,
                              @Param("skuCode") String skuCode, @Param("skuName") String skuName);

    /**
     * 查询库存预警列表
     */
    @Select("SELECT s.id, s.warehouse_id, s.sku_id, s.quantity, s.frozen_qty, " +
            "w.wh_name, k.sku_code, k.sku_name, k.safety_stock " +
            "FROM stock_main s " +
            "LEFT JOIN base_warehouse w ON s.warehouse_id = w.id " +
            "LEFT JOIN base_product_sku k ON s.sku_id = k.id " +
            "WHERE s.quantity &lt;= k.safety_stock " +
            "AND k.safety_stock &gt; 0 " +
            "ORDER BY s.quantity ASC")
    List<Map<String, Object>> selectStockWarningList();
}
