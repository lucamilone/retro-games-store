package com.betacom.retrogames.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.retrogames.dto.AccountDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.AccountReq;
import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnDelete;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.AccountService;

@RestController
@RequestMapping("/api/v1/retro-games/accounts")
public class AccountController {
	private final AccountService accountS;

	public AccountController(AccountService accountS) {
		this.accountS = accountS;
	}

	@PostMapping("/create")
	public ResponseBase create(@Validated(OnCreate.class) @RequestBody AccountReq req) {
		ResponseBase res = new ResponseBase();

		try {
			Integer id = accountS.crea(req);
			res.setReturnCode(true);
			res.setMsg("Account creato con successo. ID: " + id);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/update")
	public ResponseBase update(@Validated(OnUpdate.class) @RequestBody AccountReq req) {
		ResponseBase res = new ResponseBase();

		try {
			accountS.aggiorna(req);
			res.setReturnCode(true);
			res.setMsg("Account aggiornato con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/disable")
	public ResponseBase disable(@Validated(OnDelete.class) @RequestBody AccountReq req) {
		ResponseBase res = new ResponseBase();

		try {
			accountS.disattiva(req);
			res.setReturnCode(true);
			res.setMsg("Account disattivato con successo");
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/get-by-id")
	public ResponseObject<AccountDTO> getById(@RequestParam Integer id) {
		ResponseObject<AccountDTO> res = new ResponseObject<>();

		try {
			res.setDati(accountS.getById(id));
			res.setReturnCode(true);
		} catch (AcademyException e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/list-active")
	public ResponseList<AccountDTO> listActive() {
		ResponseList<AccountDTO> res = new ResponseList<>();

		try {
			res.setDati(accountS.listActive());
			res.setReturnCode(true);
		} catch (Exception e) {
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}
}
