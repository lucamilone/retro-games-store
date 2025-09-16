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
import com.betacom.retrogames.cache.implementations.CachedCategoria;
import com.betacom.retrogames.dto.CategoriaDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.model.Categoria;
import com.betacom.retrogames.repository.CategoriaRepository;
import com.betacom.retrogames.request.CategoriaReq;
import com.betacom.retrogames.service.interfaces.CategoriaService;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Transactional
@SpringBootTest
public class CategoriaControllerTest 
{
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CategoriaRepository categoriaRepo;

    @Test
    void testCreaSuccesso() throws AcademyException 
    {
        CategoriaReq req = new CategoriaReq();
        String nomeCategoria = "Action";

        // Imposto il nome e altre proprietà per la categoria
        req.setNome(nomeCategoria);
        req.setAttivo(true);

        // Verifico che la categoria non esista già
        Optional<Categoria> categoriaEsistente = categoriaRepo.findByNome(req.getNome());
        categoriaEsistente.ifPresent(categoriaRepo::delete);

        Integer id = categoriaService.crea(req);

        // Verifico che l'id restituito non sia nullo
        assertNotNull(id);

        // Ottengo la categoria creata
        CategoriaDTO dto = categoriaService.getById(id);

        // Verifico che il nome e la categoria siano salvati correttamente
        assertEquals(nomeCategoria, dto.getNome());
        assertTrue(dto.getAttivo());
    }

    @Test
    void testAggiornaSuccesso() throws AcademyException 
    {
        // Crea una categoria di test
        Categoria categoria = new Categoria();
        categoria.setNome("Action");
        categoria.setAttivo(true);
        categoria = categoriaRepo.save(categoria);

        cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

        CategoriaReq req = new CategoriaReq();
        req.setId(categoria.getId());
        req.setNome("Adventure");

        // Aggiorno la categoria
        categoriaService.aggiorna(req);

        CategoriaDTO updated = categoriaService.getById(categoria.getId());
        assertEquals("Adventure", updated.getNome());
    }

    @Test
    void testAggiornaNonEsistente() 
    {
        CategoriaReq req = new CategoriaReq();

        // Id che non esiste
        req.setId(99);
        req.setNome("NonEsistente");

        assertThrows(AcademyException.class, () -> categoriaService.aggiorna(req));
    }

    @Test
    public void testDisattivaSuccesso() throws AcademyException
    {
    	CategoriaReq req = new CategoriaReq();

        // Supponiamo che esista una piattaforma con id 1
        req.setId(1);
        categoriaService.disattiva(req);

        // Verifico che la piattaforma sia stata disattivata correttamente
        assertFalse(cacheManager.isRecordCached(TabellaCostante.CATEGORIA, req.getId()));
    }

    @Test
    void testDisattivaNonEsistenteLanciaEccezione() 
    {
        CategoriaReq req = new CategoriaReq();

        // Id inesistente
        req.setId(99);
        assertThrows(AcademyException.class, () -> categoriaService.disattiva(req));
    }

    @Test
    void testListActive() 
    {
        Categoria categoria1 = new Categoria();
        categoria1.setNome("Action");
        categoria1.setAttivo(true);
        categoriaRepo.save(categoria1);

        Categoria categoria2 = new Categoria();
        categoria2.setNome("Adventure");
        categoria2.setAttivo(false);
        categoriaRepo.save(categoria2);

        List<CategoriaDTO> lista = categoriaService.listActive();

        // Verifico che la lista non sia vuota
        assertFalse(lista.isEmpty());

        // Verifico che tutte le categorie siano attive
        assertTrue(lista.stream().allMatch(CategoriaDTO::getAttivo));
    }
}