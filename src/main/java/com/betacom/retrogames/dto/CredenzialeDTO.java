package com.betacom.retrogames.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@ToString
@Builder
public class CredenzialeDTO 
{
    private Integer id;
    private String account;
    private String email;
    private LocalDateTime ultimoLogin;
}