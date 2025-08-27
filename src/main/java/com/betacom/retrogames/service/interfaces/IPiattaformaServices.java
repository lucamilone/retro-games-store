package com.betacom.retrogames.service.interfaces;

import java.util.List;
import com.betacom.retrogames.dto.PiattaformaDTO;
import com.betacom.retrogames.request.PiattaformaReq;
import com.betacom.retrogames.exception.AcademyException;

public interface IPiattaformaServices
{
    Integer create(PiattaformaReq req) throws AcademyException;
    void update(PiattaformaReq req) throws AcademyException;
    void delete(PiattaformaReq req) throws AcademyException;

    List<PiattaformaDTO> listAll();
}