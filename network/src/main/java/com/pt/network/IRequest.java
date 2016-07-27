package com.pt.network;

/**
 * @author: yorkzhang
 * @time: 16/7/18 15:41
 * @email: xtcqw13@126.com
 * @note:
 */
public interface IRequest {

    /**
     * @note send request
     */
    void send();

    /**
     * @note cancel request
     */
    void cancel();
}
