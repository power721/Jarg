package cn.har01d.tool.jarg;

public class ParseException extends RuntimeException {

    public static final int UNKNOWN_OPTION = 1;
    public static final int COMMAND_REQUIRED = 3;
    public static final int OPTION_VAL_REQUIRED = 4;
    public static final int ARG_REQUIRED = 5;
    public static final int CONSOLE_ACCESS = 6;

    private final int code;
    private Object data;

    public ParseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ParseException(int code, Object data, String message) {
        super(message);
        this.data = data;
        this.code = code;
    }

    public ParseException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ParseException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }

}
