package com.izmar.lunatech.viewcontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.izmar.lunatech.model.Country;

/*
 * Servlet that makes a list of all existing country codes and names available to query.jsp
 */

public class AutoComplete extends HttpServlet {

	private static List<String> countries = new ArrayList<String>();

	private static final long serialVersionUID = 1L;

	public AutoComplete() {

		if (countries.size() == 0)
			countries = Country.getAllCountryNamesAndCodes();

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		JSONArray json = new JSONArray(countries);
		response.setContentType("application/json");
		response.getWriter().print(json);

	}
}
