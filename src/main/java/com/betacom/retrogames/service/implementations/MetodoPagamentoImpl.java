package com.betacom.retrogames.service.implementations;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.betacom.retrogames.dto.MetodoPagamentoDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.mapper.MetodoPagamentoMapper;
import com.betacom.retrogames.model.Account;
import com.betacom.retrogames.model.MetodoPagamento;
import com.betacom.retrogames.model.TipoMetodoPagamento;
import com.betacom.retrogames.repository.AccountRepository;
import com.betacom.retrogames.repository.MetodoPagamentoRepository;
import com.betacom.retrogames.repository.TipoMetodoPagamentoRepository;
import com.betacom.retrogames.request.MetodoPagamentoReq;
import com.betacom.retrogames.service.interfaces.MetodoPagamentoService;
import com.betacom.retrogames.service.interfaces.MessaggioService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class MetodoPagamentoImpl implements MetodoPagamentoService 
{
    private final MetodoPagamentoRepository metodoPagamentoRepo;
    private final AccountRepository accountRepo;
    private final TipoMetodoPagamentoRepository tipoMetodoPagamentoRepo;
    private final MetodoPagamentoMapper metodoPagamentoMapper;
    private final MessaggioService msgS;

    public MetodoPagamentoImpl(MetodoPagamentoRepository metodoPagamentoRepo, AccountRepository accountRepo, TipoMetodoPagamentoRepository tipoMetodoPagamentoRepo, MetodoPagamentoMapper metodoPagamentoMapper, MessaggioService msgS) 
    {
        this.metodoPagamentoRepo = metodoPagamentoRepo;
        this.accountRepo = accountRepo;
        this.tipoMetodoPagamentoRepo = tipoMetodoPagamentoRepo;
        this.metodoPagamentoMapper = metodoPagamentoMapper;
        this.msgS = msgS;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    @Override
    public Integer crea(MetodoPagamentoReq req) throws AcademyException 
    {
        log.debug("Crea: {}", req);

        // Verifico se il metodo pagamento esiste già
        if (metodoPagamentoRepo.findByToken(req.getToken()).isPresent()) {
            throw new AcademyException(msgS.getMessaggio("metodo-pagamento-esistente"));
        }
        
    	// Recupero l'account dal DB per salvare i dati
     	Account account = accountRepo.findById(req.getAccountId())
     			.orElseThrow(() -> new AcademyException(msgS.getMessaggio("account-non-esistente")));
     	
     	// Recupero il metodo pagamento dal DB per salvare i dati
     	TipoMetodoPagamento tipoMetodoPagamento = tipoMetodoPagamentoRepo.findById(req.getTipoMetodoPagamentoId())
     			.orElseThrow(() -> new AcademyException(msgS.getMessaggio("tipo-metodo-pagamento-non-trovato")));

        // Creo l'oggetto MetodoPagamento
        MetodoPagamento metodoPagamento = new MetodoPagamento();
        
        metodoPagamento.setAccount(account); 
        metodoPagamento.setTipo(tipoMetodoPagamento);        
        metodoPagamento.setToken(req.getToken());      
        metodoPagamento.setMetodoDefault(req.getMetodoDefault()); 
        metodoPagamento.setAttivo(true); 

        // Salvo nel database
        metodoPagamento = metodoPagamentoRepo.save(metodoPagamento);

        log.debug("Metodo di pagamento creato con successo con ID: {}", metodoPagamento.getId());
        return metodoPagamento.getId();
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    @Override
    public void aggiorna(MetodoPagamentoReq req) throws AcademyException 
    {
        log.debug("Aggiorna: {}", req);

        // Verifico se il metodo di pagamento esiste
        MetodoPagamento metodoPagamento = metodoPagamentoRepo.findById(req.getId())
                .orElseThrow(() -> new AcademyException(msgS.getMessaggio("metodo-pagamento-non-esistente")));

        // Aggiorno i campi modificabili  
        if (req.getToken() != null) 
        {
            metodoPagamento.setToken(req.getToken());
        }
        
        if (req.getMetodoDefault() != null) 
        {
            metodoPagamento.setMetodoDefault(req.getMetodoDefault());
        }
        
        if (req.getAttivo() != null) 
        {
            metodoPagamento.setAttivo(req.getAttivo());
        }

        // Salvo il metodo di pagamento aggiornato
        metodoPagamentoRepo.save(metodoPagamento);

        log.debug("Metodo di pagamento aggiornato con successo con ID: {}", req.getId());
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    @Override
    public void disattiva(MetodoPagamentoReq req) throws AcademyException 
    {
        log.debug("Disattiva: {}", req);

        // Verifico se il metodo di pagamento esiste
        MetodoPagamento metodoPagamento = metodoPagamentoRepo.findById(req.getId())
                .orElseThrow(() -> new AcademyException(msgS.getMessaggio("metodo-pagamento-non-esistente")));

        // Disattivo il metodo pagamento
        metodoPagamento.setAttivo(false);

        // Salvo la modifica
        metodoPagamentoRepo.save(metodoPagamento);

        log.debug("Metodo di pagamento disattivato con successo con ID: {}", req.getId());
    }

    @Override
    public MetodoPagamentoDTO getById(Integer id) throws AcademyException 
    {
        log.debug("GetById: {}", id);

        // Recupero il metodo di pagamento dal DB
        MetodoPagamento metodoPagamento = metodoPagamentoRepo.findById(id)
                .orElseThrow(() -> new AcademyException(msgS.getMessaggio("metodo-pagamento-non-trovato")));

        return metodoPagamentoMapper.toDto(metodoPagamento);
    }

    @Override
    public List<MetodoPagamentoDTO> listActiveByAccount(Integer accountId) 
    {
        log.debug("ListActiveByAccount: {}", accountId);

        // Recupero tutti i metodi di pagamento attivi
        List<MetodoPagamento> metodiPagamento = metodoPagamentoRepo.findByAccountIdAndAttivoTrue(accountId);

        return metodoPagamentoMapper.toDtoList(metodiPagamento);
    }
}
