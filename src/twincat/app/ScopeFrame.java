package twincat.app;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicMenuBarUI;

import twincat.Resources;
import twincat.TwincatLogger;
import twincat.app.constant.Browser;
import twincat.app.constant.Navigation;
import twincat.app.constant.Propertie;
import twincat.app.constant.Window;
import twincat.app.layer.XReference;
import twincat.constant.DefaultColorTable;

public class ScopeFrame extends JPanel {
    private static final long serialVersionUID = 1L;

    /*********************************/
    /******** cross reference ********/
    /*********************************/

    private final XReference xref = new XReference();

    /*********************************/
    /****** local final variable *****/
    /*********************************/

    private final ResourceBundle languageBundle = ResourceBundle.getBundle(Resources.PATH_LANGUAGE);

    private final JMenuBar scopeMenu = new JMenuBar();

    private final JMenuItem menuItemNewFile = new JMenuItem();

    private final JMenuItem menuItemFileOpen = new JMenuItem();

    private final JMenuItem menuItemConsole = new JMenuItem();

    private final JMenuItem menuItemSettings = new JMenuItem();

    private final JMenuItem menuItemWindowScope = new JMenuItem();

    private final JMenuItem menuItemWindowAds = new JMenuItem();

    private final JMenuItem menuItemWindowAxis = new JMenuItem();

    private final Logger logger = TwincatLogger.getLogger();

    /*********************************/
    /****** predefined variable ******/
    /*********************************/

    private final BasicMenuBarUI scopeMenuBarUI = new BasicMenuBarUI() {
        @Override
        public void paint(Graphics graphics, JComponent jComponent) {
            graphics.setColor(DefaultColorTable.TRANSLUCENT.color);
            graphics.fillRect(0, 0, jComponent.getWidth(), jComponent.getHeight());
        }
    };

    private final ActionListener windowScopeActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            xref.windowPanel.setCard(Window.SCOPE);
            xref.navigationPanel.setCard(Navigation.CHART);
        }
    };
    
    private final ActionListener windowAdsActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            xref.windowPanel.setCard(Window.ADS);
        }
    };    
    
    private final ActionListener windowAxisActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            xref.windowPanel.setCard(Window.AXIS);
        }
    };     
    
    private final ActionListener settingsActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            xref.windowPanel.setCard(Window.SETTINGS);
        }
    };      
    
    private final ActionListener consoleActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            xref.contentPanel.consoleToggle();
        }
    };     
    
    private final ActionListener newFileActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            xref.scopeBrowser.clearScope();
            xref.propertiesPanel.setCard(Propertie.EMPTY);
            xref.browserPanel.setCard(Browser.SCOPE);
        }
    }; 
    
    private final ActionListener openFileActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            xref.navigationPanel.setCard(Navigation.LOADER);
            xref.windowPanel.setCard(Window.SCOPE);
        }
    }; 
      
    /*********************************/
    /********** constructor **********/
    /*********************************/

    public ScopeFrame() {
        menuItemWindowScope.setText(languageBundle.getString(Resources.TEXT_WINDOW_SCOPE));
        menuItemWindowScope.addActionListener(windowScopeActionListener);
        
        menuItemWindowAds.setText(languageBundle.getString(Resources.TEXT_WINDOW_ADS));
        menuItemWindowAds.addActionListener(windowAdsActionListener);

        menuItemWindowAxis.setText(languageBundle.getString(Resources.TEXT_WINDOW_AXIS));
        menuItemWindowAxis.addActionListener(windowAxisActionListener);

        menuItemSettings.setText(languageBundle.getString(Resources.TEXT_EXTRAS_SETTINGS));
        menuItemSettings.addActionListener(settingsActionListener);

        menuItemConsole.setText(languageBundle.getString(Resources.TEXT_EXTRAS_CONSOLE));
        menuItemConsole.addActionListener(consoleActionListener);

        menuItemNewFile.setText(languageBundle.getString(Resources.TEXT_FILE_NEW));
        menuItemNewFile.addActionListener(newFileActionListener);

        menuItemFileOpen.setText(languageBundle.getString(Resources.TEXT_FILE_OPEN));
        menuItemFileOpen.addActionListener(openFileActionListener);

        JMenu menuFile = new JMenu(languageBundle.getString(Resources.TEXT_FILE));
        menuFile.add(menuItemNewFile);
        menuFile.add(menuItemFileOpen);

        JMenu menuWindow = new JMenu(languageBundle.getString(Resources.TEXT_WINDOW));
        menuWindow.add(menuItemWindowScope);
        menuWindow.add(menuItemWindowAxis);

        JMenu menuExtras = new JMenu(languageBundle.getString(Resources.TEXT_EXTRAS));
        menuExtras.add(menuItemSettings);
        menuExtras.add(menuItemConsole);
        menuExtras.add(menuItemWindowAds);

        scopeMenu.setOpaque(true);
        scopeMenu.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scopeMenu.setUI(scopeMenuBarUI);
        scopeMenu.add(menuFile);
        scopeMenu.add(menuWindow);
        scopeMenu.add(menuExtras);
        
        xref.contentPanel.consoleHide();

        this.setLayout(new BorderLayout());
        this.add(scopeMenu, BorderLayout.PAGE_START);
        this.add(xref.contentPanel, BorderLayout.CENTER);
    }

    /*********************************/
    /********* public method *********/
    /*********************************/

    public Level getLoggerLevel() {
        return logger.getLevel();
    }

    public void setLoggerLevel(Level level) {
        logger.setLevel(level);
    }

    public void setMenuVisible(boolean flag) {
        scopeMenu.setVisible(flag);
    }

    public void setConsoleVisible(boolean flag) {
        if (flag) {
            xref.contentPanel.consoleShow();
        } else {
            xref.contentPanel.consoleHide();
        }
    }

    public void setWindow(Window card) {
        xref.windowPanel.setCard(card);
    }

    public void preBuildSymbolBrowser() {
        xref.symbolBrowser.build();
    }

    public void minifyMenuItems() {
        menuItemNewFile.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        menuItemNewFile.setHorizontalAlignment(JMenuItem.LEFT);
        menuItemNewFile.setHorizontalTextPosition(JMenuItem.LEFT);
        menuItemNewFile.setIcon(null);
        menuItemNewFile.setIconTextGap(0);
        menuItemNewFile.setMargin(new Insets(0, 0, 0, 0));
        menuItemNewFile.setPressedIcon(null);

        menuItemFileOpen.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        menuItemFileOpen.setHorizontalAlignment(JMenuItem.LEFT);
        menuItemFileOpen.setHorizontalTextPosition(JMenuItem.LEFT);
        menuItemFileOpen.setIcon(null);
        menuItemFileOpen.setIconTextGap(0);
        menuItemFileOpen.setMargin(new Insets(0, 0, 0, 0));
        menuItemFileOpen.setPressedIcon(null);

        menuItemConsole.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        menuItemConsole.setHorizontalAlignment(JMenuItem.LEFT);
        menuItemConsole.setHorizontalTextPosition(JMenuItem.LEFT);
        menuItemConsole.setIcon(null);
        menuItemConsole.setIconTextGap(0);
        menuItemConsole.setMargin(new Insets(0, 0, 0, 0));
        menuItemConsole.setPressedIcon(null);

        menuItemSettings.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        menuItemSettings.setHorizontalAlignment(JMenuItem.LEFT);
        menuItemSettings.setHorizontalTextPosition(JMenuItem.LEFT);
        menuItemSettings.setIcon(null);
        menuItemSettings.setIconTextGap(0);
        menuItemSettings.setMargin(new Insets(0, 0, 0, 0));
        menuItemSettings.setPressedIcon(null);

        menuItemWindowScope.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        menuItemWindowScope.setHorizontalAlignment(JMenuItem.LEFT);
        menuItemWindowScope.setHorizontalTextPosition(JMenuItem.LEFT);
        menuItemWindowScope.setIcon(null);
        menuItemWindowScope.setIconTextGap(0);
        menuItemWindowScope.setMargin(new Insets(0, 0, 0, 0));
        menuItemWindowScope.setPressedIcon(null);

        menuItemWindowAds.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        menuItemWindowAds.setHorizontalAlignment(JMenuItem.LEFT);
        menuItemWindowAds.setHorizontalTextPosition(JMenuItem.LEFT);
        menuItemWindowAds.setIcon(null);
        menuItemWindowAds.setIconTextGap(0);
        menuItemWindowAds.setMargin(new Insets(0, 0, 0, 0));
        menuItemWindowAds.setPressedIcon(null);

        menuItemWindowAxis.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        menuItemWindowAxis.setHorizontalAlignment(JMenuItem.LEFT);
        menuItemWindowAxis.setHorizontalTextPosition(JMenuItem.LEFT);
        menuItemWindowAxis.setIcon(null);
        menuItemWindowAxis.setIconTextGap(0);
        menuItemWindowAxis.setMargin(new Insets(0, 0, 0, 0));
        menuItemWindowAxis.setPressedIcon(null);
    }
}
