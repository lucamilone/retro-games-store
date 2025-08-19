package com.betacom.retrogames.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseBase {

	private Boolean returnCode;
	private String msg;
}
