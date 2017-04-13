package com.randy.randyclientdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.randy.randyclient.RandyClient;
import com.randy.randyclient.base.BaseResponse;
import com.randy.randyclient.base.BaseResponseCallback;
import com.randy.randyclient.exception.SelfDefineThrowable;
import com.randy.randyclient.helper.Api;
import com.randy.randyclientdemo.bean.LoginResultBean;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.tv_is_register)
    TextView tvIsRegister;
    @Bind(R.id.tv_login)
    TextView tvLogin;

    RandyClient randyClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        RandyClient.Builder builder = new RandyClient.Builder(this);
         randyClient = builder.baseUrl(Api.BaseUrl)
                .addLog(true)
                .build();
//        new RandyClient.Builder(this).baseUrl(Api.BaseUrl)

    }

    @OnClick({R.id.tv_is_register, R.id.tv_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_is_register:
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("mobile", "15868480733");
                paramMap.put("password", "87b750fdfeb4468f58c3247b303704ab");
                randyClient.executePost(Api.LOGIN, paramMap, new BaseResponseCallback<BaseResponse<LoginResultBean>>() {
                    @Override
                    public void onStart() {
                        Toast.makeText(MainActivity.this, "onStart", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCompleted() {
                        Toast.makeText(MainActivity.this, "onCompleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onExceptionError(SelfDefineThrowable e) {
                        Toast.makeText(MainActivity.this, "onExceptionError", Toast.LENGTH_SHORT).show();
                        Log.e("onExceptionError", e.getMessage());
                    }

                    @Override
                    public void onSuccess(BaseResponse<LoginResultBean> response) {
                        Toast.makeText(MainActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                        Log.i("onSuccess", response.getData().toString());
                    }
                });
                break;
            case R.id.tv_login:
                break;
        }
    }
}
