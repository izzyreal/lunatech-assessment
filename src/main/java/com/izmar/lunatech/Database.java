package com.izmar.lunatech;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Database {

	static HashMap<String, String> countryNameCodeMap = new HashMap<String, String>();
	static HashMap<String, String> countryCodeNameMap = new HashMap<String, String>();

	private static void populateCountryMaps() {

		try {

			Connection c = getConnection();

			String sql = "SELECT * FROM countries;";
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				countryNameCodeMap.put(rs.getString("name"), rs.getString("code"));
				countryCodeNameMap.put(rs.getString("code"), rs.getString("name"));
			}

			c.close();

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String getCountryCode(String countryName) {

		if (countryNameCodeMap.size() == 0)
			populateCountryMaps();

		return countryNameCodeMap.get(countryName);
	}

	public static String getCountryName(String countryCode) {

		if (countryCodeNameMap.size() == 0)
			populateCountryMaps();

		return countryCodeNameMap.get(countryCode);
	}

	private static List<String> getFromCountries(String column) {
		List<String> res = new ArrayList<String>();
		try {

			Connection c = getConnection();

			String sql = "SELECT * FROM countries;";
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next())
				res.add(rs.getString(column));

			c.close();

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static List<String> getCountryNames() {
		return getFromCountries("name");
	}

	public static List<String> getCountryCodes() {
		return getFromCountries("code");
	}

	public static List<String> getAirportsAsStrings(String country) {
		List<String> res = new ArrayList<String>();
		ResultSet rs = getAirportsAsResultSet(country);

		if (rs != null) {
			try {
				while (rs.next()) {
					String airport = rs.getString("name");
					res.add(airport);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		return res;
	}

	private static ResultSet getAirportsAsResultSet(String country) {

		ResultSet rs = null;

		String countryCode = country;

		if (countryCode.length() != 2)
			countryCode = getCountryCode(countryCode);

		try {

			Connection c = getConnection();

			String sql = "SELECT * FROM airports WHERE iso_country='" + countryCode + "';";
			Statement stmt = c.createStatement();
			rs = stmt.executeQuery(sql);

			c.close();

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}

	public static List<ResultSet> getRunways(List<String> airports) {

		List<ResultSet> res = new ArrayList<ResultSet>();
		Iterator<String> airportsIt = airports.iterator();

		try {

			Connection c = getConnection();

			while (airportsIt.hasNext()) {

				String airport = airportsIt.next();
				String sql = "SELECT * FROM airports WHERE name=?";
				PreparedStatement stmt = c.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				stmt.setString(1, airport);
				ResultSet airportsRs = stmt.executeQuery();
				airportsRs.next();
				String airportIdent = airportsRs.getString("ident");
				sql = "SELECT * FROM runways WHERE airport_ident=?;";
				stmt = c.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				stmt.setString(1, airportIdent);
				ResultSet rs = stmt.executeQuery();
				res.add(rs);

			}

			c.close();

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}

	public static int getAirportCount(String countryCode) {
		int res = 0;
		try {
			
			Connection c = getConnection();
			
			String sql = "SELECT COUNT(*) FROM airports WHERE iso_country='" + countryCode + "';";
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next())
				res = rs.getInt("count");
			
			c.close();
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	private static Connection getConnection() throws URISyntaxException, SQLException {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		String dbUrl = System.getenv("JDBC_DATABASE_URL");
		return DriverManager.getConnection(dbUrl);
	}

}
