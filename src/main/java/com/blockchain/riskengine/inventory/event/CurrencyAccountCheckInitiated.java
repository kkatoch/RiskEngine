package com.blockchain.riskengine.inventory.event;

import com.blockchain.riskengine.inventory.model.CurrencyEntity;
import lombok.Getter;

@Getter
public class CurrencyAccountCheckInitiated {
    private CurrencyEntity currencyEntity;

    public CurrencyAccountCheckInitiated(CurrencyEntity currencyEntity) {
        this.currencyEntity = currencyEntity;
    }
}
