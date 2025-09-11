package com.betacom.retrogames.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.betacom.retrogames.dto.CategoriaDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.request.CategoriaReq;
import com.betacom.retrogames.request.validation.ValidationGroup.OnCreate;
import com.betacom.retrogames.request.validation.ValidationGroup.OnDelete;
import com.betacom.retrogames.request.validation.ValidationGroup.OnUpdate;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;
import com.betacom.retrogames.service.interfaces.CategoriaService;

@RestController
@RequestMapping("/api/v1/retro-games/categorie")
public class CategoriaController 
{
	private final CategoriaService categoriaS;

	public CategoriaController(CategoriaService categoriaS) 
	{
		this.categoriaS = categoriaS;
	}

	@PostMapping("/create")
	public ResponseBase create(@Validated(OnCreate.class) @RequestBody CategoriaReq req) 
	{
		ResponseBase res = new ResponseBase();

		try 
		{
			Integer id = categoriaS.crea(req);
			
			res.setReturnCode(true);
			res.setMsg("Categoria creata con successo. Id: " + id);
		} 
		catch (AcademyException e) 
		{
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/update")
	public ResponseBase update(@Validated(OnUpdate.class) @RequestBody CategoriaReq req) 
	{
		ResponseBase res = new ResponseBase();

		try 
		{
			categoriaS.aggiorna(req);
			
			res.setReturnCode(true);
			res.setMsg("Categoria aggiornata con successo");
		} 
		catch (AcademyException e) 
		{
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@PutMapping("/disattiva")
	public ResponseBase disattiva(@Validated(OnDelete.class) @RequestBody CategoriaReq req) 
	{
		ResponseBase res = new ResponseBase();

		try 
		{
			categoriaS.disattiva(req);
			
			res.setReturnCode(true);
			res.setMsg("Categoria disattivata con successo");
		} 
		catch (AcademyException e) 
		{
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/get-by-id")
	public ResponseObject<CategoriaDTO> getById(@RequestParam Integer id)
	{
		ResponseObject<CategoriaDTO> res = new ResponseObject<>();

		try 
		{
			res.setDati(categoriaS.getById(id));
			res.setReturnCode(true);
		} 
		catch (AcademyException e) 
		{
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}

	@GetMapping("/list-active")
	public ResponseList<CategoriaDTO> listActive() 
	{
		ResponseList<CategoriaDTO> res = new ResponseList<>();

		try 
		{
			res.setDati(categoriaS.listActive());
			res.setReturnCode(true);
		} 
		catch (Exception e) 
		{
			res.setReturnCode(false);
			res.setMsg(e.getMessage());
		}

		return res;
	}
}
