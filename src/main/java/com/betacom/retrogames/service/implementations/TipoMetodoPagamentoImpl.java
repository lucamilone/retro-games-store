package com.betacom.retrogames.service.implementations;

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
import static com.betacom.retrogames.util.Utils.normalizza;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class TipoMetodoPagamentoImpl implements TipoMetodoPagamentoService 
{
	private final CacheManager cacheManager;
	private final MessaggioService msgS;
	private final TipoMetodoPagamentoRepository tipoRepo;
	private final TipoMetodoPagamentoMapper tipoMapper;

	public TipoMetodoPagamentoImpl(CacheManager cacheManager, MessaggioService msgS, TipoMetodoPagamentoRepository tipoRepo, TipoMetodoPagamentoMapper tipoMapper)
	{
		this.cacheManager = cacheManager;
		this.msgS = msgS;
		this.tipoRepo = tipoRepo;
		this.tipoMapper = tipoMapper;
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public Integer crea(TipoMetodoPagamentoReq req) throws AcademyException 
	{
		log.debug("Crea: {}", req);

		// Verifico se esiste già un tipo con lo stesso nome
		Optional<TipoMetodoPagamento> opt = tipoRepo.findByNome(req.getNome());
		if (opt.isPresent()) 
		{
			throw new AcademyException(msgS.getMessaggio("tipo-metodo-pagamento-esiste"));
		}

		TipoMetodoPagamento tipo = new TipoMetodoPagamento();
		
		tipo.setNome(normalizza(req.getNome()));
		tipo.setAttivo(true);

		// Salvataggio del tipo
		TipoMetodoPagamento saved = tipoRepo.save(tipo);

		// Aggiorno la cache
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.TIPO_METODO_PAGAMENTO, new CachedTipoMetodoPagamento(saved));

		log.debug("TipoMetodoPagamento creato con successo con Id: {}", saved.getId());
		
		// Restituisce l'id generato
		return saved.getId();
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiorna(TipoMetodoPagamentoReq req) throws AcademyException 
	{
		log.debug("Aggiorna: {}", req);

		// Controllo rapido in cache
		if (!cacheManager.isRecordCached(TabellaCostante.TIPO_METODO_PAGAMENTO, req.getId())) 
		{
			throw new AcademyException(msgS.getMessaggio("tipo-metodo-pagamento-non-esiste"));
		}

		// Recupero dal DB per salvare i dati
		TipoMetodoPagamento tipo = tipoRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("tipo-metodo-pagamento-non-esiste")));

		if (req.getNome() != null) 
		{
			tipo.setNome(normalizza(req.getNome()));
		}
		if (req.getAttivo() != null) 
		{
			tipo.setAttivo(req.getAttivo());
		}

		// Salvataggio del tipo
		TipoMetodoPagamento saved = tipoRepo.save(tipo);

		// Aggiorno la cache
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.TIPO_METODO_PAGAMENTO, new CachedTipoMetodoPagamento(saved));

		log.debug("TipoMetodoPagamento aggiornato con successo con Id: {}", saved.getId());
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void disattiva(TipoMetodoPagamentoReq req) throws AcademyException 
	{
		log.debug("Disattiva: {}", req);

		// Controllo rapido in cache
		if (!cacheManager.isRecordCached(TabellaCostante.TIPO_METODO_PAGAMENTO, req.getId())) 
		{
			throw new AcademyException(msgS.getMessaggio("tipo-metodo-pagamento-non-esiste"));
		}

		// Recupero dal DB per salvare i dati
		TipoMetodoPagamento tipo = tipoRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("tipo-metodo-pagamento-non-esiste")));

		// Disattivo il tipo
		tipo.setAttivo(false);

		// Salvataggio del tipo
		tipoRepo.save(tipo);

		// Rimuovo il record dalla cache
		cacheManager.removeRecordFromCachedTable(TabellaCostante.TIPO_METODO_PAGAMENTO, req.getId());

		log.debug("TipoMetodoPagamento disattivato con successo con Id: {}", req.getId());
	}

	@Override
	public TipoMetodoPagamentoDTO getById(Integer id) throws AcademyException 
	{
		log.debug("GetById: {}", id);

		// Controllo in cache
		CachedTipoMetodoPagamento cached = (CachedTipoMetodoPagamento) cacheManager.getCachedEntryFromTable(TabellaCostante.TIPO_METODO_PAGAMENTO, id);

		if (cached != null) 
		{
			// Se trovato in cache, ritorna DTO direttamente
			return tipoMapper.toDtoFromCached(cached);
		}

		// Se non presente, recupero dal DB
		TipoMetodoPagamento tipo = tipoRepo.findById(id)
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("tipo-metodo-pagamento-non-esiste")));

		// Aggiorno la cache
		cacheManager.addOrUpdateRecordInCachedTable(TabellaCostante.TIPO_METODO_PAGAMENTO, new CachedTipoMetodoPagamento(tipo));

		return tipoMapper.toDto(tipo);
	}

	@Override
	public List<TipoMetodoPagamentoDTO> listActive() 
	{
		log.debug("ListActive");
		
		// Recupero solo i tipi attivi
		List<TipoMetodoPagamento> attivi = tipoRepo.findByAttivoTrue();
		
		return tipoMapper.toDtoList(attivi);
	}
}