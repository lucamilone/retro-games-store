package com.betacom.retrogames.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.retrogames.dto.RuoloDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.RuoloReq;
import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnDelete;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.RuoloService;

@RestController
@RequestMapping("/api/v1/retro-games/ruoli")
public class RuoloController {
	private final RuoloService ruoloS;

	public RuoloController(RuoloService ruoloS) {
		this.ruoloS = ruoloS;
	}

	@PostMapping("/create")
	public ResponseBase create(@Validated(OnCreate.class) @RequestBody(required = true) RuoloReq req) {
		ResponseBase res = new ResponseBase();

		try {
			Integer id = ruoloS.crea(req);
			res.setReturnCode(true);
			res.setMsg("Ruolo creato con successo. ID: " + id);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/update")
	public ResponseBase update(@Validated(OnUpdate.class) @RequestBody(required = true) RuoloReq req) {
		ResponseBase res = new ResponseBase();

		try {
			ruoloS.aggiorna(req);
			res.setReturnCode(true);
			res.setMsg("Ruolo aggiornato con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/disattiva")
	public ResponseBase disattiva(@Validated(OnDelete.class) @RequestBody(required = true) RuoloReq req) {
		ResponseBase res = new ResponseBase();

		try {
			ruoloS.disattiva(req);
			res.setReturnCode(true);
			res.setMsg("Ruolo disattivato con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/get-by-id")
	public ResponseObject<RuoloDTO> getById(@RequestParam(required = true) Integer id) {
		ResponseObject<RuoloDTO> res = new ResponseObject<>();

		try {
			res.setDati(ruoloS.getById(id));
			res.setReturnCode(true);
		} catch (Exception e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/list-active")
	public ResponseList<RuoloDTO> listActive() {
		ResponseList<RuoloDTO> res = new ResponseList<>();

		try {
			res.setDati(ruoloS.listActive());
			res.setReturnCode(true);
		} catch (Exception e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}
}
