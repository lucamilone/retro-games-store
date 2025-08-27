package com.betacom.retrogames.service.interfaces;

import com.betacom.retrogames.dto.ProdottoDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.ProdottoReq;
import java.util.List;

public interface IProdottoServices
{
    Integer create(ProdottoReq req) throws AcademyException;
    void update(ProdottoReq req) throws AcademyException;
    void delete(ProdottoReq req) throws AcademyException;
    
    List<ProdottoDTO> listAll();
}