package com.graduation.inventory.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.graduation.inventory.business.entity.BusStockCheck;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 库存盘点主表Mapper接口
 *
 * @author graduation
 * @version 1.0.0
 */
@Mapper
public interface BusStockCheckMapper extends BaseMapper<BusStockCheck> {

    /**
     * 分页查询盘点列表（联表查询）
     */
    @Select("<script>" +
            "SELECT c.id, c.check_no, c.warehouse_id, c.check_status, c.check_time, c.create_time, " +
            "w.wh_name " +
            "FROM bus_stock_check c " +
            "LEFT JOIN base_warehouse w ON c.warehouse_id = w.id " +
            "WHERE c.is_deleted = 0 " +
            "<if test='checkNo != null and checkNo != \"\"'> AND c.check_no LIKE CONCAT('%', #{checkNo}, '%') </if>" +
            "<if test='warehouseId != null'> AND c.warehouse_id = #{warehouseId} </if>" +
            "<if test='checkStatus != null'> AND c.check_status = #{checkStatus} </if>" +
            "ORDER BY c.create_time DESC " +
            "LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Map<String, Object>> selectStockCheckList(@Param("offset") long offset, @Param("limit") long limit,
                                                    @Param("checkNo") String checkNo,
                                                    @Param("warehouseId") Long warehouseId,
                                                    @Param("checkStatus") Integer checkStatus);

    /**
     * 查询盘点列表总数
     */
    @Select("<script>" +
            "SELECT COUNT(1) FROM bus_stock_check c " +
            "WHERE c.is_deleted = 0 " +
            "<if test='checkNo != null and checkNo != \"\"'> AND c.check_no LIKE CONCAT('%', #{checkNo}, '%') </if>" +
            "<if test='warehouseId != null'> AND c.warehouse_id = #{warehouseId} </if>" +
            "<if test='checkStatus != null'> AND c.check_status = #{checkStatus} </if>" +
            "</script>")
    Long selectStockCheckListCount(@Param("checkNo") String checkNo,
                                   @Param("warehouseId") Long warehouseId,
                                   @Param("checkStatus") Integer checkStatus);
}
