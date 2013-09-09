# Mybatis数据库物理分页插件
支持Mysql、MSSQL、Oracle、MSSQL2005、Postgre SQL、DB2数据库。

## 使用方法
在Mybatis的配置文件中，增加这个插件
	
	 <plugins>
        <plugin interceptor="org.mybatis.pagination.PaginationInterceptor">
              <property name="dbms" value="mysql"/>
              <property name="sqlRegex" value=".*findAll.*"/>
        </plugin>
    </plugins>

* 第一个属性 `dbms`，方言类型，目前支持 MYSQL\MSSQL\ORACLE\MSSQL2005等
* 第二个属性`sqlRegex`表示插件需要拦截的SQL ID，为正则表达式。
	* 例如`.*findAll.*` 表示拦截 包含 findAll 的查询sql

### Sql Mapper配置
示例
	
	<select id="findAllDict"
            resultType="org.noo.module.Dict">
        SELECT
          ID,CREATORTIME,DATASOURCE,DATATYPE,DICTNAME,DICTNUMBER,ENABLE,RENEWTIME,SORT
         from CD_DICT ORDER BY SORT
    </select>

# Spring例子

@see [config](https://github.com/yfyang/mybatis-pagination/blob/master/src/test/resources/spring/test-context.xml) And [TestCase](https://github.com/yfyang/mybatis-pagination/tree/master/src/test/java/org/mybatis/pagination/service)