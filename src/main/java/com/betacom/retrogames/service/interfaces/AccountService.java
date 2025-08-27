package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.AccountDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.AccountReq;

public interface AccountService {

	Integer create(AccountReq req) throws AcademyException;

	void update(AccountReq req) throws AcademyException;

	void delete(AccountReq req) throws AcademyException;

	List<AccountDTO> listAll();
}
