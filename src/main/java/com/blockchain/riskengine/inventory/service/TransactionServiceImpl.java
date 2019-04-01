package com.blockchain.riskengine.inventory.service;

import com.blockchain.riskengine.inventory.model.CurrencyEntity;
import com.blockchain.riskengine.util.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("TransactionService")
public class TransactionServiceImpl implements TransactionService {
    public static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    @Autowired
    CurrencyService currencyService;

    public void withdraw(String userId, String currencyCode, double amount) throws TransactionException {
        List<CurrencyEntity> currencyUserAccounts = currencyService.getAllCurrencyUserAccounts();
        CurrencyEntity currencyUserAccount = null;
        for (CurrencyEntity userAccount : currencyUserAccounts) {
            if (userAccount.getUserId() == userId && userAccount.getCurrencyCode() == currencyCode) {
                currencyUserAccount = userAccount;
            }
        }

        if (currencyUserAccount == null) {
            logger.error("An error occurred! No Account found by user id {} and name {}", userId, currencyCode);
            throw new TransactionException(String.format("Account not found for user id %s", userId));
        }
        if (currencyUserAccount.getCurrencyAmount() < 0) {
            logger.error("An error occurred! Money in the account {} is not enough", userId);
            throw new TransactionException(String.format("Money in the account %s is not enough ", userId));
        }
        if (currencyUserAccount.getCurrencyAmount() < amount) {
            logger.error("An error occurred! Cannot withdraw more than the balance for the user {}", userId);
            throw new TransactionException(String.format("Cannot withdraw more than the balance for the user %s ", userId));
        }
        double newBalance = currencyUserAccount.getCurrencyAmount() - amount;
        if (newBalance < 0) {
            logger.error("An error occurred! Balance cannot be negative for user {}", userId);
            throw new TransactionException(String.format("Cannot withdraw more than the balance for the user %s ", userId));
        }
        currencyUserAccount.setCurrencyAmount(newBalance);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void addAmount(String userId, String currencyCode, double amount) throws TransactionException {
        List<CurrencyEntity> currencyUserAccounts = currencyService.getAllCurrencyUserAccounts();
        CurrencyEntity currencyUserAccount = null;
        for (CurrencyEntity userAccount : currencyUserAccounts) {
            if (userAccount.getUserId() == userId && userAccount.getCurrencyCode() == currencyCode) {
                currencyUserAccount = userAccount;
            }
        }
        if (currencyUserAccount == null) {
            logger.error("An error occurred! No Account found by user id {} and name {}", userId, currencyCode);
            throw new TransactionException(String.format("Account not found for user id %s", userId));
        }
        double newBalance = currencyUserAccount.getCurrencyAmount() + amount;
        if (currencyUserAccount.getCurrencyAmount() + amount < 0) {
            logger.error("An error occurred! Money in the account {} is not enough", userId);
            throw new TransactionException(String.format("Money in the account %s is not enough ", userId));
        }
        currencyUserAccount.setCurrencyAmount(newBalance);
    }
}
