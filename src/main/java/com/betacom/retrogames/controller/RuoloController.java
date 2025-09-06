package com.betacom.retrogames.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.betacom.retrogames.dto.RuoloDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.RuoloReq;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.service.interfaces.RuoloService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/rest/ruolo")
public class RuoloController 
{
    private final RuoloService ruoloS;

    public RuoloController(RuoloService ruoloS) 
    {
        this.ruoloS = ruoloS;
    }

    @PostMapping("/create")
    public ResponseBase create(@RequestBody RuoloReq req)
    {
        ResponseBase response = new ResponseBase();
        try 
        {
            Integer id = ruoloS.crea(req);
            
            response.setReturnCode(true);
            response.setMsg("Ruolo creato con successo. Id: " + id);
        } 
        catch(AcademyException e) 
        {
            response.setReturnCode(false);
            response.setMsg(e.getMessage());
        }
        
        return response;
    }

    @PutMapping("/update")
    public ResponseBase update(@RequestBody RuoloReq req) 
    {
        ResponseBase response = new ResponseBase();
        try 
        {
            ruoloS.aggiorna(req);
            
            response.setReturnCode(true);
            response.setMsg("Ruolo aggiornato con successo");
        } 
        catch(AcademyException e) 
        {
            response.setReturnCode(false);
            response.setMsg(e.getMessage());
        }
        
        return response;
    }

    @DeleteMapping("/disattiva")
    public ResponseBase disattiva(@RequestBody RuoloReq req)
    {
        ResponseBase response = new ResponseBase();
        try 
        {
            ruoloS.disattiva(req);
            
            response.setReturnCode(true);
            response.setMsg("Ruolo disattivato con successo");
        } 
        catch (AcademyException e) 
        {
            response.setReturnCode(false);
            response.setMsg(e.getMessage());
        }
        
        return response;
    }

    @GetMapping("/listActive")
    public ResponseList<RuoloDTO> listActive() 
    {
        ResponseList<RuoloDTO> response = new ResponseList<>();
        try 
        {
            response.setDati(ruoloS.listActive());
            
            response.setReturnCode(true);
        } 
        catch (Exception e) 
        {
            response.setReturnCode(false);
            response.setMsg(e.getMessage());
        }
        
        return response;
    }
}
