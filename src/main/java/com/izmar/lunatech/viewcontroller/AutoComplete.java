package com.izmar.lunatech.viewcontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.izmar.lunatech.Database;

public class AutoComplete extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private List<String> countries = new ArrayList<String>();

	public AutoComplete() {
		super();
		countries = Database.getFromCountries("name");
		countries.addAll(Database.getFromCountries("code"));
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {		
		JSONArray json = new JSONArray(countries);
		response.setContentType("application/json");
		response.getWriter().print(json);
	}
}
