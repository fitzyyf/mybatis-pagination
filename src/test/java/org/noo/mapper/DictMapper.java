package org.noo.mapper;

import org.noo.module.Dict;
import org.noo.dialect.model.Pagetag;

import java.util.List;

/**
 * <p>
 * .
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-05-08 下午1:19
 * @since JDK 1.5
 */
public interface DictMapper {

    List<Dict> findAllDict(Pagetag tag);
}
