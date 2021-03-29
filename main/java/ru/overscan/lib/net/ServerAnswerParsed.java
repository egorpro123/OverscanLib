package ru.overscan.lib.net;

public class ServerAnswerParsed<T> {
	public ServerAnswer answer;
	public T rec;
	
	public ServerAnswerParsed() {
		answer = new ServerAnswer();
	}

	public ServerAnswerParsed(int status) {
		answer = new ServerAnswer();
		answer.status = status;
		answer.message = ServerAnswer.getAppropriateMessage(status);
	}

	public boolean success() {
        return answer != null && answer.status == ServerAnswer.SUCCESS;
    }
}
