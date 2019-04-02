package com.blockchain.riskengine.inventory.kafka;

import com.blockchain.riskengine.inventory.event.CurrencyAccountCheckInitiated;
import com.blockchain.riskengine.inventory.event.CurrencyAccountCheckInitiatedSerialiser;
import com.blockchain.riskengine.inventory.model.CurrencyEntity;
import com.blockchain.riskengine.inventory.model.TradeEntity;
import com.blockchain.riskengine.inventory.model.UserEntity;
import com.blockchain.riskengine.inventory.model.WithdrawEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KafkaProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private KafkaTemplate<Integer, CurrencyAccountCheckInitiated> kafkaWithdrawalTemplate = createWithdrawalCheckKafkaTemplate();

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

    public void postWithdrawalCheck(String topic, Integer currencyAccountId, CurrencyAccountCheckInitiated currencyAccountCheck) {
        try {
            logger.info("Sending data to kafka = '{}' with topic '{}'", currencyAccountId, topic);
            ObjectMapper mapper = new ObjectMapper();
            kafkaWithdrawalTemplate.send(topic, currencyAccountId, currencyAccountCheck);
        } catch (Exception e) {
            logger.error("An error occurred! '{}'", e.getMessage());
        }
    }

    public void postCheckedWithdrawal(String topic, Integer currencyAccountId, WithdrawEntity withdrawalChecked) {
        try {
            logger.info("Sending data to kafka = '{}' with topic '{}'", withdrawalChecked.getUserId(), topic);
            ObjectMapper mapper = new ObjectMapper();
            kafkaTemplate.send(topic, currencyAccountId.toString(), mapper.writeValueAsString(withdrawalChecked));
        } catch (Exception e) {
            logger.error("An error occurred! '{}'", e.getMessage());
        }
    }

    private <T> KafkaTemplate<Integer, T> createWithdrawalCheckKafkaTemplate() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CurrencyAccountCheckInitiatedSerialiser.class.getName());
        ProducerFactory<Integer, T> pf =
                new DefaultKafkaProducerFactory<>(props);
        KafkaTemplate<Integer, T> template = new KafkaTemplate<>(pf);
        return template;
    }


}