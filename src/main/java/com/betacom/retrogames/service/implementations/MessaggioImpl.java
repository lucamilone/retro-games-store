package com.betacom.retrogames.service.implementations;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.betacom.retrogames.model.Messaggio;
import com.betacom.retrogames.model.MessaggioId;
import com.betacom.retrogames.repository.MessaggioRepository;
import com.betacom.retrogames.service.interfaces.MessaggioService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessaggioImpl implements MessaggioService {

	@Value("${app.lang}")
	private String lang;
	private MessaggioRepository msgR;

	public MessaggioImpl(MessaggioRepository msgR) {
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
