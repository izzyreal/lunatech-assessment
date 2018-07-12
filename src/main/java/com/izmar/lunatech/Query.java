package com.izmar.lunatech;

import static j2html.TagCreator.h1;
import static j2html.TagCreator.head;
import static j2html.TagCreator.link;
import static j2html.TagCreator.title;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

		ResultSetToTable rstt = new ResultSetToTable();
		List<String> airports = Database.getAirportsAsStrings(country);

		if (airports.size() == 0) {

			w.print("No airports found for country " + country);
			return;

		}

		// At this point we are sure that either a valid country name or code has been submitted.
		// We figure out the code if a name was submitted, or vice versa, so we can display a nice heading.
		
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
		
		List<List<String>> chunks = new ArrayList<List<String>>();

		List<String> chunk = new ArrayList<String>();
		for (int i = 0; i < airports.size(); i++) {
			chunk.add(airports.get(i));
			if (chunk.size() == 10) {
				chunks.add(chunk);
				chunk = new ArrayList<String>();
			}
		}

		for (int i = 0; i < chunks.size(); i++) {

			Iterator<String> airportsIt = chunks.get(i).iterator();
			Iterator<ResultSet> rss = Database.getRunways(chunks.get(i)).iterator();

			while (rss.hasNext()) {
				ResultSet rs = rss.next();
				String airport = airportsIt.next();
				try {
					
					if (!rs.next()) {
						w.print("<b>" + airport + "</b> has no runways.<br><br>");
						continue;
					}
			
					rs.previous();
					w.print("<b>" + airport + "</b> has the following runway(s):<br>");
					rstt.writeTable(rs, w);

				} catch (SQLException e) {
					e.printStackTrace();
				}
				w.print("<br><br>");
			}
		}
	}

}
