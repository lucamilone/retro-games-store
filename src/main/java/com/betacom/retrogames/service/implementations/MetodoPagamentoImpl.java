package com.betacom.retrogames.service.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.cache.CacheManager;
import com.betacom.retrogames.dto.MetodoPagamentoDTO;
import com.betacom.retrogames.enums.TabellaCostante;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.MetodoPagamentoMapper;
import com.betacom.retrogames.model.Account;
import com.betacom.retrogames.model.MetodoPagamento;
import com.betacom.retrogames.model.TipoMetodoPagamento;
import com.betacom.retrogames.repository.AccountRepository;
import com.betacom.retrogames.repository.MetodoPagamentoRepository;
import com.betacom.retrogames.repository.TipoMetodoPagamentoRepository;
import com.betacom.retrogames.request.MetodoPagamentoReq;
import com.betacom.retrogames.service.interfaces.MessaggioService;
import com.betacom.retrogames.service.interfaces.MetodoPagamentoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MetodoPagamentoImpl implements MetodoPagamentoService {
	private final CacheManager cacheManager;
	private final MetodoPagamentoRepository metodoPagamentoRepo;
	private final AccountRepository accountRepo;
	private final TipoMetodoPagamentoRepository tipoMetodoPagamentoRepo;
	private final MessaggioService msgS;
	private final MetodoPagamentoMapper metodoPagamentoMapper;

	public MetodoPagamentoImpl(CacheManager cacheManager, MetodoPagamentoRepository metodoPagamentoRepo,
			AccountRepository accountRepo, TipoMetodoPagamentoRepository tipoMetodoPagamentoRepo, MessaggioService msgS,
			MetodoPagamentoMapper metodoPagamentoMapper) {
		this.cacheManager = cacheManager;
		this.metodoPagamentoRepo = metodoPagamentoRepo;
		this.accountRepo = accountRepo;
		this.tipoMetodoPagamentoRepo = tipoMetodoPagamentoRepo;
		this.msgS = msgS;
		this.metodoPagamentoMapper = metodoPagamentoMapper;
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public MetodoPagamentoDTO crea(MetodoPagamentoReq req) throws AcademyException {
		log.debug("Crea: {}", req);

		// Verifico l'esistenza dell'account
		Account account = accountRepo.findById(req.getAccountId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("account-non-trovato")));

		// Controllo se il tipo metodo pagamento è presente nella cache
		if (!cacheManager.isRecordCached(TabellaCostante.TIPO_METODO_PAGAMENTO, req.getTipoMetodoPagamentoId())) {
			throw new AcademyException(msgS.getMessaggio("tipo-metodo-pagamento-non-trovato"));
		}

		// Recupero dal DB per salvare i dati
		TipoMetodoPagamento tipo = tipoMetodoPagamentoRepo.findById(req.getTipoMetodoPagamentoId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("tipo-metodo-pagamento-non-trovato")));

		// Verifico l'esistenza del token
		Optional<MetodoPagamento> esistente = metodoPagamentoRepo.findByToken(req.getToken());
		if (esistente.isPresent()) {
			throw new AcademyException(msgS.getMessaggio("metodo-pagamento-duplicato"));
		}

		MetodoPagamento metodoPagamento = new MetodoPagamento();
		metodoPagamento.setAccount(account);
		metodoPagamento.setTipo(tipo);
		metodoPagamento.setToken(req.getToken());
		metodoPagamento.setAttivo(true);

		// Recupero tutti i metodi attivi dell'account
		List<MetodoPagamento> metodiAttivi = metodoPagamentoRepo.findByAccountIdAndAttivoTrue(account.getId());

		// Se è il primo metodo, lo imposto automaticamente come default
		if (metodiAttivi.isEmpty()) {
			metodoPagamento.setMetodoDefault(true);
		} else {
			// Se ci sono già altri metodi, uso il flag fornito nella request
			metodoPagamento.setMetodoDefault(req.getMetodoDefault() != null ? req.getMetodoDefault() : false);

			// Se è impostato come default, resetto gli altri metodi dell'account
			if (Boolean.TRUE.equals(metodoPagamento.getMetodoDefault())) {
				metodiAttivi.forEach(mp -> mp.setMetodoDefault(false));
				metodoPagamentoRepo.saveAll(metodiAttivi);
			}
		}

		// Salvo il metodo di pagamento
		metodoPagamento = metodoPagamentoRepo.save(metodoPagamento);

		log.debug("Metodo di pagamento creato con successo. ID: {}", metodoPagamento.getId());

		return metodoPagamentoMapper.toDto(metodoPagamento);
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void aggiorna(MetodoPagamentoReq req) throws AcademyException {
		log.debug("Aggiorna: {}", req);

		// Verifico l'esistenza del metodo di pagamento
		MetodoPagamento metodoPagamento = metodoPagamentoRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("metodo-pagamento-non-trovato")));

		// Se viene aggiornato il token, verifico che non sia duplicato
		if (req.getToken() != null && !req.getToken().equals(metodoPagamento.getToken())) {
			Optional<MetodoPagamento> duplicato = metodoPagamentoRepo.findByToken(req.getToken())
					.filter(mp -> !mp.getId().equals(req.getId()));

			if (duplicato.isPresent()) {
				throw new AcademyException(msgS.getMessaggio("metodo-pagamento-duplicato"));
			}

			metodoPagamento.setToken(req.getToken());
		}

		// Aggiorno il flag metodoDefault
		if (req.getMetodoDefault() != null && req.getMetodoDefault() != metodoPagamento.getMetodoDefault()) {
			if (Boolean.TRUE.equals(req.getMetodoDefault())) {
				// Resetto gli altri metodi dell'account
				resetDefaultMetodo(metodoPagamento);
				metodoPagamento.setMetodoDefault(true);
			} else {
				metodoPagamento.setMetodoDefault(false);
			}
		}

		if (req.getAttivo() != null) {
			metodoPagamento.setAttivo(req.getAttivo());
		}

		// Salvo il metodo di pagamento aggiornato
		metodoPagamentoRepo.save(metodoPagamento);

		log.debug("Metodo di pagamento aggiornato con successo. ID: {}", req.getId());
	}

	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
	@Override
	public void disattiva(MetodoPagamentoReq req) throws AcademyException {
		log.debug("Disattiva: {}", req);

		// Verifico l'esistenza del metodo di pagamento
		MetodoPagamento metodoPagamento = metodoPagamentoRepo.findById(req.getId())
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("metodo-pagamento-non-trovato")));

		// Disattivo il metodo pagamento
		boolean eraDefault = metodoPagamento.getMetodoDefault();
		metodoPagamento.setAttivo(false);
		metodoPagamento.setMetodoDefault(false); // Non può essere default se non è attivo

		// Salvo il metodo pagamento aggiornato
		metodoPagamentoRepo.save(metodoPagamento);

		log.debug("Metodo di pagamento disattivato con successo. ID: {}", req.getId());

		// Se era il metodo di default, scelgo un nuovo default
		if (eraDefault) {
			List<MetodoPagamento> metodiAttivi = metodoPagamentoRepo
					.findByAccountIdAndAttivoTrueOrderByCreatoIlDesc(metodoPagamento.getAccount().getId());

			if (!metodiAttivi.isEmpty()) {
				// Imposto quello più recente
				MetodoPagamento nuovoDefault = metodiAttivi.get(0);
				nuovoDefault.setMetodoDefault(true);
				metodoPagamentoRepo.save(nuovoDefault);

				log.debug("Nuovo metodo di pagamento default impostato con successo. ID: {}", nuovoDefault.getId());
			}
		}
	}

	@Override
	public MetodoPagamentoDTO getById(Integer id) throws AcademyException {
		log.debug("GetById: {}", id);

		// Recupero il metodo di pagamento dal DB
		MetodoPagamento metodoPagamento = metodoPagamentoRepo.findById(id)
				.orElseThrow(() -> new AcademyException(msgS.getMessaggio("metodo-pagamento-non-trovato")));

		return metodoPagamentoMapper.toDto(metodoPagamento);
	}

	@Override
	public List<MetodoPagamentoDTO> listActiveByAccount(Integer accountId) {
		log.debug("ListActiveByAccount: {}", accountId);

		// Recupero tutti i metodi di pagamento attivi
		List<MetodoPagamento> metodiPagamento = metodoPagamentoRepo.findByAccountIdAndAttivoTrue(accountId);

		return metodoPagamentoMapper.toDtoList(metodiPagamento);
	}

	private void resetDefaultMetodo(MetodoPagamento nuovoDefault) {
		List<MetodoPagamento> metodiAttivi = metodoPagamentoRepo
				.findByAccountIdAndAttivoTrue(nuovoDefault.getAccount().getId());

		metodiAttivi.forEach(mp -> mp.setMetodoDefault(false));
		metodoPagamentoRepo.saveAll(metodiAttivi);
	}
}
