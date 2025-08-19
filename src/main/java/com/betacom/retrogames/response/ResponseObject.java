package com.betacom.retrogames.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseObject<T> extends ResponseBase {

	private T dati;
}
