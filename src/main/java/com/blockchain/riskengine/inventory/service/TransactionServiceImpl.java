package com.blockchain.riskengine.inventory.service;

import com.blockchain.riskengine.inventory.model.CurrencyEntity;
import com.blockchain.riskengine.inventory.model.TradeEntity;
import com.blockchain.riskengine.inventory.model.WithdrawStatus;
import com.blockchain.riskengine.util.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("TransactionService")
public class TransactionServiceImpl implements TransactionService {
    public static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    @Autowired
    CurrencyService currencyService;

    public synchronized WithdrawStatus withdraw(String userId, String currencyCode, double amount) throws TransactionException {
        logger.info("Initiating withdraw amount for user {} and currency {}", userId, currencyCode);
        CurrencyEntity currencyUserAccount = currencyService.findByUserIdAndCurrencyCode(userId, currencyCode);

        if (currencyUserAccount == null) {
            logger.error("An error occurred! No Account found by user id {} and name {}", userId, currencyCode);
            throw new TransactionException(String.format("Account not found for user id %s", userId));
        }
        if (currencyUserAccount.getBalance() < 0) {
            logger.error("An error occurred! Money in the account {} is not enough", userId);
            return WithdrawStatus.INSUFFICIENT_BALANCE;
        }
        if (currencyUserAccount.getBalance() < amount) {
            logger.error("An error occurred! Cannot withdraw more than the balance for the user {}", userId);
            return WithdrawStatus.INSUFFICIENT_BALANCE;
        }
        double newBalance = currencyUserAccount.getBalance() - amount;
        if (newBalance < 0) {
            logger.error("An error occurred! Balance cannot be negative for user {}", userId);
            return WithdrawStatus.INSUFFICIENT_BALANCE;
        }
        currencyUserAccount.setBalance(newBalance);
        currencyService.updateCurrency(currencyUserAccount);
        logger.info("Successful! Account {} updated currency {}", userId, currencyCode);
        return WithdrawStatus.SUFFICIENT_BALANCE;
    }


    public synchronized void addAmount(String userId, String currencyCode, double amount) throws TransactionException {
        logger.info("Initiating add amount for user {} and currency {}", userId, currencyCode);
        CurrencyEntity currencyUserAccount = currencyService.findByUserIdAndCurrencyCode(userId, currencyCode);
        if (currencyUserAccount == null) {
            logger.error("An error occurred! No Account found by user id {} and name {}", userId, currencyCode);
            throw new TransactionException(String.format("Account not found for user id %s", userId));
        }
        double newBalance = currencyUserAccount.getBalance() + amount;
        if (currencyUserAccount.getBalance() + amount < 0) {
            logger.error("An error occurred! Money in the account {} is not enough", userId);
            throw new TransactionException(String.format("Money in the account %s is not enough ", userId));
        }
        currencyUserAccount.setBalance(newBalance);
        currencyService.updateCurrency(currencyUserAccount);
        logger.info("Successful! Account {} updated currency {}", userId, currencyCode);
    }

    public void settlement(TradeEntity trade) throws TransactionException {
        logger.info("Initiating settlement of trade for user {} buying currency {} and selling {}", trade.userId, trade.soldToken, trade.soldQuantity);
        try {
            withdraw(trade.userId, trade.soldToken, trade.soldQuantity);
        } catch (TransactionException e) {
            logger.error("Trade settlement failed! Could not withdraw sold currency {} for user {}", trade.soldToken, trade.userId);
            throw new TransactionException(e.getMessage());
        }
        try {
            addAmount(trade.userId, trade.boughtToken, trade.boughtQuantity);
        } catch (TransactionException e) {
            logger.error("Trade settlement failed! Could not add for user {} newly bought currency {}", trade.userId, trade.boughtToken);
            throw new TransactionException(e.getMessage());
        }
    }
}
