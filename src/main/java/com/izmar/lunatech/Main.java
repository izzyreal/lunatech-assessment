package com.izmar.lunatech;
import static j2html.TagCreator.attrs;
import static j2html.TagCreator.body;
import static j2html.TagCreator.button;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.link;
import static j2html.TagCreator.main;
import static j2html.TagCreator.title;
import static j2html.TagCreator.form;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import j2html.tags.ContainerTag;
import j2html.tags.Tag;

public class Main extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		PrintWriter w = resp.getWriter();
		
		Tag<?> b1 = actionButtonForm("Query", "query");
		Tag<?> b2 = actionButtonForm("Reports", "reports");
		Tag<?> b3 = form(button("Query JSP")).withAction("query.jsp");

		ContainerTag page =

				html(head(title("Welcome"), link().withRel("stylesheet").withHref("static/css/style.css")),
						body(main(attrs("#main.content"), h1("Welcome to Izmar's Lunatech assessment!"), b1, b2, b3)));
		w.print(page.renderFormatted());
	}
	
	private Tag<?> actionButtonForm(String text, String action) {
		return form(button(text)).withAction(action);
	}
}
