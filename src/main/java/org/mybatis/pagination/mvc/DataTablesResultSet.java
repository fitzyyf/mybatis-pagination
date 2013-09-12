/*
 * Copyright © 2012-2013 mumu@yfyang. All Rights Reserved.
 */

package org.mybatis.pagination.mvc;

import java.util.Collections;
import java.util.List;

import org.mybatis.pagination.dto.PageMyBatis;

/**
 * <p>
 * .
 * </p>
 *
 * @author mumu@yfyang
 * @version 1.0 2013-09-05 10:48 PM
 * @since JDK 1.5
 */
public final class DataTablesResultSet<T> {
    private final int sEcho;
    private final long iTotalRecords;
    private final long iTotalDisplayRecords;
    private final List<T> aaData;

    /**
     * Instantiates a new Data tables result set.
     *
     * @param sEcho the pc
     * @param rs the rs
     */
    public DataTablesResultSet(int sEcho, PageMyBatis<T> rs) {
        this.sEcho = sEcho;
        this.aaData = rs;
        this.iTotalRecords = rs.getTotal();
        this.iTotalDisplayRecords = rs.getTotal();
    }

    /**
     * Gets echo.
     *
     * @return the echo
     */
    public int getsEcho() {
        return sEcho;
    }

    /**
     * Gets total records.
     *
     * @return the total records
     */
    public long getiTotalRecords() {
        return iTotalRecords;
    }

    /**
     * Gets total display records.
     *
     * @return the total display records
     */
    public long getiTotalDisplayRecords() {
        return iTotalDisplayRecords;
    }

    /**
     * Gets aa data.
     *
     * @return the aa data
     */
    public List<T> getAaData() {
        return Collections.unmodifiableList(aaData);
    }
}
