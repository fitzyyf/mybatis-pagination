/*
 * Copyright © 2012-2013 mumu@yfyang. All Rights Reserved.
 */

package org.mybatis.pagination.dto.datatables;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * <p>
 * jQuery DataTables's PagingCriteria.
 * </p>
 *
 * @author mumu@yfyang
 * @version 1.0 2013-09-05 10:37 PM
 * @since JDK 1.5
 */
public final class PagingCriteria {
    public static final int DEFAULT_SIZE = 10;
    /** start display */
    private final Integer displayStart;
    /** disaplaySize */
    private final Integer displaySize;
    /** sort fields */
    private final List<SortField> sortFields;
    /** pageNumber */
    private final Integer pageNumber;

    /**
     * Instantiates a new Paging criteria.
     *
     * @param displayStart the display start
     * @param displaySize  the display size
     * @param pageNumber   the page number
     * @param sortFields   the sort fields
     */
    public PagingCriteria(Integer displayStart, Integer displaySize,
                          Integer pageNumber, List<SortField> sortFields) {
        this.displayStart = displayStart;
        this.displaySize = displaySize;
        this.pageNumber = pageNumber;
        this.sortFields = sortFields;
    }

    /**
     * Gets display start.
     *
     * @return the display start
     */
    public Integer getDisplayStart() {
        return displayStart;
    }

    /**
     * Gets display size.
     *
     * @return the display size
     */
    public Integer getDisplaySize() {
        return displaySize;
    }

    /**
     * Gets sort fields.
     *
     * @return the sort fields
     */
    public List<SortField> getSortFields() {
        if(this.sortFields == null){
            return Lists.newArrayListWithCapacity(0);
        }
        return Collections.unmodifiableList(sortFields);
    }

    /**
     * Gets page number.
     *
     * @return the page number
     */
    public Integer getPageNumber() {
        return pageNumber;
    }

}
