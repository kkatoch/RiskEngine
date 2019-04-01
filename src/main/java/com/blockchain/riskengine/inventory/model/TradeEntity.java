package com.blockchain.riskengine.inventory.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TradeEntity {

    public String userId;
    public String boughtToken;
    public double boughtQuantity;
    public String soldToken;
    public double soldQuantity;

    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @JsonProperty("bought_token")
    public String getBoughtToken() {
        return boughtToken;
    }

    public void setBoughtToken(String boughtToken) {
        this.boughtToken = boughtToken;
    }

    @JsonProperty("bought_quantity")
    public double getBoughtQuantity() {
        return boughtQuantity;
    }

    public void setBoughtQuantity(double boughtQuantity) {
        this.boughtQuantity = boughtQuantity;
    }

    @JsonProperty("sold_token")
    public String getSoldToken() {
        return soldToken;
    }

    public void setSoldToken(String soldToken) {
        this.soldToken = soldToken;
    }

    @JsonProperty("sold_quantity")
    public double getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(double soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "user_id=" + userId +
                ", bought_token='" + boughtToken + '\'' +
                ", bought_quantity='" + boughtQuantity + '\'' +
                ", sold_token='" + soldToken + '\'' +
                ", sold_quantity='" + soldQuantity + '\'' +
                '}';
    }
}
