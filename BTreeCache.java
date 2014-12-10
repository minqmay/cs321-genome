import java.util.Iterator;
import java.util.LinkedList;

public class BTreeCache implements Iterable<BTreeCacheNode>{

    private final int MAX_SIZE;
    private int numHits, numMisses;

    private LinkedList<BTreeCacheNode> list;

    /**
     * Create a new BTreeCache of the specified capacity.
     * 
     * @param MAX_SIZE max capacity of the cache
     */
    public BTreeCache(int MAX_SIZE){
        this.MAX_SIZE = MAX_SIZE;
        list = new LinkedList<BTreeCacheNode>();
    }
    
    /**
     * Add a node to the BTreeCache. If this causes a node to fall off the list,
     * that cache node will be returned; otherwise null is returned.
     * 
     * @param o the BTreeNode to add
     * @param o the offset of the node in the file
     */
    public BTreeCacheNode add(BTreeNode o,int offset){
    	BTreeCacheNode rnode = null;
        if (isFull()){
            rnode = list.removeLast();
        }
        list.addFirst(new BTreeCacheNode(o,offset));
        return rnode;
    }
    
    /**
     * Remove all elements from the BTreeCache.
     */
    public void clearCache(){
        list.clear();
    }
    
    /**
     * Read a node from the cache, if it is cached; return false otherwise. If
     * the node is found it is automatically moved to the beginning of the
     * cache.
     * 
     * @param offset offset of the node to read
     * @return the node at offset, or null if the node was not in the cache
     */
    public BTreeNode readNode(int offset) {
    	for (BTreeCacheNode n : list) {
    		if (n.getOffset() == offset) {
    			// XXX: crap performance here since we traverse the list again
    			// just to remove the node...
    			list.remove(n);
    			list.addFirst(n);
    			increaseNumHits();
    			return n.getData();
    		}
    	}
    	// we went through the whole list without finding it
    	increaseNumMisses();
    	return null;
    }
    
    /**
     * Get the number of reads performed on this BTreeCache.
     * 
     * @return number of reads
     */
    public int getNumReferences(){
        return numHits + numMisses;
    }

    private void increaseNumHits(){
        numHits++;
    }
    
    private void increaseNumMisses(){
        numMisses++;
    }
    
    /**
     * Get the number of cache hits.
     * 
     * @return number of cache hits
     */
    public int getNumHits(){
        return numHits;
    }
    
    /**
     * Get the number of cache misses.
     * 
     * @return number of cache misses
     */
    public int getNumMisses(){
        return numMisses;
    }
 
    /**
     * Get the hit ratio for this cache.
     * 
     * @return number of cache hits / number of cache reads
     */
    public double getHitRatio(){
        double ratio = ((double) getNumHits()) / getNumReferences();
        return ratio;
    }
    
    /**
     * Get the number of elements stored in this cache.
     * 
     * @return number of elements
     */
    public int getSize(){
        return list.size();
    }
    
    /**
     * Get whether this cache is full.
     * 
     * @return true if the cache is full, false otherwise
     */
    public boolean isFull(){
        return getSize() == MAX_SIZE;
    }

	@Override
	public Iterator<BTreeCacheNode> iterator() {
		return list.iterator();
	}
}                                                                                                                                                                     
