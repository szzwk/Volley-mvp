package com.pt.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: yorkzhang
 * @time: 16/7/18 15:45
 * @email: xtcqw13@126.com
 * @note:
 */
public class VolleyErrorHelper {


    /**
     * Gets network error.
     *
     * @param context the context
     * @return the network error
     */
    public static NetworkException getNetworkException(Context context) {
        return new NetworkException(-4, context.getResources().getString(R.string.parsedata_error));
    }

    /**
     * Gets network error.
     *
     * @param error   the error
     * @param context the context
     * @return the network error
     */
    public static NetworkException getNetworkException(VolleyError error, Context context) {
        if (error instanceof TimeoutError) {
            return new NetworkException(-1, context.getResources().getString(
                    R.string.generic_server_down));
        } else if (isServerProblem(error)) {
            return handleServerError(error, context);
        } else if (isNetworkProblem(error)) {
            return new NetworkException(-2, context.getResources().getString(R.string.no_internet));
        }
        return new NetworkException(-3, context.getResources().getString(R.string.generic_error));
    }

    /**
     * Determines whether the error is related to network
     *
     * @param error
     * @return
     */
    private static boolean isNetworkProblem(VolleyError error) {
        return (error instanceof com.android.volley.NetworkError)
                || (error instanceof NoConnectionError);
    }

    /**
     * Determines whether the error is related to server
     *
     * @param error
     * @return
     */
    private static boolean isServerProblem(VolleyError error) {
        return (error instanceof ServerError)
                || (error instanceof AuthFailureError);
    }

    /**
     * Handles the server error, tries to determine whether to show a stock
     * message or to show a message retrieved from the server.
     *
     * @param err
     * @param context
     * @return
     */
    private static NetworkException handleServerError(VolleyError error, Context context) {
        NetworkResponse response = error.networkResponse;
        if (response != null) {
            switch (response.statusCode) {
                case 404:
                case 422:
                case 401:
                    try {
                        // server might return error like this { "error":
                        // "Some error occured" }
                        // Use "Gson" to parse the result
                        HashMap<String, String> result = new Gson().fromJson(
                                new String(response.data),
                                new TypeToken<Map<String, String>>() {
                                }.getType());

                        if (result != null && result.containsKey("error")) {
                            return new NetworkException(response.statusCode, result.get("error"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // invalid request
                    return new NetworkException(response.statusCode, error.getMessage());

                default:
                    return new NetworkException(response.statusCode, context.getResources().getString(
                            R.string.generic_server_down));
            }
        }
        return new NetworkException(-3, context.getResources().getString(R.string.generic_error));
    }
}
