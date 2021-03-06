package twincat.app.layer;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

public class ContentPanel extends JSplitPane {
    private static final long serialVersionUID = 1L;

    /*********************************/
    /**** local constant variable ****/
    /*********************************/

    private static final int DIVIDER_SIZE = 5;

    private static final double DIVIDER_LOCATION = 0.8;

    /*********************************/
    /********** constructor **********/
    /*********************************/

    public ContentPanel(XReference xref) {
        this.setLeftComponent(xref.windowPanel);
        this.setRightComponent(xref.consolePanel);
        this.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.setContinuousLayout(true);
        this.setOneTouchExpandable(false);
        this.setBorder(new EmptyBorder(3, 3, 3, 3));
    }

    /*********************************/
    /********* public method *********/
    /*********************************/

    public void consoleToggle() {
        if (this.getDividerSize() != 0) {
            consoleHide();
        } else {
            consoleShow();
        }
    }

    public void consoleShow() {
        setDividerLocation(DIVIDER_LOCATION);
        getRightComponent().setVisible(true);
        getLeftComponent().setVisible(true);
        setDividerSize(DIVIDER_SIZE);
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                setDividerLocation(DIVIDER_LOCATION);
            }
        });
    }

    public void consoleHide() {
        getRightComponent().setVisible(false);
        getLeftComponent().setVisible(true);
        setDividerSize(0);
    }
}
