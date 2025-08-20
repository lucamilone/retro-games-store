package com.betacom.retrogames.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betacom.retrogames.model.Account;

@Repository
public interface IAccountRepository extends JpaRepository<Account, Integer> {

}
