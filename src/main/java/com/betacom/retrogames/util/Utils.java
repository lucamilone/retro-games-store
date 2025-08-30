package com.betacom.retrogames.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Utils {

	public static String capitalizeWords(String str) {
		if (str == null || str.trim().isEmpty()) {
			return null;
		}

		return Arrays.stream(str.trim().toLowerCase().split("\\s+"))
				.map(s -> s.substring(0, 1).toUpperCase() + s.substring(1)).collect(Collectors.joining(" "));
	}

	public static String normalizza(String str) {
		if (str == null || str.trim().isEmpty()) {
			return null;
		}

		return str.trim().toLowerCase();
	}
}
