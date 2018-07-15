package com.izmar.lunatech.viewcontroller;

import static j2html.TagCreator.h1;
import static j2html.TagCreator.head;
import static j2html.TagCreator.link;
import static j2html.TagCreator.meta;
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

import com.izmar.lunatech.model.Airport;
import com.izmar.lunatech.model.Country;
import com.izmar.lunatech.model.Runway;

/*
 * Servlet that displays miscellaneous reports.
 */

public class Reports extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter w = resp.getWriter();

		w.print(head(title("Reports"), meta().withCharset("UTF-8"), link().withRel("stylesheet").withHref("static/css/style.css"))
				.renderFormatted());

		w.print(h1("The 10 countries with the most airports").renderFormatted());

		Iterator<Country> aci = Airport.getCountriesWithMostAirports().iterator();
		
		while (aci.hasNext()) {			

			Country c = aci.next();			
			w.print(c.toString() + " has " + c.getAirportCount() + " airports.<br>");
			
		}
		
		w.print("<br>");
		w.print(h1("The 10 countries with the least airports").renderFormatted());

		aci = Airport.getCountriesWithLeastAirports().iterator();
		
		while (aci.hasNext()) {			

			Country c = aci.next();			
			w.print(c.toString() + " has " + (c.getAirportCount() == 0 ? "no" : c.getAirportCount()) + " airport(s).<br>");
			
		}

		w.print("<br>" + h1("Runway surface types per country").renderFormatted());

		Map<String, List<String>> surfaceMap = Country.getRunwaySurfaceTypesPerCountry();
		Iterator<String> keySetIt = surfaceMap.keySet().iterator();

		while (keySetIt.hasNext()) {
			
			String country = keySetIt.next();
			Iterator<String> surfaces = surfaceMap.get(country).iterator();
			w.print("<b>" + country + "</b> has the following surfaces:<br>");
			
			while (surfaces.hasNext()) {
				
				String surface = surfaces.next();
			
				if (surface == null)
					continue;
				
				w.print(surface + "<br>");
				
			}
		
			w.print("<br><br>");
		}

		w.print(h1("The 10 most common runway identifications").renderFormatted());
		
		Iterator<String> ri = Runway.getMostCommonIdents().iterator();

		while (ri.hasNext())
			w.print(ri.next() + "<br>");
		
	}

}
