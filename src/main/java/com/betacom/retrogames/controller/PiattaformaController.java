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

import com.betacom.retrogames.dto.PiattaformaDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.PiattaformaReq;
import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnDelete;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.PiattaformaService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/retro-games/piattaforme")
public class PiattaformaController {
	private final PiattaformaService piattaformaS;

	public PiattaformaController(PiattaformaService piattaformaS) {
		this.piattaformaS = piattaformaS;
	}

	@PostMapping("/create")
	public ResponseBase create(@Validated(OnCreate.class) @RequestBody(required = true) PiattaformaReq req) {
		ResponseBase res = new ResponseBase();

		try {
			Integer id = piattaformaS.crea(req);
			res.setReturnCode(true);
			res.setMsg("Piattaforma creata con successo. ID: " + id);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/update")
	public ResponseBase update(@Validated(OnUpdate.class) @RequestBody(required = true) PiattaformaReq req) {
		ResponseBase res = new ResponseBase();

		try {
			piattaformaS.aggiorna(req);
			res.setReturnCode(true);
			res.setMsg("Piattaforma aggiornata con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/disable")
	public ResponseBase disable(@Validated(OnDelete.class) @RequestBody(required = true) PiattaformaReq req) {
		ResponseBase res = new ResponseBase();

		try {
			piattaformaS.disattiva(req);
			res.setReturnCode(true);
			res.setMsg("Piattaforma disattivata con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/get-by-id")
	public ResponseObject<PiattaformaDTO> getById(@RequestParam(required = true) Integer id) {
		ResponseObject<PiattaformaDTO> res = new ResponseObject<>();

		try {
			res.setDati(piattaformaS.getById(id));
			res.setReturnCode(true);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/list-active")
	public ResponseList<PiattaformaDTO> listActive() {
		ResponseList<PiattaformaDTO> res = new ResponseList<>();

		try {
			res.setDati(piattaformaS.listActive());
			res.setReturnCode(true);
		} catch (Exception e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}
}
