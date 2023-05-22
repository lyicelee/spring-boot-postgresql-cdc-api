package com.orizonsh.cdc.api.engine.config;

import java.util.Properties;

import org.springframework.stereotype.Component;

import com.orizonsh.cdc.api.exception.CDCApiCoreException;
import com.orizonsh.cdc.api.exception.CDCEngineException;

import io.debezium.connector.postgresql.PostgresConnector;

@Component
public final class PgsqlEngineConfig extends AbstractEngineConfig {

	private static final long serialVersionUID = 2121930554641157595L;

	/**
	 * CDC エンジンの設定情報を取得する。
	 *
	 * @return CDC エンジンの設定情報
	 * @throws CDCApiCoreException
	 */
	public Properties getProps() throws CDCEngineException {

		try {
			// 設定情報を初期化する
			final Properties props = initProps(PostgresConnector.class);

			props.setProperty("database.server.name", getEnvProperty("db-config.server.name"));

			props.setProperty("database.hostname", getEnvProperty("db-config.hostname"));
			props.setProperty("database.port", getEnvProperty("db-config.port"));
			props.setProperty("database.dbname", getEnvProperty("db-config.dbname"));
			props.setProperty("database.user", getEnvProperty("db-config.user"));
			props.setProperty("database.password", getEnvProperty("db-config.password"));

			props.setProperty("table.include.list", getEnvProperty("db-config.table.include.list"));

			props.setProperty("plugin.name", "decoderbufs");

			props.setProperty("include.schema.changes", "false");

			props.setProperty("decimal.handling.mode", "double");

			logEngineConfigValue(props);

			return props;
		} catch (Exception e) {
			throw new CDCEngineException(e.getMessage(), e);
		}
	}
}
