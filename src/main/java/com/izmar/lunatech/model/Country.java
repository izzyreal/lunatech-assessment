package com.izmar.lunatech.model;

import java.util.List;
import java.util.Map;

import com.izmar.lunatech.Database;

/*
 * Models a country.
 */

public class Country {

	final private String code;
	private int airportCount = -1;

	/**
	 * A country has to be instantiated based on ISO code or full (English) name,
	 * case insensitive.
	 * 
	 * None of its methods require the country to exist.
	 */
	public Country(String codeOrName) {

		if (codeOrName.length() == 2) {

			this.code = codeOrName.toUpperCase();

		} else {

			if (codeOrName.length() > 2) {

				// Camelcase to make sure e.g. 'netherlands' or 'nEthErlANds' can be found

				String p1 = codeOrName.substring(0, 1).toUpperCase();
				String p2 = codeOrName.substring(1).toLowerCase();

				codeOrName = p1 + p2;

			}

			this.code = Database.getCountryCode(codeOrName);

		}

	}

	/*
	 * Optionally a country can be instantiated providing the number of airports it
	 * has.
	 */
	public Country(String codeOrName, int airportCount) {

		this(codeOrName);
		this.airportCount = airportCount;

	}

	public boolean exists() {
		return Database.checkIfCountryExists(code);
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return Database.getCountryName(code);
	}

	public List<Airport> getAirports() {
		return Database.getAirports(this);
	}

	public int getAirportCount() {

		// We actually never call this method without having used the constructor
		// that takes airportCount as an argument, but this check is added anyway and
		// airportCount is assigned a sane value.
		if (airportCount == -1)
			airportCount = getAirports().size();

		return airportCount;
	}

	public static Map<String, List<String>> getRunwaySurfaceTypesPerCountry() {
		return Database.getSurfaceTypes();
	}

	public static List<String> getAllCountryNamesAndCodes() {

		List<String> res = Database.getFromCountries("name");
		res.addAll(Database.getFromCountries("code"));
		return res;

	}

	@Override
	public String toString() {
		return getName() + " (" + code + ")";
	}

}
