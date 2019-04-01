package com.blockchain.riskengine.controller;

import com.blockchain.riskengine.inventory.kafka.KafkaConsumer;
import com.blockchain.riskengine.inventory.kafka.KafkaProducer;
import com.blockchain.riskengine.inventory.service.CurrencyService;
import com.blockchain.riskengine.inventory.service.TransactionService;
import com.blockchain.riskengine.util.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/transact")
public class TransactionController {

    public static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);

    @Autowired
    TransactionService transactionService;
    @Autowired
    CurrencyService currencyService;

    @Autowired
    KafkaProducer kafkaProducer;

    @Autowired
    KafkaConsumer kafkaConsumer;

    @Value("${spring.kafka.consumer.group-id}")
    String kafkaGroupId;

    @GetMapping(value = "/{userId}/{currencyCode}/{amount}")
    public ResponseEntity<?> withdrawCurrency(@PathVariable("userId") String userId, @PathVariable("currencyCode") String currencyCode, @PathVariable("amount") double amount) {
        logger.info("Withdraw {}", amount);
        try {
            transactionService.withdraw(userId, currencyCode, amount);
        } catch (TransactionException e) {
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(e.getMessage());
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Complete");
    }
}
