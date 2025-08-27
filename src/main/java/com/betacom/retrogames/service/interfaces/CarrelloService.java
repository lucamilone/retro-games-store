package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.CarrelloDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.CarrelloReq;

public interface CarrelloService {

	Integer create(CarrelloReq req) throws AcademyException;

	void update(CarrelloReq req) throws AcademyException;

	void delete(CarrelloReq req) throws AcademyException;

	List<CarrelloDTO> listAll();
}