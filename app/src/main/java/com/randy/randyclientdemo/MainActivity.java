package com.randy.randyclientdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.randy.randyclient.RandyClient;
import com.randy.randyclient.base.BaseListData;
import com.randy.randyclient.base.BaseResponse;
import com.randy.randyclient.base.BaseCallback;
import com.randy.randyclient.exception.SelfDefineThrowable;
import com.randy.randyclientdemo.bean.Api;
import com.randy.randyclientdemo.bean.LoginResultBean;
import com.randy.randyclientdemo.bean.ProductBean;

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
                .addLog(true)
                .build();

    }

    @OnClick({R.id.tv_is_register, R.id.tv_login, R.id.tv_get_product_list})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_is_register:
                isRegister();
                break;
            case R.id.tv_login:
                login();
                break;
            case R.id.tv_get_product_list:
                getProductList();
                break;
        }
    }

    void isRegister() {
        Map<String, Object> pm = new HashMap<>();
        pm.put("mobile", "15868480733");
        randyClient.executePost(Api.IS_REGISTER, pm,
                new BaseCallback<BaseResponse<Boolean>>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        Toast.makeText(MainActivity.this, "onStart", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Toast.makeText(MainActivity.this, "onCompleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onExceptionError(SelfDefineThrowable e) {
                        super.onExceptionError(e);
                        Toast.makeText(MainActivity.this, "onExceptionError", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReadCacheSuccess(BaseResponse<Boolean> response) {
                        super.onReadCacheSuccess(response);
                        Toast.makeText(MainActivity.this, "onReadCacheSuccess", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        Toast.makeText(MainActivity.this, "onSuccess -> " + response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 登录
     */
    void login() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mobile", "15868480733");
        paramMap.put("password", "87b750fdfeb4468f58c3247b303704ab");
        randyClient.executePost(Api.LOGIN, paramMap,
                new BaseCallback<BaseResponse<LoginResultBean>>() {
                    @Override
                    public void onStart() {
//                        super.onStart();
                        Toast.makeText(MainActivity.this, "onStart", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCompleted() {
//                        super.onCompleted();
                        Toast.makeText(MainActivity.this, "onCompleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onExceptionError(SelfDefineThrowable e) {
//                        super.onExceptionError(e);
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                        Log.e("onExceptionError", e.getMessage());
                    }

                    @Override
                    public void onReadCacheSuccess(BaseResponse<LoginResultBean> response) {
                        super.onReadCacheSuccess(response);
                    }

                    @Override
                    public void onSuccess(BaseResponse<LoginResultBean> response) {
                        Toast.makeText(MainActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                        Log.i("onSuccess", response.getData().toString());
                    }
                });
    }

    void getProductList() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("city", "0");
        paramMap.put("limitCode", "0");
        paramMap.put("termCode", "0");
        randyClient = new RandyClient.Builder(this)
                .baseUrl(Api.BaseUrl)
                .addLog(true)
                .addLog(true)
                .addDbCache(true)
                .build();
        randyClient.executePost(Api.GET_PRODUCT_LIST, paramMap,
                new BaseCallback<BaseResponse<BaseListData<ProductBean>>>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        Toast.makeText(MainActivity.this, "onStart", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Toast.makeText(MainActivity.this, "onCompleted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onExceptionError(SelfDefineThrowable e) {
                        super.onExceptionError(e);
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                        Log.e("onExceptionError", e.getMessage());
                    }

                    @Override
                    public void onReadCacheSuccess(BaseResponse<BaseListData<ProductBean>> response) {
                        super.onReadCacheSuccess(response);
//                        Log.e("onReadCacheSuccess", response.toString());
                        Log.i("onReadCacheSuccess", response.getMessage());
                    }

                    @Override
                    public void onSuccess(BaseResponse<BaseListData<ProductBean>> response) {
                        Toast.makeText(MainActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                        Log.i("onSuccess", response.getData().toString());
                    }
                });
    }
}
