package twincat.app.common;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import twincat.Resources;
import twincat.Utilities;
import twincat.constant.DefaultColorTable;

public class SymbolTreeRenderer extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 1L;

    /*********************************/
    /*** global constant variable ****/
    /*********************************/

    public static final Color DEFAULT_BACKGROUND_COLOR = DefaultColorTable.WHITE.color;
    
    /*********************************/
    /**** local constant variable ****/
    /*********************************/

    private static final Color TEXT_SELECTION_COLOR = DefaultColorTable.BLACK.color;

    private static final Color BORDER_SELECTION_COLOR = DefaultColorTable.GRAY.color;

    private static final Color BACKGROUND_SELECTION_COLOR = DefaultColorTable.JAVAGRAY.color;

    private static final ImageIcon ICON_BIT = new ImageIcon(Utilities.getImageFromFilePath(Resources.PATH_ICON_DATATYPE_BIT));

    private static final ImageIcon ICON_INT8 = new ImageIcon(Utilities.getImageFromFilePath(Resources.PATH_ICON_DATATYPE_INT8));

    private static final ImageIcon ICON_UINT8 = new ImageIcon(Utilities.getImageFromFilePath(Resources.PATH_ICON_DATATYPE_UINT8));

    private static final ImageIcon ICON_INT16 = new ImageIcon(Utilities.getImageFromFilePath(Resources.PATH_ICON_DATATYPE_INT16));

    private static final ImageIcon ICON_UINT16 = new ImageIcon(Utilities.getImageFromFilePath(Resources.PATH_ICON_DATATYPE_UINT16));

    private static final ImageIcon ICON_INT32 = new ImageIcon(Utilities.getImageFromFilePath(Resources.PATH_ICON_DATATYPE_INT32));

    private static final ImageIcon ICON_UINT32 = new ImageIcon(Utilities.getImageFromFilePath(Resources.PATH_ICON_DATATYPE_UINT32));

    private static final ImageIcon ICON_REAL32 = new ImageIcon(Utilities.getImageFromFilePath(Resources.PATH_ICON_DATATYPE_REAL32));

    private static final ImageIcon ICON_REAL64 = new ImageIcon(Utilities.getImageFromFilePath(Resources.PATH_ICON_DATATYPE_REAL64));

    private static final ImageIcon ICON_STRING = new ImageIcon(Utilities.getImageFromFilePath(Resources.PATH_ICON_DATATYPE_STRING));

    private static final ImageIcon ICON_BIGTYPE = new ImageIcon(Utilities.getImageFromFilePath(Resources.PATH_ICON_DATATYPE_BIGTYPE));

    private static final ImageIcon ICON_TIME = new ImageIcon(Utilities.getImageFromFilePath(Resources.PATH_ICON_DATATYPE_TIME));

    private static final ImageIcon ICON_OPEN = new ImageIcon(Utilities.getImageFromFilePath(Resources.PATH_ICON_APP_FOLDER_OPEN));

    private static final ImageIcon ICON_CLOSED = new ImageIcon(Utilities.getImageFromFilePath(Resources.PATH_ICON_APP_FOLDER_CLOSED));

    /*********************************/
    /******** override method ********/
    /*********************************/

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();

        if (userObject instanceof SymbolNode) {
            SymbolNode symbolNode = (SymbolNode) userObject;

            switch (symbolNode.getSymbol().getDataType()) {
                case BIT:
                    this.setIcon(ICON_BIT);
                    break;

                case INT8:
                    this.setIcon(ICON_INT8);
                    break;

                case UINT8:
                    this.setIcon(ICON_UINT8);
                    break;

                case INT16:
                    this.setIcon(ICON_INT16);
                    break;

                case UINT16:
                    this.setIcon(ICON_UINT16);
                    break;

                case INT32:
                    this.setIcon(ICON_INT32);
                    break;

                case UINT32:
                    this.setIcon(ICON_UINT32);
                    break;

                case REAL32:
                    this.setIcon(ICON_REAL32);
                    break;
                    
                case REAL64:
                    this.setIcon(ICON_REAL64);
                    break;

                case STRING:
                    this.setIcon(ICON_STRING);
                    break;

                case BIGTYPE:
                    this.setIcon(ICON_BIGTYPE);
                    break;

                case TIME:
                    this.setIcon(ICON_TIME);
                    break;
                    
                default:
                    break;
            }
        } else if (expanded) {
            this.setIcon(ICON_OPEN);
        } else {
            this.setIcon(ICON_CLOSED);
        }

        return this;
    }

    @Override
    public void setTextSelectionColor(Color newColor) {
        super.setTextSelectionColor(TEXT_SELECTION_COLOR);
    }
    
    @Override
    public void setBorderSelectionColor(Color newColor) {
        super.setBorderSelectionColor(BORDER_SELECTION_COLOR);
    }
    
    @Override
    public void setBackgroundSelectionColor(Color newColor) {
        super.setBackgroundSelectionColor(BACKGROUND_SELECTION_COLOR);
    }

    @Override
    public Color getBackgroundNonSelectionColor() {
        return DEFAULT_BACKGROUND_COLOR;
    }
}
