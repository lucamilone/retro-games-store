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
import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnDelete;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.CarrelloService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/retro-games/carrelli")
public class CarrelloController {
	private final CarrelloService carrelloS;

	@PostMapping("/create")
	public ResponseBase create(@Validated(OnCreate.class) @RequestBody(required = true) CarrelloReq req) {
		ResponseBase res = new ResponseBase();
		try {
			Integer id = carrelloS.creaPerAccount(req);
			res.setReturnCode(true);
			res.setMsg("Carrello creato con successo. Id: " + id);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}
		return res;
	}
	
	@PostMapping("/clear")
	public ResponseBase clear(@Validated(OnDelete.class) @RequestBody(required = true) CarrelloReq req) {
		ResponseBase res = new ResponseBase();
		try {
			carrelloS.svuotaCarrello(req);
			res.setReturnCode(true);
			res.setMsg("Carrello svuotato con successo.");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}
		return res;
	}
	
	@GetMapping("/getByAccountId")
	public ResponseObject<CarrelloDTO> getByAccountId(@RequestParam(required = true) Integer accountId) {
		ResponseObject<CarrelloDTO> res = new ResponseObject<>();
		try {
			res.setReturnCode(true);
			res.setMsg("Carrello recuperato con successo.");
			res.setDati(carrelloS.getCarrelloByAccount(accountId));
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}
		return res;
	}

}
