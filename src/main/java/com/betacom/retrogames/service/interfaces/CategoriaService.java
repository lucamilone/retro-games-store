package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.CategoriaDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.CategoriaReq;

public interface CategoriaService {

	Integer create(CategoriaReq req) throws AcademyException;

	void update(CategoriaReq req) throws AcademyException;

	void delete(CategoriaReq req) throws AcademyException;

	List<CategoriaDTO> listAll();
}