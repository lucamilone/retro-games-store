package com.betacom.retrogames.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MetodoPagamentoReq 
{
    private Integer id;
    private Integer accountId;
    private Integer tipoMetodoPagamentoId;
    private String token;
    private Boolean metodoDefault;
    private LocalDateTime creatoIl;
    private LocalDateTime aggiornatoIl;
}