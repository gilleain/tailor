package aigen;

import java.util.Collections;
import java.util.Iterator;

abstract class Description implements Iterable<Description> {
    public abstract String getLevelCode();
    public abstract String getName();
    public abstract int length();
    public abstract boolean describes(Feature feature);
    public abstract Class<? extends Feature> getFeatureType();
    
    @Override
    public Iterator<Description> iterator() {
        return Collections.emptyIterator();
    }
}

