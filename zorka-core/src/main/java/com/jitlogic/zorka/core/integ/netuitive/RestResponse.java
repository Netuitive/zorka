package com.jitlogic.zorka.core.integ.netuitive;

public class RestResponse {

    /**
     * Response status code.
     *
     */
    private int statusCode;

    /**
     * Content type (i.e. application/json).
     *
     */
    private String contentType;

    /**
     * Content encoding (i.e. gzip).
     *
     */
    private String encoding;

    /**
     * Response body.
     *
     */
    private String content;

    public RestResponse(int statusCode, String contentType, String encoding, String content) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.encoding = encoding;
        this.content = content;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getContent() {
        return content;
    }

    /**
     * Return true if response is successful (status = 2xx).
     *
     * @return
     */
    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }
}
