package com.randy.randyclientdemo.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * LoginResultBean
 * Created by RandyZhang on 2017/2/15.
 */

public class LoginResultBean implements Serializable {
    @SerializedName("message")
    private String message;
    /**
     * identityId : null
     * uid : 5
     * mobile : 15868480733
     */

    private String identityId;
    private int uid;
    private String mobile;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "LoginResultBean{" +
                "message='" + message + '\'' +
                ", identityId='" + identityId + '\'' +
                ", uid=" + uid +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}
