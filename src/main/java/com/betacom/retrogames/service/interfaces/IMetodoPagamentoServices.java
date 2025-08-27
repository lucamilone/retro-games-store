package com.betacom.retrogames.service.interfaces;

import com.betacom.retrogames.dto.MetodoPagamentoDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.MetodoPagamentoReq;
import java.util.List;

public interface IMetodoPagamentoServices 
{
    Integer create(MetodoPagamentoReq req) throws AcademyException;
    void update(MetodoPagamentoReq req) throws AcademyException;
    void delete(MetodoPagamentoReq req) throws AcademyException;
    
    List<MetodoPagamentoDTO> listAll();
}