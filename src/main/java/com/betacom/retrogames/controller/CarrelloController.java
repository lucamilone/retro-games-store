package com.betacom.retrogames.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.retrogames.dto.CarrelloDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.CarrelloReq;
import com.betacom.retrogames.request.validation.ValidationGroup.OnDelete;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.CarrelloService;

@RestController
@RequestMapping("/api/v1/retro-games/carrelli")
public class CarrelloController {
	private final CarrelloService carrelloS;

	public CarrelloController(CarrelloService carrelloS) {
		this.carrelloS = carrelloS;
	}

	@PostMapping("/clear")
	public ResponseBase clear(@Validated(OnDelete.class) @RequestBody(required = true) CarrelloReq req) {
		ResponseBase res = new ResponseBase();
		try {
			carrelloS.svuotaCarrello(req);
			res.setReturnCode(true);
			res.setMsg("Carrello svuotato con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}
		return res;
	}

	@GetMapping("/get-carrello-by-account")
	public ResponseObject<CarrelloDTO> getCarrelloByAccount(@RequestParam(required = true) Integer accountId) {
		ResponseObject<CarrelloDTO> res = new ResponseObject<>();
		try {
			res.setDati(carrelloS.getCarrelloByAccount(accountId));
			res.setReturnCode(true);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}
		return res;
	}
}
