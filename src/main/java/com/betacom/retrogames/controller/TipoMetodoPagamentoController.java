package com.betacom.retrogames.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.retrogames.dto.TipoMetodoPagamentoDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.TipoMetodoPagamentoReq;
import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnDelete;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.TipoMetodoPagamentoService;

@RestController
@RequestMapping("/api/v1/retro-games/tipi-metodo-pagamento")
public class TipoMetodoPagamentoController {
	private final TipoMetodoPagamentoService tipoS;

	public TipoMetodoPagamentoController(TipoMetodoPagamentoService tipoS) {
		this.tipoS = tipoS;
	}

	@PostMapping("/create")
	public ResponseBase create(@Validated(OnCreate.class) @RequestBody(required = true) TipoMetodoPagamentoReq req) {
		ResponseBase res = new ResponseBase();

		try {
			Integer id = tipoS.crea(req);
			res.setReturnCode(true);
			res.setMsg("Tipo metodo pagamento creato con successo. ID: " + id);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/update")
	public ResponseBase update(@Validated(OnUpdate.class) @RequestBody(required = true) TipoMetodoPagamentoReq req) {
		ResponseBase res = new ResponseBase();

		try {
			tipoS.aggiorna(req);
			res.setReturnCode(true);
			res.setMsg("Tipo metodo pagamento aggiornato con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/disable")
	public ResponseBase disable(@Validated(OnDelete.class) @RequestBody(required = true) TipoMetodoPagamentoReq req) {
		ResponseBase res = new ResponseBase();

		try {
			tipoS.disattiva(req);
			res.setReturnCode(true);
			res.setMsg("Tipo metodo pagamento disattivato con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/get-by-id")
	public ResponseObject<TipoMetodoPagamentoDTO> getById(@RequestParam(required = true) Integer id) {
		ResponseObject<TipoMetodoPagamentoDTO> res = new ResponseObject<>();

		try {
			res.setDati(tipoS.getById(id));
			res.setReturnCode(true);
		} catch (Exception e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/list-active")
	public ResponseList<TipoMetodoPagamentoDTO> listActive() {
		ResponseList<TipoMetodoPagamentoDTO> res = new ResponseList<>();

		try {
			res.setDati(tipoS.listActive());
			res.setReturnCode(true);
		} catch (Exception e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}
}