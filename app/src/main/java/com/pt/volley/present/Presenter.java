package com.pt.volley.present;

import com.pt.network.ICallback;
import com.pt.network.NetworkException;
import com.pt.volley.bean.LatestNewsBean;
import com.pt.volley.request.LatestNewsRequest;
import com.pt.volley.request.WeatherReportRequest;
import com.pt.volley.view.IViewCallback;

/**
 * @author: yorkzhang
 * @time: 16/7/21 11:52
 * @email: xtcqw13@126.com
 * @note:
 */
public class Presenter {

    IViewCallback viewCallback;

    public Presenter(IViewCallback viewCallback) {
        this.viewCallback = viewCallback;
    }

    public void requestLatestNews() {
        new LatestNewsRequest(new ICallback() {
            @Override
            public void onSuccess(Object response) {
                if (!(response instanceof LatestNewsBean)) {
                    viewCallback.getLatestNewsFailed(10, "类型不匹配");
                    return;
                }
                LatestNewsBean bean = (LatestNewsBean) response;
                viewCallback.getLatestNewsSuccess(bean);
            }

            @Override
            public void onFailure(NetworkException error) {
                viewCallback.getLatestNewsFailed(error.getStatusCode(), error.getMessage());
            }

        }).send();
    }

    public void requestSinaWeatherReport(String city) {
        new WeatherReportRequest(city, new ICallback() {
            @Override
            public void onSuccess(Object response) {
                viewCallback.getWeatherReportSuccess((String)response);
            }

            @Override
            public void onFailure(NetworkException error) {
                viewCallback.getWeatherReportFailed(error.getStatusCode(), error.getMessage());
            }
        }).send();
    }
}
