package com.betacom.retrogames.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrdineReq 
{
    private Integer id;
    private Integer accountId;
    private List<OrdineRigaReq> righe;
    private IndirizzoReq indirizzoSpedizione;
    private Integer pagamentoId;
    private LocalDateTime creatoIl;
    private LocalDateTime aggiornatoIl;
}