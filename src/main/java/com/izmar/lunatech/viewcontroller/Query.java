package com.izmar.lunatech.viewcontroller;

import static j2html.TagCreator.h1;
import static j2html.TagCreator.head;
import static j2html.TagCreator.link;
import static j2html.TagCreator.title;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.izmar.lunatech.Database;
import com.izmar.lunatech.model.Airport;
import com.izmar.lunatech.model.Runway;

public class Query extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		PrintWriter w = resp.getWriter();
		w.print(head(title("Query Results"), link().withRel("stylesheet").withHref("static/css/style.css"))
				.renderFormatted());

		if (req.getParameter("submitbutton") == null)
			return;

		String country = req.getParameter("country");

		if (country == null || country == "") {

			w.print("Please enter a valid country or country code.");
			return;
		}

		List<Airport> airports = Database.getAirports(country);

		if (airports.size() == 0) {

			w.print("No airports found for country " + country);
			return;

		}

		// At this point we are sure that either a valid country name or code has been
		// submitted.
		// We figure out the code if a name was submitted, or vice versa, so we can
		// display a nice heading.

		String countryCode = "";
		String countryName = "";

		if (country.length() == 2) {
			countryCode = country;
			countryName = Database.getCountryName(countryCode);
		} else {
			countryName = country;
			countryCode = Database.getCountryCode(countryName);
		}

		w.print(h1("The following airports are found in " + countryName + " (" + countryCode + ")").renderFormatted());

		Iterator<Airport> airportsIt = airports.iterator();

		while (airportsIt.hasNext()) {
			Airport airport = airportsIt.next();
			List<Runway> runways = airport.getRunways();
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
