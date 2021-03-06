package twincat.app.layer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import twincat.Resources;
import twincat.app.components.ComboBox;
import twincat.app.components.NumberTextField;
import twincat.app.components.TextField;
import twincat.app.components.TitledPanel;
import twincat.app.constant.Propertie;
import twincat.scope.TriggerChannel;
import twincat.scope.TriggerChannel.Combine;
import twincat.scope.TriggerChannel.Release;

public class TriggerChannelProperties extends JPanel {
    private static final long serialVersionUID = 1L;

    /*********************************/
    /******** cross reference ********/
    /*********************************/

    private final XReference xref;

    /*********************************/
    /******** global variable ********/
    /*********************************/

    private TriggerChannel triggerChannel = new TriggerChannel();

    /*********************************/
    /****** local final variable *****/
    /*********************************/

    private final JScrollPane scrollPanel = new JScrollPane();
    
    private final TextField triggerChannelName = new TextField();

    private final ComboBox combine = new ComboBox();

    private final ComboBox release = new ComboBox();

    private final NumberTextField threshold = new NumberTextField();

    private final ResourceBundle languageBundle = ResourceBundle.getBundle(Resources.PATH_LANGUAGE);

    /*********************************/
    /****** predefined variable ******/
    /*********************************/

    private PropertyChangeListener triggerChannelNamePropertyChangeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            triggerChannel.getChannel().setChannelName(triggerChannelName.getText());
            xref.scopeBrowser.reloadSelectedTreeNode();
        }
    };

    private final ItemListener combineItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                if (combine.getSelectedIndex() == 0) {
                    triggerChannel.setCombine(Combine.AND);
                } else {
                    triggerChannel.setCombine(Combine.OR);
                }
            }
        }
    };

    private final ItemListener releaseItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                if (release.getSelectedIndex() == 0) {
                    triggerChannel.setRelease(Release.RISING_EDGE);
                } else {
                    triggerChannel.setRelease(Release.FALLING_EDGE);
                }
            }
        }
    };

    private PropertyChangeListener thresholdPropertyChangeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals("number")) {
                NumberTextField numberTextField = (NumberTextField) propertyChangeEvent.getSource();
                triggerChannel.setThreshold((int) numberTextField.getValue());            
            }
        }
    };

    private AbstractAction scrollPanelDisableKey = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            /* empty */
        }
    };

    /*********************************/
    /********** constructor **********/
    /*********************************/

    public TriggerChannelProperties(XReference xref) {
        this.xref = xref;
        
        // build combo box
        buildComboBox();

        // name properties
        triggerChannelName.setText(triggerChannel.getChannel().getChannelName());
        triggerChannelName.addPropertyChangeListener(triggerChannelNamePropertyChangeListener);
        triggerChannelName.setBounds(15, 30, 210, 23);

        TitledPanel namePanel = new TitledPanel(languageBundle.getString(Resources.TEXT_TRIGGER_CHANNEL_PROPERTIES_NAME));
        namePanel.setMaximumSize(new Dimension(PropertiesPanel.TEMPLATE_WIDTH_SMALL, 70));
        namePanel.add(triggerChannelName);

        // trigger properties
        JLabel combineLabel = new JLabel(languageBundle.getString(Resources.TEXT_TRIGGER_CHANNEL_PROPERTIES_COMBINE));
        combineLabel.setFont(new Font(Resources.DEFAULT_FONT, Font.PLAIN, Resources.DEFAULT_FONT_SIZE_SMALL));
        combineLabel.setBounds(20, 25, 205, 20);

        combine.setFont(new Font(Resources.DEFAULT_FONT, Font.BOLD, Resources.DEFAULT_FONT_SIZE_SMALL));
        combine.setBounds(18, 45, 205, 22);

        JLabel releaseLabel = new JLabel(languageBundle.getString(Resources.TEXT_TRIGGER_CHANNEL_PROPERTIES_RELEASE));
        releaseLabel.setFont(new Font(Resources.DEFAULT_FONT, Font.PLAIN, Resources.DEFAULT_FONT_SIZE_SMALL));
        releaseLabel.setBounds(20, 75, 205, 20);

        release.setFont(new Font(Resources.DEFAULT_FONT, Font.BOLD, Resources.DEFAULT_FONT_SIZE_SMALL));
        release.setBounds(18, 95, 205, 22);

        threshold.setValue((int) triggerChannel.getThreshold());
        threshold.setHorizontalAlignment(JTextField.LEFT);
        threshold.addPropertyChangeListener(thresholdPropertyChangeListener);
        threshold.setBounds(20, 135, 120, 23);

        JLabel thresholdText = new JLabel(languageBundle.getString(Resources.TEXT_TRIGGER_CHANNEL_PROPERTIES_THRESHOLD));
        thresholdText.setFont(new Font(Resources.DEFAULT_FONT, Font.BOLD, Resources.DEFAULT_FONT_SIZE_SMALL));
        thresholdText.setBounds(150, 135, 110, 23);

        TitledPanel triggerPanel = new TitledPanel(languageBundle.getString(Resources.TEXT_TRIGGER_CHANNEL_PROPERTIES_TRIGGER));
        triggerPanel.setMaximumSize(new Dimension(PropertiesPanel.TEMPLATE_WIDTH_SMALL, 180));
        triggerPanel.add(combine);
        triggerPanel.add(combineLabel);
        triggerPanel.add(release);
        triggerPanel.add(releaseLabel);
        triggerPanel.add(threshold);
        triggerPanel.add(thresholdText);

        // default content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPanel.setPreferredSize(new Dimension(PropertiesPanel.TEMPLATE_WIDTH_SMALL + 20, 250));
        contentPanel.add(namePanel);
        contentPanel.add(triggerPanel);

        scrollPanel.getVerticalScrollBar().setPreferredSize(new Dimension(Resources.DEFAULT_SCROLLBAR_WIDTH, 0));
        scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPanel.setBorder(BorderFactory.createEmptyBorder());
        scrollPanel.setViewportView(contentPanel);
        scrollPanel.getActionMap().put("unitScrollUp", scrollPanelDisableKey);
        scrollPanel.getActionMap().put("unitScrollDown", scrollPanelDisableKey);

        this.setBorder(BorderFactory.createEmptyBorder());
        this.setLayout(new BorderLayout());
        this.add(scrollPanel, BorderLayout.CENTER);
    }

    /*********************************/
    /******** setter & getter ********/
    /*********************************/

    public TriggerChannel getTriggerChannel() {
        return triggerChannel;
    }

    /*********************************/
    /********* public method *********/
    /*********************************/

    public void setTriggerChannel(TriggerChannel triggerChannel) {
        this.triggerChannel = triggerChannel;
    }

    public void load() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                reload();
            }
        });
    }

    /*********************************/
    /******** private method *********/
    /*********************************/

    private void reload() {
        // reload common properties
        triggerChannelName.removePropertyChangeListener(triggerChannelNamePropertyChangeListener);
        triggerChannelName.setText(triggerChannel.getChannel().getChannelName());
        triggerChannelName.setCaretPosition(0);
        triggerChannelName.addPropertyChangeListener(triggerChannelNamePropertyChangeListener);
        
        // reload trigger properties
        if (triggerChannel.getCombine() == Combine.AND) {
            combine.setSelectedIndex(0);
        } else {
            combine.setSelectedIndex(1);
        }
        
        if (triggerChannel.getRelease() == Release.RISING_EDGE) {
            release.setSelectedIndex(0);
        } else {
            release.setSelectedIndex(1);
        }

        threshold.setValue((long) triggerChannel.getThreshold());

        // reload trigger channel properties
        scrollPanel.getVerticalScrollBar().setValue(0);
        xref.propertiesPanel.setCard(Propertie.TRIGGER_CHANNEL);
    }

    private void buildComboBox() {
        combine.addItem(TriggerChannel.Combine.AND.toString());
        combine.addItem(TriggerChannel.Combine.OR.toString());
        combine.addItemListener(combineItemListener);

        release.addItem(TriggerChannel.Release.RISING_EDGE.toString().replace("_", " "));
        release.addItem(TriggerChannel.Release.FALLING_EDGE.toString().replace("_", " "));
        release.addItemListener(releaseItemListener);
    }

}
