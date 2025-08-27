package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.RuoloDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.RuoloReq;

public interface RuoloService {

	Integer create(RuoloReq req) throws AcademyException;

	void update(RuoloReq req) throws AcademyException;

	void delete(RuoloReq req) throws AcademyException;

	List<RuoloDTO> listAll();
}