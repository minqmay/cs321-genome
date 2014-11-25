import java.util.LinkedList;

public class BTreeNode{

    private int n;
    private LinkedList<TreeObject> keys;
    private LinkedList<Integer> children;
    private boolean isLeaf;
    private int parent;
    private int offset;

    public BTreeNode(){
        parent = -1;
        keys = new LinkedList<TreeObject>();
        children = new LinkedList<Integer>();
        n = 0;
    }
    public int getN(){
        return n;
    }
    public void setParent(int parent){
        this.parent = parent;
    }
    public int getParent(){
        return parent;
    }
    public void setOffset(int offset){
        this.offset = offset;
    }
    public int getOffset(){
        return offset;
    }
    public void setN(int a){
        n = a;
    }
    public void addChild(int n){
        children.add(n);
    }
    public int removeChild(int i){
        return children.remove(i);
    }
    public int getChild(int i){
        return children.get(i).intValue();
    }
    public TreeObject removeKey(int i){
        return keys.remove(i);
    }
    public void addKey(TreeObject obj){
        keys.add(obj);
    }
    public TreeObject getKey(int k){
        TreeObject obj = keys.get(k);
        return obj;
    }
    public void setIsLeaf(boolean isLeaf){
        this.isLeaf = isLeaf;
    }
    public boolean isLeaf(){
        return isLeaf;
    }
    public void addKey(TreeObject obj, int i){
        keys.add(i,obj);
    }
    public void addChild(Integer x, int i){
        children.add(i,x);
    }
    public LinkedList<TreeObject> getKeys(){
        return keys;
    }
    public String toString(){
        String s = new String();
        for (int i = 0; i < keys.size(); i++){
            s += ("keys: " + keys);
        }
        for (int i = 0; i < children.size(); i++){
            s += ("children: " + children);
        }
        return s;
    }
    public LinkedList<Integer> getChildren(){
        return children;
    }
}
