package com.izmar.lunatech.model;

import java.util.List;

import com.izmar.lunatech.Database;

/*
 * Models an airport, to the extent that its properties are required in the app.
 * What the methods do is clear from their names.
 */

public class Airport {

	final private String ident;
	final private String name;

	public Airport(String ident, String name) {

		this.ident = ident;
		this.name = name;

	}

	public String getIdent() {
		return ident;
	}

	public String getName() {
		return name;
	}

	public List<Runway> getRunways() {
		return Database.getRunways(this);
	}

	public static List<Country> getCountriesWithMostAirports() {
		return Database.getCommonAirportCountries(true);
	}

	public static List<Country> getCountriesWithLeastAirports() {
		return Database.getCommonAirportCountries(false);
	}

}
