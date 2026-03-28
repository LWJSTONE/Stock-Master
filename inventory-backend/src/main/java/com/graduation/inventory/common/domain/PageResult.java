package com.graduation.inventory.common.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果类
 *
 * @param <T> 数据类型
 * @author graduation
 * @version 1.0.0
 */
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据列表
     */
    private List<T> rows;

    /**
     * 总记录数
     */
    private Long total;

    public PageResult() {
    }

    public PageResult(List<T> rows, Long total) {
        this.rows = rows;
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "rows=" + rows +
                ", total=" + total +
                '}';
    }
}
