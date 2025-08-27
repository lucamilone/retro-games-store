package com.betacom.retrogames.service.interfaces;

import java.util.List;
import com.betacom.retrogames.dto.PagamentoDTO;
import com.betacom.retrogames.request.PagamentoReq;
import com.betacom.retrogames.exception.AcademyException;

public interface IPagamentoServices 
{
    Integer create(PagamentoReq req) throws AcademyException;
    void update(PagamentoReq req) throws AcademyException;
    void delete(PagamentoReq req) throws AcademyException;

    List<PagamentoDTO> listAll();
}