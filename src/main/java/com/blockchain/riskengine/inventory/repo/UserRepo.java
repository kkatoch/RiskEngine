package com.blockchain.riskengine.inventory.repo;

import com.blockchain.riskengine.inventory.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "/api/user")
public interface UserRepo extends JpaRepository<UserEntity, Integer> {
//    @Query(value = "SELECT nextval('user_id_seq')", nativeQuery = true)
    //  Long getNextSeriesId();
}