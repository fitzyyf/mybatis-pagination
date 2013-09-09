/*
 * Copyright © 2012-2013 mumu@yfyang. All Rights Reserved.
 */

package org.mybatis.pagination.mvc;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Lists;
import org.mybatis.pagination.dto.datatables.PagingCriteria;
import org.mybatis.pagination.dto.datatables.SortField;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * <p>
 * resolver paramArgument with annotation.
 * </p>
 *
 * @author mumu@yfyang
 * @version 1.0 2013-09-05 10:44 PM
 * @see
 * @see
 * @since JDK 1.5
 */
public class TableParamArgumentResolver implements WebArgumentResolver {
    private static final String S_ECHO = "sEcho";
    private static final String I_DISPLAY_START = "iDisplayStart";
    private static final String I_DISPLAY_LENGTH = "iDisplayLength";
    private static final String I_SORTING_COLS = "iSortingCols";
    private static final String I_SORT_COLS = "iSortCol_";
    private static final String S_SORT_DIR = "sSortDir_";
    private static final String S_DATA_PROP = "mDataProp_";

    @Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
        TableParam tableParamAnnotation = methodParameter.getParameterAnnotation(TableParam.class);
        if (tableParamAnnotation != null) {
            HttpServletRequest httpRequest = (HttpServletRequest) webRequest.getNativeRequest();

            String sEcho = httpRequest.getParameter(S_ECHO);
            String sDisplayStart = httpRequest.getParameter(I_DISPLAY_START);
            String sDisplayLength = httpRequest.getParameter(I_DISPLAY_LENGTH);
            String sSortingCols = httpRequest.getParameter(I_SORTING_COLS);

            Integer iEcho = Integer.parseInt(sEcho);
            Integer iDisplayStart = Integer.parseInt(sDisplayStart);
            Integer iDisplayLength = Integer.parseInt(sDisplayLength);
            Integer iSortingCols = Integer.parseInt(sSortingCols);

            List<SortField> sortFields = Lists.newArrayList();
            for (int colCount = 0; colCount < iSortingCols; colCount++) {
                String sSortCol = httpRequest.getParameter(I_SORT_COLS + colCount);
                String sSortDir = httpRequest.getParameter(S_SORT_DIR + colCount);
                String sColName = httpRequest.getParameter(S_DATA_PROP + sSortCol);
                sortFields.add(new SortField(sColName, sSortDir));
            }

            return new PagingCriteria(iDisplayStart, iDisplayLength, iEcho, sortFields);
        }

        return WebArgumentResolver.UNRESOLVED;
    }
}
