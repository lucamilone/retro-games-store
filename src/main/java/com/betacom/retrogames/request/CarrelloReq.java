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
public class CarrelloReq 
{
    private Integer id;
    private Integer accountId;
    private List<CarrelloRigaReq> righe;
    private LocalDateTime creatoIl;
    private LocalDateTime aggiornatoIl;
}