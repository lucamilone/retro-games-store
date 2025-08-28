package com.betacom.retrogames.service.interfaces;

import com.betacom.retrogames.dto.CarrelloDTO;

public interface CarrelloService {

	void svuotaCarrello(Integer carrelloId);

	CarrelloDTO getCarrelloByAccount(Integer accountId);
}