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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.izmar.lunatech.model.Airport;
import com.izmar.lunatech.model.Country;
import com.izmar.lunatech.model.Runway;

/*
 * The database class is static method only, and only used by the model.
 */

public class Database {

	private static boolean initialized = false;

	private static final String DB_DRIVER = "org.h2.Driver";
	private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
	private static final String DB_USER = "";
	private static final String DB_PASSWORD = "";

	static HashMap<String, String> countryNameCodeMap = new HashMap<String, String>();
	static HashMap<String, String> countryCodeNameMap = new HashMap<String, String>();

	/**
	 * Create in-memory H2 database and read tables from CSV files
	 */
	public static void initTables() {

		// initialize only once
		if (initialized)
			return;

		Connection c = getConnection();

		PreparedStatement st = null;
		List<String> sql = new ArrayList<String>();

		sql.add("create table airports(id integer not null, ident varchar(255), type varchar(255), name varchar(255), latitude_deg varchar(255), longitude_deg varchar(255), elevation_ft varchar(255), continent varchar(255), iso_country varchar(255), iso_region varchar(255), municipality varchar(255), scheduled_service varchar(255), gps_code varchar(255), iata_code varchar(255), local_code varchar(255), home_link varchar(255), wikipedia_link varchar(255), keywords varchar(255), primary key (id));");
		sql.add("insert into `airports` select * from csvread('classpath:airports.csv', null, 'charset=UTF-8 fieldDelimiter=\" fieldSeparator=, lineSeparator=\\n');");

		sql.add("create table runways(id integer not null, airport_ref integer, airport_ident varchar(255), length_ft varchar(255), width_ft varchar(255), surface varchar(255), lighted varchar(255), closed varchar(255), le_ident varchar(255), le_latitude_deg varchar(255), le_longitude_deg varchar(255), le_elevation_ft varchar(255), le_heading_degT varchar(255), le_displaced_threshold_ft varchar(255), he_ident varchar(255), he_latitude_deg varchar(255), he_longitude_deg varchar(255), he_elevation_ft varchar(255), he_heading_degT varchar(255), he_displaced_threshold_ft varchar(255), primary key (id));");
		sql.add("insert into `runways` select * from csvread('classpath:runways.csv', null, 'charset=UTF-8 fieldDelimiter=\" fieldSeparator=, lineSeparator=\\n');");

		sql.add("create table countries(id integer, code varchar(2) not null, name varchar(255), continent varchar(255), wikipedia_link varchar(255), keywords varchar(255), primary key (code));");
		sql.add("insert into `countries` select * from csvread('classpath:countries.csv', null, 'charset=UTF-8 fieldDelimiter=\" fieldSeparator=, lineSeparator=\\n');");

		Iterator<String> sqlIt = sql.iterator();

		try {

			c.setAutoCommit(false);

			while (sqlIt.hasNext()) {

				st = c.prepareStatement(sqlIt.next());
				st.executeUpdate();
				st.close();

			}

			c.commit();
			c.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		initialized = true;
	}

	/**
	 * @return true if countryCode is found in database
	 */
	public static boolean checkIfCountryExists(String countryCode) {

		if (countryCode == null)
			return false;

		Connection c = getConnection();
		String sql = "select * from countries where code='" + countryCode + "';";

		boolean res = false;

		try {

			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			if (rs.next())
				res = true;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * We create some maps so we can figure out which country code belongs to which
	 * country name and vice versa to reduce the number of database connections and
	 * queries.
	 */
	private static void populateCountryMaps() {

		Connection c = getConnection();
		String sql = "select * from countries;";

		try {

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

	/**
	 * @return country code if countryName can be found, return null if not
	 */
	public static String getCountryCode(String countryName) {

		if (countryNameCodeMap.size() == 0)
			populateCountryMaps();

		return countryNameCodeMap.get(countryName);
	}

	/**
	 * @return country name if countryCode can be found, return null if not
	 */
	public static String getCountryName(String countryCode) {

		if (countryCodeNameMap.size() == 0)
			populateCountryMaps();

		return countryCodeNameMap.get(countryCode);
	}

	/**
	 * @return all values from a specific column in the countries table
	 */
	public static List<String> getFromCountries(String column) {

		Connection c = getConnection();
		List<String> res = new ArrayList<String>();
		String sql = "select " + column + " from countries;";

		try {

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

	/**
	 * @return list of airports of a country
	 */
	public static List<Airport> getAirports(Country country) {

		Connection c = getConnection();
		List<Airport> res = new ArrayList<Airport>();
		String sql = "select * from airports where iso_country='" + country.getCode() + "';";

		try {

			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();

			while (rs.next())
				res.add(new Airport(rs.getString("ident"), rs.getString("name")));

			rs.close();
			c.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * @return a map of all distinct surface types per country
	 */
	public static Map<String, List<String>> getSurfaceTypes() {

		// Approach copied from https://github.com/arjun-1

		Connection c = getConnection();
		Map<String, List<String>> res = new HashMap<String, List<String>>();
		String sql = "select distinct(r.surface), c.name from airports a, runways r, countries c where a.id = r.airport_ref and c.code = a.iso_country order by c.name;";

		try {

			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			String prevCountry = "";
			List<String> currentCountrySurfaces = new ArrayList<String>();

			while (rs.next()) {

				if (prevCountry.equals(""))
					prevCountry = rs.getString("name");

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

	/**
	 * @return a list of runways for a given airport
	 */
	public static List<Runway> getRunways(Airport airport) {

		Connection c = getConnection();
		List<Runway> res = new ArrayList<Runway>();

		try {

			String sql = "select id from runways where airport_ident='" + airport.getIdent() + "';";
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next())
				res.add(new Runway(rs.getInt("id")));

			c.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * @param most if true, the most common values are returned. If false, the least
	 *             common.
	 * @return a map with the most or least common values in a given table's column.
	 *         Key of the map is the value of the table's column. Value of the map
	 *         is the number of times the key is in the table's column.
	 */
	public static Map<String, Integer> getCommon(String table, String column, boolean most) {

		Connection c = getConnection();
		Map<String, Integer> res = new LinkedHashMap<String, Integer>();
		String sql = "select " + column + ", count(*) as counted from " + table + " group by " + column
				+ " order by counted " + (most ? "desc" : "asc") + " limit 10;";

		try {

			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next())
				res.put(rs.getString(column), rs.getInt("counted"));

			c.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * @return most or least common mentions of country codes in the airports table.
	 */
	public static List<Country> getCommonAirportCountries(boolean most) {

		List<Country> res = new ArrayList<Country>();
		Map<String, Integer> tm = getCommon("airports", "iso_country", most);
		Iterator<String> keySet = tm.keySet().iterator();

		/*
		 * getCommon()'s sql query only counts country codes that DO exist in the
		 * airports table. Some countries have no airports at all, so we use a separate
		 * method to fetch those.
		 */
		if (!most)
			res = getCountriesWithNoAirports();

		while (keySet.hasNext()) {

			String code = keySet.next();
			res.add(new Country(code, tm.get(code).intValue()));

		}

		if (!most && res.size() > 10)
			res = res.subList(0, 10);

		return res;
	}

	/**
	 * @return a list of countries with no airports at all
	 */
	private static List<Country> getCountriesWithNoAirports() {

		List<Country> res = new ArrayList<Country>();
		String sql = "select code from countries where code not in (select iso_country from airports);";
		Connection c = getConnection();

		try {

			Statement st = c.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next())
				res.add(new Country(rs.getString("code"), 0));

			c.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * @return a list of the most common runway idents
	 */
	public static List<String> getMostCommonRunwayIdents() {

		List<String> res = new ArrayList<String>();
		Map<String, Integer> tm = getCommon("runways", "le_ident", true);
		Iterator<String> keySet = tm.keySet().iterator();

		while (keySet.hasNext()) {

			String key = keySet.next();
			res.add("Runway identification " + key + " occurs " + tm.get(key).intValue() + " times.");

		}

		return res;
	}

	/**
	 * @return a database connection
	 */
	private static Connection getConnection() {
		Connection dbConnection = null;
		try {
			Class.forName(DB_DRIVER);
			dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
			return dbConnection;
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dbConnection;
	}

}
