package com.izmar.lunatech.view;

import static j2html.TagCreator.h1;
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
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.izmar.lunatech.Database;
import com.izmar.lunatech.model.Country;

public class Reports extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter w = resp.getWriter();

		w.print(head(title("Reports"), link().withRel("stylesheet").withHref("static/css/style.css"))
				.renderFormatted());

		HashMap<String, Integer> map = new HashMap<String, Integer>();

		Iterator<String> countryCodes = Database.getCountryCodes().iterator();
		
		while (countryCodes.hasNext()) {
			String countryCode = countryCodes.next();
			Integer count = new Country(countryCode).getAirportCount();
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

		Set<String> keySet = sortedMap.keySet();

		w.print(h1("The 10 countries with the most airports").renderFormatted());
		
		printList(keySet, w, sortedMap, keySet.size() - 1, keySet.size() - 10);
		
		w.print(h1("The 10 countries with the least airports").renderFormatted());

		printList(keySet, w, sortedMap, 10 - 1, 0);

		w.print("<br><br>" + h1("Runway surface types per country"));
		
		Map<String, List<String>> surfaceMap = Database.getSurfaceTypes();
		Iterator<String> keySetIt = surfaceMap.keySet().iterator();
		while (keySetIt.hasNext()) {
			String country = keySetIt.next();
			Iterator<String> surfaces = surfaceMap.get(country).iterator();
			w.print(country + " has the following surfaces:<br>");
			while (surfaces.hasNext()) {
				w.print(surfaces.next() + "<br>");
			}
			w.print("<br><br>");
		}

	}

	private void printList(Set<String> keySet, PrintWriter w, Map<String, Integer> sortedMap, int firstIndex, int lastIndex) {
		Object[] keySetArray = keySet.toArray();

		for (int i = firstIndex; i >= lastIndex; i--) {
			String countryCode = (String) keySetArray[i];
			String countryName = Database.getCountryName(countryCode);
			w.print(countryName + " (" + countryCode + ")" + " has " + sortedMap.get(countryCode) + " airports.<br>");							
		}
	}
	
}
