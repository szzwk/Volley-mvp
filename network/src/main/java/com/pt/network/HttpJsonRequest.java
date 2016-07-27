package com.pt.network;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.google.gson.Gson;
import com.pt.network.context.AppContext;
import com.pt.network.utils.Util;

import org.apache.http.NameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: yorkzhang
 * @time: 16/7/18 15:43
 * @email: xtcqw13@126.com
 * @note:
 */
public abstract class HttpJsonRequest extends BasicRequest {

    private static final String TAG = "HttpJsonRequest";

    private HttpParams httpParams;

    public HttpJsonRequest() {
        super();
    }

    public HttpJsonRequest(ICallback callback) {
        super(callback);
    }

    /**
     * 用于支持同步请求
     */
    private RequestFuture mFuture;

    public HttpJsonRequest(RequestFuture future) {
        mFuture = future;
    }

    public abstract Type getType();

    private HttpParams getCacheHttpParams() {
        if (httpParams == null) {
            httpParams = getHttpParams();
        }
        return httpParams;
    }

    protected Map<String, String> getHeaderParams() {
        Map<String, String> header = new HashMap<String, String>();
        if (getCacheHttpParams() == null) {
            return header;
        }
        List<NameValuePair> params = getCacheHttpParams().getHeaderParams();
        for (int i = 0; i < params.size(); i++) {
            NameValuePair tmp = params.get(i);
            header.put(tmp.getName(), tmp.getValue());
        }
        return header;
    }

    protected String getBodyParams() {
        String paramsBody = "";
        if (getCacheHttpParams() == null) {
            return paramsBody;
        }
        List<NameValuePair> params = getCacheHttpParams().getBodyParams();
        for (int i = 0; i < params.size(); i++) {
            NameValuePair tmp = params.get(i);
            if (i == 0) {
                paramsBody = tmp.getName() + "=" + encode(tmp.getValue());
            } else {
                try {
                    paramsBody = paramsBody + "&" + tmp.getName() + "=" + encode(tmp.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return paramsBody;
    }


    protected String getBodyType() {
        return "application/x-www-form-urlencoded; charset=UTF-8";
    }

    private String encode(String value) {
        if (value == null) {
            return "";
        }
        try {
            Log.d(TAG, "encode value: " + value);
            return URLEncoder.encode(value, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 成功时的JSONObject返回,便于子类进行扩展
     *
     * @param response
     */
    protected void onDeliverResponseReceived(JSONObject response) {
    }

    ICache cacheListener;

    public interface ICache {
        void saveCache(String jsonString);

        void saveCacheEnable(boolean value);
    }

    public HttpJsonRequest setICache(ICache cacheListener) {
        this.cacheListener = cacheListener;
        return this;
    }

    protected Request getRequest() {
        String url = getUrl();
        Log.d(TAG, "url: " + url);

        String bodyParams = null;  //bodyParams这里为空，所以需要重写getBody方法，在此方法中添加每次不一样的UUID
        return new JsonObjectRequest(getHttpMethod(), url, bodyParams, mFuture, mFuture) {
            Object resp;
            Map<String, String> headers;
            String jsonString;

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    resp = new Gson().fromJson(jsonString, getType());
                    headers = response.headers;
                    Log.d(TAG, "response header: " + headers);

                    return super.parseNetworkResponse(response);
                } catch (UnsupportedEncodingException e) {
                    if (e != null) {
                        Log.e(TAG, "parseNetworkResponse(),url:" + getUrl());
                        Log.e(TAG, e.getMessage());
                    }
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(JSONObject response) {
                onHeaderReceive(headers);
                onDeliverResponseReceived(response);
                Log.d(TAG, "response: " + response);

                try {
                    String code = getCommonCode(resp);
                    if (handleCommonCodeOpt(code)) {
                        // 同步请求错误处理
                        if (mFuture != null) {
                            mFuture.onErrorResponse(new VolleyError(""));
                            return;
                        }
                        if (callback != null) {
                            callback.onFailure(new NetworkException(Util.convertToInt(code, 0), ""));
                        }
                        return;
                    }

                    // 同步请求成功处理
                    if (mFuture != null) {
                        mFuture.onResponse(resp);
                        return;
                    }

                    if (callback != null) {
                        callback.onSuccess(resp);
                        if (cacheListener != null) {
                            cacheListener.saveCache(jsonString);
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(AppContext.getApplicationContext(),
                            "解析网络数据出现异常: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "deliverResponse(),url:" + getUrl());
                    Log.e(TAG, "解析网络数据出现异常: " + e.getMessage());
                    e.printStackTrace();

                    // 同步请求错误处理
                    if (mFuture != null) {
                        mFuture.onErrorResponse(new VolleyError(e));
                        return;
                    }

                    NetworkException networkException = VolleyErrorHelper.getNetworkException(AppContext
                            .getApplicationContext());
                    if (callback != null) {
                        callback.onFailure(networkException);
                    }
                }
            }


            @Override
            public void deliverError(VolleyError error) {
                Log.e(TAG, "VolleyError: " + error);

                if (mFuture != null) {
                    mFuture.onErrorResponse(error);
                    return;
                }

                NetworkException networkException = VolleyErrorHelper.getNetworkException(error, AppContext.getApplicationContext());
                if (callback != null) {
                    callback.onFailure(networkException);
                }
            }

            @Override
            public String getBodyContentType() {
                return getBodyType();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = getHeaderParams();
                if (header.size() == 0) {
                    return super.getHeaders();
                } else {
                    header.putAll(super.getHeaders());
                    Log.d(TAG, "header: " + header);
                    return header;
                }
            }

            @Override
            public byte[] getBody() {
                //在这里每次添加不同的UUID
                String mRequestBody = getBodyParams();
                Log.d(TAG, "body: " + mRequestBody);

                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            mRequestBody, PROTOCOL_CHARSET);
                    return null;
                }
            }

            @Override
            public Priority getPriority() {
                return getRequestPriority();
            }
        };
    }

    public String getCommonCode(Object response) {
        if (response == null) {
            return "";
        }
        try {
            Field f = getField(response.getClass(), "CODE");
            if (f != null) {
                f.setAccessible(true);
                String code = (String) f.get(response);
                Log.d("HttpJsonRequet", "response(),code:" + code + ",url:" + getUrl());
                return code;

            }
        } catch (IllegalAccessException e) {
        }
        return "";
    }

    public static Field getField(Class clazz, String filedName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(filedName);
        } catch (NoSuchFieldException e) {
            Log.e("ReloginDialog", "getField cannot find DeclareField:" + filedName);
            try {
                field = clazz.getField(filedName);
            } catch (NoSuchFieldException e1) {
                Log.e("ReloginDialog", "getField cannot find Field:" + filedName);
            }
        }
        return field;
    }

    protected boolean handleCommonCodeOpt(String code) {
        if (codeOperator != null) {
            return codeOperator.onCommonCodeReceived(code);
        }
        return false;
    }
}
