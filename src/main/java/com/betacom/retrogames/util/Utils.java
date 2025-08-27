package com.betacom.retrogames.util;

public class Utils {

	public static String normalizza(String str) {
		if (str == null || str.trim().isEmpty()) {
			return null;
		}

		return str.trim().toUpperCase();
	}
}
