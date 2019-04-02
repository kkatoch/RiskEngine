package com.blockchain.riskengine.inventory.kafka;

import com.blockchain.riskengine.inventory.event.WithdrawalChecked;
import com.blockchain.riskengine.inventory.model.CurrencyEntity;
import com.blockchain.riskengine.inventory.model.TradeEntity;
import com.blockchain.riskengine.inventory.model.UserEntity;
import com.blockchain.riskengine.inventory.model.WithdrawEntity;
import com.blockchain.riskengine.inventory.service.CurrencyService;
import com.blockchain.riskengine.inventory.service.TransactionService;
import com.blockchain.riskengine.inventory.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Transactional
public class KafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    CurrencyService currencyService;
    @Autowired
    UserService userService;
    @Autowired
    TransactionService transactionService;

    private CurrencyEntity currencyEntityFromKafka = new CurrencyEntity();

    @KafkaListener(topics = "4igc0qsg-inventories.kafka.post.currency", groupId = "riskdata")
    public void processPostCurrency(String currencyJSON) {
        logger.info("received content = '{}'", currencyJSON);
        try {
            ObjectMapper mapper = new ObjectMapper();
            CurrencyEntity currencyEntity = mapper.readValue(currencyJSON, CurrencyEntity.class);
            CurrencyEntity currency = currencyService.addCurrency(currencyEntity);
            logger.info("Success process currency '{}' with topic '{}'", currency.getName(), "inventories.kafka.post.currency");
        } catch (Exception e) {
            logger.error("An error occurred! '{}'", e.getMessage());
        }
    }

    @KafkaListener(topics = "4igc0qsg-inventories.kafka.put.currency", groupId = "riskdata")
    public void processPutCurrency(String currencyJSON) {
        logger.info("received content = '{}'", currencyJSON);
        try {
            ObjectMapper mapper = new ObjectMapper();
            CurrencyEntity currency = mapper.readValue(currencyJSON, CurrencyEntity.class);
            currencyEntityFromKafka = currency;
            logger.info("Success process currency '{}' with topic '{}'", currency.getName(), "inventories.kafka.put.currency");
        } catch (Exception e) {
            logger.error("An error occurred! '{}'", e.getMessage());
        }
    }

    public CurrencyEntity getCurrencyEntityFromKafka(int id) {
        return currencyEntityFromKafka;
    }

    @KafkaListener(topics = "4igc0qsg-inventories.kafka.patch.currency", groupId = "riskdata")
    public void processPatchCurrency(String currencyJSON) {
        logger.info("received content = '{}'", currencyJSON);
        try {
            ObjectMapper mapper = new ObjectMapper();
            CurrencyEntity currencyEntity = mapper.readValue(currencyJSON, CurrencyEntity.class);
            CurrencyEntity currency = currencyService.updateCurrency(currencyEntity);
            logger.info("Success process currency '{}' with topic '{}'", currency.getName(), "inventories.kafka.patch.currency");
        } catch (Exception e) {
            logger.error("An error occurred! '{}'", e.getMessage());
        }
    }

    @KafkaListener(topics = "4igc0qsg-inventories.kafka.post.currency.user", groupId = "riskdata")
    public void processPostUser(String userJSON) {
        logger.info("received content = '{}'", userJSON);
        try {
            ObjectMapper mapper = new ObjectMapper();
            UserEntity userEntity = mapper.readValue(userJSON, UserEntity.class);
            UserEntity user = userService.addUser(userEntity);
            logger.info("Success process currency user '{}' with topic '{}'", user.getUserName(), "inventories.kafka.post.user");
        } catch (Exception e) {
            logger.error("An error occurred! '{}'", e.getMessage());
        }
    }

    @KafkaListener(topics = "4igc0qsg-inventories.kafka.post.trade", groupId = "riskdata")
    public void processPostTrade(String tradeJSON) {
        logger.info("received content = '{}'", tradeJSON);
        try {
            ObjectMapper mapper = new ObjectMapper();
            TradeEntity tradeEntity = mapper.readValue(tradeJSON, TradeEntity.class);
            transactionService.settlement(tradeEntity);
            logger.info("Success process currency user '{}' with topic '{}'", tradeEntity.getUserId(), "inventories.kafka.post.trade");
        } catch (Exception e) {
            logger.error("An error occurred! '{}'", e.getMessage());
        }
    }

    @KafkaListener(topics = "inventories.kafka.post.check.account")
    public void processGetWithdrawal(WithdrawalChecked withdrawalChecked) {
        logger.info("Received withdrawal Checked event UserId:{}, CurrencyToken: {}", withdrawalChecked.withdraw.getUserId(), withdrawalChecked.withdraw.getCurrencyCode());
        try {
            transactionService.withdraw(withdrawalChecked.withdraw.getUserId(), withdrawalChecked.withdraw.getCurrencyCode(), withdrawalChecked.withdraw.getAmount());
            logger.info("Success process currency user '{}' with topic '{}'", withdrawalChecked.withdraw.getUserId(), "inventories.kafka.get.withdrawal");
        } catch (Exception e) {
            logger.error("An error occurred! '{}'", e.getMessage());
        }
    }

    @KafkaListener(topics = "inventories.kafka.post.check.account.withdrawal")
    public void processPostCheckedWithdrawal(String checkedWithdrawJSON) {
        logger.info("received content = '{}'", checkedWithdrawJSON);
        try {
            ObjectMapper mapper = new ObjectMapper();
            WithdrawEntity withdrawalChecked = mapper.readValue(checkedWithdrawJSON, WithdrawEntity.class);
            transactionService.withdraw(withdrawalChecked.getUserId(), withdrawalChecked.getCurrencyCode(), withdrawalChecked.getAmount());
            logger.info("Success! process currency user '{}' with topic '{}'", withdrawalChecked.getUserId(), "inventories.kafka.post.check.account.withdrawal");
        } catch (Exception e) {
            logger.error("An error occurred! '{}'", e.getMessage());
        }
    }
}
