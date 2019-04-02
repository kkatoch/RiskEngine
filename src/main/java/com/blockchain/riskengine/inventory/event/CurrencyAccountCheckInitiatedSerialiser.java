package com.blockchain.riskengine.inventory.event;

import com.blockchain.riskengine.inventory.kafka.KafkaProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CurrencyAccountCheckInitiatedSerialiser implements Serializer<CurrencyAccountCheckInitiated> {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, CurrencyAccountCheckInitiated data) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(data).getBytes();
        } catch (Exception exception) {
            logger.error("Error in serializing object {} . Exception {}" + data, exception.getMessage());
        }
        return retVal;
    }

    @Override
    public void close() {
    }
}
