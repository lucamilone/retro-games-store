package com.betacom.retrogames.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.retrogames.dto.OrdineDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.OrdineReq;
import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.OrdineService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/retro-games/ordini")
public class OrdineController {
	private final OrdineService ordineS;

	public OrdineController(OrdineService ordineS) {
		this.ordineS = ordineS;
	}

	@PostMapping("/create")
	public ResponseBase create(@Validated(OnCreate.class) @RequestBody(required = true) OrdineReq req) {
		ResponseBase res = new ResponseBase();

		try {
			Integer id = ordineS.crea(req);
			res.setReturnCode(true);
			res.setMsg("Ordine creato con successo. ID: " + id);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/update")
	public ResponseBase update(@Validated(OnUpdate.class) @RequestBody(required = true) OrdineReq req) {
		ResponseBase res = new ResponseBase();

		try {
			ordineS.aggiorna(req);
			res.setReturnCode(true);
			res.setMsg("Ordine aggiornato con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/update-status")
	public ResponseBase updateStatus(@Validated(OnUpdate.class) @RequestBody(required = true) OrdineReq req) {
		ResponseBase res = new ResponseBase();

		try {
			ordineS.aggiornaStato(req);
			res.setReturnCode(true);
			res.setMsg("Stato ordine aggiornato con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/get-by-id")
	public ResponseObject<OrdineDTO> getById(@RequestParam(required = true) Integer ordineId) {
		ResponseObject<OrdineDTO> res = new ResponseObject<>();

		try {
			res.setDati(ordineS.getById(ordineId));
			res.setReturnCode(true);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/list-by-account")
	public ResponseList<OrdineDTO> listByAccount(@RequestParam(required = true) Integer accountId) {
		ResponseList<OrdineDTO> res = new ResponseList<>();

		try {
			res.setDati(ordineS.listByAccount(accountId));
			res.setReturnCode(true);
		} catch (Exception e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}
}