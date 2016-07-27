package com.pt.volley;

import android.app.Application;

import com.pt.network.context.AppContext;

/**
 * @author: yorkzhang
 * @time: 16/7/21 11:27
 * @email: xtcqw13@126.com
 * @note:
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext.init(this);
    }
}
