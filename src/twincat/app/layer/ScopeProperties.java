package twincat.app.layer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import twincat.Resources;
import twincat.app.components.ScrollablePanel;
import twincat.app.components.TextField;
import twincat.app.components.TimeTextField;
import twincat.app.components.TitledPanel;
import twincat.app.constant.Propertie;
import twincat.scope.Scope;

public class ScopeProperties extends JPanel {
    private static final long serialVersionUID = 1L;

    /*********************************/
    /******** cross reference ********/
    /*********************************/

    private final XReference xref;

    /*********************************/
    /******** global variable ********/
    /*********************************/

    private Scope scope = new Scope();

    /*********************************/
    /****** local final variable *****/
    /*********************************/
    
    private final JScrollPane scrollPanel = new JScrollPane();
    
    private final TextField scopeName = new TextField();

    private final TimeTextField recordTimeTextField = new TimeTextField();

    private final ResourceBundle languageBundle = ResourceBundle.getBundle(Resources.PATH_LANGUAGE);

    /*********************************/
    /****** predefined variable ******/
    /*********************************/

    private PropertyChangeListener recordTimePropertyChanged = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            TimeTextField timeTextField = (TimeTextField) propertyChangeEvent.getSource();
            scope.setRecordTime(timeTextField.getText());
        }
    };

    private PropertyChangeListener scopeNamePropertyChanged = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            scope.setScopeName(scopeName.getText());
            xref.scopeBrowser.reloadSelectedTreeNode();
        }
    };

    private AbstractAction scrollPanelDisableKey = new AbstractAction() {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            /* empty */
        }
    };

    /*********************************/
    /********** constructor **********/
    /*********************************/

    public ScopeProperties(XReference xref) {
        this.xref = xref;

        // common properties
        scopeName.setText(scope.getScopeName());
        scopeName.addPropertyChangeListener("text", scopeNamePropertyChanged);
        scopeName.setBounds(15, 25, 210, 23);

        TitledPanel namePanel = new TitledPanel(languageBundle.getString(Resources.TEXT_SCOPE_PROPERTIES_NAME));
        namePanel.setPreferredSize(new Dimension(PropertiesPanel.TEMPLATE_WIDTH_SMALL, 60));
        namePanel.add(scopeName);
        
        JPanel namePanelContainer = new JPanel();
        namePanelContainer.setLayout(new FlowLayout(FlowLayout.LEADING));
        namePanelContainer.add(namePanel); 
        
        // record mode properties
        recordTimeTextField.setText(Scope.TIME_FORMAT_MIN_TIME);
        recordTimeTextField.addPropertyChangeListener("time", recordTimePropertyChanged);
        recordTimeTextField.setBounds(15, 25, 100, 23);

        JLabel recordTimeText = new JLabel("[" + Scope.TIME_FORMAT_TEMPLATE + "]");
        recordTimeText.setFont(new Font(Resources.DEFAULT_FONT, Font.BOLD, Resources.DEFAULT_FONT_SIZE_SMALL));
        recordTimeText.setBounds(125, 24, 120, 23);

        TitledPanel recordTimePanel = new TitledPanel(languageBundle.getString(Resources.TEXT_SCOPE_PROPERTIES_RECORD_TIME));
        recordTimePanel.setPreferredSize(new Dimension(PropertiesPanel.TEMPLATE_WIDTH_SMALL, 60));
        recordTimePanel.add(recordTimeTextField);
        recordTimePanel.add(recordTimeText);
        
        JPanel recordTimePanelContainer = new JPanel();
        recordTimePanelContainer.setLayout(new FlowLayout(FlowLayout.LEADING));
        recordTimePanelContainer.add(recordTimePanel); 
        
        // default content
        ScrollablePanel contentPanel = new ScrollablePanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPanel.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        contentPanel.add(namePanelContainer);
        contentPanel.add(recordTimePanelContainer);

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

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    /*********************************/
    /********* public method *********/
    /*********************************/

    public void reloadScope() {
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
        scopeName.setText(scope.getScopeName());
        scopeName.setCaretPosition(0);

        // reload record mode properties
        recordTimeTextField.setText(scope.getRecordTime());

        // display scope properties
        scrollPanel.getVerticalScrollBar().setValue(0);
        xref.propertiesPanel.setCard(Propertie.SCOPE);
    }
}
