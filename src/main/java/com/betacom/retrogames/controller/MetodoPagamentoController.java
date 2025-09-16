package com.betacom.retrogames.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.retrogames.dto.MetodoPagamentoDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.MetodoPagamentoReq;
import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnDelete;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.MetodoPagamentoService;

@RestController
@RequestMapping("/api/v1/retro-games/metodi-pagamento")
public class MetodoPagamentoController {
	private final MetodoPagamentoService metodoPagamentoS;

	public MetodoPagamentoController(MetodoPagamentoService metodoPagamentoS) {
		this.metodoPagamentoS = metodoPagamentoS;
	}

	@PostMapping("/create")
	public ResponseBase create(@Validated(OnCreate.class) @RequestBody MetodoPagamentoReq req) {
		ResponseBase res = new ResponseBase();

		try {
			Integer id = metodoPagamentoS.crea(req);
			res.setReturnCode(true);
			res.setMsg("Metodo di pagamento creato con successo. ID: " + id);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/update")
	public ResponseBase update(@Validated(OnUpdate.class) @RequestBody MetodoPagamentoReq req) {
		ResponseBase res = new ResponseBase();

		try {
			metodoPagamentoS.aggiorna(req);
			res.setReturnCode(true);
			res.setMsg("Metodo di pagamento aggiornato con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/disable")
	public ResponseBase disable(@Validated(OnDelete.class) @RequestBody MetodoPagamentoReq req) {
		ResponseBase res = new ResponseBase();

		try {
			metodoPagamentoS.disattiva(req);
			res.setReturnCode(true);
			res.setMsg("Metodo di pagamento disattivato con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/get-by-id")
	public ResponseObject<MetodoPagamentoDTO> getById(@RequestParam Integer id) {
		ResponseObject<MetodoPagamentoDTO> res = new ResponseObject<>();

		try {
			res.setDati(metodoPagamentoS.getById(id));
			res.setReturnCode(true);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/list-active-by-account")
	public ResponseList<MetodoPagamentoDTO> listActiveByAccount(@RequestParam Integer accountId) {
		ResponseList<MetodoPagamentoDTO> res = new ResponseList<>();

		try {
			res.setDati(metodoPagamentoS.listActiveByAccount(accountId));
			res.setReturnCode(true);
		} catch (Exception e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}
}
