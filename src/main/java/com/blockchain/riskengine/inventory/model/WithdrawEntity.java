package com.blockchain.riskengine.inventory.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WithdrawEntity {

    public String userId;
    public String currencyCode;
    public double amount;

    public WithdrawEntity(String userId, String currencyCode, double amount) {
        this.userId = userId;
        this.amount = amount;
        this.currencyCode = currencyCode;
    }

    public WithdrawEntity() {
    }

    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("currency_code")
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @JsonProperty("amount")
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Withdraw{" +
                "user_id=" + userId +
                ", currency_code='" + currencyCode + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
