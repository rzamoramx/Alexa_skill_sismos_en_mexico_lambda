import com.ivansoft.alexa.skills.sismos.lamda1.model.SSNModel;
import com.ivansoft.alexa.skills.sismos.lamda1.service.RssSSNService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimestampTest {
    private static final Map<String,String> states = Map.ofEntries(
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

    public static void main(String[] args) {
        //testTimestampData();
        //testParseDescription();
        testFeed();
    }

    public static void testTimestampData() {
        Timestamp timestamp = new Timestamp( Long.parseLong("1577557903036") );
        System.out.println("fecha antes: "+ timestamp.toLocalDateTime().toString());

        timestamp.setTime(timestamp.getTime() + TimeUnit.MINUTES.toMillis(9));

        System.out.println("fecha despues: "+ timestamp.toLocalDateTime().toString());

        String data = "";

        if (timestamp.before(new Date())) {
            data = "-1";
        }
        else {
            data = "earthquakes bla bla";
        }

        if (data.equals("-1")) {
            System.out.println("Expired and re-generated");
        }
        else {
            System.out.println("Still valid");
        }
    }

    public static void testParseDescription() {
        String desc = "<![CDATA[<p>Fecha:2020-01-07 15:56:58 (Hora de M&eacute;xico)<br/>Lat/Lon: 15.87/-94.86<br/>Profundidad: 33.0 km </p>]]>";
        System.out.println("i:"+desc.indexOf("cha:"));
        desc = desc.substring(desc.indexOf("cha:")+14, desc.indexOf("cha:")+20);
        System.out.println("result: "+desc);
    }

    public static void testFeed() {
        RssSSNService rssSSNService = new RssSSNService();
        List<SSNModel> earthQuakes = rssSSNService.readFeed();

        String allEarthquakes = "Últimos sismos registrados por el Servicio Sismológico Nacional, mayores a cuatro grados. ";

        // If we couldn't get data from service
        if (earthQuakes.size()==1) {
            System.out.println(" Ninguno registrado. Intenta mas tarde.");
        }

        // Only we care earthquakes more than 4 grades
        List<SSNModel> tmpEarthquakes = new ArrayList<>();
        for (int i = 1; i < earthQuakes.size(); i++) {
            if (Integer.parseInt(earthQuakes.get(i).getTitle().substring(0,1)) < 3) {
                continue;
            }

            tmpEarthquakes.add(earthQuakes.get(i));
        }

        // If there isn't important earthquakes
        if (tmpEarthquakes.size()<2) {
            System.out.println(" Ninguno mayor a cuatro grados registrado. Vuelve mas tarde.");
        }

        // for each earthquake
        for (int i = 1; i < tmpEarthquakes.size(); i++) {
            String newString = "";

            // Read title and replace number of intensity with string to speech
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

            // Add hour that happened
            String hour = tmpEarthquakes.get(i).getDescription();
            hour = hour.substring(hour.indexOf("cha:")+14, hour.indexOf("cha:")+20);

            System.out.println(hour);

            newString = "sismo de " + newString + " sucedido a las " + hour + "| " +
                    tmpEarthquakes.get(i).getTitle().substring(tmpEarthquakes.get(i).getTitle().indexOf("al"));

            System.out.println(newString);

            // Change abreviation of states to string to speech
            newString = " " + newString.substring(0, newString.indexOf(',')) + " "
                    + states.get(newString.substring(newString.indexOf(',') + 1).trim());

            System.out.println("-----> "+newString);

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

        System.out.println(allEarthquakes + ". Vuelve pronto.");
    }
}
