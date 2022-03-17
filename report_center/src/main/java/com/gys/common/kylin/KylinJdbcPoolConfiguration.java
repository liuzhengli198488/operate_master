package com.gys.common.kylin;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

@Slf4j
@Configuration
@Component
public class KylinJdbcPoolConfiguration implements BeanFactoryPostProcessor, EnvironmentAware {
    private static final String LOCAL_CONFIG_FILE = "kylin.properties";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private ConfigurableEnvironment environment;

    @Value("${kylin.decrypt}")
    private boolean decrypt = false;

    private final static String prefixName = "kylin";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        log.info("kylin配置文件:"+"kylin-"+environment.getActiveProfiles()[0]+".properties");
        String kylinProperties = "kylin-"+environment.getActiveProfiles()[0]+".properties";
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(kylinProperties);
        Properties prop = new Properties();
        KylinSqlProperties properties = new KylinSqlProperties();
        try {
            prop.load(new InputStreamReader(inputStream, DEFAULT_ENCODING));
            properties.setUserName(prop.getProperty("UserName"));
            properties.setPassword(prop.getProperty("Password"));
            properties.setConnectionUrl(prop.getProperty("ConnectionUrl"));
            properties.setSSL(prop.getProperty("SSL"));
            if (StringUtils.isNotEmpty(prop.getProperty("POOL_SIZE"))){
                properties.setPoolSize(Integer.valueOf(prop.getProperty("POOL_SIZE")));
            }
        }catch (IOException e){
            log.error("读取Kylin配置文件异常",e);
        }
        properties.setDecrypt(decrypt);
        prop.clear();
        createDataSourceBean(beanFactory, properties);
    }

    public void createDataSourceBean(ConfigurableListableBeanFactory beanFactory, KylinSqlProperties sqlProperties) {
        DataSource baseDataSource = new KylinDataSource(sqlProperties);
        register(beanFactory, new JdbcTemplate(baseDataSource), prefixName + "JdbcTemplateFactory", prefixName);
    }

    private void register(ConfigurableListableBeanFactory beanFactory, Object bean, String name, String alias) {
        beanFactory.registerSingleton(name, bean);
        if (!beanFactory.containsSingleton(alias)) {
            beanFactory.registerAlias(name, alias);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

}
