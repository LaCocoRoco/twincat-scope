package twincat.app.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;

public class WrapTopLayout extends WrapLayout {
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new <code>WrapTopLayout</code> with a left alignment and a default
     * 5-unit horizontal and vertical gap.
     */
    public WrapTopLayout() {
        super();
    }
    
    /**
     * Constructs a new <code>FlowLayout</code> with the specified alignment and a
     * default 5-unit horizontal and vertical gap. The value of the alignment
     * argument must be one of <code>WrapTopLayout</code>, <code>WrapTopLayout</code>, or
     * <code>WrapTopLayout</code>.
     * 
     * @param align the alignment value
     */
    public WrapTopLayout(int align) {
        super(align);
    }

    @Override
    public void layoutContainer(Container target) {
        super.layoutContainer(target);

        if (target.getComponentCount() != 0 ) {
            Component component = target.getComponent(0);

            int lineStart = getVgap();
            int lineHeight = lineStart + component.getSize().height;

            for (int i = 0; i < target.getComponentCount(); i++) {
                component = target.getComponent(i);

                Point point = component.getLocation();
                Dimension dimension = component.getSize();

                if (point.y < lineHeight) {
                    point.y = lineStart;
                    lineHeight = Math.max(lineHeight, lineStart + dimension.height);
                } else {
                    lineStart = lineHeight + getVgap();
                    point.y = lineStart;
                    lineHeight = lineStart + dimension.height;
                }

                point.y = lineStart;
                component.setLocation(point);
            }   
        }
    }
}
