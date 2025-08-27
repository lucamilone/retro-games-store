package com.betacom.retrogames.service.interfaces;

import com.betacom.retrogames.dto.TipoMetodoPagamentoDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.TipoMetodoPagamentoReq;
import java.util.List;

public interface ITipoMetodoPagamentoServices 
{
    Integer create(TipoMetodoPagamentoReq req) throws AcademyException;
    void update(TipoMetodoPagamentoReq req) throws AcademyException;
    void delete(TipoMetodoPagamentoReq req) throws AcademyException;
    
    List<TipoMetodoPagamentoDTO> listAll();
}