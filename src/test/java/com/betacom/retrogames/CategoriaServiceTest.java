package com.betacom.retrogames;

import static com.betacom.retrogames.util.Utils.normalizza;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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

@SpringBootTest
@Transactional
public class CategoriaServiceTest 
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
        req.setNome("Action");
        req.setAttivo(true);

        // Verifico che la categoria non esista già
        Categoria existingCategory = categoriaRepo.findByNome(normalizza(req.getNome())).orElse(null);
        if (existingCategory != null) 
        {
            categoriaRepo.delete(existingCategory);
        }

        // Creo la categoria
        Integer id = categoriaService.crea(req);

        // Verifico che l'id non sia nullo
        assertNotNull(id);

        // Recupero la categoria creata
        CategoriaDTO dto = categoriaService.getById(id);

        // Verifico che il nome e la categoria siano correttamente salvati
        assertEquals(normalizza("Action"), dto.getNome());
        assertTrue(dto.getAttivo());
    }

    @Test
    void testCreaGiaEsistente() throws AcademyException 
    {
        CategoriaReq req = new CategoriaReq();
        req.setNome("Action");
        req.setAttivo(true);

        // Creiamo una categoria
        categoriaService.crea(req);

        // Verifico che non posso creare una categoria con lo stesso nome
        assertThrows(AcademyException.class, () -> categoriaService.crea(req));
    }

    @Test
    void testAggiornaSuccesso() throws AcademyException 
    {
        Categoria categoria = new Categoria();
        categoria.setNome(normalizza("Action"));
        categoria.setAttivo(true);
        categoria = categoriaRepo.save(categoria);

        // Aggiungo la categoria alla cache
        cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

        CategoriaReq req = new CategoriaReq();
        req.setId(categoria.getId());
        req.setNome(normalizza("Adventure"));

        // Aggiorno la categoria
        categoriaService.aggiorna(req);

        CategoriaDTO updated = categoriaService.getById(categoria.getId());

        // Verifico che il nome sia stato aggiornato correttamente
        assertEquals(normalizza("Adventure"), updated.getNome());
    }

    @Test
    void testAggiornaNonEsistente() 
    {
        CategoriaReq req = new CategoriaReq();
        req.setId(9999);  // ID non esistente
        req.setNome("NonEsistente");

        // Verifico che venga lanciata un'eccezione se cerco di aggiornare una categoria inesistente
        assertThrows(AcademyException.class, () -> categoriaService.aggiorna(req));
    }

    @Test
    void testDisattivaSuccesso() throws AcademyException 
    {
        // Creo una categoria di test con tutti i campi obbligatori
        Categoria categoria = new Categoria();
        categoria.setNome(normalizza("Test Categoria"));
        categoria.setAttivo(true);
        categoria = categoriaRepo.save(categoria);

        // Aggiungo la categoria alla cache
        cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, new CachedCategoria(categoria));

        CategoriaReq req = new CategoriaReq();
        req.setId(categoria.getId());

        // Disattivo la categoria
        categoriaService.disattiva(req);

        // Recupero la categoria aggiornata dal DB
        Categoria categoriaDisattivata = categoriaRepo.findById(categoria.getId())
            .orElseThrow(() -> new RuntimeException("Categoria non trovata"));

        // Verifico che la categoria sia stata disattivata correttamente
        assertFalse(categoriaDisattivata.getAttivo());
    }

    @Test
    void testDisattivaNonEsistente() 
    {
        CategoriaReq req = new CategoriaReq();
        req.setId(999); 

        // Verifico che venga lanciata un'eccezione se cerco di disattivare una categoria inesistente
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

        // Verifico che la lista di categorie attive non sia vuota
        List<CategoriaDTO> lista = categoriaService.listActive();
        assertFalse(lista.isEmpty());

        // Verifico che tutte le categorie siano attive
        assertTrue(lista.stream().allMatch(CategoriaDTO::getAttivo));
    }
}