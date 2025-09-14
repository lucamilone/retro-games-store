package com.betacom.retrogames.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.CarrelloRigaReq;
import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnDelete;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.service.interfaces.CarrelloRigaService;

@RestController
@RequestMapping("/api/v1/retro-games/carrello-righe")
public class CarrelloRigaController {
	private final CarrelloRigaService carrelloRigaS;

	public CarrelloRigaController(CarrelloRigaService carrelloRigaS) {
		this.carrelloRigaS = carrelloRigaS;
	}

	@PostMapping("/create")
	public ResponseBase create(@Validated(OnCreate.class) @RequestBody(required = true) CarrelloRigaReq req) {
		ResponseBase res = new ResponseBase();
		try {
			carrelloRigaS.aggiungiProdotto(req);
			res.setReturnCode(true);
			res.setMsg("Prodotto aggiunto con successo. ProdottoId: " + req.getProdottoId() + ", Quantità: "
					+ req.getQuantita());
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
			res.setMsg("Riga aggiornata con successo");
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
			res.setMsg("Prodotto rimosso con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}
		return res;
	}
}
