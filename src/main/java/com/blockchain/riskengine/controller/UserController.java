package com.blockchain.riskengine.controller;

import com.blockchain.riskengine.inventory.kafka.KafkaProducer;
import com.blockchain.riskengine.inventory.model.UserEntity;
import com.blockchain.riskengine.inventory.service.UserService;
import com.blockchain.riskengine.util.CustomMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping("api/admin/user")
public class UserController {
    public static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @Autowired
    KafkaProducer kafkaProducer;

    @Value("${spring.kafka.consumer.group-id}")
    String kafkaGroupId;

    @Value("${inventories.kafka.post.currency.user}")
    String postUserTopic;

    @GetMapping(value = "")
    public ResponseEntity<?> getAllUser() {
        List<UserEntity> user = null;
        try {
            user = userService.getAllUsers();
        } catch (Exception e) {
            logger.error("An error occurred! {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(e.getMessage());
        }
        Resources<List<UserEntity>> res = new Resources(user);
        res.add(linkTo(UserController.class).withSelfRel());
        res.add(linkTo(CurrencyController.class).withRel("brand"));
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> addUser(@RequestBody UserEntity userEntity) {
        UserEntity user = null;
        logger.info(("Process add new currency user"));
        Resources<CustomMessage> res = null;
        CustomMessage customMessage = new CustomMessage();
        try {
            kafkaProducer.postUser(postUserTopic, kafkaGroupId, userEntity);
            List<CustomMessage> customMessageList = new ArrayList<CustomMessage>();
            customMessageList.add(new CustomMessage("Created new user from Kafka", HttpStatus.OK));
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