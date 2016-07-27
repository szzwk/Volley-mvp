package com.pt.network;

/**
 * @author: yorkzhang
 * @time: 16/7/18 15:44
 * @email: xtcqw13@126.com
 * @note:
 */
public class NetworkException extends Exception {

    private int statusCode;

    /**
     * Instantiates a new Network error.
     *
     * @param statusCode the status code
     * @param msg        the msg
     */
    public NetworkException(int statusCode, String msg) {
        super(msg);
        this.statusCode = statusCode;
    }

    /**
     * Gets status code.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }
}
