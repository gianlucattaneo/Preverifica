package servizioweb;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.*;
import java.sql.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author Gianluca
 */
public class Automobili extends HttpServlet {

    final private String driver = "com.mysql.jdbc.Driver";
    final private String dbms_url = "jdbc:mysql://localhost/";
    final private String database = "cattaneo_automobili";
    final private String user = "root";
    final private String password = "";
    private Connection conn;
    private boolean connected;

    @Override
    public void init() {
        String url = dbms_url + database;
        try {
            Class.forName(driver);

            conn = DriverManager.getConnection(url, user, password);
            connected = true;
        } catch (SQLException e) {
            connected = false;
        } catch (ClassNotFoundException e) {
            connected = false;
        }
    }

    @Override
    public void destroy() {
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Automobili.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet WS_Phone</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet WS_Phone at " + request.getContextPath() + "</h1>");
            out.println("<p> Prova</p>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String targa, scadAssicurazione, scadBollo, classeInq, ricercata;
        
        if (!connected) {
            response.sendError(500, "DBMS server error!");
            return;
        }

        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

            Statement statement = conn.createStatement();

            String sql ="";
            if (request.getParameter("targa")!=""){  
                sql = "SELECT * FROM automobili WHERE targa = '" + request.getParameter("targa")+"'";
            }else{
                sql = "SELECT * FROM automobili";
            }
            ResultSet result = statement.executeQuery(sql);

            out.println("<entry>");
            
            while(result.next()) {
                    targa = result.getString(1);
                    scadAssicurazione = result.getString(2);
                    scadBollo = result.getString(3);
                    classeInq= result.getString(4);
                    ricercata = result.getString(5);

                    out.println("<automobile>");

                    out.print("<targa>");
                    out.print(targa);
                    out.println("</targa>");

                    out.print("<scadAssicurazione>");
                    out.print(scadAssicurazione);
                    out.println("</scadAssicurazione>");

                    out.print("<scadBollo>");
                    out.print(scadBollo);
                    out.println("</scadBollo>");

                    out.print("<classeInq>");
                    out.print(classeInq);
                    out.println("</classeInq>");

                    out.print("<ricercata>");
                    out.print(ricercata);
                    out.println("</ricercata>");

                    out.println("</automobile>");
                }
            
            out.println("</entry>");
        } catch (Exception ex) {
        } finally {
            out.close();
        }

        response.setStatus(200); // OK

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      String targa = request.getParameter("targa");
      String scadenzaAssicurazione = request.getParameter("ScadenzaAssicurazione");
      String scadenzaBollo = request.getParameter("ScadenzaBollo");
      String classeInquinamento = request.getParameter("ClasseInquinamento");
      String ricercata = request.getParameter("Ricercato");

        if (!connected) {
            response.sendError(500, "DBMS server error!");
            return;
        }

      Statement statement = null;
        try {
            statement = conn.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(Automobili.class.getName()).log(Level.SEVERE, null, ex);
        }
                        String stringaSql = "INSERT INTO automobili(targa, scadenzaAssicurazione, scadenzaBollo, classeInquinamento, ricercata) VALUES ('"+targa+"','"+scadenzaAssicurazione+"','"+scadenzaBollo+"',"+classeInquinamento+",'"+ricercata+"')";
        try {
            if (statement.executeUpdate(stringaSql) <= 0) {
                statement.close();
                return;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Automobili.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Automobili.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url_name;
        String url;
        String line;
        String[] url_section;

        // verifica stato connessione a DBMS
        if (!connected) {
            response.sendError(500, "DBMS server error!");
            return;
        }
        // estrazione nominativo da URL
        url = request.getRequestURL().toString();
        url_section = url.split("/");
        url_name = url_section[url_section.length - 1];
        if (url_name == null) {
            response.sendError(400, "Request syntax error!");
            return;
        }
        if (url_name.isEmpty()) {
            response.sendError(400, "Request syntax error!");
            return;
        }
        try {
            // scrittura nel file "entry.xml" del body della richiesta
            BufferedReader input = request.getReader();
            BufferedWriter file = new BufferedWriter(new FileWriter("entry.xml"));
            while ((line = input.readLine()) != null) {
                file.write(line);
                file.newLine();
            }
            input.close();
            file.flush();
            file.close();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("entry.xml");
            Element root = document.getDocumentElement();

            NodeList list = root.getElementsByTagName("titolo");
            String titolo = null;
            if (list != null && list.getLength() > 0) {
                titolo = list.item(0).getFirstChild().getNodeValue();
            }

            list = root.getElementsByTagName("descrizione");
            String descrizione = null;
            if (list != null && list.getLength() > 0) {
                descrizione = list.item(0).getFirstChild().getNodeValue();
            }

            list = root.getElementsByTagName("tagIdentificativo");
            String tagIdentificativo = null;
            if (list != null && list.getLength() > 0) {
                tagIdentificativo = list.item(0).getFirstChild().getNodeValue();
            }

            list = root.getElementsByTagName("rilevante");
            String rilevante = null;
            if (list != null && list.getLength() > 0) {
                rilevante = list.item(0).getFirstChild().getNodeValue();
            }

            list = root.getElementsByTagName("livelloAutorizzativo");
            String livelloAutorizzativo = null;
            if (list != null && list.getLength() > 0) {
                livelloAutorizzativo = list.item(0).getFirstChild().getNodeValue();
            }

            if (titolo == null || descrizione == null || tagIdentificativo == null || rilevante == null || livelloAutorizzativo == null) {
                response.sendError(400, "Malformed XML!");
                return;
            }
            if (titolo.isEmpty() || descrizione.isEmpty() || tagIdentificativo.isEmpty() || rilevante.isEmpty() || livelloAutorizzativo.isEmpty()) {
                response.sendError(400, "Malformed XML!");
                return;
            }
            if (!titolo.equalsIgnoreCase(url_name)) {
                response.sendError(400, "URL name mismtach XML name!");
                return;
            }
            try {
                Statement statement = conn.createStatement();
                if (statement.executeUpdate("UPDATE circolari SET Number='" + "number" + "'WHERE Name = '" + "name" + "';") <= 0) {
                    response.sendError(404, "Entry not found!");
                    statement.close();
                    return;
                }
                statement.close();
            } catch (SQLException e) {
                response.sendError(500, "DBMS server error!");
                return;
            }
            response.setStatus(204); // OK
        } catch (ParserConfigurationException e) {
            response.sendError(500, "XML parser error!");
        } catch (SAXException e) {
            response.sendError(500, "XML parser error!");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String operazione = "";
        String line = "";

        if (!connected) {
            response.sendError(500, "DBMS server error!");
            return;
        }

        try {

            BufferedReader input = request.getReader();
            BufferedWriter file = new BufferedWriter(new FileWriter("entry.xml"));
            while ((line = input.readLine()) != null) {
                file.write(line);
                file.newLine();
            }
            input.close();
            file.flush();
            file.close();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse("entry.xml");
            Element root = document.getDocumentElement();

            NodeList list = root.getElementsByTagName("operazione");
            if (list != null && list.getLength() > 0) {
                operazione = list.item(0).getFirstChild().getNodeValue();
            }

            list = root.getElementsByTagName("id");
            String id = null;
            if (list != null && list.getLength() > 0) {
                id = list.item(0).getFirstChild().getNodeValue();
            }
            if (operazione.equals("deletebyid")) {
                Statement statement = conn.createStatement();
                if (statement.executeUpdate("DELETE FROM calendario WHERE ID_Calendario = '" + id + "';") <= 0) {
                    response.sendError(404, "Entry not found!");
                    statement.close();
                    return;
                }
                statement.close();
                response.setStatus(204); // OK
            }

        } catch (SQLException e) {
            response.sendError(500, "DBMS server error!");
            return;
        } catch (SAXException ex) {
            Logger.getLogger(Automobili.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Automobili.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getServletInfo() {
        return "CircolariDB";
    }// </editor-fold>
}
