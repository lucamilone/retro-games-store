package com.betacom.retrogames.service.interfaces;

import java.util.List;
import com.betacom.retrogames.dto.CredenzialeDTO;
import com.betacom.retrogames.request.CredenzialeReq;
import com.betacom.retrogames.exception.AcademyException;

public interface ICredenzialeServices 
{
    Integer create(CredenzialeReq req) throws AcademyException;
    void update(CredenzialeReq req) throws AcademyException;
    void delete(CredenzialeReq req) throws AcademyException;

    List<CredenzialeDTO> listAll();
}