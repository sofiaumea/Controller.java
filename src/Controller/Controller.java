package Controller;

import Model.XMLParser;
import View.Gui;
import javax.swing.*;
import java.time.LocalDate;

/**
 * The Controller class runs updates on the gui from the background thread and displays gui from EDT.
 */
public class Controller {
    private XMLParser xmlParser;
    private Gui gui;
    //One hour in milliseconds
    private final int BACKGROUND_WAIT_SECONDS = 60 * 60 * 1000;

    /**
     * Updates and views the GUI on event dispatch thread
     */
    public Controller() {
        SwingUtilities.invokeLater(() -> {
            gui = new Gui();
            SwingWorker thread = new SwingWorker();
            thread.execute();
            gui.show();
        });
    }

    /**
     * Runs the update of GUI on the worker thread
     */
    private class SwingWorker extends javax.swing.SwingWorker {
        @Override
        protected Void doInBackground() {
            LocalDate now = LocalDate.now();
            xmlParser = new XMLParser();
            xmlParser.parseChannel("");
            xmlParser.parseEpisodes(now);
            return null;
        }

        @Override
        protected void done() {
            if(xmlParser.checkIfErrorOcccurred()) {
                gui.showError(xmlParser);
            }
            gui.createMenuBar(xmlParser);
            gui.createTablePanel(xmlParser);
            gui.updateGUIEveryHour(BACKGROUND_WAIT_SECONDS);
            gui.rePaint();
        }
    }
}

