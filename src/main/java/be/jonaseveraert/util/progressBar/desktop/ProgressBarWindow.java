package be.jonaseveraert.util.progressBar.desktop;

import javax.swing.*;
import java.awt.*;

// TODO: add option to add the percentage to the window

/**
 * A {@code JFrame} containing a {@link JProgressBar} and a {@link JTextArea}.
 * @author Jonas Everaert
 * @author <a href="https://jonaseveraert.be">jonaseveraert.be</a>
 * @version %I%
 * @since 1.0
 *
 * @see ProgressBarHandler
 */
public class ProgressBarWindow extends JFrame {
    // Components
    private final JProgressBar progressBar;
    private JTextArea textArea;
    private final JPanel jPanel;

    /**
     * Constructs a JFrame containing a {@code JProgressBar} and a {@code JTextArea} in a {@code BorderLayout}.
     * The {@code JTextArea} is used to display the current sub-process.
     * <p>To control the progressbar, pass the {@link #getjPanel() jPanel} into a {@link ProgressBarHandler}. See the
     * {@link ProgressBarHandler}'s constructor javadoc for implementation.</p>
     *
     * @see ProgressBarHandler
     */
    public ProgressBarWindow() {
        jPanel = new JPanel();
        progressBar = new JProgressBar();
        textArea = new JTextArea();

        textArea.setEditable(false);
        textArea.setPreferredSize(new Dimension(150, 50));

        progressBar.setBorderPainted(true);

        // set vertical layout
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.PAGE_AXIS));

        jPanel.add(progressBar);
        jPanel.add(textArea);

        textArea.setBackground(new Color(0, 0, 0, 0));

        this.setContentPane(jPanel);
        this.setUndecorated(true);
        this.setBackground(new Color(0, 0, 0, 0));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    /**
     * Use this to pass the panel in a {@link ProgressBarHandler}.
     *
     * @return JPanel containing JProgressBar and JextArea
     */
    public JPanel getjPanel() {
        return jPanel;
    }

    /**
     * Can be used to perform actions on the progressbar
     *
     * @return be.jonaseveraert.util.progressBar
     */
    public JProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * Can be used to perform actions on the text area, like setting it invisble when you don't need it.
     *
     * @return textArea
     */
    public JTextArea jTextArea() {
        return textArea;
    }
}