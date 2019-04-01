package com.blockchain.riskengine.inventory.repo;

import com.blockchain.riskengine.inventory.model.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;

public interface CurrencyRepo extends JpaRepository<CurrencyEntity, Integer> {
    CurrencyEntity findAllByNameIsLike(String name);

    CurrencyEntity getAllByName(Pageable pageable);

    CurrencyEntity findById(int id);

    @Query(value = "SELECT t FROM CurrencyEntity t where t.user = :userId AND t.code = :currencyCode")
    CurrencyEntity findByUserIdAndCurrencyCode(@Param("userId") String userId, @Param("currencyCode") String currencyCode);
}