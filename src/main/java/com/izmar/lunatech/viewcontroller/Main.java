package com.izmar.lunatech.viewcontroller;

import static j2html.TagCreator.button;
import static j2html.TagCreator.form;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.link;
import static j2html.TagCreator.title;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.izmar.lunatech.Database;

import j2html.tags.ContainerTag;
import j2html.tags.Tag;

public class Main extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		PrintWriter w = resp.getWriter();

		Tag<?> b1 = actionButtonForm("Query", "query.jsp");
		Tag<?> b2 = actionButtonForm("Reports", "reports");

		ContainerTag page = html(head(title("Welcome"), link().withRel("stylesheet").withHref("static/css/style.css")),
				h1("Welcome to Izmar's Lunatech assessment!"), b1, b2);
		
		w.print(page.renderFormatted());

		Database.initTables();
	}

	private Tag<?> actionButtonForm(String text, String action) {
		return form(button(text)).withAction(action);
	}
}
