package com.kyleduo.rabbits.annotations.utils;

/**
 * Method's name parser.
 * Created by kyle on 2016/12/07.
 */

public class NameParser {
	public static String parseRoute(String page) {
		return parse(page, "route");
	}

	public static String parseObtain(String page) {
		return parse(page, "obtain");
	}

	private static String parse(String page, String prefix) {
		if (page == null || page.length() == 0) {
			return "";
		}
		String[] parts = page.split("_");
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		for (String part : parts) {
			if (part.length() == 0) {
				continue;
			}
			sb.append(Character.toUpperCase(part.charAt(0))).append(part.toLowerCase().substring(1));
		}
		return sb.toString();
	}
}
