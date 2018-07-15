package com.izmar.lunatech.viewcontroller;

import static j2html.TagCreator.h1;
import static j2html.TagCreator.head;
import static j2html.TagCreator.link;
import static j2html.TagCreator.title;
import static j2html.TagCreator.meta;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.izmar.lunatech.model.Airport;
import com.izmar.lunatech.model.Country;
import com.izmar.lunatech.model.Runway;

/*
 * Servlet that displays all airports for a given country code or name, and a list of each airport's runway IDs.
 */

public class Query extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter w = resp.getWriter();
		w.print(head(title("Query Results"), meta().withCharset("UTF-8"), link().withRel("stylesheet").withHref("static/css/style.css"))
				.renderFormatted());

		if (req.getParameter("submitbutton") == null)
			return;

		String country = req.getParameter("country");

		if (country == null || country == "") {

			w.print("Please enter a country code or name.");
			return;

		}

		Country c = new Country(country);

		if (!c.exists()) {
			
			w.print("No country found with code or name " + country + ".");
			return;
			
		}

		List<Airport> airports = c.getAirports();

		if (airports.size() == 0) {

			w.print("No airports found in " + c.toString());
			return;

		}

		w.print(h1("The following airports are found in " + c.toString()).renderFormatted());

		Iterator<Airport> airportsIt = airports.iterator();

		while (airportsIt.hasNext()) {

			Airport airport = airportsIt.next();
			List<Runway> runways = airport.getRunways();

			System.out.println("Airport name: " + airport.getName());

			if (runways.size() == 0) {
				w.print("<b>" + airport.getName() + "</b> has no runways.<br><br>");
				continue;

			}

			w.print("<b>" + airport.getName() + "</b> has the following runway(s):<br>");

			Iterator<Runway> runwaysIt = runways.iterator();
			
			while (runwaysIt.hasNext())
				w.print(runwaysIt.next().getId() + "<br>");
			
			w.print("<br>");

		}
		
	}

}
