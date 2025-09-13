package com.betacom.retrogames.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.betacom.retrogames.dto.OrdineRigaDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.OrdineRigaReq;
import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnDelete;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.service.interfaces.OrdineRigaService;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/retro-games/ordineRighe")
public class OrdineRigaController {
    private final OrdineRigaService ordineRigaS;

    @PostMapping("/create")
    public ResponseBase create(@Validated(OnCreate.class) @RequestBody(required = true) OrdineRigaReq req) {
        ResponseBase res = new ResponseBase();
        try {
            Integer id = ordineRigaS.aggiungiProdotto(req);
            res.setReturnCode(true);
            res.setMsg("Prodotto aggiunto all'ordine con successo. Id: " + id);
        } catch (AcademyException e) {
            res.setReturnCode(false);
            res.setMsg(e.getMessage());
        }
        return res;
    }

    @PutMapping("/update")
    public ResponseBase update(@Validated(OnUpdate.class) @RequestBody(required = true) OrdineRigaReq req) {
        ResponseBase res = new ResponseBase();
        try {
            ordineRigaS.aggiornaRiga(req);
            res.setReturnCode(true);
            res.setMsg("Riga ordine aggiornata con successo.");
        } catch (AcademyException e) {
            res.setReturnCode(false);
            res.setMsg(e.getMessage());
        }
        return res;
    }

    @PostMapping("/delete")
    public ResponseBase delete(@Validated(OnDelete.class) @RequestBody(required = true) OrdineRigaReq req) {
        ResponseBase res = new ResponseBase();
        try {
            ordineRigaS.rimuoviProdotto(req);
            res.setReturnCode(true);
            res.setMsg("Prodotto rimosso dall'ordine con successo.");
        } catch (AcademyException e) {
            res.setReturnCode(false);
            res.setMsg(e.getMessage());
        }
        return res;
    }

    @GetMapping("/getAllByOrdineId")
    public ResponseList<OrdineRigaDTO> getAllByOrdineId(@RequestParam(required = true) Integer ordineId) {
        ResponseList<OrdineRigaDTO> res = new ResponseList<>();
        res.setDati(ordineRigaS.listByOrdine(ordineId));
		res.setReturnCode(true);
		res.setMsg("Righe dell'ordine recuperate con successo.");
        return res;
    }
    
}