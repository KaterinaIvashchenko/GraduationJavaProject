package ru.ifmo.server;

/**
 * HTTP constants.
 */
public class Http {
    // Status codes
    public static final int SC_CONTINUE = 100;
    public static final int SC_OK = 200;
    public static final int SC_MULTIPLE_CHOICES = 300;
    public static final int SC_BAD_REQUEST = 400;
    public static final int SC_NOT_FOUND = 404;
    public static final int SC_SERVER_ERROR = 500;
    public static final int SC_NOT_IMPLEMENTED = 501;
    public static String[] statusNames = new String[600];
    static {
        statusNames[SC_CONTINUE] = "Continue";
        statusNames[SC_OK] = "OK";
        statusNames[SC_MULTIPLE_CHOICES] = "Multiple Choise";
        statusNames[SC_BAD_REQUEST] = "Bad Request";
        statusNames[SC_NOT_FOUND] = "Not Found";
        statusNames[SC_SERVER_ERROR] = "Internal Server Error";
        statusNames[SC_NOT_IMPLEMENTED] = "Not Implemented";
    }

    // Header names
    public static final String HEADER_NAME_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_NAME_CONTENT_LENGTH = "Content-Length";

    // Mime types
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String MIME_URL_ENCODED = "application/x-www-form-urlencoded";


    /** OK header that preceded rest response data. */
    public static final String OK_HEADER = "HTTP/1.0 200 OK\r\n\r\n";
}
