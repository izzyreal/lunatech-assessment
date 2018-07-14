package com.izmar.lunatech.viewcontroller;

import static j2html.TagCreator.h1;
import static j2html.TagCreator.head;
import static j2html.TagCreator.link;
import static j2html.TagCreator.title;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.izmar.lunatech.Database;

public class Reports extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter w = resp.getWriter();

		w.print(head(title("Reports"), link().withRel("stylesheet").withHref("static/css/style.css"))
				.renderFormatted());

		w.print(h1("The 10 countries with the most airports").renderFormatted());

		boolean most = true;
		
		Iterator<String> aci = Database.getCommonAirportCountries(most).iterator();
		while (aci.hasNext())
			w.print(aci.next() + "<br>");

		w.print("<br>");
		w.print(h1("The 10 countries with the least airports").renderFormatted());

		most = false;
		
		List<String> leastAirports = Database.getCommonAirportCountries(most);
		List<String> noAirports = Database.getCountriesWithNoAirports();

		for (int i = 0; i < 10; i++) {
			if (i < noAirports.size()) {
				w.print(noAirports.get(i) + "<br>");
			} else {
				w.print(leastAirports.get(i - noAirports.size()) + "<br>");
			}
		}
		
		w.print("<br>" + h1("Runway surface types per country").renderFormatted());

		Map<String, List<String>> surfaceMap = Database.getSurfaceTypes();
		Iterator<String> keySetIt = surfaceMap.keySet().iterator();

		while (keySetIt.hasNext()) {
			String country = keySetIt.next();
			Iterator<String> surfaces = surfaceMap.get(country).iterator();
			w.print("<b>" + country + "</b> has the following surfaces:<br>");
			while (surfaces.hasNext()) {
				String surface = surfaces.next();
				if (surface == null) continue;
				w.print(surface + "<br>");
			}
			w.print("<br><br>");
		}

		w.print(h1("The 10 most common runway identifications").renderFormatted());
		Iterator<String> ri = Database.getMostCommonRunwayIdents().iterator();
		while (ri.hasNext())
			w.print(ri.next() + "<br>");
	}

}
