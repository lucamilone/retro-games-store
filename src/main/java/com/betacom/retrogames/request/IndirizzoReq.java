package com.betacom.retrogames.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IndirizzoReq 
{
    private String via;
    private String citta;
    private String cap;
    private String paese;
}