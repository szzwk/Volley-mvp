package com.pt.volley;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pt.volley.bean.LatestNewsBean;
import com.pt.volley.view.IViewCallback;
import com.pt.volley.present.Presenter;

public class MainActivity extends Activity implements View.OnClickListener {

    Button btn1, btn2;
    Presenter presenter;
    String city = "武汉";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initPresent();
    }

    private void initView() {
        btn1 = (Button) findViewById(R.id.button01);
        btn2 = (Button) findViewById(R.id.button02);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    private void initPresent() {
        presenter = new Presenter(viewCallback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button01:
                presenter.requestLatestNews();
                break;

            case R.id.button02:
                presenter.requestSinaWeatherReport(city);
                break;
        }
    }

    IViewCallback viewCallback = new IViewCallback.Stub() {
        @Override
        public void getLatestNewsSuccess(LatestNewsBean bean) {
            super.getLatestNewsSuccess(bean);
            Toast.makeText(MainActivity.this, "拉取消息成功, 轮播图消息" + bean.getTop_stories().size()
                    + "个，下方消息" + bean.getStories().size() + "个", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void getLatestNewsFailed(int errorCode, String errorMessage) {
            super.getLatestNewsFailed(errorCode, errorMessage);
            Toast.makeText(MainActivity.this, "拉取消息失败:" + errorMessage, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void getWeatherReportSuccess(String weather) {
            super.getWeatherReportSuccess(weather);
            Toast.makeText(MainActivity.this, "获取天气预报成功:\n" + weather, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void getWeatherReportFailed(int errorCode, String errorMessage) {
            super.getWeatherReportFailed(errorCode, errorMessage);
            Toast.makeText(MainActivity.this, "获取天气预报失败:" + errorMessage, Toast.LENGTH_SHORT).show();
        }
    };
}