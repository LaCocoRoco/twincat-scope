package twincat.app.layer;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import twincat.Resources;
import twincat.TwincatLogger;
import twincat.Utilities;
import twincat.ads.common.RouteSymbolData;
import twincat.ads.common.Symbol;
import twincat.ads.constant.AmsPort;
import twincat.ads.constant.DataType;
import twincat.ads.worker.RouteLoader;
import twincat.ads.worker.SymbolLoader;
import twincat.app.common.SymbolNode;
import twincat.app.common.SymbolTreeModel;
import twincat.app.common.SymbolTreeNode;
import twincat.app.common.SymbolTreeRenderer;
import twincat.app.components.ComboBox;
import twincat.app.constant.Browser;
import twincat.app.constant.Filter;

public class SymbolBrowser extends JPanel {
    private static final long serialVersionUID = 1L;

    /*********************************/
    /******** cross reference ********/    
    /*********************************/

    private final XReference xref;

    /*********************************/
    /**** local constant variable ****/
    /*********************************/

    private static enum View {
        LOADING, TREE
    };

    /*********************************/
    /****** local final variable *****/
    /*********************************/

    private final JLabel loadingState = new JLabel();

    private final JScrollPane scrollPanel = new JScrollPane();
    
    private final JTree searchTree = new JTree();

    private final JTree browseTree = new JTree();
    
    private final JPanel viewPanel = new JPanel();

    private final JTextField searchTextField = new JTextField();

    private final ComboBox routeComboBox = new ComboBox();

    private final ComboBox portComboBox = new ComboBox();  

    private final List<SymbolNode> searchSymbolNodeList = new ArrayList<SymbolNode>();

    private final ResourceBundle languageBundle = ResourceBundle.getBundle(Resources.PATH_LANGUAGE);

    private final RouteLoader routeLoader = new RouteLoader();

    private final Logger logger = TwincatLogger.getLogger();

    /*********************************/
    /****** predefined variable ******/
    /*********************************/

    private final MouseAdapter browseTreeMouseAdapter = new MouseAdapter() {
        public void mousePressed(MouseEvent mouseEvent) {
            if (mouseEvent.getClickCount() == 1) {
                TreePath treePath = browseTree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
                
                if (treePath != null) {
                    treeNodeSelectedSingleClick(treePath);   
                }
            }
             
            if (mouseEvent.getClickCount() == 2) {
                TreePath treePath = browseTree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
                
                if (treePath != null) {
                    treeNodeSelectedDoubleClick(treePath);
                }
            }
        }
    };
 
    private final MouseAdapter searchTreeMouseAdapter = new MouseAdapter() {
        public void mousePressed(MouseEvent mouseEvent) {
            if (mouseEvent.getClickCount() == 1) {
                TreePath treePath = searchTree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
                
                if (treePath != null) {
                    treeNodeSelectedSingleClick(treePath);   
                }
            }
            
            if (mouseEvent.getClickCount() == 2) {
                TreePath treePath = searchTree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
                
                if (treePath != null) {
                    treeNodeSelectedDoubleClick(treePath);
                }
            }
        }
    };

    private final BasicTreeUI browseTreeUI = new BasicTreeUI() {
        @Override
        protected boolean shouldPaintExpandControl(TreePath p, int r, boolean iE, boolean hBE, boolean iL) {
            return false;
        }
    };

    private final BasicTreeUI searchTreeUI = new BasicTreeUI() {
        @Override
        protected boolean shouldPaintExpandControl(TreePath p, int r, boolean iE, boolean hBE, boolean iL) {
            return false;
        }
    };
    
    private final ItemListener comboBoxItemListener = new ItemListener() {
        private ScheduledFuture<?> schedule = null;

        private final Runnable task = new Runnable() {
            public void run() {
                updateRouteComboBox();
                updateTreeView();
            }
        };

        @Override
        public void itemStateChanged(ItemEvent itemEvent) {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                Utilities.stopSchedule(schedule);
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
                schedule = scheduler.schedule(task, 0, TimeUnit.MILLISECONDS);
            }
        }
    };

    private final FocusListener searchTextFieldFocusListener = new FocusListener() {
        @Override
        public void focusGained(FocusEvent focusEvent) {
            if (searchTextField.getText().equals(languageBundle.getString(Resources.TEXT_SYMBOL_TREE_HINT))) {
                searchTextField.setText("");
                searchTextField.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(FocusEvent focusEvent) {
            if (searchTextField.getText().equals("")) {
                searchTextField.setText(languageBundle.getString(Resources.TEXT_SYMBOL_TREE_HINT));
                searchTextField.setForeground(Color.GRAY);
            }
        }
    };

    private final DocumentListener searchTextFieldDocumentListener = new DocumentListener() {
        private static final int DELAY_TIME = 300;

        private ScheduledFuture<?> schedule = null;
      
        private final Runnable task = new Runnable() {
            public void run() {
                updateSearchTreeModel();
            }
        };

        private void delayUpdateSearchTreeModel() {
            Utilities.stopSchedule(schedule);
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            schedule = scheduler.schedule(task, DELAY_TIME, TimeUnit.MILLISECONDS);                
        }

        @Override
        public void insertUpdate(DocumentEvent documentEvent) {  
            delayUpdateSearchTreeModel();
        }

        @Override
        public void removeUpdate(DocumentEvent documentEvent) {
            delayUpdateSearchTreeModel();
        }

        @Override
        public void changedUpdate(DocumentEvent documentEvent) {
            delayUpdateSearchTreeModel();
        }
    };

    private final ActionListener abortButtonActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            xref.scopeBrowser.abortSymbolAcquisition();
        }
    };

    private final Observer routeLoadObserver = new Observer() {
        @Override
        public void update(Observable observable, Object object) {
            loadingState.setText(routeLoader.getLoadingState());
        }
    };
    
    private final ComponentAdapter symbolBrowserComponentAdapter = new ComponentAdapter() {
        @Override
        public void componentShown(ComponentEvent e ) {
            requestFocusInWindow();
        }
    };
    
    /*********************************/
    /********** constructor **********/
    /*********************************/

    public SymbolBrowser(XReference xref) {
        this.xref = xref;

        browseTree.setCellRenderer(new SymbolTreeRenderer());
        browseTree.setRowHeight(20);
        browseTree.setBorder(BorderFactory.createEmptyBorder(5, -5, 0, 0));
        browseTree.setRootVisible(false);
        browseTree.setScrollsOnExpand(false);
        browseTree.setShowsRootHandles(true);
        browseTree.setFont(new Font(Resources.DEFAULT_FONT, Font.PLAIN, Resources.DEFAULT_FONT_SIZE_NORMAL));
        browseTree.setModel(new SymbolTreeModel(new SymbolTreeNode()));
        browseTree.addMouseListener(browseTreeMouseAdapter);
        browseTree.setUI(browseTreeUI);
        browseTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        searchTree.setCellRenderer(new SymbolTreeRenderer());
        searchTree.setRowHeight(20);
        searchTree.setBorder(BorderFactory.createEmptyBorder(5, -5, 0, 0));
        searchTree.setRootVisible(false);
        searchTree.setScrollsOnExpand(false);
        searchTree.setShowsRootHandles(true);
        searchTree.setFont(new Font(Resources.DEFAULT_FONT, Font.PLAIN, Resources.DEFAULT_FONT_SIZE_NORMAL));
        searchTree.setModel(new SymbolTreeModel(new SymbolTreeNode()));
        searchTree.addMouseListener(searchTreeMouseAdapter);
        searchTree.setUI(searchTreeUI);
        searchTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        routeComboBox.setFont(new Font(Resources.DEFAULT_FONT, Font.BOLD, Resources.DEFAULT_FONT_SIZE_NORMAL));
        routeComboBox.addItem(languageBundle.getString(Resources.TEXT_SYMBOL_TREE_LOADING));

        portComboBox.setFont(new Font(Resources.DEFAULT_FONT, Font.BOLD, Resources.DEFAULT_FONT_SIZE_NORMAL));
        portComboBox.addItem(languageBundle.getString(Resources.TEXT_SYMBOL_TREE_LOADING));

        JToolBar routeToolBar = new JToolBar();
        routeToolBar.setFloatable(false);
        routeToolBar.setRollover(false);
        routeToolBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        routeToolBar.setLayout(new GridLayout(0, 2));
        routeToolBar.add(routeComboBox);
        routeToolBar.add(portComboBox);

        Border searchBorderInside = BorderFactory.createLoweredBevelBorder();
        Border searchBorderOutside = BorderFactory.createEmptyBorder(0, 4, 0, 0);
        CompoundBorder searchCompoundBorder = new CompoundBorder(searchBorderInside, searchBorderOutside);

        searchTextField.setBorder(searchCompoundBorder);
        searchTextField.setFont(new Font(Resources.DEFAULT_FONT, Font.PLAIN, Resources.DEFAULT_FONT_SIZE_NORMAL));
        searchTextField.setText(languageBundle.getString(Resources.TEXT_SYMBOL_TREE_HINT));
        searchTextField.setForeground(Color.GRAY);
        searchTextField.addFocusListener(searchTextFieldFocusListener);
        searchTextField.getDocument().addDocumentListener(searchTextFieldDocumentListener);

        JToolBar searchToolBar = new JToolBar();
        searchToolBar.setFloatable(false);
        searchToolBar.setRollover(false);
        searchToolBar.add(searchTextField);

        JPanel acquisitionToolbar = new JPanel();
        acquisitionToolbar.setLayout(new BoxLayout(acquisitionToolbar, BoxLayout.Y_AXIS));
        acquisitionToolbar.add(routeToolBar);
        acquisitionToolbar.add(searchToolBar);

        JButton abortButton = new JButton(languageBundle.getString(Resources.TEXT_SYMBOL_TREE_ABORT));
        abortButton.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
        abortButton.setFont(new Font(Resources.DEFAULT_FONT, Font.BOLD, Resources.DEFAULT_FONT_SIZE_NORMAL));
        abortButton.setFocusable(false);
        abortButton.addActionListener(abortButtonActionListener);

        JToolBar abortToolBar = new JToolBar();
        abortToolBar.setLayout(new BorderLayout());
        abortToolBar.setFloatable(false);
        abortToolBar.setRollover(false);
        abortToolBar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        abortToolBar.add(abortButton);

        JLabel loadingText = new JLabel();
        loadingText.setText(languageBundle.getString(Resources.TEXT_SYMBOL_TREE_LOADING));
        loadingText.setFont(new Font(Resources.DEFAULT_FONT, Font.BOLD, Resources.DEFAULT_FONT_SIZE_BIG));
        loadingState.setFont(new Font(Resources.DEFAULT_FONT, Font.PLAIN, Resources.DEFAULT_FONT_SIZE_SMALL));

        JPanel loadingBackground = new JPanel();
        loadingBackground.setLayout(new GridBagLayout());
        loadingBackground.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        loadingBackground.setMaximumSize(new Dimension(200, 50));
        loadingBackground.setBackground(Color.WHITE);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        loadingBackground.add(loadingText, gridBagConstraints);
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        loadingBackground.add(loadingState, gridBagConstraints);

        JPanel loadingPanel = new JPanel();
        loadingPanel.setLayout(new BoxLayout(loadingPanel, BoxLayout.PAGE_AXIS));
        loadingPanel.add(Box.createVerticalGlue());
        loadingPanel.add(loadingBackground);
        loadingPanel.add(Box.createVerticalGlue());
 
        scrollPanel.getVerticalScrollBar().setPreferredSize(new Dimension(Resources.DEFAULT_SCROLLBAR_WIDTH, 0));
        scrollPanel.setBorder(BorderFactory.createEmptyBorder());
        scrollPanel.setViewportView(browseTree);
        
        viewPanel.setLayout(new CardLayout());
        viewPanel.add(loadingPanel, View.LOADING.toString());
        viewPanel.add(scrollPanel, View.TREE.toString());

        this.addComponentListener(symbolBrowserComponentAdapter);
        this.setLayout(new BorderLayout());
        this.add(acquisitionToolbar, BorderLayout.PAGE_START);
        this.add(viewPanel, BorderLayout.CENTER);
        this.add(abortToolBar, BorderLayout.PAGE_END);
        this.setBorder(BorderFactory.createEmptyBorder());
    }

    /*********************************/
    /********* public method *********/
    /*********************************/

    public void load() {
        // display symbol browser
        xref.browserPanel.setCard(Browser.SYMBOL);  
        
        // load symbol tree 
        build();
    }
    
    public void build() {
        // build symbol tree
        if (routeLoader.getRouteSymbolDataList().isEmpty()) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        disableSymbolTree();
                        buildSymbolTree();
                        enableSymbolTree();
                        
                        logger.fine("Build Symbol Tree Finished");
                    } catch (Exception e) {
                        logger.fine(Utilities.exceptionToString(e));
                    }

                    return null; 
                }
            }.execute(); 
        }
    }
    
    /***********************************/
    /******** private function *********/
    /***********************************/

    private void setViewPanel(View card) {
        CardLayout cardLayout = (CardLayout) (viewPanel.getLayout());
        cardLayout.show(viewPanel, card.toString());
    }

    private void disableSymbolTree() {
        setViewPanel(View.LOADING);

        portComboBox.setEditable(true);
        ComboBoxEditor portComboBoxEditor = portComboBox.getEditor();
        JTextField portComboBoxEditorTextField = (JTextField) portComboBoxEditor.getEditorComponent();
        portComboBoxEditorTextField.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        portComboBoxEditorTextField.setDisabledTextColor(Color.GRAY);
        portComboBoxEditorTextField.setOpaque(false);
        portComboBox.setEnabled(false);

        routeComboBox.setEditable(true);
        ComboBoxEditor routeComboBoxEditor = routeComboBox.getEditor();
        JTextField routeComboBoxEditorTextField = (JTextField) routeComboBoxEditor.getEditorComponent();
        routeComboBoxEditorTextField.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        routeComboBoxEditorTextField.setDisabledTextColor(Color.GRAY);
        routeComboBoxEditorTextField.setOpaque(false);
        routeComboBox.setEnabled(false);

        searchTextField.setDisabledTextColor(Color.GRAY);
        searchTextField.setEnabled(false);
    }

    private void enableSymbolTree() {
        setViewPanel(View.TREE);

        portComboBox.setEditable(false);
        portComboBox.setEnabled(true);

        routeComboBox.setEditable(false);
        routeComboBox.setEnabled(true);

        searchTextField.setEnabled(true);
    }

    private void buildSymbolTree() {
        routeLoader.addObserver(routeLoadObserver);
        routeLoader.loadRouteSymbolDataList();

        SymbolTreeNode rootBrowseTreeNode = (SymbolTreeNode) browseTree.getModel().getRoot();
        SymbolTreeNode rootSearchTreeNode = (SymbolTreeNode) searchTree.getModel().getRoot();

        for (RouteSymbolData routeSymbolData : routeLoader.getRouteSymbolDataList()) {
            SymbolLoader symbolLoader = routeSymbolData.getSymbolLoader();

            String route = routeSymbolData.getRoute().getHostName();
            String port = symbolLoader.getAmsPort().toString();

            loadingState.setText(route + " | " + port);

            SymbolTreeNode portBrowseSymbolTreeNode = rootBrowseTreeNode.getNode(route).getNode(port);
            SymbolTreeNode portSearchSymbolTreeNode = rootSearchTreeNode.getNode(route).getNode(port);

            List<Symbol> routeSymbolList = symbolLoader.getSymbolList();
            for (Symbol symbol : routeSymbolList) {
                SymbolNode browseSymbolNode = new SymbolNode(symbol, symbolLoader, false);
                portBrowseSymbolTreeNode.addSymbolNodeSplitName(browseSymbolNode);

                SymbolNode searchSymbolNode = new SymbolNode(symbol, symbolLoader, true);
                portSearchSymbolTreeNode.addSymbolNodeFullName(searchSymbolNode);

                searchSymbolNodeList.add(searchSymbolNode);
            }
        }

        SymbolTreeModel browseSymbolTreeModel = (SymbolTreeModel) browseTree.getModel();
        SymbolTreeModel searchSymbolTreeModel = (SymbolTreeModel) searchTree.getModel();
        
        browseSymbolTreeModel.setFilterLevel(Filter.NODE);
        searchSymbolTreeModel.setFilterLevel(Filter.ALL);

        reloadAndExpandBrowseTree();

        String allRoutes = languageBundle.getString(Resources.TEXT_SYMBOL_TREE_ALL_ROUTES);
        String allPorts = languageBundle.getString(Resources.TEXT_SYMBOL_TREE_ALL_PORTS);

        List<String> routeList = new ArrayList<String>();
        List<String> portList = new ArrayList<String>();

        routeList.add(allRoutes);
        portList.add(allPorts);

        for (RouteSymbolData routeSymbolData : routeLoader.getRouteSymbolDataList()) {
            String route = routeSymbolData.getRoute().getHostName();
            String port = routeSymbolData.getSymbolLoader().getAmsPort().toString();

            if (!routeList.contains(route)) {
                routeList.add(route);
            }

            if (!portList.contains(port)) {
                portList.add(port);
            }
        }

        routeComboBox.removeAllItems();
        portComboBox.removeAllItems();

        for (String route : routeList) {
            routeComboBox.addItem(route);
        }

        for (String port : portList) {
            portComboBox.addItem(port);
        }

        routeComboBox.addItemListener(comboBoxItemListener);
        portComboBox.addItemListener(comboBoxItemListener);
    }

    private void reloadAndExpandSearchTree() {
        // hide tree
        setViewPanel(View.LOADING);
        searchTextField.setEnabled(false);
        loadingState.setText("Reload Tree");

        // reload tree model
        SymbolTreeModel searchSymbolTreeModel = (SymbolTreeModel) searchTree.getModel();
        searchSymbolTreeModel.reload();

        // expand tree nodes
        for (int i = 0; i < searchTree.getRowCount(); i++) {
            searchTree.expandRow(i);
        }

        // show tree
        setViewPanel(View.TREE);
        searchTextField.setEnabled(true);
    }

    private void reloadAndExpandBrowseTree() {
        // reload tree model
        SymbolTreeModel browseSymbolTreeModel = (SymbolTreeModel) browseTree.getModel();
        browseSymbolTreeModel.reload();

        // expand tree nodes
        SymbolTreeNode rootSymbolTreeNode = (SymbolTreeNode) browseTree.getModel().getRoot();
        for (int i = 0; i < rootSymbolTreeNode.getChildCount(); i++) {
            SymbolTreeNode routeSymbolTreeNode = (SymbolTreeNode) rootSymbolTreeNode.getChildAt(i);
            for (int x = 0; x < routeSymbolTreeNode.getChildCount(); x++) {
                SymbolTreeNode portSymbolTreeNode = (SymbolTreeNode) routeSymbolTreeNode.getChildAt(x);
                TreePath symbolTreePath = new TreePath(portSymbolTreeNode.getPath());
                browseTree.setSelectionPath(symbolTreePath);
            }
        }
    }

    private void updateRouteComboBox() {
        String allRoutes = languageBundle.getString(Resources.TEXT_SYMBOL_TREE_ALL_ROUTES);
        String allPorts = languageBundle.getString(Resources.TEXT_SYMBOL_TREE_ALL_PORTS);

        Object selectedItemRoute = routeComboBox.getSelectedItem();
        Object selectedItemPort = portComboBox.getSelectedItem();

        String selectedRoute = selectedItemRoute != null ? selectedItemRoute.toString() : allRoutes;
        String selectedPort = selectedItemPort != null ? selectedItemPort.toString() : allPorts;

        List<String> portList = new ArrayList<String>();
        portList.add(allPorts);

        if (selectedRoute.equals(allRoutes)) {
            for (RouteSymbolData routeSymbolData : routeLoader.getRouteSymbolDataList()) {
                String port = routeSymbolData.getSymbolLoader().getAmsPort().toString();

                if (!portList.contains(port)) {
                    portList.add(port);
                }
            }
        } else {
            for (RouteSymbolData routeSymbolData : routeLoader.getRouteSymbolDataList()) {
                if (routeSymbolData.getRoute().getHostName().equals(selectedRoute)) {
                    portList.add(routeSymbolData.getSymbolLoader().getAmsPort().toString());
                }
            }
        }

        portComboBox.removeItemListener(comboBoxItemListener);
        portComboBox.removeAllItems();

        for (String port : portList) {
            portComboBox.addItem(port);
        }

        if (!portList.contains(selectedPort)) {
            selectedPort = allPorts;
        }

        portComboBox.setSelectedItem(selectedPort);
        portComboBox.addItemListener(comboBoxItemListener);
    }

    private void updateTreeView() {
        SymbolTreeNode browseSymbolTreeNode = (SymbolTreeNode) browseTree.getModel().getRoot();
        SymbolTreeNode searchSymbolTreeNode = (SymbolTreeNode) searchTree.getModel().getRoot();
        
        updateTreeNodeVisibility(browseSymbolTreeNode);
        updateTreeNodeVisibility(searchSymbolTreeNode);

        reloadAndExpandBrowseTree();
        reloadAndExpandSearchTree();
    }

    private void updateTreeNodeVisibility(SymbolTreeNode rootSymbolTreeNode) {
        String allRoutes = languageBundle.getString(Resources.TEXT_SYMBOL_TREE_ALL_ROUTES);
        String allPorts = languageBundle.getString(Resources.TEXT_SYMBOL_TREE_ALL_PORTS);

        Object selectedItemRoute = routeComboBox.getSelectedItem();
        Object selectedItemPort = portComboBox.getSelectedItem();

        String selectedRoute = selectedItemRoute != null ? selectedItemRoute.toString() : allRoutes;
        String selectedPort = selectedItemPort != null ? selectedItemPort.toString() : allPorts;

        for (int i = 0; i < rootSymbolTreeNode.getChildCount(); i++) {
            SymbolTreeNode routeSymbolTreeNode = (SymbolTreeNode) rootSymbolTreeNode.getChildAt(i);

            if (selectedRoute != allRoutes && !routeSymbolTreeNode.toString().equals(selectedRoute)) {
                routeSymbolTreeNode.setVisible(false);
            } else {
                routeSymbolTreeNode.setVisible(true);
            }

            if (routeSymbolTreeNode.isVisible()) {
                for (int x = 0; x < routeSymbolTreeNode.getChildCount(); x++) {
                    SymbolTreeNode portTreeNode = (SymbolTreeNode) routeSymbolTreeNode.getChildAt(x);

                    if (selectedPort != allPorts && !portTreeNode.toString().equals(selectedPort)) {
                        portTreeNode.setVisible(false);
                    } else {
                        portTreeNode.setVisible(true);
                    }
                }
            }
        }
    }
    
    private void updateSearchTreeModel() { 
        String inputText = searchTextField.getText();
        String searchHint = languageBundle.getString(Resources.TEXT_SYMBOL_TREE_HINT);

        if (!inputText.isEmpty() && !inputText.equals(searchHint)) {
            String regex = "(.*)(" + inputText.replace(" ", ".*") + ")(.*)";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

            for (SymbolNode symbolNode : searchSymbolNodeList) {
                String symbolName = symbolNode.getSymbol().getSymbolName();
                Matcher matcher = pattern.matcher(symbolName);

                if (matcher.matches()) {
                    symbolNode.setVisible(true);
                } else {
                    symbolNode.setVisible(false);
                }
            }
            
            reloadAndExpandSearchTree();
            scrollPanel.setViewportView(searchTree);
            searchTextField.requestFocus();
        } else {
            scrollPanel.setViewportView(browseTree);
        }
    }

    private void treeNodeSelectedSingleClick(TreePath treePath) {
        SymbolTreeNode symbolTreeNode = (SymbolTreeNode) treePath.getLastPathComponent();
        Object userObject = symbolTreeNode.getUserObject();

        if (userObject instanceof SymbolNode) {
            SymbolNode symbolNode = (SymbolNode) userObject;
            Symbol selectedSymbol = symbolNode.getSymbol();
            SymbolLoader symbolLoader = symbolNode.getSymbolLoader();
            
            if (!selectedSymbol.getDataType().equals(DataType.BIGTYPE)) {
                String symbolName = selectedSymbol.getSymbolName();
                String amsNetId = symbolLoader.getAmsNetId();
                AmsPort amsPort = symbolLoader.getAmsPort();
                
                // set symbol acquisition data
                xref.acquisitionProperties.getAcquisition().setSymbolName(symbolName);
                xref.acquisitionProperties.getAcquisition().setAmsNetId(amsNetId);
                xref.acquisitionProperties.getAcquisition().setAmsPort(amsPort);
                xref.acquisitionProperties.getAcquisition().setSymbolBased(true);
                xref.acquisitionProperties.load();
                
                // send symbol acquisition data to console
                xref.consolePanel.setClipboard(symbolName);
                xref.consolePanel.getCommandLine().getAdsClient().setAmsNetId(amsNetId);
                xref.consolePanel.getCommandLine().getAdsClient().setAmsPort(amsPort);
                
                logger.info(selectedSymbol.getSymbolName());
            }
        }
    }

    private void treeNodeSelectedDoubleClick(TreePath treePath) {
        SymbolTreeNode symbolTreeNode = (SymbolTreeNode) treePath.getLastPathComponent();
        Object userObject = symbolTreeNode.getUserObject();

        if (userObject instanceof SymbolNode) {
            SymbolNode symbolNode = (SymbolNode) userObject;
            Symbol selectedSymbol = symbolNode.getSymbol();
            SymbolLoader symbolLoader = symbolNode.getSymbolLoader();
            
            if (selectedSymbol.getDataType().equals(DataType.BIGTYPE)) {
                // update symbol tree
                List<Symbol> symbolList = symbolLoader.getSymbolList(selectedSymbol);
                for (Symbol symbol : symbolList) {
                    if (scrollPanel.getViewport().getView().equals(searchTree)) {
                        // add symbol node to search tree
                        SymbolNode symbolNodeChild = new SymbolNode(symbol, symbolLoader, true);
                        symbolTreeNode.addSymbolNodeFullName(symbolNode, symbolNodeChild);
                        searchSymbolNodeList.add(symbolNodeChild);
                    } else {
                        // add symbol node to browse tree
                        SymbolNode symbolNodeChild = new SymbolNode(symbol, symbolLoader, false);
                        symbolTreeNode.addSymbolNodeSplitName(symbolNode, symbolNodeChild);
                    }
                }

                // snapshot of symbol tree node path
                TreePath symbolTreePath = new TreePath(symbolTreeNode.getPath());

                // update search tree model
                if (scrollPanel.getViewport().getView().equals(searchTree)) {
                    symbolTreeNode.removeFromParent();
                    SymbolTreeModel searchSymbolTreeModel = (SymbolTreeModel) searchTree.getModel();
                    searchSymbolTreeModel.reload();
                }

                // set view to symbol tree not path snapshot
                searchTree.setSelectionPath(symbolTreePath);
            } else {
                xref.acquisitionProperties.applyAcquisition();
            }
        }
    }
}
