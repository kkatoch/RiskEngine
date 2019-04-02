package com.blockchain.riskengine.inventory.service;

import com.blockchain.riskengine.inventory.model.TradeEntity;
import com.blockchain.riskengine.inventory.model.WithdrawStatus;
import com.blockchain.riskengine.util.TransactionException;

import java.util.Optional;

public interface TransactionService {

    WithdrawStatus withdraw(String userId, String currencyCode, double amount) throws TransactionException;

    void addAmount(String userId, String currencyCode, double amount) throws TransactionException;

    void settlement(TradeEntity trade) throws TransactionException;

    Optional<WithdrawStatus> checkIfUserCanWithdraw(String userId, String currencyCode, double amount);
}