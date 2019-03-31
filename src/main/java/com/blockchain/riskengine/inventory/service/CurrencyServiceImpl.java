package com.blockchain.riskengine.inventory.service;

import com.blockchain.riskengine.inventory.model.CurrencyEntity;
import com.blockchain.riskengine.inventory.repo.CurrencyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("CurrencyService")
public class CurrencyServiceImpl implements CurrencyService {
    @Autowired
    CurrencyRepo currencyRepo;

    public CurrencyEntity findAllByCurrencyNameIsLike(String name) {
        return currencyRepo.findAllByUserNameIsLike(name);
    }

    public Page<CurrencyEntity> getAllByCurrencyName(int page, int size, String sort) {
        String[] sortSplit = sort.split(",");
        return currencyRepo.findAll(new PageRequest(page, size, (sortSplit[1].toUpperCase().equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC), sortSplit[0]));
    }

    @Transactional
    public CurrencyEntity addCurrency(CurrencyEntity currency) {
        currency.setId(currencyRepo.getNextSeriesId().intValue());
        return currencyRepo.save(currency);
    }

    public CurrencyEntity findById(int id) {
        return currencyRepo.findById(id);
    }

    @Transactional
    public CurrencyEntity updateCurrency(CurrencyEntity currencyEntity) {
        return currencyRepo.save(currencyEntity);
    }
}
