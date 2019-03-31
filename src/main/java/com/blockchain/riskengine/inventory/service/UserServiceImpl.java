package com.blockchain.riskengine.inventory.service;

import com.blockchain.riskengine.inventory.model.UserEntity;
import com.blockchain.riskengine.inventory.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("UserService")
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepo userRepo;

    public List<UserEntity> getAllUsers() {
        return userRepo.findAll();
    }

    public UserEntity addUser(UserEntity user) {
        user.setId(userRepo.getNextSeriesId().intValue());
        return userRepo.save(user);
    }
}
