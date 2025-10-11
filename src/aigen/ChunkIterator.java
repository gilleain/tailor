package aigen;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

//Utility class for chunking operations
class ChunkIterator<T> implements Iterator<List<T>> {
 private final List<T> list;
 private final int chunkSize;
 private int currentIndex = 0;

 public ChunkIterator(List<T> list, int chunkSize) {
     this.list = list;
     this.chunkSize = chunkSize;
 }

 @Override
 public boolean hasNext() {
     return currentIndex < list.size();
 }

 @Override
 public List<T> next() {
     if (!hasNext()) {
         throw new NoSuchElementException();
     }
     int end = Math.min(currentIndex + chunkSize, list.size());
     List<T> chunk = list.subList(currentIndex, end);
     currentIndex++;
     return chunk;
 }
}
