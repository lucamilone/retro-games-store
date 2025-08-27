package com.betacom.retrogames.request;

import java.util.Set;
import com.betacom.retrogames.model.Prodotto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CategoriaReq 
{
    private Integer id;
    private String nome;
    private Set<Prodotto> prodottiId;
}