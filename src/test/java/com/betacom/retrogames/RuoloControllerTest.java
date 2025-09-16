package com.betacom.retrogames;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.betacom.retrogames.cache.CacheManager;
import com.betacom.retrogames.cache.implementations.CachedRuolo;
import com.betacom.retrogames.dto.RuoloDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.model.Ruolo;
import com.betacom.retrogames.repository.RuoloRepository;
import com.betacom.retrogames.request.RuoloReq;
import com.betacom.retrogames.service.interfaces.RuoloService;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Transactional
@SpringBootTest
public class RuoloControllerTest 
{
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RuoloService ruoloService;

    @Autowired
    private RuoloRepository ruoloRepo;

    @Test
    void testCreaSuccesso() throws AcademyException 
    {
        RuoloReq req = new RuoloReq();
        String ruoloNome = "Admin";  

        // Normalizzo il nome a maiuscolo per evitare discrepanze case-sensitive
        req.setNome(ruoloNome.toUpperCase());

        // Verifico che il ruolo non esista già
        Optional<Ruolo> ruoloEsistente = ruoloRepo.findByNome(req.getNome());
        if (ruoloEsistente.isPresent()) 
        {
            ruoloRepo.delete(ruoloEsistente.get());
        }

        Integer id = ruoloService.crea(req);

        // Verifico che l'id restituito non sia nullo
        assertNotNull(id);

        // Ottengo il ruolo creato
        RuoloDTO dto = ruoloService.getById(id);
        
        // Verifico che il nome sia stato salvato come ci aspettiamo e sia attivo
        assertEquals(ruoloNome.toUpperCase(), dto.getNome()); 
        assertTrue(dto.getAttivo());
    }

    @Test
    void testAggiornaSuccesso() throws AcademyException 
    {
        Optional<Ruolo> ruoloOpt = ruoloRepo.findByNome("Admin");
        
        // Verifico che il ruolo esista prima di tentare di aggiornarlo
        assertTrue(ruoloOpt.isPresent(), "Il ruolo 'Admin' non esiste nel database.");
        
        // Ottengo il ruolo
        Ruolo ruolo = ruoloOpt.orElseThrow();  
        cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.RUOLO, new CachedRuolo(ruolo));
        
        RuoloReq req = new RuoloReq();
        req.setId(ruolo.getId());
        req.setNome("AdminAggiornato");
        
        // Aggiorno il ruolo con il nuovo nome
        ruoloService.aggiorna(req);  
        
        // Verifico l'aggiornamento
        RuoloDTO updated = ruoloService.getById(ruolo.getId());
        assertEquals("AdminAggiornato", updated.getNome());  
    }

    @Test
    void testAggiornaNonEsistente() 
    {
        RuoloReq req = new RuoloReq();
        req.setId(999);
        req.setNome("FakeRole");
        
        assertThrows(AcademyException.class, () -> ruoloService.aggiorna(req));
    }

    @Test
    void testDisattivaSuccesso() throws AcademyException 
    {
        RuoloReq req = new RuoloReq();
        
        // Supponiamo che esista un ruolo con id 1
        req.setId(1); 
        ruoloService.disattiva(req);
        
        // Verifico che il ruolo sia stato disattivato correttamente
        assertFalse(cacheManager.isRecordCached(TabellaCostante.RUOLO, req.getId()));
    }

    @Test
    void testDisattivaNonEsistenteLanciaEccezione() 
    {
        RuoloReq req = new RuoloReq();
        
        // Id inesistente
        req.setId(99); 
        assertThrows(AcademyException.class, () -> ruoloService.disattiva(req));
    }

    @Test
    void testListActive() 
    {
        List<RuoloDTO> lista = ruoloService.listActive();
        
        // Verifico che la lista non sia vuota
        assertFalse(lista.isEmpty());
        
        // Verifico che tutti i ruoli siano attivi
        assertTrue(lista.stream().allMatch(RuoloDTO::getAttivo));
    }
}