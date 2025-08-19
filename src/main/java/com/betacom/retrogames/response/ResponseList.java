package com.betacom.retrogames.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseList<T> extends ResponseBase {

	private List<T> dati;
}
