import java.util.LinkedList;

public class Cache<T>{

    private final int MAX_SIZE;
    private int numHits, numMisses;

    private MyLinkedList<T> list;

    public Cache(int MAX_SIZE){
        this.MAX_SIZE = MAX_SIZE;
        list = new MyLinkedList<T>();
    }
    public void addObject(T o){
        if (isFull()){
            list.removeLast();
            list.addFirst(o);
        }
        else {
            list.addFirst(o);
        }
    }
    public T removeObject(T o){
        if (list.remove(o))
            return o;
        else
            return null;
    }
    public void clearCache(){
        list.clear();
    }
    public boolean contains(T o){
        return list.contains(o);
    }
    public int getNumReferences(){
        return numHits + numMisses;
    }
    public void increaseNumHits(){
        numHits++;
    }
    public void increaseNumMisses(){
        numMisses++;
    }
    public int getNumHits(){
        return numHits;
    }
 public int getNumMisses(){
        return numMisses;
    }
    public double getHitRatio(){
        double ratio = ((double) getNumHits()) / getNumReferences();
        return ratio;
    }
    public int getSize(){
        return Math.min(list.size(), MAX_SIZE);
    }
    public boolean isFull(){
        return getSize() == MAX_SIZE;
    }
}                                                                                                                                                                     
