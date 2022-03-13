package Model;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * A class that parses documents containing information regarding the Swedish Radio's channels' program schedules.
 */
public class XMLParser {
    private final ArrayList<Channel> listOfChannels = new ArrayList();
    private final ArrayList<ScheduledEpisode> listOfEpisodes = new ArrayList();
    private String error;
    private boolean errorOccur = false;

    public XMLParser() {
    }

    /**
     * Parses the document with channel information.
     *
     * @param channelID the desired channel id to parse
     */
    public void parseChannel(String channelID) {
        DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
        DocumentBuilder DB = null;
        try {
            DB = DBF.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            System.out.println("An exception was caught, cause:" + e.getCause());
        }

        Document doc;
        try {
            String xmlURL = "http://api.sr.se/v2/channels/";
            if (!channelID.equals("")) {
                doc = DB.parse((new URL(xmlURL + channelID +
                        "/?&pagination=false&size=1000").openStream()));
            }else{
                doc = DB.parse((new URL(xmlURL +
                        "?&pagination=false&size=1000").openStream()));
            }
            doc.getDocumentElement().normalize();
            getElementsforChannel(doc);
        } catch (SAXException e) {
            errorOccurred();
            error += "An exception was caught, cause: " + e.getCause();
        } catch (IOException e) {
            errorOccurred();
            error += "An exception was caught, cause: " + e.getCause();
        }
    }

    /**
     * Loops through the nodes in document regarding channels
     * and saves the information to an arraylist.
     *
     * @param doc the document to loop through
     */
    private void getElementsforChannel(Document doc) {
        NodeList channelsList = doc.getElementsByTagName("channel");
        for (int i = 0; i < channelsList.getLength(); i++) {
            Node n = channelsList.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) n;
                Channel channel = new Channel();
                channel.setName(element.getAttribute("name"));
                channel.setId(element.getAttribute("id"));
                NodeList childList = element.getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node cn = childList.item(j);

                    if (cn.getNodeType() == Node.ELEMENT_NODE) {
                        Element childElement = (Element) cn;
                        if (childElement.getTagName().equals("scheduleurl")) {
                            channel.setScheduleURL(childElement.getTextContent());
                        }
                    }
                }
                listOfChannels.add(channel);
            }
        }
    }

    /**
     * Parses the documents containing the scheduled episodes.
     *
     * @param date the date of the episode airing added to the url
     */
    public void parseEpisodes(LocalDate date) {
        try {
            for (Channel c : listOfChannels) {
                //Does not include 4868 because channel does not have a schedule url
                if ((c.getScheduleURL() != null) && (!c.getId().equals("4868"))) {
                    DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
                    DocumentBuilder DB = null;
                    try {
                        DB = DBF.newDocumentBuilder();
                    } catch (ParserConfigurationException e) {
                        errorOccurred();
                        error += "An exception was caught, cause: " + e.getCause();
                    }
                    Document doc;
                    ArrayList<LocalDate> threeDates = this.getThreeDays(date);

                    for (LocalDate date1 : threeDates) {
                        doc = DB.parse((new URL(c.getScheduleURL() + "&date=" +
                                date1.format(DateTimeFormatter.ISO_LOCAL_DATE)
                                + "&pagination=false&size=1000").openStream()));
                        doc.getDocumentElement().normalize();
                        getElementSchedule(doc);
                    }
                }
            }
        } catch(MalformedURLException e){
            errorOccurred();
            error += "An exception was caught, cause: " + e.getCause();
        } catch(IOException e){
            errorOccurred();
            error += "An exception was caught, cause: " + e.getCause();
        } catch(SAXException e){
            errorOccurred();
            error += "An exception was caught, cause: " + e.getCause();
        }
    }

    /**
     * Loops through the nodes in document regarding scheduled episodes
     * and saves the information to an arraylist. Does not add information if it is not in the
     * right time range.
     *
     * @param doc the document to loop through
     */
    private void getElementSchedule(Document doc) {
        NodeList scheduleList = doc.getElementsByTagName("scheduledepisode");
        boolean timecheck = false;
        for (int i = 0; i < scheduleList.getLength(); i++) {
            Node nNode1 = scheduleList.item(i);
            if (nNode1.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) nNode1;
                NodeList childList = elem.getChildNodes();
                ScheduledEpisode episode = new ScheduledEpisode();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node cn = childList.item(j);


                    if (cn.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) cn;

                        if(element.getTagName().equals("channel")) {
                            episode.setName(element.getAttribute("name"));
                        }

                        if (element.getTagName().equals("title")) {
                            episode.setTitle(element.getTextContent());
                        }

                        if (element.getTagName().equals("description")) {
                            episode.setDescritption(element.getTextContent());
                        }

                        if (element.getTagName().equals("starttimeutc")) {
                            ZonedDateTime date = ZonedDateTime.parse(element.getTextContent());
                            ZonedDateTime time = ZonedDateTime.ofInstant(date.toInstant(),
                                    ZoneId.systemDefault());
                            ZonedDateTime now = ZonedDateTime.now();
                            episode.setStartTime(time.toString().substring(0, 17));
                            if(time.isAfter(now.minusHours(12)) && time.isBefore(now.plusHours(12))) {
                                timecheck = true;
                            }
                            else {
                                continue;
                            }

                        }

                        if (element.getTagName().equals("endtimeutc")) {
                            ZonedDateTime date = ZonedDateTime.parse(element.getTextContent());
                            ZonedDateTime time = ZonedDateTime.ofInstant(date.toInstant(),
                                    ZoneId.systemDefault());
                            episode.setEndTime(time.toString().substring(0, 17));
                        }

                        if (element.getTagName().equals("imageurl")) {
                            episode.setImageurl(element.getTextContent());
                        }
                    }
                }
                if (timecheck){
                    listOfEpisodes.add(episode);
                    timecheck = false;
                }
            }
        }
    }

    /**
     * Checks if an error occurred
     *
     * @return true if error occurred
     */
    public void errorOccurred(){
        errorOccur = true;
    }

    /**
     * Checks if an error has occurred
     *
     * @return false or true depending if error has occurred
     */
    public boolean checkIfErrorOcccurred(){
        return errorOccur;
    }

    /**
     * Gets the error message
     *
     * @return a String representing an error message
     */
    public String getError(){
        return error;
    }

    /**
     * Gets the information kept about the channels
     *
     * @return an arraylist of Channel objects
     */
    public ArrayList<Channel> getListOfChannels() {
        return listOfChannels;
    }

    /**
     * Gets the information kept about the scheduled episodes
     *
     * @return an arraylist of ScheduledEpisode objects
     */
    public ArrayList<ScheduledEpisode> getListOfEpisodes() {
        return listOfEpisodes;
    }

    /**
     * Clears the arraylist of values
     */
    public void resetListOfChannels() {
        listOfChannels.clear();
    }

    /**
     * Clears the arraylist of values
     */
    public void resetListOfEpisodes() {
        listOfEpisodes.clear();
    }

    /**
     * Gets an arraylist of three dates; yesterday, today, tomorrow
     *
     * @param today the date to set as the date of today
     * @return an arraylist of LocalDate objects
     */
    private ArrayList<LocalDate> getThreeDays(LocalDate today){
        ArrayList<LocalDate> dateList = new ArrayList<>();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate yesterday = today.minusDays(1);
        dateList.add(yesterday);
        dateList.add(today);
        dateList.add(tomorrow);
        return dateList;
    }
}


