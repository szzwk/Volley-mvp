package com.pt.network;

/**
 * @author: yorkzhang
 * @time: 16/7/18 15:44
 * @email: xtcqw13@126.com
 * @note:
 */
public interface ICallback {
    /**
     * On success.
     *
     * @param response the response
     */
    void onSuccess(Object response);

    /**
     * On failure.
     *
     * @param error the error
     */
    void onFailure(NetworkException error);

    /**
     * The type Stub.
     */
    class Stub implements ICallback {
        @Override
        public void onSuccess(Object response) {

        }

        @Override
        public void onFailure(NetworkException error) {

        }
    }
}
