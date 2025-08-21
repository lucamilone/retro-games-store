package com.betacom.retrogames.service.implementations;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.betacom.retrogames.model.Messaggio;
import com.betacom.retrogames.model.MessaggioId;
import com.betacom.retrogames.repository.IMessaggioRepository;
import com.betacom.retrogames.service.interfaces.IMessaggioServices;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class MessaggioImpl implements IMessaggioServices {

	@Value("${app.lang}")
	private String lang;
	private IMessaggioRepository msgR;

	public MessaggioImpl(IMessaggioRepository msgR) {
		this.msgR = msgR;
	}

	@Override
	public String getMessaggio(String code) {
		log.debug("GetMessaggio: {}", code);

		Optional<Messaggio> msg = msgR.findById(new MessaggioId(lang, code));
		String msgStr = null;

		if (msg.isEmpty()) {
			msgStr = code;
		} else {
			msgStr = msg.get().getMessaggio();
		}

		return msgStr;
	}
}
