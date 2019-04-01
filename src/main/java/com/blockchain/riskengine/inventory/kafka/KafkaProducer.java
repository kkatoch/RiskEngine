package com.blockchain.riskengine.inventory.kafka;

import com.blockchain.riskengine.inventory.model.CurrencyEntity;
import com.blockchain.riskengine.inventory.model.TradeEntity;
import com.blockchain.riskengine.inventory.model.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void postCurrency(String topic, String groupId, CurrencyEntity currency) {
        try {
            logger.info("Sending data to kafka = '{}' with topic '{}'", currency.getName(), topic);
            ObjectMapper mapper = new ObjectMapper();
            kafkaTemplate.send(topic, groupId, mapper.writeValueAsString(currency));
        } catch (Exception e) {
            logger.error("An error occurred! '{}'", e.getMessage());
        }
    }

    public void postUser(String topic, String groupId, UserEntity user) {
        try {
            logger.info("Sending data to kafka = '{}' with topic '{}'", user.getUserName(), topic);
            ObjectMapper mapper = new ObjectMapper();
            kafkaTemplate.send(topic, groupId, mapper.writeValueAsString(user));
        } catch (Exception e) {
            logger.error("An error occurred! '{}'", e.getMessage());
        }
    }

    public void postTrade(String topic, String groupId, TradeEntity trade) {
        try {
            logger.info("Sending data to kafka = '{}' with topic '{}'", trade.getUserId(), topic);
            ObjectMapper mapper = new ObjectMapper();
            kafkaTemplate.send(topic, groupId, mapper.writeValueAsString(trade));
        } catch (Exception e) {
            logger.error("An error occurred! '{}'", e.getMessage());
        }
    }
}