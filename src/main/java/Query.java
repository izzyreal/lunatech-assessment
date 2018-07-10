import static j2html.TagCreator.body;
import static j2html.TagCreator.form;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.title;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import j2html.TagCreator;
import j2html.tags.ContainerTag;
import j2html.tags.Tag;

public class Query extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter w = resp.getWriter();
		ContainerTag page = html(head(title("Query")), body(form().withMethod("post").withAction("query").with(inputField()).with(submitButton()))); 
		w.print(page.renderFormatted());
	}
	
	private static Tag submitButton() {
		return TagCreator.button().withType("submit").withName("submitbutton").withText("Submit");
	}
	
	private static Tag inputField() {
		return TagCreator.input().withType("textbox").withId("country").withName("country").withPlaceholder("country code or name");
	}
	
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter w = resp.getWriter();
		w.print(head(title("Query Results")).renderFormatted());
		if (req.getParameter("submitbutton") != null) {
        	//w.print("submit button clicked\n");
        }
        String country = req.getParameter("country");
        if (country != null) {
        	
        	//w.print("Airports in country " + req.getParameter("country") + " are:<br>");
        	Iterator<ArrayList<String>> airports = Database.getAirports(country).iterator();
        	while (airports.hasNext()) {
        		ArrayList<String> airport = airports.next();
        		for (int i = 0; i < airport.size(); i++) {
        			w.print(airport.get(i) + " ");
        		}
        		w.print("<br>");
        	}
        }
    }
}
