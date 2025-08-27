package com.betacom.retrogames.service.interfaces;

import java.util.List;
import com.betacom.retrogames.dto.OrdineRigaDTO;
import com.betacom.retrogames.request.OrdineRigaReq;
import com.betacom.retrogames.exception.AcademyException;

public interface IOrdineRigaServices
{
    Integer create(OrdineRigaReq req) throws AcademyException;
    void update(OrdineRigaReq req) throws AcademyException;
    void delete(OrdineRigaReq req) throws AcademyException;

    List<OrdineRigaDTO> listAll();
}