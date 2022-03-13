package Model;

/**
 * This class sets and gets all the variables included in a given channel's program schedule.
 */
public class ScheduledEpisode {
    private String title;
    private String descritption;
    private String imageurl;
    private String name;
    private String startDate;
    private String startTime;
    private String endTime;
    private String endDate;

    public ScheduledEpisode(){
    }

    /**
     * Gets the name of the episode
     *
     * @return a String representing a name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets an image connected to an url address
     *
     * @return a String representing an url address
     */
    public String getImageurl() {
        return imageurl;
    }

    /**
     * Gets the end time of the episode
     *
     * @return a String representing a time
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Gets the end date of the episode
     *
     * @return a String representing a date
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Gets the title of the episode
     *
     * @return a String representing the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the description of the episode
     *
     * @return a String representing the description
     */
    public String getDescritption() {
        return descritption;
    }

    /**
     * Gets the start time of the episode
     *
     * @return a String representing a time
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Gets the start date of the episode
     *
     * @return a String representing a date
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets the url address containing an image
     *
     * @param imageurl the String representing an url address
     */
    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    /**
     * Sets the title for the episode
     *
     * @param title the title of the episode as a String
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the description of the episode
     *
     * @param descritption a String containing the description
     */
    public void setDescritption(String descritption) {
        this.descritption = descritption;
    }

    /**
     * Sets the start time of the episode. Deletes unnecessary characters in string.
     *
     * @param starttimeutc the start time as a String
     */
    public void setStartTime(String starttimeutc) {
        String[] stringArray = starttimeutc.split("T");
        startDate = stringArray[0];
        startTime = stringArray[1];
        startTime = startTime.substring(0, startTime.length()-1);
    }

    /**
     * Sets the end time of the episode. Deletes unnecessary characters in string.
     *
     * @param endtimeutc the end time as a String
     */
    public void setEndTime(String endtimeutc) {
        String[] stringArray = endtimeutc.split("T");
        endDate = stringArray[0];
        endTime = stringArray[1];
        endTime = endTime.substring(0, endTime.length()-1);
    }

    /**
     * Sets the name of the episode
     *
     * @param name the name as a String
     */
    public void setName(String name) {
        this.name = name;
    }
}
