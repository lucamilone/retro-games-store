package com.betacom.retrogames.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.retrogames.dto.ProdottoDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.ProdottoReq;
import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnDelete;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.ProdottoService;

@RestController
@RequestMapping("/api/v1/retro-games/prodotti")
public class ProdottoController {
	private final ProdottoService prodottoS;

	public ProdottoController(ProdottoService prodottoS) {
		this.prodottoS = prodottoS;
	}

	@PostMapping("/create")
	public ResponseBase create(@Validated(OnCreate.class) @RequestBody ProdottoReq req) {
		ResponseBase res = new ResponseBase();

		try {
			Integer id = prodottoS.crea(req);
			res.setReturnCode(true);
			res.setMsg("Prodotto creato con successo. ID: " + id);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/update")
	public ResponseBase update(@Validated(OnUpdate.class) @RequestBody ProdottoReq req) {
		ResponseBase res = new ResponseBase();

		try {
			prodottoS.aggiorna(req);
			res.setReturnCode(true);
			res.setMsg("Prodotto aggiornato con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/disable")
	public ResponseBase disable(@Validated(OnDelete.class) @RequestBody ProdottoReq req) {
		ResponseBase res = new ResponseBase();

		try {
			prodottoS.disattiva(req);
			res.setReturnCode(true);
			res.setMsg("Prodotto disattivato con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/get-by-id")
	public ResponseObject<ProdottoDTO> getById(@RequestParam Integer id) {
		ResponseObject<ProdottoDTO> res = new ResponseObject<>();

		try {
			res.setDati(prodottoS.getById(id));
			res.setReturnCode(true);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/list-active")
	public ResponseList<ProdottoDTO> listActive() {
		ResponseList<ProdottoDTO> res = new ResponseList<>();

		try {
			res.setDati(prodottoS.listActive());
			res.setReturnCode(true);
		} catch (Exception e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}
}