package com.betacom.retrogames.service.implementations;

import static com.betacom.retrogames.util.Utils.normalizza;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.cache.CacheManager;
import com.betacom.retrogames.cache.implementations.CachedTipoMetodoPagamento;
import com.betacom.retrogames.dto.TipoMetodoPagamentoDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.TipoMetodoPagamentoMapper;
import com.betacom.retrogames.model.TipoMetodoPagamento;
import com.betacom.retrogames.repository.TipoMetodoPagamentoRepository;
import com.betacom.retrogames.request.TipoMetodoPagamentoReq;
import com.betacom.retrogames.service.interfaces.MessaggioService;
import com.betacom.retrogames.service.interfaces.TipoMetodoPagamentoService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class TipoMetodoPagamentoImpl implements TipoMetodoPagamentoService {
	private final CacheManager cacheManager;
	private final MessaggioService msgS;
	private final TipoMetodoPagamentoRepository tipoRepo;
	private final TipoMetodoPagamentoMapper tipoMapper;

	public TipoMetodoPagamentoImpl(CacheManager cacheManager, MessaggioService msgS,
			TipoMetodoPagamentoRepository tipoRepo, TipoMetodoPagamentoMapper tipoMapper) {
		this.cacheManager = cacheManager;
		this.msgS = msgS;
		this.tipoRepo = tipoRepo;
		this.tipoMapper = tipoMapper;
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public TipoMetodoPagamentoDTO crea(TipoMetodoPagamentoReq req) throws AcademyException {
		log.debug("Crea: {}", req);

		// Verifico se esiste già un tipo con lo stesso nome
		Optional<TipoMetodoPagamento> opt = tipoRepo.findByNome(normalizza(req.getNome()));
		if (opt.isPresent()) {
			throw new AcademyException(msgS.getMessaggio("tipo-metodo-pagamento-esistente"));
		}

		TipoMetodoPagamento tipo = new TipoMetodoPagamento();
		tipo.setNome(normalizza(req.getNome()));
		tipo.setAttivo(true);

		// Salvo il tipo
		TipoMetodoPagamento saved = tipoRepo.save(tipo);

		// Aggiorno la cache
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.TIPO_METODO_PAGAMENTO,
				new CachedTipoMetodoPagamento(saved));

		log.debug("TipoMetodoPagamento creato con successo. ID: {}", saved.getId());

		return tipoMapper.toDto(tipo);
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiorna(TipoMetodoPagamentoReq req) throws AcademyException {
		log.debug("Aggiorna: {}", req);

		// Controllo se il tipo è presente nella cache
		if (!cacheManager.isRecordCached(TabellaCostante.TIPO_METODO_PAGAMENTO, req.getId())) {
			throw new AcademyException(msgS.getMessaggio("tipo-metodo-pagamento-non-trovato"));
		}

		// Recupero dal DB per salvare i dati
		TipoMetodoPagamento tipo = tipoRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("tipo-metodo-pagamento-non-trovato")));

		if (req.getNome() != null) {
			tipo.setNome(normalizza(req.getNome()));
		}
		if (req.getAttivo() != null) {
			tipo.setAttivo(req.getAttivo());
		}

		// Salvo il tipo aggiornato
		TipoMetodoPagamento saved = tipoRepo.save(tipo);

		// Aggiorno la cache
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.TIPO_METODO_PAGAMENTO,
				new CachedTipoMetodoPagamento(saved));

		log.debug("TipoMetodoPagamento aggiornato con successo. ID: {}", saved.getId());
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void disattiva(TipoMetodoPagamentoReq req) throws AcademyException {
		log.debug("Disattiva: {}", req);

		// Controllo se il tipo è presente nella cache
		if (!cacheManager.isRecordCached(TabellaCostante.TIPO_METODO_PAGAMENTO, req.getId())) {
			throw new AcademyException(msgS.getMessaggio("tipo-metodo-pagamento-non-trovato"));
		}

		// Recupero dal DB per salvare i dati
		TipoMetodoPagamento tipo = tipoRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("tipo-metodo-pagamento-non-trovato")));

		// Disattivo il tipo
		tipo.setAttivo(false);

		// Salvo il tipo disattivato
		tipoRepo.save(tipo);

		// Rimuovo il record dalla cache
		cacheManager.removeRecordFromCachedTable(TabellaCostante.TIPO_METODO_PAGAMENTO, req.getId());

		log.debug("TipoMetodoPagamento disattivato con successo. ID: {}", req.getId());
	}

	@Override
	public TipoMetodoPagamentoDTO getById(Integer id) throws AcademyException {
		log.debug("GetById: {}", id);

		// Controllo se il tipo è presente nella cache
		CachedTipoMetodoPagamento cached = (CachedTipoMetodoPagamento) cacheManager
				.getCachedEntryFromTable(TabellaCostante.TIPO_METODO_PAGAMENTO, id);

		if (cached != null) {
			// Se trovato in cache, ritorna DTO direttamente
			return tipoMapper.toDtoFromCached(cached);
		}

		// Se non presente, recupero dal DB
		TipoMetodoPagamento tipo = tipoRepo.findById(id)
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("tipo-metodo-pagamento-non-trovato")));

		// Aggiorno la cache
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.TIPO_METODO_PAGAMENTO,
				new CachedTipoMetodoPagamento(tipo));

		return tipoMapper.toDto(tipo);
	}

	@Override
	public List<TipoMetodoPagamentoDTO> listActive() {
		log.debug("ListActive");

		// Recupero solo i tipi attivi
		List<TipoMetodoPagamento> tipiAttivi = tipoRepo.findByAttivoTrue();

		return tipoMapper.toDtoList(tipiAttivi);
	}
}