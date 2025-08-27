package com.betacom.retrogames.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@ToString
@Builder
public class OrdineDTO 
{
    private Integer id;
    private String account;
    private List<OrdineRigaDTO> righe;
    private BigDecimal totale;
    private String statoOrdine;
    private IndirizzoDTO indirizzoSpedizione;
    private String pagamento;
    private LocalDateTime creatoIl;
    private LocalDateTime aggiornatoIl;
}