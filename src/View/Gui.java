package View;

import Model.Channel;
import Model.ScheduledEpisode;
import Model.XMLParser;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

/**
 * The graphical user interface presenting the information regarding radio channels.
 */
public class Gui {
    private JFrame frame;
    private final XMLParser parser = new XMLParser();
    private JTable table;
    private DefaultTableModel model;

    /**
     * Initializes the frame in constructor
     */
    public Gui(){
        frame = new JFrame("RadioInfo");
        frame.setSize(800, 600);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
    }

    /**
     * Creates the menu at the top of the frame. Adds action listeners to the menu items.
     *
     * @param channel an XMLParser object to get information regarding channels
     */
    public void createMenuBar(XMLParser channel) {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Kanaler");

        for(Channel channelName : channel.getListOfChannels()){
            JMenuItem menuItem = new JMenuItem(channelName.getName());
            menu.add(menuItem);
            //Updates table when pressing a menu item
            menuItem.addActionListener(e -> {
                String id = channelName.getId();
                updateTableForChannel(LocalDate.now(), id);
            });
        }

        JMenu menuProgram = new JMenu("Program");
        JMenuItem menuItemUpdate = new JMenuItem("Uppdatera");
        menuProgram.add(menuItemUpdate);
        menuItemUpdate.addActionListener(Update);

        JMenuItem menuItemInfo = new JMenuItem("Info");
        menuProgram.add(menuItemInfo);
        menuItemInfo.addActionListener(Info);

        JMenuItem menuItemQuit = new JMenuItem("Avsluta");
        menuProgram.add(menuItemQuit);
        menuItemQuit.addActionListener(Quit);

        menuBar.add(menu);
        menuBar.add(menuProgram);
        frame.setJMenuBar(menuBar);
    }


    /**
     * Updates the JTable with new information depending on what the user wants.
     *
     * @param now a local date
     * @param channelID a channel id or "" if non-specific id
     */
    private void updateTableForChannel(LocalDate now, String channelID) {
        parser.resetListOfChannels();
        parser.resetListOfEpisodes();
        parser.parseChannel(channelID);
        parser.parseEpisodes(now);
        clearTable();
        addRowsToTable(parser);
        table.repaint();
    }

    /**
     * Clears the table from values
     */
    private void clearTable() {
        model.setRowCount(0);
    }

    /**
     * Loops through every episode and adds the information to the table
     *
     * @param parser an XMLParser containing information regarding episodes
     */
    private void addRowsToTable(XMLParser parser) {
        for (ScheduledEpisode scheduledEpisode : parser.getListOfEpisodes()) {
            model.addRow(new Object[]{
                    scheduledEpisode.getTitle(), scheduledEpisode.getStartTime(),
                    scheduledEpisode.getEndTime(), scheduledEpisode.getStartDate(),
                    scheduledEpisode.getEndDate(), scheduledEpisode.getName()});
        }
    }


    /**
     * Creates the table present in the frame. Adds mouse listener to the table.
     * When clicking on a row, a JDialog pops up.
     *
     * @param episode an XMLParser containing information regarding episodes
     */
    public void createTablePanel(XMLParser episode){
        model = new DefaultTableModel();
        table = new JTable(model);

        model.addColumn("Titel");
        model.addColumn("Starttid");
        model.addColumn("Sluttid");
        model.addColumn("Startdatum");
        model.addColumn("Slutdatum");
        model.addColumn("Namn");
        addRowsToTable(episode);

        JPanel panel = new JPanel();
        panel.add(new JScrollPane(table));
        table.setPreferredScrollableViewportSize(new Dimension
                (1000, 500));
        frame.add(panel);
        frame.pack();


        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    JTable target = (JTable)e.getSource();
                    int row = target.getSelectedRow();

                    String value = episode.getListOfEpisodes().get(row).getImageurl();
                    if(value != null){
                        JDialog dialog = new JDialog();
                        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        dialog.setTitle("Programbeskrivning: " +
                                episode.getListOfEpisodes().get(row).getDescritption());

                        try {
                            dialog.add(new JLabel(new ImageIcon(ImageIO.read(new URL(value)))));
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }


                        dialog.pack();
                        dialog.setSize(new Dimension(900, 500));
                        dialog.setLocationByPlatform(true);
                        dialog.setVisible(true);
                    }
                }
            }
        });
    }

    /**
     * Updates the GUI every hour.
     *
     * @param frameTime an integer representing one hour in milliseconds
     */
    public void updateGUIEveryHour(int frameTime) {
        Timer timer = new Timer(frameTime, Timer_Tick);
        timer.start();
    }

    /**
     * Shows error message if exception is caught
     *
     * @param error an XMLParser object
     */
    public void showError(XMLParser error){
        JOptionPane.showMessageDialog(frame, error.getError(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Updates the frame
     */
    public void rePaint(){
        frame.invalidate();
        frame.validate();
        frame.repaint();
    }

    private final ActionListener Update = evt -> {
        LocalDate now = LocalDate.now();
        updateTableForChannel(now, "");
    };

    private final ActionListener Timer_Tick = evt -> {
        LocalDate now = LocalDate.now();
        updateTableForChannel(now, "");
    };

    private final ActionListener Quit = evt -> System.exit(0);

    private final ActionListener Info = evt -> {
        JDialog jDialog = new JDialog(frame, "Info");
        JTextArea jTextArea = new  JTextArea();
        String s = """
                Det här ett program som tillåter dig att se vad olika radiokanaler sänder.
                Fönstret uppdateras varje timme och du kan uppdatera det när du vill genom att klicka på Uppdatera i Program-menyn
                Du kan välja vilken kanals tablå du vill se genom att välja från drop down menyn i vänstra hörnet
                Klickar du på en rad i tabellen kommer en bild visas som är kopplad till det programmet.
                Skapare av detta program är Sofia Leksell
                """;

        jTextArea.setText(s);
        jTextArea.setEditable(false);
        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane =  new JScrollPane(
                jTextArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        jDialog.add(scrollPane);

        jDialog.setSize(540, 380);
        jDialog.setLocationRelativeTo(frame);
        jDialog.setVisible(true);
    };

    /**
     * Sets the frame as visible
     */
    public void show() {
        frame.setVisible(true);
    }

}

