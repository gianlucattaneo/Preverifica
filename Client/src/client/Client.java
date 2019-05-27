/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Gianluca
 */
public class Client {

    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    /**
     * @param args the command line arguments
     * @throws javax.xml.parsers.ParserConfigurationException
     */
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        Operazioni ws = new Operazioni("http://localhost:8080/ServizioWeb/Automobili");

        String scelta ="";
        do {
            System.out.println("Inserisci l'operazione desiderata");
            System.out.println("1 - Visualizza stato automobile");
            System.out.println("2 - Segnala automobile");
            System.out.println("3 - Inserisci automobile");
            System.out.println("4 - Visualizza automobili");
            System.out.println("0 - Termina programma");

            scelta = br.readLine();
            System.out.println("");
            switch (Integer.parseInt(scelta)) {
                case 1:{
                    System.out.println("inserisci targa da cercare");
                    String targa = br.readLine();

                    ws.cercaAuto(targa);
                    ws.printResult();
                    break;
                }
                case 2:{
                    System.out.println("inserisci targa da segnalare");
                    String targa = br.readLine();

                    System.out.println(ws.sendPut(targa));
                    
                    break;
                }
                case 3:{
                    System.out.println("Inserisci Nuovo Veicolo");
                    
                    System.out.println("Targa");
                    String Targa = br.readLine();
                    
                    System.out.println("Scadenza assicurazione (yyyy-mm-dd)");
                    String DataAssicurazione = br.readLine();
                    
                    System.out.println("Scadenza bollo (yyyy-mm-dd)");
                    String DataBollo =  br.readLine();
                    
                    System.out.println("Classe Inquinamento");
                    String ClasseInquinamento = br.readLine();
                    
                    System.out.println("Ricercata [0,1]");
                    String Ricercato = br.readLine();
                    
                    System.out.println(ws.inserisciMacchina(Targa, DataAssicurazione, DataBollo, ClasseInquinamento, Ricercato));
                    break;
                }
                case 4:{
                    ws.cercaAuto("");
                    ws.printResult();
                    break;
                }

                default:
            }
            
        } while (!scelta.equals("0"));
    }
}
