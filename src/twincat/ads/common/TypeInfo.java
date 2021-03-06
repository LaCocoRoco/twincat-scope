package twincat.ads.common;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class TypeInfo {
    /*********************************/
    /**** local constant variable ****/
    /*********************************/    
    
    private static final String POINTER_PATTERN   = "POINTER TO";
     
    private static final String PATTERN_ARRAY_BEG = "ARRAY ";
    
    private static final String PATTERN_ARRAY_END = "OF ";
    
    /*************************/
    /*** global attributes ***/
    /*************************/
    
    private boolean pointer;

    private String type;

    private final List<String> array = new ArrayList<String>();

    /*************************/
    /**** setter & getter ****/
    /*************************/

    public boolean isPointer() {
        return pointer;
    }

    public void setPointer(boolean pointer) {
        this.pointer = pointer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getArray() {
        return array;
    }

    /*************************/
    /********* public ********/
    /*************************/

    public void parseTypeInfo(String type) {
        pointer = type.contains(POINTER_PATTERN) ? true : false;

        if (type.contains(PATTERN_ARRAY_BEG)) {
            int begPattern = type.indexOf("[");
            int endPattern = type.indexOf("]");

            List<Point> pointArray = new ArrayList<Point>();
            
            String[] data = type.substring(begPattern + 1, endPattern).split(",");
            
            for (String position : data) {
                String[] size = position.replace(" ", "").split("\\..");
                
                try {
                    int beg = Integer.valueOf(size[0]);
                    int end  = Integer.valueOf(size[1]);
                    pointArray.add(new Point(beg, end));
                } catch (NumberFormatException e) {
                    pointArray.clear();
                    break;
                }
            }
            
            if (!pointArray.isEmpty()) array.addAll(getNamedTypeArray(pointArray, 0)); 
            int begIndex = type.indexOf(PATTERN_ARRAY_END) + PATTERN_ARRAY_END.length();
            type = type.substring(begIndex, type.length());
        }
        
        this.type = type;   
    }
    
    /*************************/
    /******** private ********/
    /*************************/

    private List<String> getNamedTypeArray(List<Point> typeArrayList, int index) {
        List<String> namedList = new ArrayList<String>();
        Point typeArray = typeArrayList.get(index);
        String end = index == 0 ? "[" : "";
        for (int i = typeArray.x; i <= typeArray.y; i++) {
            if (typeArrayList.size() > index + 1) {
                for (String r : getNamedTypeArray(typeArrayList, index + 1))
                    namedList.add(end + Integer.toString(i) + "," + r);
            } else {
                namedList.add(end + Integer.toString(i) + "]");
            }
        }

        return namedList;
    } 
}
