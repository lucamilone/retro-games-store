package com.betacom.retrogames;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.dto.CredenzialeDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.model.Credenziale;
import com.betacom.retrogames.repository.CredenzialeRepository;
import com.betacom.retrogames.request.CredenzialeReq;
import com.betacom.retrogames.service.interfaces.CredenzialeService;

@SpringBootTest
@Transactional
class CredenzialeServiceTest 
{
    @Autowired
    private CredenzialeService credenzialeService;

    @Autowired
    private CredenzialeRepository credenzialeRepo;

    @Test
    void testCreaSuccess() throws AcademyException 
    {
        CredenzialeReq req = new CredenzialeReq();
        req.setEmail("test@email.com");
        req.setPassword("test123");

        Integer id = credenzialeService.crea(req);
        assertNotNull(id);

        CredenzialeDTO dto = credenzialeService.getById(id);
        assertEquals("test@email.com", dto.getEmail());
        assertTrue(dto.getAttivo());
    }

    @Test
    void testCreaEmailGiaEsistente() throws AcademyException 
    {
        // Prima creazione
        CredenzialeReq req = new CredenzialeReq();
        req.setEmail("duplicate@email.com");
        req.setPassword("pass");

        credenzialeService.crea(req);

        // Seconda creazione fallisce
        assertThrows(AcademyException.class, () -> credenzialeService.crea(req));
    }

    @Test
    void testAggiornaEmailSuccesso() throws AcademyException 
    {
        Credenziale credenziale = new Credenziale();
        credenziale.setEmail("old@email.com");
        credenziale.setPassword("pass");
        credenziale.setAttivo(true);
        credenziale = credenzialeRepo.save(credenziale);

        credenzialeService.aggiornaEmail(credenziale.getId(), "new@email.com");

        CredenzialeDTO dto = credenzialeService.getById(credenziale.getId());
        assertEquals("new@email.com", dto.getEmail());
    }

    @Test
    void testAggiornaEmailGiaEsistente() throws AcademyException 
    {
        // Creo due credenziali
        Credenziale cred1 = new Credenziale();
        cred1.setEmail("email1@test.com");
        cred1.setPassword("pass");
        cred1.setAttivo(true);
        cred1 = credenzialeRepo.save(cred1);

        Credenziale cred2 = new Credenziale();
        cred2.setEmail("email2@test.com");
        cred2.setPassword("pass");
        cred2.setAttivo(true);
        cred2 = credenzialeRepo.save(cred2);

        // Tento di aggiornare la prima con email già esistente
        int id = cred1.getId();
        assertThrows(AcademyException.class, () -> credenzialeService.aggiornaEmail(id, "email2@test.com"));
    }

    @Test
    void testAggiornaPasswordSuccesso() throws AcademyException 
    {
        Credenziale credenziale = new Credenziale();
        credenziale.setEmail("pwd@test.com");
        credenziale.setPassword("oldpwd");
        credenziale.setAttivo(true);
        credenziale = credenzialeRepo.save(credenziale);

        credenzialeService.aggiornaPassword(credenziale.getId(), "newpwd");

        Credenziale updated = credenzialeRepo.findById(credenziale.getId()).orElseThrow();
        assertEquals("newpwd", updated.getPassword());
    }

    @Test
    void testListActive() throws AcademyException 
    {
        // Creo e attivo una credenziale
        CredenzialeReq req = new CredenzialeReq();
        req.setEmail("attivo@test.com");
        req.setPassword("pass");
        credenzialeService.crea(req);

        List<CredenzialeDTO> list = credenzialeService.listActive();
        assertFalse(list.isEmpty());
        assertTrue(list.stream().allMatch(CredenzialeDTO::getAttivo));
    }
}