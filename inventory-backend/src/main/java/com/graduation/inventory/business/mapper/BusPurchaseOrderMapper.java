package com.graduation.inventory.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.inventory.business.entity.BusPurchaseOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 采购订单主表Mapper接口
 *
 * @author graduation
 * @version 1.0.0
 */
@Mapper
public interface BusPurchaseOrderMapper extends BaseMapper<BusPurchaseOrder> {

    /**
     * 分页查询采购订单列表（联表查询）
     */
    @Select("<script>" +
            "SELECT p.id, p.purchase_no, p.supplier_id, p.total_amount, p.status, " +
            "p.applicant_id, p.audit_time, p.remark, p.create_time, " +
            "s.sup_name " +
            "FROM bus_purchase_order p " +
            "LEFT JOIN base_supplier s ON p.supplier_id = s.id " +
            "WHERE p.is_deleted = 0 " +
            "<if test='purchaseNo != null and purchaseNo != \"\"'> AND p.purchase_no LIKE CONCAT('%', #{purchaseNo}, '%') </if>" +
            "<if test='supplierId != null'> AND p.supplier_id = #{supplierId} </if>" +
            "<if test='status != null'> AND p.status = #{status} </if>" +
            "ORDER BY p.create_time DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Map<String, Object>> selectPurchaseOrderList(@Param("offset") long offset, @Param("limit") long limit,
                                                       @Param("purchaseNo") String purchaseNo,
                                                       @Param("supplierId") Long supplierId,
                                                       @Param("status") Integer status);

    /**
     * 查询采购订单列表总数
     */
    @Select("<script>" +
            "SELECT COUNT(1) FROM bus_purchase_order p " +
            "WHERE p.is_deleted = 0 " +
            "<if test='purchaseNo != null and purchaseNo != \"\"'> AND p.purchase_no LIKE CONCAT('%', #{purchaseNo}, '%') </if>" +
            "<if test='supplierId != null'> AND p.supplier_id = #{supplierId} </if>" +
            "<if test='status != null'> AND p.status = #{status} </if>" +
            "</script>")
    Long selectPurchaseOrderListCount(@Param("purchaseNo") String purchaseNo,
                                      @Param("supplierId") Long supplierId,
                                      @Param("status") Integer status);
}
