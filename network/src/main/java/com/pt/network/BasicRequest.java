package com.pt.network;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: yorkzhang
 * @time: 16/7/18 17:32
 * @email: xtcqw13@126.com
 * @note:
 */
public abstract class BasicRequest implements IRequest {

    private static final String TAG = BasicRequest.class.getSimpleName();
    protected int TIME_OUT_MS = 20 * 1000;
    protected int RETRY_TIMES = 0;
    protected float BACKOFF_MULT = 1.0f;

    protected ICallback callback;
    protected boolean mShouldCache = false;

    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    public BasicRequest() {
    }

    public BasicRequest(ICallback callback) {
        this.callback = callback;
    }

    public int getHttpMethod() {
        return Request.Method.POST;
    }

    protected void setRetryPolicy(int timeoutMS, int retryTimes, float backoffMult) {
        this.TIME_OUT_MS = timeoutMS;
        this.RETRY_TIMES = retryTimes;
        this.BACKOFF_MULT = backoffMult;
    }

    protected RetryPolicy getRetryPolicy() {
        return new DefaultRetryPolicy(TIME_OUT_MS, RETRY_TIMES, BACKOFF_MULT);
    }

    protected String getTag() {
        Class<?> type = (Class<?>) getClass();
        return type.getSimpleName();
    }

    protected Map<String, String> getHeaderParams() {
        Map<String, String> header = new HashMap<String, String>();
        if (getHttpParams() == null) {
            return header;
        }
        List<NameValuePair> params = getHttpParams().getHeaderParams();
        for (int i = 0; i < params.size(); i++) {
            NameValuePair tmp = params.get(i);
            header.put(tmp.getName(), tmp.getValue());
        }
        return header;
    }

    protected Priority getPriority() {
        return Priority.NORMAL;
    }

    protected Request.Priority getRequestPriority() {
        switch (getPriority()) {
            case IMMEDIATE:
                return Request.Priority.IMMEDIATE;
            case HIGH:
                return Request.Priority.HIGH;
            case NORMAL:
                return Request.Priority.NORMAL;
            case LOW:
                return Request.Priority.LOW;
            default:
                return Request.Priority.NORMAL;
        }
    }

    protected void onHeaderReceive(Map<String, String> header) {
        String cookies = "";
        CookieStore cookieStore = new BasicCookieStore();

        Set<String> set = header.keySet();
        for (String key : set) {
            if (key.startsWith("Set-Cookie")) {
                String cookie = header.get(key);
                if (cookie == null) {
                    continue;
                }
                if (cookies.length() == 0) {
                    cookies = cookie.split(";")[0];
                } else {
                    cookies = cookies + "; " + cookie.split(";")[0];
                }

                String namevalue = cookie.split(";")[0];
                if (namevalue.contains("=")) {
                    String name = namevalue.substring(0, namevalue.indexOf("="));
                    String value = namevalue.substring(namevalue.indexOf("=") + 1);
                    BasicClientCookie clientCookie = new BasicClientCookie(name, value);
                    try {
                        String pathvalue = cookie.split(";")[1];
                        if (pathvalue.trim().startsWith("path")) {
                            String path = pathvalue.substring(pathvalue.indexOf("=") + 1);
                            clientCookie.setPath(path);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cookieStore.addCookie(clientCookie);
                }
            }
        }

        //save cookies
        //saveCookies();
    }

    protected abstract Request getRequest();

    public abstract String getUrl();

    public abstract HttpParams getHttpParams();

    public void setShouldCache(boolean shouldCache) {
        this.mShouldCache = shouldCache;
    }

    protected static IDealCodeOperator codeOperator;

    public static void setDealCodeOperator(IDealCodeOperator operator) {
        BasicRequest.codeOperator = operator;
    }

    public interface IDealCodeOperator {
        boolean onCommonCodeReceived(String code);
    }

    @Override
    public void send() {
        Request request = getRequest();
        request.setShouldCache(mShouldCache);
        request.setRetryPolicy(getRetryPolicy());
        Log.d(TAG, "mShouldCache: " + mShouldCache + " retryPolicy:" + getRetryPolicy());
        NetworkManager.getInstance().addRequest(request, getTag());
    }

    @Override
    public void cancel() {
        NetworkManager.getInstance().cancelAllRequests(getTag());
    }
}
