package com.betacom.retrogames.service.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.cache.CacheManager;
import com.betacom.retrogames.cache.implementations.CachedCategoria;
import com.betacom.retrogames.dto.CategoriaDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.CategoriaMapper;
import com.betacom.retrogames.model.Categoria;
import com.betacom.retrogames.repository.CategoriaRepository;
import com.betacom.retrogames.request.CategoriaReq;
import com.betacom.retrogames.service.interfaces.CategoriaService;
import com.betacom.retrogames.service.interfaces.MessaggioService;
import com.betacom.retrogames.util.Utils;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CategoriaImpl extends Utils implements CategoriaService 
{
	private final CacheManager cacheManager;
	private final MessaggioService msgS;
	private final CategoriaRepository categoriaRepo;
	private final CategoriaMapper categoriaMapper;

	public CategoriaImpl(CacheManager cacheManager, MessaggioService msgS, CategoriaRepository categoriaRepo, CategoriaMapper categoriaMapper) 
	{
		this.cacheManager = cacheManager;
		this.msgS = msgS;
		this.categoriaRepo = categoriaRepo;
		this.categoriaMapper = categoriaMapper;
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public Integer crea(CategoriaReq req) throws AcademyException 
	{
		log.debug("Crea Categoria: {}", req);

		// Verifico se esiste già una categoria con lo stesso nome
		Optional<Categoria> categoriaOpt = categoriaRepo.findByNome(req.getNome());
		if (categoriaOpt.isPresent()) 
		{
			throw new AcademyException(msgS.getMessaggio("categoria-esiste"));
		}

		Categoria categoria = new Categoria();
		
		categoria.setNome(req.getNome());
		categoria.setProdotti(null);
		categoria.setAttivo(true);

		// Salvo la categoria
		Categoria saved = categoriaRepo.save(categoria);

		// Creo il nuovo valore nella cache
		CachedCategoria categoriaNew = new CachedCategoria(saved);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, categoriaNew);

		log.debug("Categoria creata con successo con Id: {}", saved.getId());
		
		// Restituisce l'id generato
		return saved.getId();
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiorna(CategoriaReq req) throws AcademyException 
	{
		log.debug("Aggiorna Categoria: {}", req);

		// Controllo rapido in cache
		if (!cacheManager.isRecordCached(TabellaCostante.CATEGORIA, req.getId())) 
		{
			throw new AcademyException(msgS.getMessaggio("categoria-non-esiste"));
		}

		// Recupero dal DB per salvare i dati
		Categoria categoria = categoriaRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("categoria-non-esiste")));
		
		if(req.getNome() != null)
		{
			categoria.setNome(req.getNome());
		}
		if(req.getAttivo() != null)
		{
			categoria.setAttivo(req.getAttivo());
		}
		
		categoria.setProdotti(null);

		// Salvo la categoria aggiornata
		Categoria saved = categoriaRepo.save(categoria);

		// Aggiorno la cache
		CachedCategoria categoriaUpd = new CachedCategoria(saved);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, categoriaUpd);

		log.debug("Categoria aggiornata con successo con Id: {}", req.getId());
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void disattiva(CategoriaReq req) throws AcademyException 
	{
		log.debug("Disattiva Categoria: {}", req);

		// Controllo rapido in cache
		if (!cacheManager.isRecordCached(TabellaCostante.CATEGORIA, req.getId())) 
		{
			throw new AcademyException(msgS.getMessaggio("categoria-non-esiste"));
		}

		// Recupero dal DB per salvare i dati
		Categoria categoria = categoriaRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("categoria-non-esiste")));

		// Disattivo la categoria
		categoria.setAttivo(false);
		
		// Salvo la categoria disattivata
		categoriaRepo.save(categoria);

		// Rimuovo il record dalla cache
		cacheManager.removeRecordFromCachedTable(TabellaCostante.CATEGORIA, req.getId());

		log.debug("Categoria disattivata con successo con Id: {}", req.getId());
	}

	@Override
	public CategoriaDTO getById(Integer id) throws AcademyException
	{
		log.debug("GetById Categoria: {}", id);

		// Controllo in cache
		CachedCategoria cached = (CachedCategoria) cacheManager.getCachedEntryFromTable(TabellaCostante.CATEGORIA, id);

		if (cached != null)
		{
			// Se trovato in cache, ritorna DTO direttamente
			return categoriaMapper.toDtoFromCached(cached);
		}

		// Se non presente, recupero dal DB
		Categoria categoria = categoriaRepo.findById(id)
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("categoria-non-esiste")));

		// Aggiorno la cache
		CachedCategoria nuova = new CachedCategoria(categoria);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, nuova);

		// Converto la categoria trovata in un DTO e lo restituisco
		return categoriaMapper.toDto(categoria);
	}

	@Override
	public List<CategoriaDTO> listActive() 
	{
		log.debug("ListActive Categorie");

		// Recupero solo le categorie attive
		List<Categoria> piattaformeAttive = categoriaRepo.findByAttivoTrue();
		return categoriaMapper.toDtoList(piattaformeAttive);
	}
}
