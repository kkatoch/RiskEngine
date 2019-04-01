package com.blockchain.riskengine.inventory.service;

import com.blockchain.riskengine.util.TransactionException;

public interface TransactionService {

    void withdraw(String userId, String currencyCode, double amount) throws TransactionException;

    void addAmount(String userId, String currencyCode, double amount) throws TransactionException;
}