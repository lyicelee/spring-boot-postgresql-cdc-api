package com.orizonsh.cdc.api.engine;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.orizonsh.cdc.api.engine.callback.CDCEngineCompletionCallback;
import com.orizonsh.cdc.api.engine.callback.CDCEngineConnectorCallback;
import com.orizonsh.cdc.api.engine.config.MySqlEngineConfig;
import com.orizonsh.cdc.api.engine.config.OracleEngineConfig;
import com.orizonsh.cdc.api.engine.config.PgsqlEngineConfig;
import com.orizonsh.cdc.api.engine.constant.CDCEngineConstant;
import com.orizonsh.cdc.api.exception.CDCApiCoreException;
import com.orizonsh.cdc.api.exception.CDCEngineException;
import com.orizonsh.cdc.api.utils.ApplicationContextUtils;
import com.orizonsh.cdc.api.utils.FileUtils;

import io.debezium.engine.DebeziumEngine;

public abstract class AbstractCDCEngine<T> {

	/** Log API */
	final Logger log = LogManager.getLogger(this.getClass());

	@Autowired
	private ApplicationContextUtils contextUtils;

	@Value("${cdc-engine-config.db.type}")
	private String dbType;

	private Properties engineConfigProps = null;

	private CDCEngineConnectorCallback connectorCallback = null;

	private CDCEngineCompletionCallback completCallback = null;

	/** Debezium Engine */
	DebeziumEngine<T> engine = null;

	/** Executor Service */
	ExecutorService executor = null;

	/**
	 * CDCエンジンを起動する。
	 *
	 * @throws CDCApiException
	 */
	public final void start() throws CDCEngineException {

		if (null == engine) {

			log.info("CDC Engineを起動します。");

			// CDCエンジンを作成する。
			createEngine();
			createOffsetStorageFile();
			createSchemaHistoryFile();

			executor = Executors.newSingleThreadExecutor();
			executor.execute(engine);

			log.info("CDC Engineが起動できました。");

		} else {
			log.warn("CDC Engineが既に起動しました。");
		}
	}

	/**
	 * CDCエンジンを停止する。
	 *
	 * @throws CDCApiException
	 */
	public final void stop() throws CDCEngineException {

		try {
			if (null != engine) {
				log.info("CDC Engineを停止します。");
				engine.close();
				executor.shutdown();
				engine = null;
				connectorCallback = null;
				completCallback = null;
				log.info("CDC Engineが停止できました。");
			}
		} catch (Exception e) {
			throw new CDCEngineException(e.getMessage(), e);
		}
	}

	/**
	 * CDCエンジンを作成する。
	 * @throws CDCApiException
	 */
	void createEngine() throws CDCEngineException {
		// createEngine()を実装してください。
		throw new NotImplementedException("createEngine()を実装してください。");
	}

	/**
	 * CDCエンジンの設定情報を取得する。
	 *
	 * @return CDCエンジンの設定情報
	 * @throws CDCApiCoreException
	 *
	 * @throws CDCApiException
	 */
	final Properties getEngineConfig() throws CDCEngineException {

		try {
			if (null == engineConfigProps) {
				log.debug("[cdc-engine]DB type>>>>>{}", dbType);
				switch(dbType) {
					case CDCEngineConstant.DB_TYPE_PGSQL:
						engineConfigProps = contextUtils.getBean(PgsqlEngineConfig.class).getProps();
						break;
					case CDCEngineConstant.DB_TYPE_ORACLE:
						engineConfigProps = contextUtils.getBean(OracleEngineConfig.class).getProps();
						break;
					case CDCEngineConstant.DB_TYPE_MYSQL:
						engineConfigProps = contextUtils.getBean(MySqlEngineConfig.class).getProps();
						break;
					default:
						throw new CDCEngineException("対象外DB種類：" + dbType);
				}
			}
			return engineConfigProps;
		} catch (CDCApiCoreException e) {
			throw new CDCEngineException(e);
		}
	}

	final CDCEngineConnectorCallback getConnectorCallback() {
		if (null == connectorCallback) connectorCallback = new CDCEngineConnectorCallback();
		return connectorCallback;
	}

	final CDCEngineCompletionCallback getCompletCallback() {
		if (null == completCallback) completCallback = new CDCEngineCompletionCallback();
		return completCallback;
	}

	private void createOffsetStorageFile() throws CDCEngineException {
		try {
			FileUtils.createFile(getEngineConfig().getProperty("offset.storage.file.filename"));
		} catch (Exception e) {
			throw new CDCEngineException("ファイルの作成が失敗しました。", e);
		}
	}

	private void createSchemaHistoryFile() throws CDCEngineException {
		try {
			FileUtils.createFile(getEngineConfig().getProperty("schema.history.internal.file.filename"));
		} catch (Exception e) {
			throw new CDCEngineException("ファイルの作成が失敗しました。", e);
		}
	}
}
