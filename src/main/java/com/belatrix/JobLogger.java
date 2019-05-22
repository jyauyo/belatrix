package com.belatrix;

/**
 * 
Code Review / Refactoring exercise
Please review the following code snippet. 
Assume that all referenced assemblies have been properly included. 
The code is used to log different messages throughout an application. 
We want the ability to be able to log to a text file, the console and/or the database. 
Messages can be marked as message, warning or error. 
We also want the ability to selectively be able to choose what gets logged, 
such as to be able to log only errors or only errors and warnings. 
1.	If you were to review the following code, what feedback would you give? 
Please be specific and indicate any errors that would occur as well as 
other best practices and code refactoring that should be done. 
2.	Rewrite the code based on the feedback you provided in question 1. 
Please include unit tests on your code.

 * 
 * */

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobLogger {
	private static boolean logToFile;
	private static boolean logToConsole;
	private static boolean logMessage;
	private static boolean logWarning;
	private static boolean logError;
	private static boolean logToDatabase;

	private static Map<String, String> dbParams;
	private static Logger logger;

	public JobLogger(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam,
			boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, Map<String, String> dbParamsMap) {
		logger = Logger.getLogger("MyLog");
		
		logError = logErrorParam;
		logMessage = logMessageParam;
		logWarning = logWarningParam;
		
		logToDatabase = logToDatabaseParam;
		logToFile = logToFileParam;
		logToConsole = logToConsoleParam;
		
		dbParams = dbParamsMap;
	}

	public static void LogMessage(String messageText, boolean message, boolean warning, boolean error)
			throws Exception {
		messageText.trim();
		if (messageText == null || messageText.length() == 0) {
			return;
		}
		if (!logToConsole && !logToFile && !logToDatabase) {
			throw new Exception("Invalid configuration");
		}
		if ((!logError && !logMessage && !logWarning) || (!message && !warning && !error)) {
			throw new Exception("Error or Warning or Message must be specified");
		}

		String l = "";
		
		if (error && logError) {
			l = l + "error " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + " - " + messageText+"\n";
		}

		if (warning && logWarning) {
			l = l + "warning " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + " - " + messageText+"\n";
		}

		if (message && logMessage) {
			l = l + "info " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + " - " + messageText+"\n";
		}
		if (logToFile) {
			String pathLogFile = dbParams.get("logFileFolder") + "/logFile.txt";
			File logFile = new File(pathLogFile);
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			FileHandler fh = new FileHandler(logFile.getPath());
			logger.addHandler(fh);
			logger.log(Level.INFO, l);
		}
		if (logToConsole) {
			ConsoleHandler ch = new ConsoleHandler();
			logger.addHandler(ch);
			logger.log(Level.INFO, l);
		}
		if (logToDatabase) {
			int t = 1;// default info
			if (message && logMessage) {
				t = 1;
			}

			if (error && logError) {
				t = 2;
			}

			if (warning && logWarning) {
				t = 3;
			}
			Statement stmt = null;
			try {
				Connection connection = null;
				Properties connectionProps = new Properties();
				connectionProps.put("user", dbParams.get("userName"));
				connectionProps.put("password", dbParams.get("password"));

				// Faltaba Agregar dataBase
				connection = DriverManager
						.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName") + ":"
								+ dbParams.get("portNumber") + "/" + dbParams.get("dataBase"), connectionProps);
				stmt = connection.createStatement();
				// query sintaxis con error, decia log_values
				String query = "insert into Log Values('" + String.valueOf(message) + "', " + String.valueOf(t) + ")";
				stmt.executeUpdate(query);
				logger.log(Level.INFO, l);
			} catch (Exception ex) {
				logger.log(Level.SEVERE, l);
			} finally {
				if (stmt != null)
					stmt.close();
				logger.log(Level.INFO, l);
			}
		}
	}
}