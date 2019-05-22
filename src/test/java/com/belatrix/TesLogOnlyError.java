package com.belatrix;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TesLogOnlyError {
	static Map<String, String> dbParamsMap = new HashMap<String, String>(8);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		Properties props = new Properties();
        InputStream in = TesLogOnlyError.class.getClassLoader().getResourceAsStream("configuracion.properties");
        props.load(in);
		dbParamsMap.put("userName", props.getProperty("db.userName"));
		dbParamsMap.put("password", props.getProperty("db.password"));
		dbParamsMap.put("dbms", props.getProperty("db.dbms"));
		dbParamsMap.put("serverName", props.getProperty("db.serverName"));
		dbParamsMap.put("dataBase", props.getProperty("db.dataBaseName"));
		dbParamsMap.put("portNumber", props.getProperty("db.portNumber"));
		dbParamsMap.put("logFileFolder", props.getProperty("file.logFolder"));
	}

	@Before
	public void setUp() throws Exception {
		boolean logToFileParam = false;
		boolean logToConsoleParam = true;
		boolean logToDatabaseParam = false;
		
		boolean logMessageParam = true;
		boolean logWarningParam = true;
		boolean logErrorParam = true;
		
		new JobLogger(logToFileParam, logToConsoleParam, logToDatabaseParam, logMessageParam, logWarningParam,
				logErrorParam, dbParamsMap);
	}

	@Test
	public void test() {
		try {
			String messageText = "Mensaje ";
			boolean info = false;
			boolean warning=false;
			boolean error=true;
			JobLogger.LogMessage(messageText, info, warning, error);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}