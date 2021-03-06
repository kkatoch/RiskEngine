package com.blockchain.riskengine.inventory.service;

import com.blockchain.riskengine.inventory.model.CurrencyEntity;
import com.blockchain.riskengine.inventory.repo.CurrencyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service("CurrencyService")
public class CurrencyServiceImpl implements CurrencyService {
    @Autowired
    CurrencyRepo currencyRepo;

    public CurrencyEntity findAllByNameIsLike(String name) {
        return currencyRepo.findAllByNameIsLike(name);
    }

    public Page<CurrencyEntity> getAllByCurrencyName(int page, int size, String sort) {
        String[] sortSplit = sort.split(",");
        return currencyRepo.findAll(new PageRequest(page, size, (sortSplit[1].toUpperCase().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC), sortSplit[0]));
    }

    @Transactional
    public CurrencyEntity addCurrency(CurrencyEntity currency) {
        return currencyRepo.save(currency);
    }

    public CurrencyEntity findById(int id) {
        return currencyRepo.findById(id);
    }

    @Transactional
    public CurrencyEntity updateCurrency(CurrencyEntity currencyEntity) {
        return currencyRepo.save(currencyEntity);
    }

    public List<CurrencyEntity> getAllCurrencyUserAccounts() {
        return currencyRepo.findAll();
    }

    @CachePut(value = "accounts")
    public CurrencyEntity findByUserIdAndCurrencyCode(String userId, String currencyCode) {
        return currencyRepo.findByUserIdAndCurrencyCode(userId, currencyCode);
    }
}
