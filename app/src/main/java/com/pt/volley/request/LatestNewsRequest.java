package com.pt.volley.request;

import com.android.volley.Request;
import com.google.gson.reflect.TypeToken;
import com.pt.network.HttpJsonRequest;
import com.pt.network.HttpParams;
import com.pt.network.ICallback;
import com.pt.volley.bean.LatestNewsBean;
import com.pt.volley.constant.Constant;

import java.lang.reflect.Type;

/**
 * @author: yorkzhang
 * @time: 16/7/21 17:57
 * @email: xtcqw13@126.com
 * @note:
 */
public class LatestNewsRequest extends HttpJsonRequest {

    public LatestNewsRequest(ICallback callback) {
        super(callback);
    }

    @Override
    public int getHttpMethod() {
        return Request.Method.GET;
    }

    @Override
    public Type getType() {
        return new TypeToken<LatestNewsBean>() {}.getType();
    }

    @Override
    public String getUrl() {
        return Constant.LATEST_NEWS;
    }

    @Override
    public HttpParams getHttpParams() {
        HttpParams params = new HttpParams();
        return params;
    }
}
