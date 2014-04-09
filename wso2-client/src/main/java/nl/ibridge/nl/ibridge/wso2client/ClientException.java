package nl.ibridge.nl.ibridge.wso2client;

public class ClientException extends Exception {

    private static final long serialVersionUID = -1322153500154000516L;

    public ClientException() {
    }

    public ClientException(String message)  {
        super(message);
    }

    public ClientException(String message, Throwable cause)  {
        super(message, cause);
    }

    public ClientException(Throwable cause)  {
        super(cause);
    }
}
