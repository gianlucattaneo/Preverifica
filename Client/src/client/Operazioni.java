/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Gianluca
 */
public class Operazioni {
    private String baseUrl;
    private int statusChiamata;
    private Vector<String> response;
    
    Operazioni(String baseUrl) {
        this.baseUrl = baseUrl;
        
        this.statusChiamata = 0;
        
        response = new Vector<>();
    }
    
    public int cercaAuto(String ptarga) throws ParserConfigurationException, SAXException, MalformedURLException, IOException
    {
            //invio richiesta al web server
            
            URL server = new URL(baseUrl + "?targa=" + ptarga);
            HttpURLConnection service = (HttpURLConnection) server.openConnection();
            
            service.setRequestProperty("Accept-Charset", "UTF-8");
            service.setRequestProperty("Accept", "text/xml");
            
            service.setDoInput(true);
            service.setRequestMethod("GET");
            
            service.connect();
            
            statusChiamata = service.getResponseCode();
            if (statusChiamata != 200) {
                return statusChiamata;
            }
            //ottenimento informazioni dal web server
            
            BufferedReader input = new BufferedReader(new InputStreamReader(service.getInputStream(), "UTF-8"));
            BufferedWriter file = new BufferedWriter(new FileWriter("entry.xml"));
            
            String line;
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
            response.clear();
            
            NodeList list = root.getElementsByTagName("automobile");
            if (list != null && list.getLength() > 0) {
                for(int i = 0; i< list.getLength(); i++)
                {
                    Element automobile = (Element) list.item(i);
                    
                    response.add("automobile:");
                    
                    NodeList targa = automobile.getElementsByTagName("targa");
                    if (targa != null && targa.getLength() > 0) {
                        response.add("targa:" +targa.item(0).getFirstChild().getNodeValue());
                    }
                    
                    NodeList scadAssicurazione = automobile.getElementsByTagName("scadAssicurazione");
                    if (scadAssicurazione != null && scadAssicurazione.getLength() > 0) {
                        response.add("scad Assicurazione:" +scadAssicurazione.item(0).getFirstChild().getNodeValue());
                    }
                    
                    NodeList scadBollo = automobile.getElementsByTagName("scadBollo");
                    if (scadBollo != null && scadBollo.getLength() > 0) {
                        response.add("scad Bollo:" +scadBollo.item(0).getFirstChild().getNodeValue());
                    }
                    
                    NodeList classeInq = automobile.getElementsByTagName("classeInq");
                    if (classeInq != null && classeInq.getLength() > 0) {
                        response.add("classe Inquinamento:" +classeInq.item(0).getFirstChild().getNodeValue());
                    }
                    
                    NodeList ricercata = automobile.getElementsByTagName("ricercata");
                    if (ricercata != null && ricercata.getLength() > 0) {
                        if (ricercata.item(0).getFirstChild().getNodeValue().equals(1))
                            response.add("ricercata: true");
                        else
                            response.add("ricercata: false");
                    }
                    
                    
                    response.add("");
                }
            }
           
        return statusChiamata;
    }
    
   public int inserisciMacchina(String targa, String ScadenzaAssicurazione , String ScadenzaBollo, String ClasseInquinamento, String Ricercato) throws ParserConfigurationException, SAXException
    {

        try {

            URL server = new URL(baseUrl);
            HttpURLConnection service = (HttpURLConnection) server.openConnection();

            service.setRequestProperty("Host", "localhost");
            service.setRequestProperty("Accept", "application/text");
            service.setRequestProperty("Accept-Charset", "UTF-8");

            service.setDoOutput(true);
            service.setRequestMethod("POST");

            String urlParameters = "targa="+targa+"&ScadenzaAssicurazione="+ScadenzaAssicurazione+"&ScadenzaBollo="+ScadenzaBollo+"&ClasseInquinamento="+ClasseInquinamento+"&Ricercato="+Ricercato;
            service.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(service.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            service.connect();

            statusChiamata = service.getResponseCode();

            return statusChiamata;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Operazioni.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Operazioni.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Operazioni.class.getName()).log(Level.SEVERE, null, ex);
        }
        return statusChiamata;
    }
    
    void printResult() {
        for(String a : response)
            System.out.println(a);
    }
}
