package tailor.datasource;



/**
 * @author maclean
 *
 */
public class Generator implements StructureSource {

    public boolean hasNext() {
        return false;
    }
    
    public tailor.structure.Structure next() {
        return null;
    }
    
    public int size() {
        return -1;
    }
}
