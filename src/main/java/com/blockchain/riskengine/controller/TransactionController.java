package com.blockchain.riskengine.controller;

import com.blockchain.riskengine.inventory.event.CurrencyAccountCheckInitiated;
import com.blockchain.riskengine.inventory.event.WithdrawalChecked;
import com.blockchain.riskengine.inventory.kafka.KafkaConsumer;
import com.blockchain.riskengine.inventory.kafka.KafkaProducer;
import com.blockchain.riskengine.inventory.model.CurrencyEntity;
import com.blockchain.riskengine.inventory.model.TradeEntity;
import com.blockchain.riskengine.inventory.model.WithdrawEntity;
import com.blockchain.riskengine.inventory.model.WithdrawStatus;
import com.blockchain.riskengine.inventory.service.CurrencyService;
import com.blockchain.riskengine.inventory.service.TransactionService;
import com.blockchain.riskengine.util.CustomMessage;
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
import java.util.Optional;
import java.util.concurrent.*;

@RestController
@RequestMapping(value = "/api/transact")
public class TransactionController {

    public static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);

    @Autowired
    TransactionService transactionService;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

    @Autowired
    KafkaProducer kafkaProducer;

    @Autowired
    KafkaConsumer kafkaConsumer;

    @Value("${spring.kafka.consumer.group-id}")
    String kafkaGroupId;
    @Autowired
    CurrencyService currencyService;

    @Value("${inventories.kafka.post.trade}")
    String postTradeTopic;
    @Value("inventories.kafka.post.check.account.withdrawal")
    String getWithdrawalTopic;
    @Value("${inventories.kafka.post.check.account}")
    String checkWithdrawalTopic;

    //@Autowired
    //CacheManager cacheManager;

    @GetMapping(value = "/withdraw")
    public ResponseEntity<?> withdrawCurrency(@RequestParam("userId") String userId, @RequestParam("currencyCode") String currencyCode, @RequestParam("amount") double amount) throws InterruptedException, ExecutionException, TimeoutException {
        logger.info("Request to Withdraw {} {} received", amount, currencyCode);
        CurrencyEntity currencyEntity = currencyService.findByUserIdAndCurrencyCode(userId, currencyCode);
        kafkaProducer.postWithdrawalCheck(checkWithdrawalTopic, currencyEntity.getId(), new CurrencyAccountCheckInitiated(currencyEntity));
        WithdrawStatus withdrawStatus = pollForCheckWithdrawal(userId, currencyCode, amount).get(50, TimeUnit.SECONDS);
        if (withdrawStatus == WithdrawStatus.SUFFICIENT_BALANCE) {
            WithdrawalChecked withdrawalChecked = new WithdrawalChecked(new WithdrawEntity(userId, currencyCode, amount));
            kafkaProducer.postCheckedWithdrawal(getWithdrawalTopic, currencyEntity.getId(), withdrawalChecked.withdraw);
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(withdrawStatus.toString());
    }

    private CompletableFuture<WithdrawStatus> pollForCheckWithdrawal(String userId, String currencyCode, double amount) {
        CompletableFuture<WithdrawStatus> checkResultCompletableFuture = new CompletableFuture<>();

        final ScheduledFuture<?> checkResultScheduledFuture = executor.scheduleAtFixedRate(() -> {
            logger.info("Checking withdrawal capacity for user with id: {}", userId);
            Optional<WithdrawStatus> optionalCheckResult = transactionService.checkIfUserCanWithdraw(userId, currencyCode, amount);
            logger.info("Status: {}", optionalCheckResult.get());
            checkResultCompletableFuture.complete(optionalCheckResult.get());
        }, 1, 1, TimeUnit.SECONDS);
        //we don't want to run this future indefinitely
        executor.schedule(() -> {
            logger.info("Cancelling check for user with id: {}", userId);
            checkResultScheduledFuture.cancel(true);
        }, 65, TimeUnit.SECONDS);
        //cancel polling when result is received
        checkResultCompletableFuture.whenComplete((optionalCheckResult, throwable) -> checkResultScheduledFuture.cancel(true));
        return checkResultCompletableFuture;
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
