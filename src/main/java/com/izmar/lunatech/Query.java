package com.izmar.lunatech;

import static j2html.TagCreator.body;
import static j2html.TagCreator.form;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.title;
import static j2html.TagCreator.link;
import static j2html.TagCreator.meta;

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

import j2html.TagCreator;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;

public class Query extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter w = resp.getWriter();
		ContainerTag page = html(head(title("Query")),
				body(form().withMethod("post").withAction("query").with(inputField()).with(submitButton())));
		w.print(page.renderFormatted());
	}

	private static Tag submitButton() {
		return TagCreator.button().withType("submit").withName("submitbutton").withText("Submit");
	}

	private static Tag inputField() {
		return TagCreator.input().withType("textbox").withId("country").withName("country")
				.withPlaceholder("country code or name");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter w = resp.getWriter();
		w.print(head(title("Query Results")
				//, meta().withCharset("UTF-8")
				, link().withRel("stylesheet").withHref("static/css/style.css")).renderFormatted());
		if (req.getParameter("submitbutton") != null) {
			String country = req.getParameter("country");
			if (country != null) {

				ResultSetToTable rstt = new ResultSetToTable();
				List<String> airports = Database.getAirportsAsStrings(country);

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
								w.print(airport + " has no runways.<br>");
							} else {
								rs.previous();
								w.print(airport + " has the following runway(s):<br>");
								rstt.writeTable(rs, w);
							}

						} catch (SQLException e) {
							e.printStackTrace();
						}
						w.print("<br><br>");
					}
				}
			}
		}
	}
}
