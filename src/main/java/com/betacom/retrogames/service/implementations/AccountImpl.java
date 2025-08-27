package com.betacom.retrogames.service.implementations;

import java.util.List;
import org.springframework.stereotype.Service;
import com.betacom.retrogames.dto.AccountDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.AccountReq;
import com.betacom.retrogames.service.interfaces.IAccountServices;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AccountImpl implements IAccountServices 
{
	@Override
	public Integer create(AccountReq req) throws AcademyException 
	{
		return null;
	}

	@Override
	public void update(AccountReq req) throws AcademyException 
	{}

	@Override
	public void delete(AccountReq req) throws AcademyException 
	{}

	@Override
	public List<AccountDTO> listAll() 
	{
		return null;
	}
}
