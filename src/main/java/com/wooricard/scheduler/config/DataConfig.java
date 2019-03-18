package com.wooricard.scheduler.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.dkntech.security.Security;
import com.dkntech.security.StringCryptor;
import com.dkntech.utils.Utils;
import com.wooricard.scheduler.common.Constants;

@Configuration
public class DataConfig {
	private static final Logger logger = LoggerFactory.getLogger(DataConfig.class);

	@Value("${fetcher.single.threads}")
	private int singleThreadCount;
	
	@Value("${fetcher.batch.threads}")
	private int batchThreadCount;
	
	@Value("${database.driver}")
	private String dbDriver;
	
	@Value("${database.url}")
	private String dbUrl;
	
	@Value("${database.user}")
	private String dbUser;
	
	@Value("${database.pwd}")
	private String dbPwd;
	
	@Value("${dbcp.initialSize}")
	private int initialSize;
	
	@Value("${dbcp.maxActive}")
	private int maxActive;

	@Value("${dbcp.maxWait}")
	private int maxWait;
	
	@Value("${dbcp.maxIdle}")
	private int maxIdle;

	@Value("${dbcp.minIdle}")
	private int minIdle;

	@Value("${dbcp.validation}")
	private String validationQuery;

	@Value("${encrypt.url}")
	private String encryptUrl;

	@Value("${encrypt.user}")
	private String encryptUser;

	@Value("${encrypt.pwd}")
	private String encryptPwd;

	@Bean
	public DataSource dataSource() {
		logger.debug("dataSource : DB driver = " + dbDriver);
		logger.debug("dataSource : DB url = " + dbUrl);
		logger.debug("dataSource : DB user = " + dbUser);
		logger.debug("dataSource : DB pwd = " + Utils.pwdToAsterisk(dbPwd));
		
		BasicDataSource ds = new BasicDataSource();
		
		if(Utils.isEmpty(dbDriver) || Utils.isEmpty(dbUrl) || Utils.isEmpty(dbUser) || Utils.isEmpty(dbPwd)) {
			logger.error("dataSource : Mandatory info missing.");
			return ds;
		}
		
		ds.setDriverClassName(dbDriver);
		
		StringCryptor cryptor = null;
		try {
			cryptor = new StringCryptor(Constants.ENC_SEED);
		} 
		catch (Exception e) {
			logger.error("dataSource : StringCryptor creation failed.", e);
		}
		
		boolean needEncrypt = false;
		String url = dbUrl, user = dbUser, pwd = dbPwd;
		
		if(url.startsWith(Security.ENC_HEADER)) {
			if(cryptor != null) {
				url = cryptor.decryptString(url.substring(Security.ENC_HEADER.length()));
			}
		}
		else if("Y".equalsIgnoreCase(encryptUrl)) {
			needEncrypt = true;
		}
		
		if(user.startsWith(Security.ENC_HEADER)) {
			if(cryptor != null) {
				user = cryptor.decryptString(user.substring(Security.ENC_HEADER.length()));
			}
		}
		else if("Y".equalsIgnoreCase(encryptUser)) {
			needEncrypt = true;
		}

		if(pwd.startsWith(Security.ENC_HEADER)) {
			if(cryptor != null) {
				pwd = cryptor.decryptString(pwd.substring(Security.ENC_HEADER.length()));
			}
		}
		else if("Y".equalsIgnoreCase(encryptPwd)) {
			needEncrypt = true;
		}
		
		ds.setUrl(url);
		ds.setUsername(user);
		ds.setPassword(pwd);
		
		ds.setInitialSize(initialSize);
		ds.setMaxTotal(maxActive);
		ds.setMaxIdle(maxIdle);
		ds.setMinIdle(minIdle);
		
		ds.setMaxWaitMillis(maxWait);
		
		ds.setTestOnBorrow(true);
		ds.setRemoveAbandonedOnBorrow(true);
		
		ds.setTestWhileIdle(true);
		ds.setMinEvictableIdleTimeMillis(-1);
		ds.setNumTestsPerEvictionRun(1);
		ds.setTimeBetweenEvictionRunsMillis(60000);
				
		if(! Utils.isEmpty(validationQuery)) {
			ds.setValidationQuery(validationQuery);
		}
		
		if(needEncrypt) {
			try {
				Security.encryptDbPropertiesFile(Constants.PROP_FILE_PREFIX_DATABASE, Constants.ENC_SEED,
					"database", Utils.ynToBool(encryptUrl), Utils.ynToBool(encryptUser), Utils.ynToBool(encryptPwd));
			} 
			catch (Exception e) {
				logger.error("dataSource : encryptDbPropertiesFile() failed.", e);
			}
		}
		
		return ds;
	}
	
	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		
		SqlSessionFactory factory = null;
		
		try {
			String path = System.getProperty("user.dir");

			logger.info("sqlSessionFactory : path = " + path);
			
			Resource[] res = new PathMatchingResourcePatternResolver().getResources("file:" + path + "/mybatis/mapper/*mapper.xml");
			sqlSessionFactoryBean.setMapperLocations(res);
			Resource conf = new PathMatchingResourcePatternResolver().getResource("file:" + path + "/mybatis/mybatis-config.xml"); 
			sqlSessionFactoryBean.setConfigLocation(conf);
			
			factory = sqlSessionFactoryBean.getObject();
		}
		catch(Exception e) {
			logger.error("sqlSessionFactory", e);
		}
		
		return factory;
	}
	
	@Bean
	public DataSourceTransactionManager transactionManager(DataSource dataSource) {
		DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
		return dataSourceTransactionManager;
	}

//	@Bean
//	public MapperFactoryBean<PushMapper> mapperFactoryBean(SqlSessionFactory sqlSessionFactory) {
//		MapperFactoryBean<PushMapper> mapper = new MapperFactoryBean<PushMapper>();
//		mapper.setSqlSessionFactory(sqlSessionFactory);
//		mapper.setMapperInterface(PushMapper.class);
//		
//		return mapper;
//	}
	
	@Bean
	TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		int count = singleThreadCount + batchThreadCount; 
		count += 2 + 2 + 5;		// fetchers + collectors + etc
		executor.setCorePoolSize(count);
		executor.setMaxPoolSize(count);
		executor.setThreadNamePrefix("scheduler");
		executor.initialize();
		
		return executor;
	}	
}
