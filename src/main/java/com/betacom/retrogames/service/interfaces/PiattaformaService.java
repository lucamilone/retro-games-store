package com.betacom.retrogames.service.interfaces;

import java.util.List;

import com.betacom.retrogames.dto.PiattaformaDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.PiattaformaReq;

public interface PiattaformaService {

	Integer create(PiattaformaReq req) throws AcademyException;

	void update(PiattaformaReq req) throws AcademyException;

	void delete(PiattaformaReq req) throws AcademyException;

	List<PiattaformaDTO> listAll();
}