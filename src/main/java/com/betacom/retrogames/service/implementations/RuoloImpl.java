package com.betacom.retrogames.service.implementations;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.betacom.retrogames.dto.RuoloDTO;
import com.betacom.retrogames.exception.AcademyException;
import com.betacom.retrogames.model.Ruolo;
import com.betacom.retrogames.repository.RuoloRepository;
import com.betacom.retrogames.request.RuoloReq;
import com.betacom.retrogames.service.interfaces.RuoloService;
import com.betacom.retrogames.util.Utils;

@Service
public class RuoloImpl extends Utils implements RuoloService 
{
    private final RuoloRepository ruoloRepo;

    public RuoloImpl(RuoloRepository ruoloRepo) 
    {
        this.ruoloRepo = ruoloRepo;
    }

    @Override
    public Integer crea(RuoloReq req) throws AcademyException 
    {
        // Verifico se esiste già un ruolo con lo stesso nome
        Optional<Ruolo> existingRuolo = ruoloRepo.findByNome(req.getNome());
        
        if (existingRuolo.isPresent()) 
        {
            throw new AcademyException("Ruolo con nome " + req.getNome() + " già esistente.");
        }

        Ruolo ruolo = new Ruolo();
        ruolo.setNome(req.getNome());
        ruolo.setAttivo(true);

        // Salvo il ruolo e restituisce l'id generato
        return ruoloRepo.save(ruolo).getId();
    }

    @Override
    public void aggiorna(RuoloReq req) throws AcademyException 
    {
        // Verifico se il ruolo esiste
        Optional<Ruolo> ruolo = ruoloRepo.findById(req.getId());
        
        if (ruolo.isEmpty()) 
        {
            throw new AcademyException("Ruolo con id " + req.getId() + " non trovato.");
        }

        Ruolo existingRuolo = ruolo.get();
        
        // Se il nome non è nullo, aggiorno il nome del ruolo
        existingRuolo.setNome(req.getNome() != null ? req.getNome() : existingRuolo.getNome());
        
        // Salvo il ruolo aggiornato
        ruoloRepo.save(existingRuolo);
    }

    @Override
    public void disattiva(RuoloReq req) throws AcademyException 
    {
        // Verifico se il ruolo esiste
        Optional<Ruolo> ruolo = ruoloRepo.findById(req.getId());
        
        if (ruolo.isEmpty()) 
        {
            throw new AcademyException("Ruolo con id " + req.getId() + " non trovato.");
        }

        Ruolo existingRuolo = ruolo.get();
        
        // Imposto il ruolo come non attivo
        existingRuolo.setAttivo(false);
        
        // Salvo il ruolo disattivato
        ruoloRepo.save(existingRuolo);
    }

    @Override
    public RuoloDTO getById(Integer id) throws AcademyException 
    {
        // Verifico se il ruolo esiste
        Optional<Ruolo> ruolo = ruoloRepo.findById(id);
        
        if (ruolo.isEmpty()) 
        {
            throw new AcademyException("Ruolo con id " + id + " non trovato.");
        }

        // Converto il ruolo trovato in un DTO e lo restituisco
        return RuoloDTO.builder()
                .id(ruolo.get().getId())
                .nome(ruolo.get().getNome())
                .build();
    }

    @Override
    public List<RuoloDTO> listActive()
    {
        // Recupero tutti i ruoli e li converte in DTO
        List<Ruolo> ruoli = ruoloRepo.findAll();
        return buildListRuoloDTO(ruoli);
    }
}
