package com.pt.volley.request;

import com.pt.network.HttpParams;
import com.pt.network.HttpStringRequest;
import com.pt.network.ICallback;
import com.pt.volley.constant.Constant;

import java.net.URLEncoder;

/**
 * @author: yorkzhang
 * @time: 16/7/22 13:52
 * @email: xtcqw13@126.com
 * @note:
 */
public class WeatherReportRequest extends HttpStringRequest {

    private String city;

    public WeatherReportRequest(String city, ICallback callback) {
        super(callback);
        this.city = city;
    }

    @Override
    public String getUrl() {
        return Constant.WEATHER_REPORT;
    }

    @Override
    public HttpParams getHttpParams() {
        String encodeCity = "";
        try {
            encodeCity = URLEncoder.encode(city, "gb2312");
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpParams params = new HttpParams();
        params.addBodyParam("city", encodeCity);
        params.addBodyParam("password", "DJOYnieT8234jlsK");
        params.addBodyParam("day", "0");
        return params;
    }
}
