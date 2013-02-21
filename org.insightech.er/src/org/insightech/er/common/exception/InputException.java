package org.insightech.er.common.exception;

public final class InputException extends Exception {

	private static final long serialVersionUID = -6325812774566059357L;

	private final String[] args;
	
	public InputException(String message) {
		this(message, null);
	}

	public InputException(String message, String[] args) {
		super(message);
		
		this.args = args;
	}
	
	public String[] getArgs() {
		return this.args;
	}

}
