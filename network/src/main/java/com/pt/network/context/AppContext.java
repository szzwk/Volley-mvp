package com.pt.network.context;

import android.content.Context;

/**
 * @author: yorkzhang
 * @time: 16/7/21 11:20
 * @email: xtcqw13@126.com
 * @note:
 */
public class AppContext {

    private static Context context;

    /**
     * Init.
     *
     * @param application the application
     */
    public static void init(Context context) {
        AppContext.context = context;
    }

    /**
     * Gets application context.
     *
     * @return the application context
     */
    public static Context getApplicationContext() {
        return context;
    }
}
