# Mybatis数据库物理分页插件
支持Mysql、MSSQL、Oracle、MSSQL2005、Postgre SQL、DB2数据库。

## 使用方法
在Mybatis的配置文件中，增加这个插件
	
	 <plugins>
        <plugin interceptor="org.noo.dialect.interceptor.PaginationInterceptor">
              <property name="dbms" value="mysql"/>
              <property name="sqlPattern" value=".*findAll.*"/>
        </plugin>
    </plugins>

* 第一个属性`dbms`声明当前数据库的类型，插件会根据数据库类型生成对应的方言实现
	* 包含如下值:MYSQL,ORACLE,DB2,POSTGRE,SQL_SERVER,SQL_SERVER_2005
	* 分别对应的数据库为:Mysql数据库，Oracle数据库，Postgre SQL数据库，Sql Server 2005下的数据和2005以上的数据
* 第二个属性`sqlPattern`表示插件需要拦截的SQL ID，为正则表达式。
	* 例如`.*findAll.*` 表示拦截 包含 findAll 的查询sql
* 另外还有个扩展配置 `dialectClass`，支持自定义的方言实现，需要实现`org.noo.dialect.dialect.Dialect` 接口
	* **注意：如果同时配置了 `dbms`和 `dialectClass` ，那么插件默认为以 `dbms` 为准，依赖其`dbms`来生产方言实现** 

### Sql Mapper配置
示例
	
	<select id="findAllDict" parameterType="org.noo.dialect.model.Pagetag"
            resultType="org.noo.module.Dict">
        select
          ID,CREATORTIME,DATASOURCE,DATATYPE,DICTNAME,DICTNUMBER,ENABLE,RENEWTIME,SORT
         from CD_DICT ORDER BY SORT
    </select>
 需要注意的是：`parameterType`是插件内部的一个分页实体，也可以为其他的分页实体，需要实现`org.noo.dialect.page.RecordPage` 接口。
 另外，如果 `parameterType` 非 `RecordPage`的实现，而是其他的实体，那么具体用法为：
 		
 		@Paging(field="page1")
 		public class BusinessObject{
 			private RecordPage page1;
 			
 			//... get set ...
 		}
 上面的`BusinessObject`中有个`page1`的属性，它为RecordPage的实现，通过注解`Paging`来指定它为分页参数信息(field指定)。

# Spring例子
TODO