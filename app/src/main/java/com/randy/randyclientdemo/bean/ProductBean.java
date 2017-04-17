package com.randy.randyclientdemo.bean;

import java.io.Serializable;

/**
 * Created by Woodslake on 2017/2/27.
 */

public class ProductBean implements Serializable{


    /**
     * productId : 6
     * productName : 惠装贷
     * productType: HZD,
     * productIconUrl : https://testjkweb.tourongjia.com/images/jkProduct/product_icon_hzd.png
     * amountDescribe : 最高额度
     * amount : 15.0万
     * firstIllustrationUrl : https://testjkweb.tourongjia.com/images/jkProduct/product_illustration_ccqx.png
     * secondIllustrationUrl : https://testjkweb.tourongjia.com/images/jkProduct/product_illustration_shck.png
     * interestDescribe : 月利率：
     * interest : 1.00-1.80%
     * lendingTimeDescribe : 放款周期：
     * lendingTime : 2个工作日
     * termDescribe : 期限范围：
     * term : 12-36月
     */

    private int productId;
    private String productName;
    private String productType;
    private String productIconUrl;
    private String amountDescribe;
    private String amount;
    private String firstIllustrationUrl;
    private String secondIllustrationUrl;
    private String interestDescribe;
    private String interest;
    private String lendingTimeDescribe;
    private String lendingTime;
    private String termDescribe;
    private String term;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductIconUrl() {
        return productIconUrl;
    }

    public void setProductIconUrl(String productIconUrl) {
        this.productIconUrl = productIconUrl;
    }

    public String getAmountDescribe() {
        return amountDescribe;
    }

    public void setAmountDescribe(String amountDescribe) {
        this.amountDescribe = amountDescribe;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFirstIllustrationUrl() {
        return firstIllustrationUrl;
    }

    public void setFirstIllustrationUrl(String firstIllustrationUrl) {
        this.firstIllustrationUrl = firstIllustrationUrl;
    }

    public String getSecondIllustrationUrl() {
        return secondIllustrationUrl;
    }

    public void setSecondIllustrationUrl(String secondIllustrationUrl) {
        this.secondIllustrationUrl = secondIllustrationUrl;
    }

    public String getInterestDescribe() {
        return interestDescribe;
    }

    public void setInterestDescribe(String interestDescribe) {
        this.interestDescribe = interestDescribe;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getLendingTimeDescribe() {
        return lendingTimeDescribe;
    }

    public void setLendingTimeDescribe(String lendingTimeDescribe) {
        this.lendingTimeDescribe = lendingTimeDescribe;
    }

    public String getLendingTime() {
        return lendingTime;
    }

    public void setLendingTime(String lendingTime) {
        this.lendingTime = lendingTime;
    }

    public String getTermDescribe() {
        return termDescribe;
    }

    public void setTermDescribe(String termDescribe) {
        this.termDescribe = termDescribe;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
