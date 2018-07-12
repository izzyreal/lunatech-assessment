package com.izmar.lunatech;

import static j2html.TagCreator.head;
import static j2html.TagCreator.link;
import static j2html.TagCreator.title;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Reports extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter w = resp.getWriter();

		w.print(head(title("Reports"), link().withRel("stylesheet").withHref("static/css/style.css"))
				.renderFormatted());

		Iterator<String> countryCodes = Database.getCountryCodes().iterator();
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		while (countryCodes.hasNext()) {
			String countryCode = countryCodes.next();
			int count = Database.getAirportCount(countryCode);
			map.put(countryCode, count);
		}

		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		Iterator<String> keySet = sortedMap.keySet().iterator();
		while (keySet.hasNext()) {
			String countryCode = keySet.next();
			w.print(countryCode + " has " + sortedMap.get(countryCode) + " airports.<br>");
		}

	}

}
