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
        return userRepo.save(user);
    }

    @Override
    public boolean userExists(int userId) {
        List<UserEntity> allUsers = getAllUsers();
        for (UserEntity user : allUsers) {
            if (user.getId() == userId) {
                return true;
            }
        }
        return false;
    }
}
