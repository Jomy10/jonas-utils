package progressBar.desktop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Arrays;

/**
 * Handles a JProgressBar based on sub-processes and activities within that subprocess.<br/>
 * I have linked a JProgressBar demo from the Java Docs in the see also<br/><br/>
 * todo methods for web and Android
 * @author Jonas Everaert
 * @author <a href="https://jonaseveraert.be">jonaseveraert.be</a>
 * @version %I%
 * @since 1.0
 *
 * @see ProgressBarWindow
 * @see <a href="https://docs.oracle.com/javase/tutorial/uiswing/components/progress.html">JProgressBarDemo from the Java Docs</a>
 */
public class ProgressBarHandler implements progressBar.ProgressBarHandler {
    /**
     * The {@code JPanel} containing the {@code JProgressBar}
     */
    private final JPanel panel;
    /**
     * The progressbar will be divided into {@link #numSubProcesses numSubProcesses} equal parts
     */
    private int numSubProcesses = 1;
    /**
     * The amount of activities a subprocess has (e.g. if there are 100 activities in a subprocess,
     * then each activity consists of 1% of the subprocess
     */
    private int[] numActivitiesInSubprocess = new int[numSubProcesses];
    /**
     * The description of the sub process (e.g. Writing file) and the state (e.g. busy or done)
     */
    private Object[] subProcessInfo = new Object[numSubProcesses];
    /**
     * Indicates the percentage the progress bar was already completed
     */
    private double progress = 0;

    private int currentSubProcess = 0;
    private int numActivitiesCompleted = 0;

    // Components
    private JProgressBar progressBar;
    private JTextArea jTextArea;

    private boolean clearJTextAreaAfterEverySubProcess = true;

    /**
     * Creates a handler that can handle a {@code JProgressBar} in a {@code JPanel}<br/>
     * <br/>
     * <h2>Usage</h2>
     * <h3>setup</h3>
     * Call the {@link #setNumSubProcesses(int) setNumbProcesses} method to set the amount of sub processes that need to
     * be completed. This will divide the progress bar in {@link #numSubProcesses numSubProcesses} equal parts.<br/>
     * After that, the sub-processes need to be initialised. Call the {@link #setNumActivitiesInSubProcesses(int, int) #setNumActivitiesInSubProcesses(subProcessID, numActivities)}
     * to set the amount of activities that need to be completed for sub-process {@code subProcessID} to be completed.
     * This is used to divide the part of the progressbar that the sub-process takes up into {@code numActivities} equal parts.<br/>
     * Sub-processes also need a name and state. The default state is {@link #SUBPROCESSINFO_BUSY busy}, but it can also
     * be set to {@link #SUBPROCESSINFO_DONE done}, which will place a "." after the sub-process instead of a "...".<br/><br/>
     * <h3>starting the progress bar</h3>
     * Before you can do anything with the progress bar, you have to call the {@link #startProgressBar() startProgressBar} method.
     * This method initiates variables and components to start the progress bar and (tries to) show the progress window.
     * It will also set the cursor to {@link #CURSOR_BUSY busy}. <br/>
     * To get the current {@link #progress progress percentage}, call the {@link #getProgress() getProgress} method. This can
     * be use to determine if the progressbar has finished externally.<br/>
     * To complete an activity, call the {@link #completeActivity(boolean) completeActivity(boolean autoCompleteSubProcess)}. If the method's
     * boolean value is set to true, it will automatically go to the next sub-process after completing all activities in the sub-process.<br/>
     * If you want to go to the next sub-process (e.g. not all activities had to be performed, or a sub-process had to be skipped),
     * call the {@link #completeSubProcess() completeSubProcess} method. This method is also called if the {@link #completeActivity(boolean) completeActivity(boolean autoCompleteSubProcess)}'s autoCompleteSubProcess value is true.
     * When the process has completely finished, call the {@link #completeProcess() completeProcess} method. This will reset the cursor and
     * manage other things that have to be done after the progress bar has hit 100% (or if the process has been cancelled).
     * @param panel The JPanel containing the {@code JProgressBar} and, optionally, the {@code JTextArea} that will
     *              display the {@link #subProcessInfo subProcessInfo}'s {@code subProcessName} object.
     */
    public ProgressBarHandler(JPanel panel) {
        this.panel = panel;

        // Components
        Component[] components = this.panel.getComponents();
        System.out.println("components = " + Arrays.toString(components));
        for (Component component : components) {
            if (component instanceof JProgressBar) {
                this.progressBar = (JProgressBar) component;
                System.out.println("progressBar: " + progressBar);
            } else if (component instanceof  JTextArea) {
                this.jTextArea = (JTextArea) component;
            } else if (component instanceof  JScrollPane) {
                Component[] jScrollPaneComponents = ((JScrollPane) component).getComponents();
                System.out.println("\n");
                System.out.println("jScrollPaneComponents = " + Arrays.toString(jScrollPaneComponents));
                for (Component jScrollPaneComponent : jScrollPaneComponents) {
                    if (jScrollPaneComponent instanceof JTextArea) {
                        System.out.println("JText Area!");
                        System.out.println(jScrollPaneComponent);
                        this.jTextArea = (JTextArea) jScrollPaneComponent;
                        System.out.println(this.jTextArea);
                    }
                }
            }
        }
    }

    /**
     * Sets the JPanel's parent visibility.
     * Make sure you already packed your JFrame before executing this method.
     * @deprecated Use .setVisible on the JFrame instead
     */
    @Deprecated
    public void showProgressWindow() {
        Container window = this.panel.getParent();
        window.setVisible(true);
    }

    /**
     * @deprecated Unstable
     */
    @Deprecated
    public void closeProgressWindow(boolean defaultWindowCloseEvent) {
        if (defaultWindowCloseEvent) {
            Container window = this.panel.getParent();
            window.dispatchEvent(new WindowEvent((Window) window, WindowEvent.WINDOW_CLOSING));
            if (window instanceof JFrame) {
                ((JFrame) window).dispose();
            }
        } else {
            closeProgressWindow();
        }
    }
    /**
     * @deprecated Unstable
     */
    @Deprecated
    public void closeProgressWindow() {
        try {
            JFrame window = (JFrame) this.panel.getParent();
            window.setVisible(false);
            window.dispose();
        } catch (ClassCastException ignored) {
            JLayeredPane window = (JLayeredPane) this.panel.getParent();
            window.setVisible(false);
        }
    }

    /**
     * Hides the window instead of closing it (assuming you are using hte JFrame) so the window can be reused
     * for another progress bar
     * @deprecated unstable
     */
    @Deprecated
    public void hideProgressWindow() {
        Container window = this.panel.getParent();
        window.setVisible(false);
    }

    public static final Cursor CURSOR_BUSY = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    public static final Object CURSOR_NORMAL = null;
    public void setCursor(Object cursor) {
        panel.setCursor((Cursor) cursor);
    }

    @Override
    public void setNumSubProcesses(int numSubProcesses) {
        this.numSubProcesses = numSubProcesses;
        this.numActivitiesInSubprocess = new int[this.numSubProcesses];
        this.subProcessInfo = new Object[this.numSubProcesses];
    }

    @Override
    public void setNumActivitiesInSubProcesses(int subProcessID, int numActivities) {
        this.numActivitiesInSubprocess[subProcessID] = numActivities;
    }

    // TODO: een null ook zodat zonder . of ...
    /**
     * Will add "..." after the sub-process' name
     */
    public static final int SUBPROCESSINFO_BUSY = 0;
    public static final int SUBPROCESSINFO_DONE = 1;
    /**
     * Sets the name and the state of the subprocess
     * @param subProcessID the id of the subprocess
     * @param subProcessName the subprocess' name (e.g. Writing file)
     * @param state the subprocess' state (e.g. busy (= 0) or done (= 1). Used to append "..." or "." to the
     *              subprocess' name
     */
    @Override
    public void setSubProcessInfo(int subProcessID, String subProcessName, int state) {
        this.subProcessInfo[subProcessID] = new Object[2];
        Object[] subProcessInfoObject = (Object[]) this.subProcessInfo[subProcessID];
        subProcessInfoObject[0] = subProcessName;
        subProcessInfoObject[1] = state;
    }
    /**
     * Sets the name and the state of the busy subprocess.
     * @param subProcessID the id of the subprocess
     * @param subProcessName the subprocess' name (e.g. Writing file)
     */
    @Override
    public void setSubProcessInfo(int subProcessID, String subProcessName) {
        this.subProcessInfo[subProcessID] = new Object[2];
        Object[] subProcessInfoObject = (Object[]) this.subProcessInfo[subProcessID];
        subProcessInfoObject[0] = subProcessName;
        subProcessInfoObject[1] = SUBPROCESSINFO_BUSY;
    }

    double percentagePerSubProcess;
    /**
     * Initiates variables and components to start the progress bar and (tries to) show the progress window.
     */
    @Override
    public void startProgressBar() {
        setCursor(CURSOR_BUSY); // Default for desktop
        progress = 0;
        progressBar.setValue((int) progress);

        // Initial
        Object[] processInfo = (Object[]) subProcessInfo[0];
        if (this.jTextArea != null)
            this.jTextArea.setText((String) processInfo[0] + (( (int) processInfo[1]) == 0 ? "..." : "."));
        currentSubProcess = 0;
        // Number of activities completed in the current subprocess
        numActivitiesCompleted = 0;
        try {
            showProgressWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Calculate what percentage each sub process takes of the progress bar
        // TODO: add a weights system to the sub processes
        percentagePerSubProcess = 100.0/numSubProcesses;
    }

    /**
     * Completes an activity of the current subprocess
     */
    @Override
    public void completeActivity(boolean autoCompleteSubProcess) {
        numActivitiesCompleted ++;

        // Update progress bar
        // The percentage of the enitre process the activity takes up
        double percentageOfActivity = percentagePerSubProcess / (numActivitiesInSubprocess[currentSubProcess]);
        updateProgressBar(percentageOfActivity);

        if (numActivitiesCompleted >= numActivitiesInSubprocess[currentSubProcess] && autoCompleteSubProcess) {
            completeSubProcess();
        }
    }

    /**
     * Completes multiple activities, this method cannot autocomplete a sub-process,
     * so the {@link #completeSubProcess() completeSubProcess} has to be called manually
     * (assuming it is not the last sub-process
     */
    public void completeActivities(int numActivitiesCompleted) {
        this.numActivitiesCompleted += numActivitiesCompleted;

        // Update progress bar
        double percentageOfActivity = (percentagePerSubProcess / (numActivitiesInSubprocess[currentSubProcess])) * numActivitiesCompleted;
        updateProgressBar(percentageOfActivity);
    }

    /**
     * Completes the current subprocess without completing all (or any) activities
     */
    @Override
    public void completeSubProcess() {
        currentSubProcess++;

        if (currentSubProcess != numSubProcesses) {
            numActivitiesCompleted = 0;

            Object[] processInfo = (Object[]) subProcessInfo[currentSubProcess];

            if (clearJTextAreaAfterEverySubProcess) {
                if (this.jTextArea != null)
                    this.jTextArea.setText((String) processInfo[0] + (((int) processInfo[1]) == 0 ? "..." : "."));
            } else {
                String newText = (String) processInfo[0] + (((int) processInfo[1]) == 0 ? "..." : ".");
                if (jTextArea != null)
                    jTextArea.append(newText);
            }

            // Update progress bar
            setProgressBarpercentage(percentagePerSubProcess * (currentSubProcess));
        } else {
            setProgressBarpercentage(100.0);
        }
    }

    /**
     * @return {@link #currentSubProcess}
     */
    public int getCurrentSubProcess() {
        return currentSubProcess;
    }

    @Override
    public void updateProgressBar(double addPercentage) {
        progress += addPercentage;
        progressBar.setValue((int) progress);
    }

    @Override
    public void setProgressBarpercentage(double percentage) {
        progress = percentage;
        progressBar.setValue((int) progress);
    }

    @Override
    public double getProgress() {
        return progress;
    }

    /**
     * Call this method after the whole process has been completed
     */
    @Override
    public void completeProcess() {
        setCursor(CURSOR_NORMAL);
        //closeProgressWindow();
        // TODO: other things in here
        // Like closing the progress window, let the user specify a delay
    }

    @Override
    public void setMessage(String message) {
        if (this.clearJTextAreaAfterEverySubProcess)
            jTextArea.setText(message);
        else
            jTextArea.append(message);
    }
}
