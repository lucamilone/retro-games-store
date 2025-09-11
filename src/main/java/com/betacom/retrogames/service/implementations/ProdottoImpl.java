package com.betacom.retrogames.service.implementations;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

import com.betacom.retrogames.cache.CacheManager;
import com.betacom.retrogames.dto.ProdottoDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.ProdottoMapper;
import com.betacom.retrogames.model.Categoria;
import com.betacom.retrogames.model.Piattaforma;
import com.betacom.retrogames.model.Prodotto;
import com.betacom.retrogames.repository.CategoriaRepository;
import com.betacom.retrogames.repository.PiattaformaRepository;
import com.betacom.retrogames.repository.ProdottoRepository;
import com.betacom.retrogames.request.ProdottoReq;
import com.betacom.retrogames.service.interfaces.MessaggioService;
import com.betacom.retrogames.service.interfaces.ProdottoService;
import com.betacom.retrogames.util.Utils;

import lombok.extern.log4j.Log4j2;

import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
public class ProdottoImpl extends Utils implements ProdottoService 
{
    private final ProdottoRepository prodottoRepo;
    private final CategoriaRepository categoriaRepo;
    private final PiattaformaRepository piattaformaRepo;
    private final MessaggioService msgS;
    private final ProdottoMapper prodottoMapper;
    private final CacheManager cacheManager;

    public ProdottoImpl(ProdottoRepository prodottoRepo, CategoriaRepository categoriaRepo, PiattaformaRepository piattaformaRepo, MessaggioService msgS, ProdottoMapper prodottoMapper, CacheManager cacheManager) 
    {
        this.prodottoRepo = prodottoRepo;
        this.categoriaRepo = categoriaRepo;
        this.piattaformaRepo = piattaformaRepo;
        this.msgS = msgS;
        this.prodottoMapper = prodottoMapper;
        this.cacheManager = cacheManager;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    @Override
    public Integer crea(ProdottoReq req) throws AcademyException 
    {
        log.debug("Crea: {}", req);

        // Verifico se esiste già un prodotto con lo stesso SKU
        if (prodottoRepo.findBySku(req.getSku()).isPresent()) 
        {
            throw new AcademyException(msgS.getMessaggio("prodotto-sku-esistente"));
        }

        // Controllo se la categoria è presente nella cache
        if (!cacheManager.isRecordCached(TabellaCostante.CATEGORIA, req.getCategoriaId())) 
        {
            throw new AcademyException(msgS.getMessaggio("categoria-non-esiste"));
        }

        // Controllo se tutte le piattaforme sono presenti nella cache, perchè un prodotto può far parte di più piattaforme
        for (Integer piattaformaId : req.getPiattaformaId()) 
        {
            if (!cacheManager.isRecordCached(TabellaCostante.PIATTAFORMA, piattaformaId)) 
            {
                throw new AcademyException(msgS.getMessaggio("piattaforma-non-esiste"));
            }
        }

        // Recupero la categoria dal database
        Categoria categoria = categoriaRepo.findById(req.getCategoriaId())
            .orElseThrow(() -> new AcademyException(msgS.getMessaggio("categoria-non-esiste")));

        // Recupero le piattaforme dal database
        Set<Piattaforma> piattaforme = piattaformaRepo.findAllById(req.getPiattaformaId())
            .stream().collect(Collectors.toSet());

        // Creo il nuovo prodotto
        Prodotto prodotto = new Prodotto();
        
        prodotto.setSku(req.getSku());
        prodotto.setNome(req.getNome());
        prodotto.setCategoria(categoria);
        prodotto.setPiattaforme(piattaforme);
        prodotto.setDescrizione(req.getDescrizione());
        prodotto.setAnnoUscita(req.getAnnoUscita());
        prodotto.setPrezzo(req.getPrezzo());   
        prodotto.setImgUrl(req.getImgUrl());
        prodotto.setAttivo(true);

        // Salvo il prodotto
        prodotto = prodottoRepo.save(prodotto);
        
        log.debug("Prodotto creato con successo con Id: {}", prodotto.getId());
        
        // Restituisco l'id generato
        return prodotto.getId();
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    @Override
    public void aggiorna(ProdottoReq req) throws AcademyException 
    {
        log.debug("Aggiorna: {}", req);

        // Verifico l'esistenza del prodotto
        Prodotto prodotto = prodottoRepo.findById(req.getId())
            .orElseThrow(() -> new AcademyException(msgS.getMessaggio("prodotto-non-esiste")));

        // Verifico la categoria in cache
        if (!cacheManager.isRecordCached(TabellaCostante.CATEGORIA, req.getCategoriaId())) 
        {
            throw new AcademyException(msgS.getMessaggio("categoria-non-esiste"));
        }

        // Verifico tutte le piattaforme in cache
        for (Integer piattaformaId : req.getPiattaformaId()) 
        {
            if (!cacheManager.isRecordCached(TabellaCostante.PIATTAFORMA, piattaformaId)) 
            {
                throw new AcademyException(msgS.getMessaggio("piattaforma-non-esiste"));
            }
        }

        // Recupero la categoria
        Categoria categoria = categoriaRepo.findById(req.getCategoriaId())
            .orElseThrow(() -> new AcademyException(msgS.getMessaggio("categoria-non-esiste")));

        // Recupero le piattaforme
        Set<Piattaforma> piattaforme = piattaformaRepo.findAllById(req.getPiattaformaId())
            .stream().collect(Collectors.toSet());

        // Aggiorno i dati del prodotto
        prodotto.setNome(req.getNome());
        prodotto.setCategoria(categoria);
        prodotto.setPiattaforme(piattaforme);
        prodotto.setDescrizione(req.getDescrizione());
        prodotto.setAnnoUscita(req.getAnnoUscita());
        prodotto.setImgUrl(req.getImgUrl());
        prodotto.setPrezzo(req.getPrezzo());

        // Salvo il prodotto aggiornato
        prodottoRepo.save(prodotto);
        
        log.debug("Prodotto modificato con successo con Id: {}", req.getId());
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    @Override
    public void disattiva(ProdottoReq req) throws AcademyException
    {
        log.debug("Disattiva: {}", req);

        // Verifico l'esistenza del prodotto
        Prodotto prodotto = prodottoRepo.findById(req.getId())
            .orElseThrow(() -> new AcademyException(msgS.getMessaggio("prodotto-non-esiste")));

        // Disattivo il prodotto
        prodotto.setAttivo(false);
        
        prodottoRepo.save(prodotto);
        
        log.debug("Prodotto disattivato con successo con Id: {}", req.getId());
    }

    @Override
    public ProdottoDTO getById(Integer id) throws AcademyException 
    {
        log.debug("GetById: {}", id);

        // Recupero il prodotto dal database
        Prodotto prodotto = prodottoRepo.findById(id)
            .orElseThrow(() -> new AcademyException(msgS.getMessaggio("prodotto-non-esiste")));

        // Converto in DTO e lo restituisco
        return prodottoMapper.toDto(prodotto);
    }

    @Override
    public List<ProdottoDTO> listActive() 
    {
        log.debug("ListActive");

        // Recupero tutti i prodotti attivi
        List<Prodotto> prodotti = prodottoRepo.findByAttivoTrue();
        
        // Converto in lista di DTO e restituisco
        return prodottoMapper.toDtoList(prodotti);
    }
}