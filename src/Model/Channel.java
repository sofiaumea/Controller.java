package Model;

/**
 * This class sets and gets all the variables included in a given channel.
 */
public class Channel {
    private String id;
    private String name;
    private String scheduleURL;


    public Channel(){
    }

    /**
     * Gets the id for the channel
     *
     * @return an integer representing id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the name for the channel
     *
     * @return a String representing name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the url to be able to parse the scheduled episodes
     *
     * @return a String representing the url
     */
    public String getScheduleURL() {
        return scheduleURL;
    }


    /**
     * Sets the id for the channel
     *
     * @param id an integer representing id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the name for the channel
     *
     * @param name a String representing name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the schedule url
     *
     * @param scheduleURL a string representing an url address
     */
    public void setScheduleURL(String scheduleURL) {
        this.scheduleURL = scheduleURL;
    }

}
