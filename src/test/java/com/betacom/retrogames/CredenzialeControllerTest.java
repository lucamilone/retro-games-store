package com.betacom.retrogames;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.controller.CredenzialeController;
import com.betacom.retrogames.dto.CredenzialeDTO;
import com.betacom.retrogames.repository.CredenzialeRepository;
import com.betacom.retrogames.request.CredenzialeReq;
import com.betacom.retrogames.response.ResponseBase;
import com.betacom.retrogames.response.ResponseList;
import com.betacom.retrogames.response.ResponseObject;

@SpringBootTest
@Transactional
class CredenzialeControllerTest 
{
    @Autowired
    private CredenzialeController controller;

    @Autowired
    private CredenzialeRepository repo;

    private CredenzialeReq createReq(String email, String password) 
    {
        CredenzialeReq req = new CredenzialeReq();
        req.setEmail(email);
        req.setPassword(password);
        return req;
    }

    private CredenzialeReq createReqWithId(Integer id, String email, String password) 
    {
        CredenzialeReq req = new CredenzialeReq();
        req.setId(id);
        req.setEmail(email);
        req.setPassword(password);
        return req;
    }

    @Test
    void testCreateSuccess() 
    {
        CredenzialeReq req = createReq("controller@test.com", "pass");
        ResponseBase res = controller.create(req);
        assertTrue(res.getReturnCode());
        assertTrue(res.getMsg().contains("creata"));
    }

    @Test
    void testCreateDuplicateFails() 
    {
        CredenzialeReq req = createReq("duplicate@test.com", "pass");
        ResponseBase res1 = controller.create(req);
        assertTrue(res1.getReturnCode());

        ResponseBase res2 = controller.create(req);
        assertFalse(res2.getReturnCode());
        assertTrue(res2.getMsg().toLowerCase().contains("esistente"));
    }

    @Test
    void testUpdateEmailSuccess() 
    {
        // Creo una credenziale
        CredenzialeReq req = createReq("updateemail@test.com", "pass");
        ResponseBase resCreate = controller.create(req);
        assertTrue(resCreate.getReturnCode());

        // Ottengo id creato
        Integer id = repo.findByEmail("updateemail@test.com").get().getId();

        // Aggiorno email
        CredenzialeReq reqUpdate = createReqWithId(id, "updatedemail@test.com", null);
        ResponseBase resUpdate = controller.updateEmail(reqUpdate);
        assertTrue(resUpdate.getReturnCode());
    }

    @Test
    void testUpdatePasswordSuccess() 
    {
        CredenzialeReq req = createReq("updatepwd@test.com", "oldpass");
        ResponseBase resCreate = controller.create(req);
        assertTrue(resCreate.getReturnCode());

        Integer id = repo.findByEmail("updatepwd@test.com").get().getId();

        CredenzialeReq reqUpdate = createReqWithId(id, null, "newpass");
        ResponseBase res = controller.updatePassword(reqUpdate);
        assertTrue(res.getReturnCode());
        assertTrue(res.getMsg().contains("Password aggiornata"));
    }

    @Test
    void testGetByIdSuccess() 
    {
        CredenzialeReq req = createReq("getbyid@test.com", "pass");
        controller.create(req);
        Integer id = repo.findByEmail("getbyid@test.com").get().getId();

        ResponseObject<CredenzialeDTO> res = controller.getById(id);
        assertTrue(res.getReturnCode());
        assertEquals("getbyid@test.com", res.getDati().getEmail());
    }

    @Test
    void testGetByIdNotFound() 
    {
        ResponseObject<CredenzialeDTO> res = controller.getById(99999);
        assertFalse(res.getReturnCode());
        assertTrue(res.getMsg().toLowerCase().contains("non-trovata"));
    }

    @Test
    void testListActive() 
    {
        CredenzialeReq req1 = createReq("active1@test.com", "pass");
        CredenzialeReq req2 = createReq("active2@test.com", "pass");

        controller.create(req1);
        controller.create(req2);

        ResponseList<CredenzialeDTO> res = controller.listActive();
        assertTrue(res.getReturnCode());
        assertTrue(res.getDati().size() >= 2);
    }
}