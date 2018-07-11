package com.izmar.lunatech;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Database {

	Database() {
		try {
			Connection c = getConnection();
			String sql = "select * from countries;";
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("name");
				String code = rs.getString("code");
				int id = rs.getInt("id");
				System.out.println("\tName: " + name + ", code: " + code + ", id: " + id + "\n");
			}
			sql = "select * from airports;";
			stmt = c.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("name");
				String type = rs.getString("type");
				int id = rs.getInt("id");
				System.out.println("\tName: " + name + ", type: " + type + ", id: " + id + "\n");
			}

			c.close();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static List<String> getFromCountries(String column) {
		List<String> res = new ArrayList<String>();
		try {
			Connection c = getConnection();
			String sql = "select * from countries;";
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

	private static String getCountryCode(String countryNameOrCode) {
		try {
			Connection c = getConnection();
			String sql = "select * from countries;";
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("name");
				String code = rs.getString("code");
				if (name.equalsIgnoreCase(countryNameOrCode) || code.equalsIgnoreCase(countryNameOrCode))
					return code;
			}
			c.close();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "";
	}

	public static List<String> getAirportsAsStrings(String country) {
		List<String> res = new ArrayList<String>();
		ResultSet rs = getAirportsAsResultSet(country);
		try {
			while (rs.next()) {
				String airport = rs.getString("name");
				res.add(airport);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static ResultSet getAirportsAsResultSet(String country) {

		System.out.println("Country is " + country);

		ResultSet rs = null;

		String countryCode = getCountryCode(country);

		System.out.println("Country code is " + countryCode);

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

	public static ResultSet getRunways(String airport) {
		ResultSet res = null;

		String airportIdent = getAirportIdent(airport);

		try {
			Connection c = getConnection();
			String sql = "SELECT * FROM runways WHERE airport_ident='" + airportIdent + "';";
			Statement stmt = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			res = stmt.executeQuery(sql);
			c.close();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}

	static String getAirportIdent(String airport) {
		System.out.println("Trying to get airportIdent for airport " + airport);
		String res = "";
		try {
			Connection c = getConnection();
			String sql = "SELECT * FROM airports WHERE name='" + airport + "';";
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				res = rs.getString("ident");
			}
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
