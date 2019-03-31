package com.blockchain.riskengine.inventory.kafka;

import com.blockchain.riskengine.inventory.model.CurrencyEntity;
import com.blockchain.riskengine.inventory.model.UserEntity;
import com.blockchain.riskengine.inventory.service.CurrencyService;
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

    private CurrencyEntity currencyEntityFromKafka = new CurrencyEntity();

    @KafkaListener(topics = "4igc0qsg-inventories.kafka.post.currency", groupId = "riskdata")
    public void processPostCurrency(String currencyJSON) {
        logger.info("received content = '{}'", currencyJSON);
        try {
            ObjectMapper mapper = new ObjectMapper();
            CurrencyEntity currencyEntity = mapper.readValue(currencyJSON, CurrencyEntity.class);
            CurrencyEntity currency = currencyService.addCurrency(currencyEntity);
            logger.info("Success process currency '{}' with topic '{}'", currency.getCurrencyName(), "inventories.kafka.post.currency");
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
            logger.info("Success process currency '{}' with topic '{}'", currency.getCurrencyName(), "inventories.kafka.put.currency");
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
            logger.info("Success process currency '{}' with topic '{}'", currency.getCurrencyName(), "inventories.kafka.patch.currency");
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
}
