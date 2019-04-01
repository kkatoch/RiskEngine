package com.blockchain.riskengine.inventory.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "currency", schema = "public", catalog = "riskdata")
public class CurrencyEntity {

    private String user;
    private String code;
    private String name;

    private double balance;

    @Basic
    @Column(name = "balance")
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "user")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Basic
    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyEntity that = (CurrencyEntity) o;
        return id == that.id &&
                Objects.equals(user, that.user) &&
                Objects.equals(code, that.code) &&
                Objects.equals(name, that.name) &&
                Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, user, code, name, balance);
    }
}