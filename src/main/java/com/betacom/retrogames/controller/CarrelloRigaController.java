package com.betacom.retrogames.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.retrogames.dto.CarrelloRigaDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.CarrelloRigaReq;
import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnDelete;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.service.interfaces.CarrelloRigaService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/retro-games/carrelloRighe")
public class CarrelloRigaController {
	private final CarrelloRigaService carrelloRigaS;

	@PostMapping("/create")
	public ResponseBase create(@Validated(OnCreate.class) @RequestBody(required = true) CarrelloRigaReq req) {
		ResponseBase res = new ResponseBase();
		try {
			Integer id = carrelloRigaS.aggiungiProdotto(req);
			res.setReturnCode(true);
			res.setMsg("Prodotto aggiunto con successo. Id: " + id);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}
		return res;
	}
	
	@PutMapping("/update")
	public ResponseBase update(@Validated(OnUpdate.class) @RequestBody(required = true) CarrelloRigaReq req) {
		ResponseBase res = new ResponseBase();
		try {
			carrelloRigaS.aggiornaRiga(req);
			res.setReturnCode(true);
			res.setMsg("Riga aggiornata con successo.");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}
		return res;
	}
	
	@PostMapping("/delete")
	public ResponseBase delete(@Validated(OnDelete.class) @RequestBody(required = true) CarrelloRigaReq req) {
		ResponseBase res = new ResponseBase();
		try {
			carrelloRigaS.rimuoviProdotto(req);
			res.setReturnCode(true);
			res.setMsg("Prodotto rimosso con successo.");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}
		return res;
	}
	
	@GetMapping("/getAllByCarrelloId")
	public ResponseList<CarrelloRigaDTO> getAllByCarrelloId(@RequestParam(required = true) Integer carrelloId) {
		ResponseList<CarrelloRigaDTO> res = new ResponseList<>();
		try {
			res.setDati(carrelloRigaS.listByCarrello(carrelloId));
			res.setReturnCode(true);
			res.setMsg("Righe del carrello recuperate con successo.");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}
		return res;
	}
	
}
