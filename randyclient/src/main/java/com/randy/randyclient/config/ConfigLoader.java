package com.randy.randyclient.config;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.randy.randyclient.util.FileUtil;

/**
 * 配置加载
 */
public class ConfigLoader {

    private static Config config;

    private final static String CONFIG_NAME = "RandyClient-Config.json";

    public static boolean checkSuccess(Context context, int code) {
        loadConfig(context);
        Log.v("RandyClient", "web :" + code + ">>>>>>>>>>>>isOk：" + config.getSuccessCode().contains(String.valueOf(code)));
        return config.getSuccessCode().contains(String.valueOf(code));
    }

    /**
     * 加载配置的文件
     *
     * @param context context
     * @return Config对象
     */
    public static Config loadConfig(Context context) {

        if (config != null) {
            return config;
        }
        String jsonStr = FileUtil.loadFromAssets(context, CONFIG_NAME);
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        return config = new Gson().fromJson(jsonStr, Config.class);
    }

    public static boolean isFormat(Context context) {
        loadConfig(context);
        return TextUtils.equals(config.getIsFormat(), "true");
    }

}
