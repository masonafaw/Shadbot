package me.shadorc.discordbot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

public class HTMLUtils {

	/**
	 * @param url - webpage's url
	 * @return Whole HTML from the URL
	 * @throws IOException
	 */
	public static String getHTML(String url) throws IOException {
		BufferedReader reader = null;
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.91 Safari/537.36 Vivaldi/1.92.917.35");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			connection.connect();

			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

			StringBuilder html = new StringBuilder();

			String line;
			while((line = reader.readLine()) != null) {
				html.append(line + "\n");
			}

			return html.toString();

		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	/**
	 * @param text - text to parse
	 * @param start - starting String
	 * @param end - ending String
	 * @return All substrings between start and end Strings in text
	 */
	public static List<String> getAllSubstring(String text, String start, String end) {
		List<String> lines = new ArrayList<>();
		Pattern pattern = Pattern.compile(Pattern.quote(start) + "(?s)(.*?)" + Pattern.quote(end));
		Matcher matcher = pattern.matcher(text);
		while(matcher.find()) {
			lines.add(matcher.group(1));
		}

		return lines;
	}

	/**
	 * @param url - webpage's url
	 * @param toMatch - String to match in HTML code
	 * @param start - parsing begin
	 * @param end - parsing ending
	 * @return Parsed HTML from "start" to "end"
	 * @throws IOException
	 */
	public static String parseHTML(String url, String toMatch, String start, String end) throws IOException {
		BufferedReader reader = null;
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.91 Safari/537.36 Vivaldi/1.92.917.35");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			connection.connect();

			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

			String line;
			while((line = reader.readLine()) != null) {
				if(line.contains(toMatch)) {
					Pattern pattern = Pattern.compile(Pattern.quote(start) + "(.*?)" + Pattern.quote(end));
					Matcher matcher = pattern.matcher(line);
					if(matcher.find()) {
						return matcher.group(1).trim();
					}
				}
			}

		} finally {
			IOUtils.closeQuietly(reader);
		}

		return null;
	}

	/**
	 * @param html - webpage's html
	 * @param toMatch - String to match in HTML code
	 * @param start - parsing begin
	 * @param end - parsing ending
	 * @return Parsed HTML from "start" to "end"
	 */
	public static String parseTextHTML(String html, String toMatch, String start, String end) {
		for(String line : html.split("\n")) {
			if(line.contains(toMatch)) {
				Pattern pattern = Pattern.compile(Pattern.quote(start) + "(.*?)" + Pattern.quote(end));
				Matcher matcher = pattern.matcher(line);
				if(matcher.find()) {
					return matcher.group(1).trim();
				}
			}
		}

		return null;
	}
}