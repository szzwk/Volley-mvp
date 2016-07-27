package com.pt.volley.view;

import com.pt.volley.bean.LatestNewsBean;

/**
 * @author: yorkzhang
 * @time: 16/7/21 11:53
 * @email: xtcqw13@126.com
 * @note:
 */
public interface IViewCallback {

    void getLatestNewsSuccess(LatestNewsBean bean);

    void getLatestNewsFailed(int errorCode, String errorMessage);

    void getWeatherReportSuccess(String weather);

    void getWeatherReportFailed(int errorCode, String errorMessage);

    class Stub implements IViewCallback {

        @Override
        public void getLatestNewsSuccess(LatestNewsBean bean) {

        }

        @Override
        public void getLatestNewsFailed(int errorCode, String errorMessage) {

        }

        @Override
        public void getWeatherReportSuccess(String weather) {

        }

        @Override
        public void getWeatherReportFailed(int errorCode, String errorMessage) {

        }
    }
}
