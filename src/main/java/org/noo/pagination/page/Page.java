package org.noo.pagination.page;

import java.io.Serializable;

/**
 * <p>
 * 分页接口.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-05-08 下午12:53
 * @since JDK 1.5
 */
public interface Page extends Serializable {

    int getCurrentPage();

    boolean isNext();

    boolean isPrevious();

    int getPageEndRow();

    int getPageSize();

    int getPageStartRow();

    int getTotalPages();

    int getTotalRows();

    void setTotalPages(int i);

    void setCurrentPage(int i);

    void setNext(boolean b);

    void setPrevious(boolean b);

    void setPageEndRow(int i);

    void setPageSize(int i);

    void setPageStartRow(int i);

    void setTotalRows(int i);

    void init(int rows, int pageSize, int currentPage);
}
