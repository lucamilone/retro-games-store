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

import com.betacom.retrogames.dto.CredenzialeDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.CredenzialeReq;
import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.CredenzialeService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/retro-games/credenziali")
public class CredenzialeController {
	private final CredenzialeService credenzialeS;

	public CredenzialeController(CredenzialeService credenzialeS) {
		this.credenzialeS = credenzialeS;
	}

	@PostMapping("/create")
	public ResponseBase create(@Validated(OnCreate.class) @RequestBody(required = true) CredenzialeReq req) {
		ResponseBase res = new ResponseBase();

		try {
			Integer id = credenzialeS.crea(req);
			res.setReturnCode(true);
			res.setMsg("Credenziale creata con successo. ID: " + id);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/update-email")
	public ResponseBase updateEmail(@Validated(OnUpdate.class) @RequestBody(required = true) CredenzialeReq req) {
		ResponseBase res = new ResponseBase();

		try {
			credenzialeS.aggiornaEmail(req);
			res.setReturnCode(true);
			res.setMsg("Email aggiornata con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/update-password")
	public ResponseBase updatePassword(@Validated(OnUpdate.class) @RequestBody(required = true) CredenzialeReq req) {
		ResponseBase res = new ResponseBase();

		try {
			credenzialeS.aggiornaPassword(req);
			res.setReturnCode(true);
			res.setMsg("Password aggiornata con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PostMapping("/login")
	public ResponseObject<CredenzialeDTO> login(@RequestBody(required = true) CredenzialeReq req) {
		ResponseObject<CredenzialeDTO> res = new ResponseObject<>();

		try {
			res.setDati(credenzialeS.signIn(req));
			res.setReturnCode(true);
			res.setMsg("Login effettuato con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/get-by-id")
	public ResponseObject<CredenzialeDTO> getById(@RequestParam(required = true) Integer id) {
		ResponseObject<CredenzialeDTO> res = new ResponseObject<>();

		try {
			res.setDati(credenzialeS.getById(id));
			res.setReturnCode(true);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/list-active")
	public ResponseList<CredenzialeDTO> listActive() {
		ResponseList<CredenzialeDTO> res = new ResponseList<>();

		try {
			res.setDati(credenzialeS.listActive());
			res.setReturnCode(true);
		} catch (Exception e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}
}
