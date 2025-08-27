package com.betacom.retrogames.service.interfaces;

import com.betacom.retrogames.dto.AccountDTO;
import com.betacom.retrogames.request.AccountReq;
import com.betacom.retrogames.exception.AcademyException;
import java.util.List;

public interface IAccountServices 
{
    Integer create(AccountReq req) throws AcademyException;
    void update(AccountReq req) throws AcademyException;
    void delete(AccountReq req) throws AcademyException;
    
    List<AccountDTO> listAll();
}