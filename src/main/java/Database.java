import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
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
	
	private static String getCountryCodeFromName(String countryName) {		
		try {
			Connection c = getConnection();
			String sql = "select * from countries;";
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("name");
				if (name.equalsIgnoreCase(countryName))
					return rs.getString("code");
			}
			c.close();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static List<ArrayList<String>> getAirports(String country) {
		
		String countryCode = country.length() == 2 ? country : null;
		
		if (countryCode == null) countryCode = getCountryCodeFromName(country);
		
		if (countryCode == null) return Collections.emptyList();
		
		List<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		
		try {
			Connection c = getConnection();
			String sql = "select * from airports;";
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String iso_country = rs.getString("iso_country");
				if (!iso_country.equalsIgnoreCase(countryCode)) continue;
				ArrayList<String> airport = new ArrayList<String>();
				String name = rs.getString("name");
				String type = rs.getString("type");
				int id = rs.getInt("id");
				airport.add(""+id);
				airport.add(name);
				airport.add(type);
				result.add(airport);
			}
			c.close();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
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
