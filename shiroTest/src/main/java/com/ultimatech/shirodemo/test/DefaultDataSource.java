package com.ultimatech.shirodemo.test;

import org.logicalcobwebs.proxool.ProxoolDataSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

@Component("ds-default")
public class DefaultDataSource extends ProxoolDataSource {
	public DefaultDataSource() throws IOException {
		super();
		Properties conf = new Properties();
		conf.load(this.getClass().getResourceAsStream("/conf/dsConf.properties"));
		this.setDriver(conf.getProperty("driver"));
		this.setDriverUrl(conf.getProperty("driverUrl"));
		this.setUser(conf.getProperty("user"));
		this.setPassword(conf.getProperty("password"));
		this.setAlias("alias");
		this.setHouseKeepingSleepTime(new Integer(conf
				.getProperty("houseKeepingSleepTime")));
		this.setPrototypeCount(new Integer(conf.getProperty("prototypeCount")));
		this.setMaximumConnectionCount(new Integer(conf
				.getProperty("maximumConnectionCount")));
		this.setMinimumConnectionCount(new Integer(conf
				.getProperty("minimumConnectionCount")));
		this.setSimultaneousBuildThrottle(new Integer(conf
				.getProperty("simultaneousBuildThrottle")));
		this.setMaximumConnectionLifetime(new Integer(conf
				.getProperty("maximumConnectionLifetime")));
		this.setHouseKeepingTestSql(conf.getProperty("houseKeepingTestSql"));
		this.setMaximumActiveTime(Long.parseLong(conf.getProperty("maximumActiveTime")));
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}
}
