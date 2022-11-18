package com.ivansoft.alexa.skills.sismos.lamda1.service;

import com.ivansoft.alexa.skills.sismos.lamda1.model.SSNModel;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class RssSSNService {
    private URL url;

    public RssSSNService() {
        try {
            this.url = new URL("http://www.ssn.unam.mx/rss/ultimos-sismos.xml");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse data from RSS feed of SSN
     * @return a list instances of SSNModel
     */
    public List<SSNModel> readFeed() {
        SSNModel error = new SSNModel("Disculpa, estoy teniendo problemas para obtener información del " +
                "Servicio Sismológico Nacional, intenta más tarde, gracias.", "");
        List<SSNModel> sismos = new ArrayList<>();

        try {
            String title = "";
            String description = "";

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in = read();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

            // Read XML Doc
            while(eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    String localPart = event.asStartElement().getName()
                            .getLocalPart();
                    switch (localPart) {
                        case "item":
                            sismos.add(new SSNModel(title, description));
                            eventReader.nextEvent(); // event = eventReader.nextEvent();
                            break;
                        case "title":
                            title = getCharacterData(event, eventReader);
                            break;
                        case "description":
                            description = getCharacterData(event, eventReader);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + localPart);
                    }
                }
            }
        }
        catch (XMLStreamException | IOException e) {
            e.printStackTrace();
            sismos.add(error);
        }

        // The first line is not a valid earthquake, only the data source is announced, so we replace it
        if (sismos.size() == 1) {
            sismos.set(0, error);
        }
        else if (sismos.size() == 0) {
            sismos.add(error);
        }

        return sismos;
    }

    private String getCharacterData(XMLEvent event, XMLEventReader eventReader)
            throws XMLStreamException {
        String result = "";
        eventReader.nextEvent(); // event = eventReader.nextEvent();
        if (event instanceof Characters) {
            result = event.asCharacters().getData();
        }
        return result;
    }

    private InputStream read() throws IOException {
        return url.openStream();
    }
}

