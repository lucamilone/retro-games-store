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
public class CredenzialeReq 
{
    private Integer id;
    private String accountId;
    private String email;
    private String passwordHash;
    private LocalDateTime ultimoLogin;
}