/*
 * Copyright © 2012-2013 mumu@yfyang. All Rights Reserved.
 */

package org.mybatis.pagination.mvc;

import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.mybatis.pagination.dto.datatables.PagingCriteria;
import org.mybatis.pagination.dto.datatables.SearchField;
import org.mybatis.pagination.dto.datatables.SortField;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * resolver paramArgument with annotation.
 * </p>
 *
 * @author mumu @yfyang
 * @version 1.0 2013-09-05 10:44 PM
 * @see
 * @see
 * @since JDK 1.5
 */
public class TableParamArgumentResolver implements WebArgumentResolver {
    /**
     * Information for DataTables to use for rendering.
     */
    private static final String S_ECHO = "sEcho";
    /**
     * Display start point in the current data set.
     */
    private static final String I_DISPLAY_START = "iDisplayStart";
    /**
     * Number of records that the table can display in the current draw.
     * It is expected that the number of records returned will be equal to this number, unless the server has fewer records to return.
     */
    private static final String I_DISPLAY_LENGTH = "iDisplayLength";
    /**
     * Number of columns to sort on
     */
    private static final String I_SORTING_COLS = "iSortingCols";
    /**
     * Column being sorted on (you will need to decode this number for your database)
     */
    private static final String I_SORT_COLS = "iSortCol_";
    /**
     * Direction to be sorted - "desc" or "asc".
     */
    private static final String S_SORT_DIR = "sSortDir_";
    /**
     * The value specified by mDataProp for each column.
     * This can be useful for ensuring that the processing of data is independent from the order of the columns.
     */
    private static final String S_DATA_PROP = "mDataProp_";
    /**
     * Individual column filter
     */
    private static final String S_SEACHE_VAL = "sSearch_";
    /**
     * True if the individual column filter should be treated as a regular expression for advanced filtering, false if not
     */
    private static final String B_REGEX = "bRegex_";
    /**
     * Indicator for if a column is flagged as sortable or not on the client-side
     */
    private static final String B_SORTTABLE = "bSortable_";
    /**
     * Global search field value
     */
    private static final String S_SEARCH = "sSearch";
    /**
     * Number of columns being displayed (useful for getting individual column search info)
     */
    private static final String I_COLUMNS = "iColumns";
    /**
     * Hump Split colum name
     */
    private final boolean humpSplit;

    /**
     * Instantiates a new Table param argument resolver.
     *
     * @param humpSplit the hump split
     */
    public TableParamArgumentResolver(boolean humpSplit) {
        this.humpSplit = humpSplit;
    }


    @Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
        TableParam tableParamAnnotation = methodParameter.getParameterAnnotation(TableParam.class);
        if (tableParamAnnotation != null) {
            HttpServletRequest httpRequest = (HttpServletRequest) webRequest.getNativeRequest();

            String sEcho = httpRequest.getParameter(S_ECHO);
            String sDisplayStart = httpRequest.getParameter(I_DISPLAY_START);
            String sDisplayLength = httpRequest.getParameter(I_DISPLAY_LENGTH);

            int iEcho = Integer.parseInt(sEcho);
            int iDisplayStart = Integer.parseInt(sDisplayStart);
            int iDisplayLength = Integer.parseInt(sDisplayLength);

            List<SortField> sortFields = getSortFileds(httpRequest);
            List<SearchField> searchFields = getSearchParam(httpRequest);

            return PagingCriteria.createCriteriaWithAllParamter(iDisplayStart, iDisplayLength, iEcho, sortFields, searchFields);
        }

        return WebArgumentResolver.UNRESOLVED;
    }

    /**
     * Gets sort fileds.
     *
     * @param httpRequest the http request
     * @return the sort fileds
     */
    private List<SortField> getSortFileds(final HttpServletRequest httpRequest) {

        String sSortingCols = httpRequest.getParameter(I_SORTING_COLS);

        int iSortingCols = Integer.parseInt(sSortingCols);
        final List<SortField> sortFields = Lists.newArrayListWithCapacity(iSortingCols);
        String sSortDir;
        String sColName;
        String sSortCol;
        for (int colCount = 0; colCount < iSortingCols; colCount++) {
            sSortCol = httpRequest.getParameter(I_SORT_COLS + colCount);
            sSortDir = httpRequest.getParameter(S_SORT_DIR + colCount);
            sColName = httpRequest.getParameter(S_DATA_PROP + sSortCol);
            sColName = humpSplit
                    ? CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, sColName)
                    : sColName;
            sortFields.add(new SortField(sColName, sSortDir));
        }
        return sortFields;
    }

    /**
     * Gets search param.
     *
     * @param httpRequest the http request
     * @return the search param
     */
    private List<SearchField> getSearchParam(final HttpServletRequest httpRequest) {
        int iColumns = Integer.valueOf(httpRequest.getParameter(I_COLUMNS));
        final List<SearchField> searchFields = Lists.newArrayListWithCapacity(iColumns);
        boolean regex;
        boolean searchable;
        String searchValue;
        String sColName;
        final String sSearch = httpRequest.getParameter(S_SEARCH);
        for (int col = 0; col < iColumns; col++) {
            searchValue = httpRequest.getParameter(S_SEACHE_VAL + col);
            sColName = httpRequest.getParameter(S_DATA_PROP + col);
            sColName = humpSplit
                    ? CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, sColName)
                    : sColName;
            if (!Strings.isNullOrEmpty(searchValue)) {
                regex = Boolean.valueOf(httpRequest.getParameter(B_REGEX + col));
                searchable = Boolean.valueOf(httpRequest.getParameter(B_SORTTABLE + col));
                searchFields.add(new SearchField(sColName, regex, searchable, searchValue));
            } else if (!Strings.isNullOrEmpty(sSearch)) {
                searchFields.add(new SearchField(sColName, false, false, sSearch));
            }
        }
        return searchFields;
    }

}
