package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.OrdineRigaDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.OrdineRigaReq;

public interface OrdineRigaService {

	Integer create(OrdineRigaReq req) throws AcademyException;

	void update(OrdineRigaReq req) throws AcademyException;

	void delete(OrdineRigaReq req) throws AcademyException;

	List<OrdineRigaDTO> listAll();
}