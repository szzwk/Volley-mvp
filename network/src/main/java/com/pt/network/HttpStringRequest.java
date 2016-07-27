package com.pt.network;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pt.network.context.AppContext;

import org.apache.http.NameValuePair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: yorkzhang
 * @time: 16/7/18 15:43
 * @email: xtcqw13@126.com
 * @note:
 */
public abstract class HttpStringRequest extends BasicRequest {

    private static final String TAG = HttpStringRequest.class.getSimpleName();

    public HttpStringRequest() {
        super();
    }

    public HttpStringRequest(ICallback callback) {
        super(callback);
    }

    private Map<String, String> getBodyMapParams() {
        Map<String, String> paramsMap = new HashMap<String, String>();
        if (getHttpParams() == null) {
            return paramsMap;
        }
        List<NameValuePair> params = getHttpParams().getBodyParams();
        for (int i = 0; i < params.size(); i++) {
            NameValuePair tmp = params.get(i);
            paramsMap.put(tmp.getName(), tmp.getValue());
        }
        Log.d(TAG, "body: " + paramsMap);
        return paramsMap;
    }

    protected Request getRequest() {
        String url = getUrl();
        Log.d(TAG, "url: " + url);

        return new StringRequest(getHttpMethod(), url, null, null) {
            Map<String, String> headers;

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                headers = response.headers;
                Log.d(TAG, "response header: " + headers);
                return super.parseNetworkResponse(response);
            }

            @Override
            protected void deliverResponse(String response) {
                onHeaderReceive(headers);
                Log.d(TAG, "response: " + response);

                try {
                    callback.onSuccess(response);
                } catch (Exception e) {
                    Toast.makeText(AppContext.getApplicationContext(),
                            "解析网络数据出现异常: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "解析网络数据出现异常: " + e.getMessage());
                    e.printStackTrace();

                    NetworkException networkException = VolleyErrorHelper.getNetworkException(AppContext.getApplicationContext());
                    callback.onFailure(networkException);
                }
            }

            @Override
            public void deliverError(VolleyError error) {
                Log.e(TAG, "VolleyError: " + error);
                NetworkException networkException = VolleyErrorHelper.getNetworkException(error, AppContext.getApplicationContext());
                callback.onFailure(networkException);
            }

            @Override
            protected Map<String, String> getParams() {
                return getBodyMapParams();
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
            public Priority getPriority() {
                return getRequestPriority();
            }
        };
    }
}
