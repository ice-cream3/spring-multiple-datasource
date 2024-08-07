package com.ice.mybatis.demo.config;//package com.ice.mybatis.demo.config;

import com.alibaba.druid.support.http.WebStatFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: DaoConfig
 * @Description:
 * @Author: ice
 * @Date: 2021/6/9 18:17
 */
@Configuration
@MapperScan(basePackages = {"com.ice.mybatis.demo.dao.sys"}, sqlSessionFactoryRef = "sysSqlSessionFactory")
public class SysDaoConfig {

    @Value("${sys.datasource.url}")
    private String url;

    @Value("${sys.datasource.drivername}")
    private String drivername;

    @Value("${sys.datasource.username}")
    private String username;

    @Value("${sys.datasource.password}")
    private String password;

    /*@Value("${spring.datasource.initialSize}")
    private int initialSize;

    @Value("${spring.datasource.minIdle}")
    private int minIdle;

    @Value("${spring.datasource.maxActive}")
    private int maxActive;

    @Value("${spring.datasource.maxWait}")
    private int maxWait;

    @Value("${spring.datasource.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.testOnReturn}")
    private boolean testOnReturn;

    @Value("${spring.datasource.testOnBorrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.validationQuery}")
    private String validationQuery;

    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.connection-init-sqls}")
    private List<String> connectionInitSqls;*/

    @Bean(name = "sysTransactionManager")
    public DataSourceTransactionManager transactionManager() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    @Bean(name = "sysSqlSessionFactory")
    public SqlSessionFactory sysSqlSessionFactory() {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource());
        // 配置映射文件目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            bean.setConfigLocation(resolver.getResource("classpath:mybatis.xml"));
            Resource[] mybatisRootMapperXml = resolver.getResources("classpath*:*Mapper.xml");
            Resource[] mybatisMapperXml = resolver.getResources("classpath*:/**/*Mapper.xml");
            bean.setMapperLocations(ArrayUtils.addAll(mybatisMapperXml, mybatisRootMapperXml));
            return bean.getObject();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    //@Bean(initMethod = "init", destroyMethod = "close")
    @Bean(name = "sysDataSource")
    public DruidDataSource dataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setDriverClassName(drivername);
        datasource.setUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);

        /*datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setValidationQuery(validationQuery);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setConnectionInitSqls(connectionInitSqls);*/
        //sql监控
        List<Filter> filters = new ArrayList<>();
        filters.add(statFilter());
        filters.add(wallFilter());
        datasource.setProxyFilters(filters);
        return datasource;
    }

    @Bean(name = "sysStatFilter")
    public StatFilter statFilter() {
        StatFilter statFilter = new StatFilter();
        // slowSqlMillis用来配置SQL慢的标准，执行时间超过slowSqlMillis的就是慢。
        statFilter.setLogSlowSql(true);
        // SQL合并配置
        statFilter.setMergeSql(true);
        // slowSqlMillis的缺省值为3000，也就是3秒。
        statFilter.setSlowSqlMillis(1000);
        return statFilter;
    }

    /**
     * sql防火墙
     * <p>
     * selectWhereAlwayTrueCheck true 检查SELECT语句的WHERE子句是否是一个永真条件
     * <p>
     * selectHavingAlwayTrueCheck true 检查SELECT语句的HAVING子句是否是一个永真条件
     * <p>
     * deleteWhereAlwayTrueCheck true 检查DELETE语句的WHERE子句是否是一个永真条件
     * <p>
     * deleteWhereNoneCheck false 检查DELETE语句是否无where条件，这是有风险的，但不是SQL注入类型的风险
     * <p>
     * updateWhereAlayTrueCheck true 检查UPDATE语句的WHERE子句是否是一个永真条件
     * <p>
     * updateWhereNoneCheck false 检查UPDATE语句是否无where条件，这是有风险的，但不是SQL注入类型的风险
     * <p>
     * conditionAndAlwayTrueAllow false 检查查询条件(WHERE/HAVING子句)中是否包含AND永真条件
     * <p>
     * conditionAndAlwayFalseAllow false 检查查询条件(WHERE/HAVING子句)中是否包含AND永假条件
     * <p>
     * conditionLikeTrueAllow true 检查查询条件(WHERE/HAVING子句)中是否包含LIKE永真条件
     *
     * @return
     */
    @Bean(name = "sysWallFilter")
    public WallFilter wallFilter() {
        WallFilter wallFilter = new WallFilter();
        // 对被认为是攻击的SQL进行LOG.error输出
        wallFilter.setLogViolation(true);
        // 对被认为是攻击的SQL抛出SQLExcepton
        wallFilter.setThrowException(true);
        WallConfig config = new WallConfig();
        // 允许执行多条SQL
        config.setMultiStatementAllow(true);
        // 不允许无where删除语句执行
        config.setDeleteWhereNoneCheck(true);
        // 不允许无where更新语句执行
        config.setUpdateWhereNoneCheck(true);
        wallFilter.setConfig(config);
        return wallFilter;
    }

    @Bean(name = "sysServletRegistrationBean")
    public ServletRegistrationBean DruidStatViewServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        // 白名单
        servletRegistrationBean.addInitParameter("allow", "127.0.0.1");
        // IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page
        //servletRegistrationBean.addInitParameter("deny","127.0.0.1");
        // 登录查看信息的账号密码
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "admin");
        // 是否能够重置数据
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }

    @Bean(name = "sysFilterRegistrationBean")
    public FilterRegistrationBean druidStatFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        // 添加过滤规则
        filterRegistrationBean.addUrlPatterns("/*");
        // 添加不需要忽略的格式信息
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }
}
