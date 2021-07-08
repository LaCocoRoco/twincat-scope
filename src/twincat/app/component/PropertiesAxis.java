package twincat.app.component;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import twincat.Resources;
import twincat.app.container.LoremIpsum;

public class PropertiesAxis extends JPanel {
   private static final long serialVersionUID = 1L;
    
    /*************************/
    /****** constructor ******/
    /*************************/
     
    public PropertiesAxis() {
        JScrollPane propertiesPanel = new JScrollPane();
        propertiesPanel.getVerticalScrollBar().setPreferredSize(new Dimension(Resources.DEFAULT_SCROLLBAR_WIDTH, 0));
        propertiesPanel.setBorder(BorderFactory.createEmptyBorder());
        propertiesPanel.setViewportView(new LoremIpsum());

        this.setLayout(new BorderLayout());
        this.add(propertiesPanel, BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder()); 
    }
}