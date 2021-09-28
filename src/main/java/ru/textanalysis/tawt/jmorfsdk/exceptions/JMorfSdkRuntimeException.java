package ru.textanalysis.tawt.jmorfsdk.exceptions;

import ru.textanalysis.tawt.ms.exceptions.TawtRuntimeException;

public class JMorfSdkRuntimeException extends TawtRuntimeException {

	public JMorfSdkRuntimeException() {
	}

	public JMorfSdkRuntimeException(String s) {
		super(s);
	}

	public JMorfSdkRuntimeException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public JMorfSdkRuntimeException(Throwable throwable) {
		super(throwable);
	}
}
