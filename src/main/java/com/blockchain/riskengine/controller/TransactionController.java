package com.blockchain.riskengine.controller;

import com.blockchain.riskengine.inventory.kafka.KafkaConsumer;
import com.blockchain.riskengine.inventory.kafka.KafkaProducer;
import com.blockchain.riskengine.inventory.model.TradeEntity;
import com.blockchain.riskengine.inventory.model.WithdrawStatus;
import com.blockchain.riskengine.inventory.service.TransactionService;
import com.blockchain.riskengine.util.CustomMessage;
import com.blockchain.riskengine.util.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/transact")
public class TransactionController {

    public static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);

    @Autowired
    TransactionService transactionService;

    @Autowired
    KafkaProducer kafkaProducer;

    @Autowired
    KafkaConsumer kafkaConsumer;

    @Value("${spring.kafka.consumer.group-id}")
    String kafkaGroupId;

    @Value("${inventories.kafka.get.withdrawal}")
    String getWithdrawalTopic;

    @Value("${inventories.kafka.post.trade}")
    String postTradeTopic;

    @GetMapping(value = "/{userId}/{currencyCode}/{amount}")
    public ResponseEntity<?> withdrawCurrency(@PathVariable("userId") String userId, @PathVariable("currencyCode") String currencyCode, @PathVariable("amount") double amount) {
        logger.info("Request to Withdraw {} {} received", amount, currencyCode);
        WithdrawStatus status;
        try {
            status = transactionService.withdraw(userId, currencyCode, amount);
        } catch (TransactionException e) {
            logger.error("An error occurred! {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(status.toString());
    }


    @PostMapping(value = "/trade", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> addCurrency(@RequestBody TradeEntity trade) {
        logger.info("Request to trade {} {} by user", trade.boughtToken, trade.soldToken, trade.userId);
        Resources<CustomMessage> res = null;
        try {
            kafkaProducer.postTrade(postTradeTopic, kafkaGroupId, trade);
            List<CustomMessage> customMessageList = new ArrayList<CustomMessage>();
            customMessageList.add(new CustomMessage("Trade settled", HttpStatus.OK));
            res = new Resources<>(customMessageList);
        } catch (Exception e) {
            logger.error("An error occurred! {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(e.getMessage());
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
