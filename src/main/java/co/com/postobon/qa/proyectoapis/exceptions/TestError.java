package co.com.postobon.qa.proyectoapis.exceptions;

@SuppressWarnings("serial")
public class TestError extends AssertionError {

    public TestError(String message) {
        super(message);
    }

    public TestError(String message, Throwable cause) {
        super(message, cause);
    }
}
