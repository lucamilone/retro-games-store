package com.betacom.retrogames.service.interfaces;

import com.betacom.retrogames.dto.OrdineDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.OrdineReq;
import java.util.List;

public interface IOrdineServices
{
    Integer create(OrdineReq req) throws AcademyException;
    void update(OrdineReq req) throws AcademyException;
    void delete(OrdineReq req) throws AcademyException;
    
    List<OrdineDTO> listAll();
}