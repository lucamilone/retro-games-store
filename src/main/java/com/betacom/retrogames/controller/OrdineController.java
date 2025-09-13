package com.betacom.retrogames.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.OrdineService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/retro-games/ordini")
public class OrdineController {
    private final OrdineService ordineS;

    @PostMapping("/create")
    public ResponseBase create(@Validated(OnCreate.class) @RequestBody(required = true) OrdineReq req) {
        ResponseBase res = new ResponseBase();
        try {
            Integer id = ordineS.crea(req);
            res.setReturnCode(true);
            res.setMsg("Ordine creato con successo. Id: " + id);
        } catch (AcademyException e) {
            res.setReturnCode(false);
            res.setMsg(e.getMessage());
        }
        return res;
    }

    @PostMapping("/update")
    public ResponseBase update(@Validated(OnUpdate.class) @RequestBody(required = true) OrdineReq req) {
        ResponseBase res = new ResponseBase();
        try {
            ordineS.aggiorna(req);
            res.setReturnCode(true);
            res.setMsg("Ordine aggiornato con successo.");
        } catch (AcademyException e) {
            res.setReturnCode(false);
            res.setMsg(e.getMessage());
        }
        return res;
    }

    @PostMapping("/updateStatus")
    public ResponseBase updateStatus(@Validated(OnUpdate.class) @RequestParam(required = true) Integer ordineId, @RequestParam(required = true) String nuovoStato) {
        ResponseBase res = new ResponseBase();
        try {
            ordineS.aggiornaStato(ordineId, nuovoStato);
            res.setReturnCode(true);
            res.setMsg("Stato ordine aggiornato con successo.");
        } catch (AcademyException e) {
            res.setReturnCode(false);
            res.setMsg(e.getMessage());
        }
        return res;
    }

    @GetMapping("/getById")
    public ResponseObject<OrdineDTO> getById(@RequestParam(required = true) Integer ordineId) {
        ResponseObject<OrdineDTO> res = new ResponseObject<>();
        try {
            res.setReturnCode(true);
            res.setMsg("Ordine recuperato con successo.");
            res.setDati(ordineS.getById(ordineId));
        } catch (AcademyException e) {
            res.setReturnCode(false);
            res.setMsg(e.getMessage());
        }
        return res;
    }

    @GetMapping("/listByAccountId")
    public ResponseObject<List<OrdineDTO>> listByAccountId(@RequestParam(required = true) Integer accountId) {
        ResponseObject<List<OrdineDTO>> res = new ResponseObject<>();
        res.setReturnCode(true);
		res.setMsg("Ordini recuperati con successo.");
		res.setDati(ordineS.listByAccount(accountId));
        return res;
    }
}