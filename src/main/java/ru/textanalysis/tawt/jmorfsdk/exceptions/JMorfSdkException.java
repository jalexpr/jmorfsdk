package ru.textanalysis.tawt.jmorfsdk.exceptions;

import ru.textanalysis.tawt.ms.exceptions.TawtException;

public class JMorfSdkException extends TawtException {
    public JMorfSdkException() {
    }

    public JMorfSdkException(String s) {
        super(s);
    }

    public JMorfSdkException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public JMorfSdkException(Throwable throwable) {
        super(throwable);
    }
}
