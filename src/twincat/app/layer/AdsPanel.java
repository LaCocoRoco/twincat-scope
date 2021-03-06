package twincat.app.layer;

import java.awt.Font;
import java.awt.Insets;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import twincat.Resources;
import twincat.Utilities;

public class AdsPanel extends JScrollPane {
    private static final long serialVersionUID = 1L;

    /*********************************/
    /********** constructor **********/
    /*********************************/

    public AdsPanel(XReference xref) {
        String instructionText = Utilities.getStringFromFilePath(Resources.PATH_TEXT_ADS_INFO);

        JTextArea instructionTextArea = new JTextArea(instructionText);
        instructionTextArea.setCaretPosition(0);
        instructionTextArea.setMargin(new Insets(5, 5, 5, 5));
        instructionTextArea.setLineWrap(true);
        instructionTextArea.setWrapStyleWord(false);
        instructionTextArea.setEditable(false);
        instructionTextArea.setFont(new Font(Resources.DEFAULT_FONT_MONO, Font.PLAIN, Resources.DEFAULT_FONT_SIZE_SMALL));

        this.setViewportView(instructionTextArea);
    }
}
