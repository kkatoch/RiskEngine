package com.blockchain.riskengine.inventory.service;

import com.blockchain.riskengine.inventory.model.CurrencyEntity;
import org.springframework.data.domain.Page;

public interface CurrencyService {
    Page<CurrencyEntity> getAllByCurrencyName(int page, int size, String sort);

    CurrencyEntity addCurrency(CurrencyEntity currency);

    CurrencyEntity findById(int id);

    CurrencyEntity updateCurrency(CurrencyEntity currencyEntity);
}
