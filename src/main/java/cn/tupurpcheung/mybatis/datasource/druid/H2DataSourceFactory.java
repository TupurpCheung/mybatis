package cn.tupurpcheung.mybatis.datasource.druid;

import cn.tupurpcheung.mybatis.datasource.DataSourceFactory;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.util.Properties;

public class H2DataSourceFactory implements DataSourceFactory {

    private Properties properties;

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public DataSource getDataSource() {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();

        jdbcDataSource.setURL((String)properties.get("url"));
        jdbcDataSource.setUser((String)properties.get("user"));
        jdbcDataSource.setPassword((String)properties.get("password"));
        return jdbcDataSource;
    }
}
