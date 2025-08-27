package com.betacom.retrogames.service.interfaces;

import com.betacom.retrogames.dto.CategoriaDTO;
import com.betacom.retrogames.request.CategoriaReq;
import com.betacom.retrogames.exception.AcademyException;
import java.util.List;

public interface ICategoriaServices
{
    Integer create(CategoriaReq req) throws AcademyException;
    void update(CategoriaReq req) throws AcademyException;
    void delete(CategoriaReq req) throws AcademyException;
    
    List<CategoriaDTO> listAll();
}