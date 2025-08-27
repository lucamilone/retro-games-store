package com.betacom.retrogames.service.interfaces;

import com.betacom.retrogames.dto.RuoloDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.RuoloReq;
import java.util.List;

public interface IRuoloServices
{
    Integer create(RuoloReq req) throws AcademyException;
    void update(RuoloReq req) throws AcademyException;
    void delete(RuoloReq req) throws AcademyException;
    
    List<RuoloDTO> listAll();
}