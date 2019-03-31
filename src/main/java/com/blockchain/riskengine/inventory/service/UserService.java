package com.blockchain.riskengine.inventory.service;

import com.blockchain.riskengine.inventory.model.UserEntity;

import java.util.List;

public interface UserService {
    List<UserEntity> getAllUsers();

    UserEntity addUser(UserEntity user);
}