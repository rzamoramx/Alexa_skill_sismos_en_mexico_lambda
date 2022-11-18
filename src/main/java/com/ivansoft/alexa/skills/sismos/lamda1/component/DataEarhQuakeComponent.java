package com.ivansoft.alexa.skills.sismos.lamda1.component;

import com.ivansoft.alexa.skills.sismos.lamda1.model.SSNModel;
import com.ivansoft.alexa.skills.sismos.lamda1.service.AmazonDynamoService;
import com.ivansoft.alexa.skills.sismos.lamda1.service.RssSSNService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DataEarhQuakeComponent {
    Map<String,String> states = Map.ofEntries(
            Map.entry("OAX", "oaxaca"),
            Map.entry("GRO", "guerrero"),
            Map.entry("CHIS", "chiapas"),
            Map.entry("JAL", "jalisco"),
            Map.entry("SON", "Sonora"),
            Map.entry("BC", "Baja California"),
            Map.entry("VER", "Veracruz"),
            Map.entry("SIN", "Sinaloa"),
            Map.entry("COL", "Colima"),
            Map.entry("MICH", "Michoacan"),
            Map.entry("BCS", "Baja California Sur"),
            Map.entry("NAY", "Nayarit"),
            Map.entry("MI", "Michoacan"));

    private final AmazonDynamoService amazonDynamoService = new AmazonDynamoService();
    private final RssSSNService rssSSNService = new RssSSNService();

    public String getEarthquakes() {
        // Always generate data (Cron by Cloud Watch Event timer)
        String result = getFromSSN();
        setToAmazonDynamo(result);
        return "Data generated";
    }

    /**
     * Parse data from RSS feed of SSN and transform to text to speech for alexa
     * @return a string to speech
     */
    public String getFromSSN() {
        List<SSNModel> earthQuakes = rssSSNService.readFeed();

        String allEarthquakes = "Últimos sismos registrados por el Servicio Sismológico Nacional, mayores a cuatro grados. ";

        // If we couldn't get data from service
        if (earthQuakes.size()==1) {
            return allEarthquakes + " Ninguno registrado. Intenta mas tarde.";
        }

        // Only we care earthquakes more than 4 grades in Richter's scale
        List<SSNModel> tmpEarthquakes = new ArrayList<>();
        for (int i = 1; i < earthQuakes.size(); i++) {
            if (Integer.parseInt(earthQuakes.get(i).getTitle().substring(0,1)) < 4) {
                continue;
            }

            tmpEarthquakes.add(earthQuakes.get(i));
        }

        // If there aren't important earthquakes
        if (tmpEarthquakes.size()<2) {
            return allEarthquakes + " Ninguno mayor a cuatro grados registrado. Vuelve mas tarde.";
        }

        for (int i = 1; i < tmpEarthquakes.size(); i++) {
            String newString = "";

            // Read the title and get the number of intensity of the earthquake
            for ( char character : tmpEarthquakes.get(i).getTitle().toCharArray() ) {
                if (Character.isDigit(character)) {
                    newString += character;
                }
                else if (character!=','){
                    newString += character;
                }
                else {
                    break;
                }
            }

            // Add hour that when happened
            String hour = tmpEarthquakes.get(i).getDescription();
            hour = hour.substring(hour.indexOf("cha:")+14, hour.indexOf("cha:")+20);

            newString = "sismo de " + newString + " sucedido a las " + hour + "| " +
                    tmpEarthquakes.get(i).getTitle().substring(tmpEarthquakes.get(i).getTitle().indexOf("al"));

            // Change abbreviation of states to string
            newString = " " + newString.substring(0, newString.indexOf(',')) + " "
                        + states.get(newString.substring(newString.indexOf(',') + 1).trim());

            allEarthquakes += newString.replace("CD", "Ciudad")
                    .replace("NVO", "Nuevo")
                    .replace("J", "Juan")
                    .replace("|", ",");

            // Only 8 earthquakes, no need hear many
            if (i == 8) {
                break;
            }

            if ((tmpEarthquakes.size()-i) == 2) {
                allEarthquakes += " y ";
            }
            else {
                allEarthquakes += ". ";
            }
        }

        return allEarthquakes + ". Vuelve pronto.";
    }

    /**
     * Update earthquakes in DynamoDB
     * @param data the string of speech for alexa
     */
    private void setToAmazonDynamo(String data) {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        amazonDynamoService.saveData(data, String.valueOf(timestamp.getTime()));
    }

}
