package it.univaq.disim.mwt.teachify.ejb;

public class ManagerException extends RuntimeException  {



	private static final long serialVersionUID = 1L;

	public ManagerException() {
		super();
	}

	public ManagerException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ManagerException(String message) {
		super(message);
	}

	public ManagerException(Throwable cause) {
		super(cause);
	}

}
