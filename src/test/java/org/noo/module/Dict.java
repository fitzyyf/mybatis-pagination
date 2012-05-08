package org.noo.module;

/**
 * <p>
 * .
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-05-08 下午1:15
 * @since JDK 1.5
 */
public class Dict {

    private long id;
    private long creatortime;
    private String datasource;
    private int datatype;
    private String dictname;
    private String dictnumber;
    private boolean enable;
    private long renewtime;
    private int sort;

    public long getCreatortime() {
        return creatortime;
    }

    public void setCreatortime(long creatortime) {
        this.creatortime = creatortime;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public int getDatatype() {
        return datatype;
    }

    public void setDatatype(int datatype) {
        this.datatype = datatype;
    }

    public String getDictname() {
        return dictname;
    }

    public void setDictname(String dictname) {
        this.dictname = dictname;
    }

    public String getDictnumber() {
        return dictnumber;
    }

    public void setDictnumber(String dictnumber) {
        this.dictnumber = dictnumber;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public long getRenewtime() {
        return renewtime;
    }

    public void setRenewtime(long renewtime) {
        this.renewtime = renewtime;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
