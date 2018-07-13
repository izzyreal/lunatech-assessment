package com.izmar.lunatech;

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
import java.util.Map;

import com.izmar.lunatech.model.Airport;
import com.izmar.lunatech.model.Runway;

// H2 In-Memory Database Example shows about storing the database contents into memory. 

public class Database {

	private static final String DB_DRIVER = "org.h2.Driver";
	private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
	private static final String DB_USER = "";
	private static final String DB_PASSWORD = "";

	public static void initTables() {
		Connection connection = getConnection();

		PreparedStatement st = null;
		List<String> sql = new ArrayList<String>();
		sql.add("drop table if exists airports;");
		sql.add("drop table if exists runways;");
		sql.add("drop table if exists countries;");

		sql.add("create table airports(id integer not null, ident varchar(255), type varchar(255), name varchar(255), latitude_deg varchar(255), longitude_deg varchar(255), elevation_ft varchar(255), continent varchar(255), iso_country varchar(255), iso_region varchar(255), municipality varchar(255), scheduled_service varchar(255), gps_code varchar(255), iata_code varchar(255), local_code varchar(255), home_link varchar(255), wikipedia_link varchar(255), keywords varchar(255), primary key (id));");
		sql.add("insert into `airports` select * from csvread('classpath:airports.csv', null, 'fieldDelimiter=\" fieldSeparator=, lineSeparator=\\n');");

		sql.add("create table runways(id integer not null, airport_ref integer, airport_ident varchar(255), length_ft varchar(255), width_ft varchar(255), surface varchar(255), lighted varchar(255), closed varchar(255), le_ident varchar(255), le_latitude_deg varchar(255), le_longitude_deg varchar(255), le_elevation_ft varchar(255), le_heading_degT varchar(255), le_displaced_threshold_ft varchar(255), he_ident varchar(255), he_latitude_deg varchar(255), he_longitude_deg varchar(255), he_elevation_ft varchar(255), he_heading_degT varchar(255), he_displaced_threshold_ft varchar(255), primary key (id));");
		sql.add("insert into `runways` select * from csvread('classpath:runways.csv', null, 'fieldDelimiter=\" fieldSeparator=, lineSeparator=\\n');");

		sql.add("create table countries(id integer, code varchar(2) not null, name varchar(255), continent varchar(255), wikipedia_link varchar(255), keywords varchar(255), primary key (code));");
		sql.add("insert into `countries` select * from csvread('classpath:countries.csv', null, 'fieldDelimiter=\" fieldSeparator=, lineSeparator=\\n');");

		Iterator<String> sqlIt = sql.iterator();

		try {
			connection.setAutoCommit(false);

			while (sqlIt.hasNext()) {

				st = connection.prepareStatement(sqlIt.next());
				st.executeUpdate();
				st.close();
			}

			connection.commit();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	static HashMap<String, String> countryNameCodeMap = new HashMap<String, String>();
	static HashMap<String, String> countryCodeNameMap = new HashMap<String, String>();

	static List<String> countryCodes = new ArrayList<String>();
	static List<String> countryNames = new ArrayList<String>();

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

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static List<String> getCountryNames() {

		if (countryNames.size() == 0) {
			countryNames = getFromCountries("name");
		}

		return countryNames;
	}

	public static List<String> getCountryCodes() {

		if (countryCodes.size() == 0) {
			countryCodes = getFromCountries("code");
		}

		return countryCodes;
	}

	public static List<Airport> getAirports(String country) {
		List<Airport> res = new ArrayList<Airport>();

		String countryCode = country;

		if (countryCode.length() != 2)
			countryCode = getCountryCode(countryCode);

		Connection c = getConnection();
		PreparedStatement stmt = null;

		try {

			String sql = "SELECT * FROM airports WHERE iso_country='" + countryCode + "';";
			stmt = c.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			while (rs.next()) {
				res.add(new Airport(rs.getString("ident"), rs.getString("name")));
			}
			rs.close();
			c.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}

	public static Map<String, List<String>> getSurfaceTypes() {
		
		// I copied this approach, especially the sql query, from https://github.com/arjun-1
		
		Map<String, List<String>> res = new HashMap<String, List<String>>();
				
		String sql = "select distinct(r.surface), c.name from airports a, runways r, countries c where a.id = r.airport_ref and c.code = a.iso_country order by c.name;";

		try {

			Connection c = getConnection();

			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			String prevCountry = "";
			List<String> currentCountrySurfaces = new ArrayList<String>();
			while (rs.next()) {
				
				if (prevCountry.equals("")) prevCountry = rs.getString("name");
				System.out.println("prevCountry: " + prevCountry);
				if (!rs.getString("name").equals(prevCountry)) {
					res.put(prevCountry, currentCountrySurfaces);
					String country = rs.getString("name");
					prevCountry = country;
					currentCountrySurfaces = new ArrayList<String>();
				}
				
				currentCountrySurfaces.add(rs.getString("surface"));
			}

			c.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static List<Runway> getRunways(String airportIdent) {

		List<Runway> res = new ArrayList<Runway>();

		try {

			Connection c = getConnection();

			String sql = "SELECT * FROM runways WHERE airport_ident=?;";
			PreparedStatement stmt = c.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setString(1, airportIdent);
			ResultSet rs = stmt.executeQuery();

			while (rs.next())
				res.add(new Runway(rs.getInt("id")));

			c.close();

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
				res = rs.getInt(1);

			c.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}

	private static Connection getConnection() {
		Connection dbConnection = null;
		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try {
			dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
			return dbConnection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return dbConnection;
	}
}