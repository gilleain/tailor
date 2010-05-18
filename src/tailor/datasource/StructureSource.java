package tailor.datasource;

import java.io.IOException;



/**
 * @author maclean
 *
 */
public interface StructureSource {

    
    /**
     * Get the next Structure from the source.
     * 
     * @return a Structure
     * @throws IOException if there is a problem with the Structure
     */
    public Structure next() throws IOException;
    //TODO : could this throw a more general exception? Rather than Generator
    //throwing an IOException which doesn't make much sense...
    
    public boolean hasNext();
    
    public int size();
    
}
