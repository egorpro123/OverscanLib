package ru.overscan.lib.net;

import ru.overscan.lib.sys.ErrorCollector;

public class ServerAnswer {
	public static final int UNKNOWN_ERROR = 1; 
	public static final int NOT_FOUND = 2; 
	public static final int SUCCESS = 3; 
	public static final int UNAUTHORIZED = 4; 
	public static final int BAD_URL = 5; 
	public static final int NO_NETWORK = 6; 
	public final static String SERVER_ERROR_MESSAGE = "Неверный ответ сервера"; //i18n 
	public final static String UNAUTHORIZED_MESSAGE = "Необходима регистрация"; //i18n 
	public final static String NO_NETWORK_MESSAGE = "Отсутствует сетевое подключение"; //i18n 
	
	public int status; 
	public int originalStatus;
	public String message;

	public static ServerAnswer createApplicationError(String message, Throwable error){
		ErrorCollector.add(message, error);
		return internalCreateApplicationError();
	}
	
	public static ServerAnswer createApplicationError(Throwable error){
		ErrorCollector.add(error);
		return internalCreateApplicationError();
	}

	
	public static String getAppropriateMessage(int status){
		 switch (status) {
	         case NO_NETWORK: return NO_NETWORK_MESSAGE;
	         case UNAUTHORIZED: return UNAUTHORIZED_MESSAGE;
	         default: return null;
		 }
	}
	
	private static ServerAnswer internalCreateApplicationError(){
		ServerAnswer a = new ServerAnswer();
		a.status = ServerAnswer.UNKNOWN_ERROR;
		a.message = "Проблема в приложении"; // i18n
		return a;
	}

	public static ServerAnswer createServerError(String message, Throwable error){
		ErrorCollector.add(message, error);
		return internalCreateServerError();
	}
	
	public static ServerAnswer createServerError(Throwable error){
		ErrorCollector.add(error);
		return internalCreateServerError();
	}

	public static ServerAnswer createServerError(String message){
		ErrorCollector.add(message);
		return internalCreateServerError();
	}
	
	private static ServerAnswer internalCreateServerError(){
		ServerAnswer a = new ServerAnswer();
		a.status = ServerAnswer.UNKNOWN_ERROR;
		a.message = ServerAnswer.SERVER_ERROR_MESSAGE;
		return a;
	}

}
