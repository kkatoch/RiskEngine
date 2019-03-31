package com.blockchain.riskengine.inventory.repo;

import com.blockchain.riskengine.inventory.model.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;

public interface CurrencyRepo extends JpaRepository<CurrencyEntity, Integer> {
    CurrencyEntity findAllByCurrencyNameIsLike(String name);

    CurrencyEntity getAllByCurrencyName(Pageable pageable);

    CurrencyEntity findById(int id);
}