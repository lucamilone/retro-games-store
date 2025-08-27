package com.betacom.retrogames.service.interfaces;

import java.util.List;
import com.betacom.retrogames.dto.CarrelloRigaDTO;
import com.betacom.retrogames.request.CarrelloRigaReq;
import com.betacom.retrogames.exception.AcademyException;

public interface ICarrelloRigaServices 
{
    Integer create(CarrelloRigaReq req) throws AcademyException;
    void update(CarrelloRigaReq req) throws AcademyException;
    void delete(CarrelloRigaReq req) throws AcademyException;

    List<CarrelloRigaDTO> listAll();
}