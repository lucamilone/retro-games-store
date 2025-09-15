package com.betacom.retrogames.service.implementations;

import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.betacom.retrogames.dto.OrdineRigaDTO;
import com.betacom.retrogames.mapper.OrdineRigaMapper;
import com.betacom.retrogames.model.OrdineRiga;
import com.betacom.retrogames.repository.OrdineRigaRepository;
import com.betacom.retrogames.service.interfaces.OrdineRigaService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class OrdineRigaImpl implements OrdineRigaService {
	private final OrdineRigaRepository ordineRigaRepo;
	private final OrdineRigaMapper ordineRigaMapper;

	public OrdineRigaImpl(OrdineRigaRepository ordineRigaRepo, OrdineRigaMapper ordineRigaMapper) {
		this.ordineRigaRepo = ordineRigaRepo;
		this.ordineRigaMapper = ordineRigaMapper;
	}

	@Override
	public List<OrdineRigaDTO> listByOrdine(Integer ordineId) {
		log.debug("ListByOrdine: {}", ordineId);

		// Recupero le righe dell'ordine
		List<OrdineRiga> righe = ordineRigaRepo.findAllByOrdineId(ordineId);

		// Mappo Entity a DTO
		List<OrdineRigaDTO> righeDtos = ordineRigaMapper.toDtoList(righe);

		// Calcolo il SubTotale dinamicamente da esporre verso il frontend
		Iterator<OrdineRiga> it = righe.iterator();
		for (OrdineRigaDTO riga : righeDtos) {
			riga.setSubTotale(it.next().getSubTotale());
		}

		return righeDtos;
	}
}
