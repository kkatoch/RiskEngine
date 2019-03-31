package com.blockchain.riskengine.Controller;

import com.blockchain.riskengine.inventory.kafka.KafkaConsumer;
import com.blockchain.riskengine.inventory.kafka.KafkaProducer;
import com.blockchain.riskengine.inventory.model.CurrencyEntity;
import com.blockchain.riskengine.inventory.service.CurrencyService;
import com.blockchain.riskengine.inventory.service.UserService;
import com.blockchain.riskengine.util.CustomMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/currency")
public class CurrencyController {
    public static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);

    @Autowired
    UserService userService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    KafkaProducer kafkaProducer;

    @Autowired
    KafkaConsumer kafkaConsumer;

    @Value("${spring.kafka.consumer.group-id}")
    String kafkaGroupId;

    @Value("${inventories.kafka.post.currency}")
    String postCurrencyTopic;

    @Value("${inventories.kafka.put.currency}")
    String putCurrencyTopic;

    @Value("${inventories.kafka.patch.currency}")
    String patchCurrencyTopic;

    @GetMapping(value = "")
    public ResponseEntity<?> getAllByCurrencyName(
            @RequestParam("page") int page, @RequestParam("size") int size,
            @RequestParam(value = "sort", defaultValue = "currencyName,asc") String sort,
            PagedResourcesAssembler pagedResourcesAssembler,
            @RequestHeader("User-Agent") String userAgent
    ) {
        logger.info("Fetching all currencies");
        Page<CurrencyEntity> currency = null;
        try {
            currency = currencyService.getAllByCurrencyName(page, size, sort);
        } catch (Exception e) {
            logger.error("An error occurred! {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(e.getMessage());
        }
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.put(HttpHeaders.USER_AGENT, Arrays.asList(userAgent));
        PagedResources<CurrencyEntity> pagedResources = pagedResourcesAssembler.toResource(currency);
        return new ResponseEntity<PagedResources>(pagedResources, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getCurrencyById(@PathVariable("id") int id) {
        logger.info("Fetching currency with ID {}", id);
        CurrencyEntity currency = null;
        try {
            currency = kafkaConsumer.getCurrencyEntityFromKafka(id);
            if (currency.getId() == 0) currency = currencyService.findById(id);
        } catch (Exception e) {
            logger.error("An error occurred! {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(e.getMessage());
        }
        return new ResponseEntity<CurrencyEntity>(currency, HttpStatus.OK);
    }
/*
    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> addCurrency(@RequestBody CurrencyEntity currencyEntity){
        logger.info(("Process add new currency"));
        Resources<CustomMessage> res = null;
        try {
            currencyService.addCurrency(currencyEntity);
            List<CustomMessage> customMessageList = new ArrayList<CustomMessage>();
            customMessageList.add(new CustomMessage("Created new currency layered", HttpStatus.OK));
            res = new Resources<>(customMessageList);
            res.add(linkTo(CurrencyController.class).withSelfRel());
            res.add(linkTo(UserController.class).withRel("currency_user"));
        } catch (Exception e){
            logger.error("An error occurred! {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(e.getMessage());
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }*/

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> addCurrencyKafka(@RequestBody CurrencyEntity currencyEntity) {
        logger.info(("Process add new currency"));
        Resources<CustomMessage> res = null;
        if (checkIFCurrencyUserExists(currencyEntity.getUserId())) {
            logger.error("An error occurred! User ID {} does not exist", currencyEntity.getUserId());
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(String.format("User id %s does not exists", currencyEntity.getUserId()));
        }
        try {
            kafkaProducer.postCurrency(postCurrencyTopic, kafkaGroupId, currencyEntity);
            List<CustomMessage> customMessageList = new ArrayList<CustomMessage>();
            customMessageList.add(new CustomMessage("Created new currency layered", HttpStatus.OK));
            res = new Resources<>(customMessageList);
            res.add(linkTo(CurrencyController.class).withSelfRel());
            res.add(linkTo(UserController.class).withRel("currency_user"));
        } catch (Exception e) {
            logger.error("An error occurred! {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(e.getMessage());
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    private boolean checkIFCurrencyUserExists(String userId) {
        return userService.userExists(Integer.valueOf(userId));
    }
}
