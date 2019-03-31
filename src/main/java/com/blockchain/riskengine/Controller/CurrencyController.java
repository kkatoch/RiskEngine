package com.blockchain.riskengine.Controller;

import com.blockchain.riskengine.inventory.kafka.KafkaConsumer;
import com.blockchain.riskengine.inventory.kafka.KafkaProducer;
import com.blockchain.riskengine.inventory.model.CurrencyEntity;
import com.blockchain.riskengine.inventory.service.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping(value = "/api/currency")
public class CurrencyController {
    public static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);

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
}
