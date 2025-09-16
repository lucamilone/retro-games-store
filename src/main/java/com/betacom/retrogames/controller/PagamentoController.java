package com.betacom.retrogames.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.retrogames.dto.PagamentoDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.PagamentoReq;
import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.PagamentoService;

@RestController
@RequestMapping("/api/v1/retro-games/pagamenti")
public class PagamentoController {
	private final PagamentoService pagamentoS;

	public PagamentoController(PagamentoService pagamentoS) {
		this.pagamentoS = pagamentoS;
	}

	@PostMapping("/create")
	public ResponseBase create(@Validated(OnCreate.class) @RequestBody PagamentoReq req) {
		ResponseBase res = new ResponseBase();

		try {
			Integer id = pagamentoS.crea(req);
			res.setReturnCode(true);
			res.setMsg("Pagamento creato con successo. ID: " + id);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/update")
	public ResponseBase update(@Validated(OnUpdate.class) @RequestBody PagamentoReq req) {
		ResponseBase res = new ResponseBase();

		try {
			pagamentoS.aggiorna(req);
			res.setReturnCode(true);
			res.setMsg("Pagamento aggiornato con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/get-by-id")
	public ResponseObject<PagamentoDTO> getById(@RequestParam Integer id) {
		ResponseObject<PagamentoDTO> res = new ResponseObject<>();

		try {
			res.setDati(pagamentoS.getById(id));
			res.setReturnCode(true);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/list")
	public ResponseList<PagamentoDTO> list() {
		ResponseList<PagamentoDTO> res = new ResponseList<>();

		try {
			res.setDati(pagamentoS.list());
			res.setReturnCode(true);
		} catch (Exception e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}
}