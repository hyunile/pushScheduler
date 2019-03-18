package com.wooricard.scheduler.config;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.dkntech.d3f.commander.Commander;
import com.dkntech.d3f.task.HealthChecker;
import com.dkntech.d3f.task.ServerWatcher;
import com.dkntech.utils.Utils;
import com.wooricard.scheduler.Scheduler;
import com.wooricard.scheduler.common.Constants;

@Configuration
@ComponentScan(basePackages="com.wooricard.scheduler")
@MapperScan("com.wooricard.scheduler")
public class GeneralConfig {
	private static final Logger logger = LoggerFactory.getLogger(GeneralConfig.class);

	@Bean
	public ServerWatcher serverWatcher() {
		return new ServerWatcher();
	}
	
	@Bean
	public HealthChecker healthChecker() {
		return new HealthChecker();
	}
	
	@Bean
	public Commander commander() {
		return new Commander();
	}
	
	@Bean
	public Scheduler scheduler() {
		return new Scheduler();
	}	
	
	@Bean
	public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
		PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
		
		configurer.setFileEncoding("UTF-8");
		
		String filePath = Utils.getPropsFilePath(Constants.PROP_FILE_PREFIX_DATABASE);
		logger.info("propertyPlaceholderConfigurer : db properties file = " + filePath);
		
		Resource dbResource = new FileSystemResource(filePath);
		
		filePath = Utils.getPropsFilePath(Constants.PROP_FILE_PREFIX_DAEMON);
		logger.info("propertyPlaceholderConfigurer : daemon properties file = " + filePath);
		
		Resource daemonResource = new FileSystemResource(filePath);
		
		configurer.setLocations(dbResource, daemonResource);
		
		return configurer;
	}
}
