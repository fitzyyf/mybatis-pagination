package org.noo.module;

import org.noo.dialect.annotation.Paging;
import org.noo.dialect.page.Page;

/**
 * <p>
 * .
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-05-12 上午10:51
 * @since JDK 1.5
 */
@Paging(field = "pageVO")
public class TestFind {

    private Page pageVO;

    public Page getPageVO() {
        return pageVO;
    }

    public void setPageVO(Page pageVO) {
        this.pageVO = pageVO;
    }
}
