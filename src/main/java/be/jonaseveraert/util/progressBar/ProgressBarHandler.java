package be.jonaseveraert.util.progressBar;

/**
 * An interface for the ProgressBarHandler class. This is used so that ProgressBarHandlers can be used in any other
 * class while the ProgressBarHandler itself can be implemented to fit the needs of the platform the code is run on.
 *
 * @author Jonas Everaert
 * @author <a href="https://jonaseveraert.be">jonaseveraert.be</a>
 * @version %I%
 * @since 1.0
 *
 * @see be.jonaseveraert.util.progressBar.desktop.ProgressBarHandler
 */
public interface ProgressBarHandler {
    // Progressbar info
    /**
     * The progressbar will be divided into {@link #numSubProcesses numSubProcesses} equal parts
     */
    int numSubProcesses = 1;

    /**
     * The amount of activities a subprocess has (e.g. if there are 100 activities in a subprocess,
     * then each activity consists of 1% of the subprocess
     */
    int[] numActivitiesInSubprocess = new int[numSubProcesses];

    /**
     * The description of the sub process (e.g. Writing file) and the state (e.g. busy or done)
     */
    Object[] subProcessInfo = new Object[numSubProcesses];

    // Tracking progress
    /**
     * Indicates the percentage the progress bar was already completed
     */
    double progress = 0;

    int currentSubProcess = 0;
    /**
     * The number of activities completed for the {@link #currentSubProcess current sub-process}
     */
    int numActivitiesCompleted = 0;

    /**
     * Sets the amount of sub-processes the process has. e.g. if there are 2 sub-processes, then each of them
     * will take up 50% of the progressbar.
     * @param numSubProcesses The number of sub-processes the process has.
     */
    void setNumSubProcesses(int numSubProcesses);

    /**
     * Sets the {@code numActivities} a sub-process with id {@code subProcessID} has.
     * If there are 2 activities in a sub-process, then each activity will take up 50% of the sub-process' part
     * of the progressbar
     * @param subProcessID The id of the sub-process (in order from when you added the sub-processes, starting at 0)
     * @param numActivities number of activities the sub-process {@code subProcessID} has
     */
    void setNumActivitiesInSubProcesses(int subProcessID, int numActivities);

    public static final int SUBPROCESSINFO_BUSY = 0;
    public static final int SUBPROCESSINFO_DONE = 1;

    /**
     * Sets the name and the state of the subprocess
     * @param subProcessID the id of the subprocess
     * @param subProcessName the subprocess' name (e.g. Writing file)
     * @param state the subprocess' state (e.g. busy (= 0) or done (= 1). Used to append "..." or "." to the
     *              subprocess' name
     */
    void setSubProcessInfo(int subProcessID, String subProcessName, int state);

    /**
     * Sets the name and the state of the busy subprocess.
     * @param subProcessID the id of the subprocess
     * @param subProcessName the subprocess' name (e.g. Writing file)
     */
    void setSubProcessInfo(int subProcessID, String subProcessName);

    /**
     * Initiates variables and components to start the progress bar and (tries to) show the progress window.
     * Implementation note: This method is used to do things like set the cursor to busy, but most importantly, to calculate
     *           what part of the progressbar each sub-process and each activity takes up.
     */
    void startProgressBar();

    /**
     * Completes an activity of the current subprocess
     * @param autoCompleteSubProcess If true, then {@link #completeSubProcess completeSubProcess} will automatically be
     *                               called when all activities of a sub-process have completed.
     */
    void completeActivity(boolean autoCompleteSubProcess);

    /**
     * Completes multiple activities, this method cannot autocomplete a sub-process,
     * so the {@link #completeSubProcess() completeSubProcess} has to be called manually
     * (assuming it is not the last sub-process
     * @param numActivitiesCompleted the number of activities the process has to complete
     */
    void completeActivities(int numActivitiesCompleted);

    /**
     * Completes the current subprocess without completing all (or any) activities
     */
    void completeSubProcess();

    /**
     * Adds a percentage to the {@link #progress progress} variable and updates the progressbar that
     * was given in the constructor.
     * @param addPercentage the percentage that needs to be added to the progress bar
     */
    void updateProgressBar(double addPercentage);

    /**
     * Sets the {@link #progress progress} variable and the progressbar to a set percentage.
     * @param percentage the percentage the progressbar will be set to
     */
    void setProgressBarpercentage(double percentage);

    /**
     * @return {@link #progress}
     */
    double getProgress();

    /**
     * This method is called when the process has finished. The implementation of this is different for
     * every platform.<p>
     * i.e. for desktop, the cursor is set back to normal.
     */
    void completeProcess();

    /**
     * Sets a message for the loading screen. Can be used to tell the user that the process has finished.
     * @param message the message that will be desplayed in the text field
     */
    void setMessage(String message);
}
