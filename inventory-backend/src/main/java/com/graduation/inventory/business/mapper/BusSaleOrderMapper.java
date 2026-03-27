package com.graduation.inventory.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.inventory.business.entity.BusSaleOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 销售订单主表Mapper接口
 *
 * @author graduation
 * @version 1.0.0
 */
@Mapper
public interface BusSaleOrderMapper extends BaseMapper<BusSaleOrder> {

    /**
     * 分页查询销售订单列表（联表查询）
     */
    @Select("<script>" +
            "SELECT o.id, o.sale_no, o.customer_id, o.total_amount, o.status, o.pay_status, " +
            "o.deliver_time, o.remark, o.create_time, " +
            "c.cust_name " +
            "FROM bus_sale_order o " +
            "LEFT JOIN base_customer c ON o.customer_id = c.id " +
            "WHERE o.is_deleted = 0 " +
            "<if test='saleNo != null and saleNo != \"\"'> AND o.sale_no LIKE CONCAT('%', #{saleNo}, '%') </if>" +
            "<if test='customerId != null'> AND o.customer_id = #{customerId} </if>" +
            "<if test='status != null'> AND o.status = #{status} </if>" +
            "ORDER BY o.create_time DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Map<String, Object>> selectSaleOrderList(@Param("offset") long offset, @Param("limit") long limit,
                                                   @Param("saleNo") String saleNo,
                                                   @Param("customerId") Long customerId,
                                                   @Param("status") Integer status);

    /**
     * 查询销售订单列表总数
     */
    @Select("<script>" +
            "SELECT COUNT(1) FROM bus_sale_order o " +
            "WHERE o.is_deleted = 0 " +
            "<if test='saleNo != null and saleNo != \"\"'> AND o.sale_no LIKE CONCAT('%', #{saleNo}, '%') </if>" +
            "<if test='customerId != null'> AND o.customer_id = #{customerId} </if>" +
            "<if test='status != null'> AND o.status = #{status} </if>" +
            "</script>")
    Long selectSaleOrderListCount(@Param("saleNo") String saleNo,
                                  @Param("customerId") Long customerId,
                                  @Param("status") Integer status);
}
