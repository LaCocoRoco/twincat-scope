package twincat.app.layer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.BadLocationException;

import twincat.Resources;
import twincat.TwincatLogger;
import twincat.ads.worker.CommandLine;

public class ConsolePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    /*********************************/
    /**** local constant variable ****/
    /*********************************/

    private static final int MAX_LINE_COUNT = 2000;

    /*********************************/
    /******** global variable ********/
    /*********************************/
    
    private String clipboard = new String();

    /*********************************/
    /***** global final variable *****/
    /*********************************/

    private final CommandLine commandLine = new CommandLine();

    private final Logger logger = TwincatLogger.getLogger();
    
    /*********************************/
    /********** constructor **********/
    /*********************************/

    public ConsolePanel(XReference xref) {
        JTextArea textArea = new JTextArea();
        textArea.setMargin(new Insets(5, 5, 5, 5));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(false);
        textArea.setEditable(false);
        textArea.setFont(new Font(Resources.DEFAULT_FONT, Font.PLAIN, Resources.DEFAULT_FONT_SIZE_SMALL));

        StreamHandler consoleHandler = new StreamHandler() {
            @Override
            public synchronized void publish(LogRecord record) {
                try {
                    if (textArea.getLineCount() > MAX_LINE_COUNT) {
                        int lineEndOffset = textArea.getLineEndOffset(0);
                        textArea.replaceRange(new String(), 0, lineEndOffset);
                    }
                } catch (BadLocationException e) {}

                String message = getFormatter().format(record);
                textArea.append(message);
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        };

        consoleHandler.setFormatter(TwincatLogger.getFormatter());

        logger.addHandler(consoleHandler);

        JScrollPane consolePanel = new JScrollPane(textArea);
        consolePanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        consolePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        consolePanel.setViewportView(textArea);

        JTextField consoleInput = new JTextField();
        consoleInput.setMargin(new Insets(2, 2, 2, 2));
        consoleInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        consoleInput.setAlignmentY(Component.CENTER_ALIGNMENT);
        consoleInput.setFont(new Font(Resources.DEFAULT_FONT, Font.BOLD, Resources.DEFAULT_FONT_SIZE_SMALL));
        consoleInput.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String input = consoleInput.getText();
                    consoleInput.setText(new String());

                    if (!input.isEmpty()) {
                        clipboard = input;
                        commandLine.send(input);
                    }
                }

                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    consoleInput.setText(clipboard);
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        consoleInput.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                consoleInput.requestFocus();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {}

            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });

        this.setLayout(new BorderLayout());
        this.add(consoleInput, BorderLayout.PAGE_START);
        this.add(consolePanel, BorderLayout.CENTER);
    }

    /*********************************/
    /******** setter & getter ********/
    /*********************************/

    public String getClipboard() {
        return clipboard;
    }

    public void setClipboard(String clipboard) {
        this.clipboard = clipboard;
    }
 
    public CommandLine getCommandLine() {
        return commandLine;
    }
}
