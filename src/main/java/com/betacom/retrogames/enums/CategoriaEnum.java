package com.betacom.retrogames.enums;

import static com.betacom.retrogames.util.Utils.normalizza;

import java.util.Optional;

public enum CategoriaEnum {

	VIDEOGIOCHI("videogiochi"), CONSOLE("console"), ACCESSORI("accessori");

	private final String nome;

	CategoriaEnum(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public static Optional<CategoriaEnum> fromNomeSafe(String nome) {
		String normalized = normalizza(nome);

		for (CategoriaEnum c : values()) {
			if (c.getNome().equals(normalized)) {
				return Optional.of(c);
			}
		}
		return Optional.empty(); // Categoria non mappata nell'enum
	}
}
