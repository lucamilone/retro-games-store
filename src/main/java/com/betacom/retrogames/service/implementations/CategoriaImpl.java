package com.betacom.retrogames.service.implementations;

import static com.betacom.retrogames.util.Utils.normalizza;

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

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CategoriaImpl implements CategoriaService {
	private final CacheManager cacheManager;
	private final MessaggioService msgS;
	private final CategoriaRepository categoriaRepo;
	private final CategoriaMapper categoriaMapper;

	public CategoriaImpl(CacheManager cacheManager, MessaggioService msgS, CategoriaRepository categoriaRepo,
			CategoriaMapper categoriaMapper) {
		this.cacheManager = cacheManager;
		this.msgS = msgS;
		this.categoriaRepo = categoriaRepo;
		this.categoriaMapper = categoriaMapper;
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public Integer crea(CategoriaReq req) throws AcademyException {
		log.debug("Crea: {}", req);

		// Verifico se esiste già una categoria con lo stesso nome
		Optional<Categoria> categoriaOpt = categoriaRepo.findByNome(normalizza(req.getNome()));
		if (categoriaOpt.isPresent()) {
			throw new AcademyException(msgS.getMessaggio("categoria-esistente"));
		}

		Categoria categoria = new Categoria();
		categoria.setNome(normalizza(req.getNome()));
		categoria.setProdotti(null);
		categoria.setAttivo(true);

		// Salvo la categoria
		Categoria saved = categoriaRepo.save(categoria);

		// Creo il nuovo valore nella cache
		CachedCategoria categoriaNew = new CachedCategoria(saved);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, categoriaNew);

		log.debug("Categoria creata con successo con ID: {}", saved.getId());

		// Restituisce l'id generato
		return saved.getId();
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiorna(CategoriaReq req) throws AcademyException {
		log.debug("Aggiorna: {}", req);

		// Controllo se la categoria è presente nella cache
		if (!cacheManager.isRecordCached(TabellaCostante.CATEGORIA, req.getId())) {
			throw new AcademyException(msgS.getMessaggio("categoria-non-trovata"));
		}

		// Recupero dal DB per salvare i dati
		Categoria categoria = categoriaRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("categoria-non-trovata")));

		if (req.getNome() != null) {
			categoria.setNome(normalizza(req.getNome()));
		}

		if (req.getAttivo() != null) {
			categoria.setAttivo(req.getAttivo());
		}

		categoria.setProdotti(null);

		// Salvo la categoria aggiornata
		Categoria saved = categoriaRepo.save(categoria);

		// Aggiorno la cache
		CachedCategoria categoriaUpd = new CachedCategoria(saved);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, categoriaUpd);

		log.debug("Categoria aggiornata con successo con ID: {}", req.getId());
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void disattiva(CategoriaReq req) throws AcademyException {
		log.debug("Disattiva: {}", req);

		// Controllo se la categoria è presente nella cache
		if (!cacheManager.isRecordCached(TabellaCostante.CATEGORIA, req.getId())) {
			throw new AcademyException(msgS.getMessaggio("categoria-non-trovata"));
		}

		// Recupero dal DB per salvare i dati
		Categoria categoria = categoriaRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("categoria-non-trovata")));

		// Disattivo la categoria
		categoria.setAttivo(false);

		// Salvo la categoria disattivata
		categoriaRepo.save(categoria);

		// Rimuovo il record dalla cache
		cacheManager.removeRecordFromCachedTable(TabellaCostante.CATEGORIA, req.getId());

		log.debug("Categoria disattivata con successo con ID: {}", req.getId());
	}

	@Override
	public CategoriaDTO getById(Integer id) throws AcademyException {
		log.debug("GetById: {}", id);

		// Controllo se la categoria è presente nella cache
		CachedCategoria cached = (CachedCategoria) cacheManager.getCachedEntryFromTable(TabellaCostante.CATEGORIA, id);

		if (cached != null) {
			// Se trovato in cache, ritorna DTO direttamente
			return categoriaMapper.toDtoFromCached(cached);
		}

		// Se non presente, recupero dal DB
		Categoria categoria = categoriaRepo.findById(id)
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("categoria-non-trovata")));

		// Aggiorno la cache
		CachedCategoria categoriaNew = new CachedCategoria(categoria);
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.CATEGORIA, categoriaNew);

		return categoriaMapper.toDto(categoria);
	}

	@Override
	public List<CategoriaDTO> listActive() {
		log.debug("ListActive");

		// Recupero solo le categorie attive
		List<Categoria> categorieAttive = categoriaRepo.findByAttivoTrue();

		return categoriaMapper.toDtoList(categorieAttive);
	}
}
