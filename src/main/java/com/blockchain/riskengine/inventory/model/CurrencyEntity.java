package com.blockchain.riskengine.inventory.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "currency", schema = "public", catalog = "riskdata")
public class CurrencyEntity {

    private String userId;
    private String currencyCode;
    private String currencyName;

    private double currencyAmount;

    @Basic
    @Column(name = "currency_amount")
    public double getCurrencyAmount() {
        return currencyAmount;
    }

    public void setCurrencyAmount(double currencyAmount) {
        this.currencyAmount = currencyAmount;
    }

    public int getId() {
        return id;
    }

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "currency_code")
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Basic
    @Column(name = "currency_name")
    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyEntity that = (CurrencyEntity) o;
        return id == that.id &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(currencyCode, that.currencyCode) &&
                Objects.equals(currencyName, that.currencyName) &&
                Objects.equals(currencyAmount, that.currencyAmount);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, userId, currencyCode, currencyName, currencyAmount);
    }
}