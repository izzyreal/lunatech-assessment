import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Main extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter w = resp.getWriter();
		try {
			Connection c = getConnection();
			String sql = "select * from countries;";
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("name");
				String code = rs.getString("code");
				int id = rs.getInt("id");
				w.print("\tName: " + name + ", code: " + code + ", id: " + id + "\n");
			}
			sql = "select * from airports;";
			stmt = c.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("name");
				String type = rs.getString("type");
				int id = rs.getInt("id");
				w.print("\tName: " + name + ", type: " + type + ", id: " + id + "\n");
			}

			c.close();
		} catch (URISyntaxException e) {
			w.print(e.getMessage());
			e.printStackTrace();
		} catch (SQLException e) {
			w.print(e.getMessage());
			e.printStackTrace();
		}
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
