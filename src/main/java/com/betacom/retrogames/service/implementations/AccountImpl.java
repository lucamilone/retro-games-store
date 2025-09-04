package com.betacom.retrogames.service.implementations;

import java.util.List;

import org.springframework.stereotype.Service;

import com.betacom.retrogames.dto.AccountDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.AccountReq;
import com.betacom.retrogames.service.interfaces.AccountService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AccountImpl implements AccountService {

	@Override
	public Integer crea(AccountReq req) throws AcademyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void aggiorna(AccountReq req) throws AcademyException {
		// TODO Auto-generated method stub

	}

	@Override
	public void disattiva(AccountReq req) throws AcademyException {
		// TODO Auto-generated method stub

	}

	@Override
	public AccountDTO getById(Integer accountId) throws AcademyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AccountDTO> listActive() {
		// TODO Auto-generated method stub
		return null;
	}
}
