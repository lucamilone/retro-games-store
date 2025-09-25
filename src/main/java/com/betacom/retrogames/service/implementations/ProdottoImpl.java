package com.betacom.retrogames.service.implementations;

import static com.betacom.retrogames.util.Utils.normalizza;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.cache.CacheManager;
import com.betacom.retrogames.dto.ProdottoDTO;
import com.betacom.retrogames.enums.CategoriaEnum;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProdottoImpl implements ProdottoService {
	private final CacheManager cacheManager;
	private final ProdottoRepository prodottoRepo;
	private final CategoriaRepository categoriaRepo;
	private final PiattaformaRepository piattaformaRepo;
	private final MessaggioService msgS;
	private final ProdottoMapper prodottoMapper;

	public ProdottoImpl(CacheManager cacheManager, ProdottoRepository prodottoRepo, CategoriaRepository categoriaRepo,
			PiattaformaRepository piattaformaRepo, MessaggioService msgS, ProdottoMapper prodottoMapper) {
		this.cacheManager = cacheManager;
		this.prodottoRepo = prodottoRepo;
		this.categoriaRepo = categoriaRepo;
		this.piattaformaRepo = piattaformaRepo;
		this.msgS = msgS;
		this.prodottoMapper = prodottoMapper;
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public ProdottoDTO crea(ProdottoReq req) throws AcademyException {
		log.debug("Crea: {}", req);

		// Verifico se esiste già un prodotto con lo stesso SKU
		Optional<Prodotto> prodottoOpt = prodottoRepo.findBySku(normalizza(req.getSku()));
		if (prodottoOpt.isPresent()) {
			throw new AcademyException(msgS.getMessaggio("prodotto-sku-esistente"));
		}

		// Controllo se la categoria è presente nella cache
		if (!cacheManager.isRecordCached(TabellaCostante.CATEGORIA, req.getCategoriaId())) {
			throw new AcademyException(msgS.getMessaggio("categoria-non-trovata"));
		}

		// Recupero la categoria dal DB per salvare i dati
		Categoria categoria = categoriaRepo.findById(req.getCategoriaId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("categoria-non-trovata")));

		Prodotto prodotto = new Prodotto();
		prodotto.setSku(normalizza(req.getSku()));
		prodotto.setNome(req.getNome());
		prodotto.setCategoria(categoria);
		prodotto.setDescrizione(req.getDescrizione());
		prodotto.setAnnoUscita(req.getAnnoUscita());
		prodotto.setPrezzo(req.getPrezzo());
		if (req.getImgUrl() != null) {
			prodotto.setImgUrl(req.getImgUrl());
		}
		prodotto.setAttivo(true);

		validatePiattaforme(req, prodotto, categoria);

		// Salvo il prodotto
		prodotto = prodottoRepo.save(prodotto);

		log.debug("Prodotto creato con successo. ID: {}", prodotto.getId());

		return prodottoMapper.toDto(prodotto);
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiorna(ProdottoReq req) throws AcademyException {
		log.debug("Aggiorna: {}", req);

		// Verifico l'esistenza del prodotto
		Prodotto prodotto = prodottoRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("prodotto-non-trovato")));

		if (req.getSku() != null) {
			prodotto.setSku(normalizza(req.getSku()));
		}

		if (req.getNome() != null) {
			prodotto.setNome(req.getNome());
		}

		if (req.getCategoriaId() != null) {
			// Controllo se la categoria è presente nella cache
			if (!cacheManager.isRecordCached(TabellaCostante.CATEGORIA, req.getCategoriaId())) {
				throw new AcademyException(msgS.getMessaggio("categoria-non-trovata"));
			}

			// Recupero dal DB per salvare i dati
			Categoria categoria = categoriaRepo.findById(req.getCategoriaId())
					.orElseThrow(() -> new AcademyException(msgS.getMessaggio("categoria-non-trovata")));

			prodotto.setCategoria(categoria);
			validatePiattaforme(req, prodotto, categoria);
		}

		if (req.getDescrizione() != null) {
			prodotto.setDescrizione(req.getDescrizione());
		}

		if (req.getAnnoUscita() != null) {
			prodotto.setAnnoUscita(req.getAnnoUscita());
		}

		if (req.getPrezzo() != null) {
			prodotto.setPrezzo(req.getPrezzo());
		}

		if (req.getImgUrl() != null) {
			prodotto.setImgUrl(req.getImgUrl());
		}

		if (req.getAttivo() != null) {
			prodotto.setAttivo(req.getAttivo());
		}

		// Salvo il prodotto aggiornato
		prodottoRepo.save(prodotto);

		log.debug("Prodotto aggiornato con successo. ID: {}", req.getId());
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void disattiva(ProdottoReq req) throws AcademyException {
		log.debug("Disattiva: {}", req);

		// Verifico l'esistenza del prodotto
		Prodotto prodotto = prodottoRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("prodotto-non-trovato")));

		// Disattivo il prodotto
		prodotto.setAttivo(false);

		// Salvo il prodotto disattivato
		prodottoRepo.save(prodotto);

		log.debug("Prodotto disattivato con successo. ID: {}", req.getId());
	}

	@Override
	public ProdottoDTO getById(Integer id) throws AcademyException {
		log.debug("GetById: {}", id);

		// Recupero il prodotto dal DB
		Prodotto prodotto = prodottoRepo.findById(id)
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("prodotto-non-trovato")));

		return prodottoMapper.toDto(prodotto);
	}

	@Override
	public List<ProdottoDTO> listActive() {
		log.debug("ListActive");

		// Recupero solo i prodotti attivi
		List<Prodotto> prodottiAttivi = prodottoRepo.findByAttivoTrue();

		return prodottoMapper.toDtoList(prodottiAttivi);
	}

	/**
	 * Assegna la categoria al prodotto e valida eventuali piattaforme
	 * (obbligatorie se la categoria è VIDEOGIOCHI)
	 */
	private void validatePiattaforme(ProdottoReq req, Prodotto prodotto, Categoria categoria) throws AcademyException {
		// Mappo il nome della categoria preso dal DB in Enum
		CategoriaEnum categoriaEnum = CategoriaEnum.fromNomeSafe(categoria.getNome())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("categoria-non-supportata")));

		// Controllo specifico per videogiochi
		if (categoriaEnum == CategoriaEnum.VIDEOGIOCHI) {
			if (req.getPiattaformaId() == null || req.getPiattaformaId().isEmpty()) {
				throw new AcademyException(msgS.getMessaggio("piattaforma-videogioco-obbligatoria"));
			}
			prodotto.setPiattaforme(new HashSet<>(checkCachedPiattaforme(req)));
		} else {
			prodotto.setPiattaforme(new HashSet<>()); // Set vuoto se non è un videogioco
		}
	}

	private Set<Piattaforma> checkCachedPiattaforme(ProdottoReq req) throws AcademyException {
		Set<Integer> idsRichiesti = req.getPiattaformaId();

		// Controllo se le piattaforme sono presenti nella cache
		boolean allCached = idsRichiesti.stream()
				.allMatch(id -> cacheManager.isRecordCached(TabellaCostante.PIATTAFORMA, id));

		if (!allCached) {
			throw new AcademyException(msgS.getMessaggio("piattaforma-non-trovata"));
		}

		// Recupero effettivo dal DB
		Set<Piattaforma> piattaforme = piattaformaRepo.findAllById(idsRichiesti).stream().collect(Collectors.toSet());

		// Controllo che il DB abbia restituito tutte le piattaforme richieste
		if (piattaforme.size() != idsRichiesti.size()) {
			throw new AcademyException(msgS.getMessaggio("piattaforma-non-trovata"));
		}

		return piattaforme;
	}
}