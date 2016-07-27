package com.pt.network;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * @author: yorkzhang
 * @time: 16/7/18 15:43
 * @email: xtcqw13@126.com
 * @note:
 */
public class HttpParams {

    private ArrayList<NameValuePair> headerParams = new ArrayList<NameValuePair>();
    private ArrayList<NameValuePair> bodyParams = new ArrayList<NameValuePair>();

    /**
     * Add header param.
     *
     * @param key   the key
     * @param value the value
     */
    public void addHeaderParam(String key, String value) {
        headerParams.add(new BasicNameValuePair(key, value));
    }


    /**
     * Add body param.
     *
     * @param key   the key
     * @param value the value
     */
    public void addBodyParam(String key, String value) {
        removeIfContainKey(bodyParams, key);
        bodyParams.add(new BasicNameValuePair(key, value));
    }


    private int removeIfContainKey(ArrayList<NameValuePair> params, String key) {
        if (params == null || params.isEmpty()) {
            return -1;
        }
        if (key == null || key.length() == 0) {
            return -1;
        }
        for (int i = 0; i < params.size(); i++) {
            NameValuePair valuePair = params.get(i);
            if (key.equals(valuePair.getName())) {
                params.remove(i);
                return i;
            }
        }
        return -1;
    }


    /**
     * remove header param.
     *
     * @param key the key
     */
    public void removeHeaderParam(String key) {
        for (NameValuePair headerParam : getHeaderParams()) {
            if (key != null && key.equals(headerParam.getName())) {
                headerParams.remove(headerParam);
                return;
            }
        }
    }

    /**
     * remove body param.
     *
     * @param key the key
     */
    public void removeBodyParam(String key) {
        for (NameValuePair bodyParam : getBodyParams()) {
            if (key != null && key.equals(bodyParam.getName())) {
                bodyParams.remove(bodyParam);
                return;
            }
        }
    }

    /**
     * Get header params array list.
     *
     * @return the array list
     */
    public ArrayList<NameValuePair> getHeaderParams() {
        return headerParams;
    }

    /**
     * Get body params array list.
     *
     * @return the array list
     */
    public ArrayList<NameValuePair> getBodyParams() {
        return bodyParams;
    }

}
