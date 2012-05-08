/*
 * Copyright (c) 2010-2011 NOO. All Rights Reserved.
 * [Id:PagingParameter.java  2011-11-18 下午12:55 poplar.yfyang ]
 */
package org.noo.dialect.model;

/**
 * <p>
 * 分页参数对象.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2011-11-18 下午12:55
 * @since JDK 1.5
 */
public class Pagetag implements RecordPage {
    private static final long serialVersionUID = 1208542536262699297L;


    /**
     * 分页大小
     * 默认显示10条
     */
    private int pagingSize = 10;
    /**
     * 总记录数
     */
    private int total;


    /**
     * 当前查询的索引位置，可以对应页面的当前页
     */
    private int leaf;



    @Override
    public int getTotal() {
        return total;
    }


    @Override
    public void setTotal(int total) {
        this.total = total;
    }


    @Override
    public int getPagingSize() {
        return pagingSize;
    }


    @Override
    public void setPagingSize(int pagingSize) {
        this.pagingSize = pagingSize;
    }



    @Override
    public int getLeaf() {
        return leaf;
    }


    @Override
    public void setLeaf(int leaf) {
        this.leaf = leaf;
    }
}
