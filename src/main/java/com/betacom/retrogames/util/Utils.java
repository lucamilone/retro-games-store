package com.betacom.retrogames.util;

public class Utils {

	public static String capitalizeFirstLetter(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}

		str = str.trim();

		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}

	public static String normalizza(String s) {
		return s.trim().toUpperCase();
	}
}
