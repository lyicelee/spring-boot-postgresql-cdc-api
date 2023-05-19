package com.orizonsh.cdc.api.engine.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.orizonsh.cdc.api.exception.CDCApiException;

import io.debezium.connector.postgresql.PostgresConnector;

@Component
public final class PgsqlEngineConfig extends AbstractEngineConfig {

	private static final long serialVersionUID = 2121930554641157595L;

	@Value("${db-config.server.name}")
	private String dbServerName;

	@Value("${db-config.hostname}")
	private String dbHostname;

	@Value("${db-config.port}")
	private String dbPort;

	@Value("${db-config.dbname}")
	private String dbname;

	@Value("${db-config.user}")
	private String dbUser;

	@Value("${db-config.password}")
	private String dbPassword;

	/** 監視対象となるテーブルのリスト */
	@Value("${db-config.table.include.list}")
	private String tableIncludeList;

	/**
	 * CDC エンジンの設定情報を取得する。
	 *
	 * @return CDC エンジンの設定情報
	 * @throws CDCApiException
	 */
	public Properties getProps() throws CDCApiException {

		try {
			// 設定情報を初期化する
			final Properties props = initProps(PostgresConnector.class);

			props.setProperty("snapshot.mode", "never");

			props.setProperty("database.server.name", dbServerName);
			props.setProperty("database.hostname", dbHostname);
			props.setProperty("database.port", dbPort);
			props.setProperty("database.dbname", dbname);
			props.setProperty("database.user", dbUser);
			props.setProperty("database.password", dbPassword);

			props.setProperty("decimal.handling.mode", "double");

			props.setProperty("table.include.list", tableIncludeList);

			logEngineConfigValue(props);

			return props;
		} catch (Exception e) {
			throw new CDCApiException(e.getMessage(), e);
		}
	}
}
