package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.OrdineDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.OrdineReq;

public interface OrdineService {

	Integer create(OrdineReq req) throws AcademyException;

	void update(OrdineReq req) throws AcademyException;

	void delete(OrdineReq req) throws AcademyException;

	List<OrdineDTO> listAll();
}