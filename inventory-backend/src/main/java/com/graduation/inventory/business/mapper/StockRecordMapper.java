package com.graduation.inventory.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.inventory.business.entity.StockRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 库存流水Mapper接口
 *
 * @author graduation
 * @version 1.0.0
 */
@Mapper
public interface StockRecordMapper extends BaseMapper<StockRecord> {

    /**
     * 分页查询库存流水列表（联表查询）
     */
    @Select("<script>" +
            "SELECT r.id, r.order_no, r.order_type, r.warehouse_id, r.sku_id, " +
            "r.change_qty, r.before_qty, r.after_qty, r.batch_no, r.remark, r.create_time, " +
            "w.wh_name, k.sku_code, k.sku_name " +
            "FROM stock_record r " +
            "LEFT JOIN base_warehouse w ON r.warehouse_id = w.id " +
            "LEFT JOIN base_product_sku k ON r.sku_id = k.id " +
            "WHERE 1=1 " +
            "<if test='warehouseId != null'> AND r.warehouse_id = #{warehouseId} </if>" +
            "<if test='skuId != null'> AND r.sku_id = #{skuId} </if>" +
            "<if test='orderType != null'> AND r.order_type = #{orderType} </if>" +
            "<if test='orderNo != null and orderNo != \"\"'> AND r.order_no LIKE CONCAT('%', #{orderNo}, '%') </if>" +
            "ORDER BY r.create_time DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Map<String, Object>> selectStockRecordList(@Param("offset") long offset, @Param("limit") long limit,
                                                     @Param("warehouseId") Long warehouseId,
                                                     @Param("skuId") Long skuId,
                                                     @Param("orderType") Integer orderType,
                                                     @Param("orderNo") String orderNo);

    /**
     * 查询库存流水列表总数
     */
    @Select("<script>" +
            "SELECT COUNT(1) FROM stock_record r " +
            "WHERE 1=1 " +
            "<if test='warehouseId != null'> AND r.warehouse_id = #{warehouseId} </if>" +
            "<if test='skuId != null'> AND r.sku_id = #{skuId} </if>" +
            "<if test='orderType != null'> AND r.order_type = #{orderType} </if>" +
            "<if test='orderNo != null and orderNo != \"\"'> AND r.order_no LIKE CONCAT('%', #{orderNo}, '%') </if>" +
            "</script>")
    Long selectStockRecordListCount(@Param("warehouseId") Long warehouseId,
                                    @Param("skuId") Long skuId,
                                    @Param("orderType") Integer orderType,
                                    @Param("orderNo") String orderNo);
}
