import java.io.RandomAccessFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BTree{

    private int degree;
    private BTreeNode root;
    private RandomAccessFile raf;
    private File myfile;
    private int placeToInsert;
    private final int rootOffset = 12;
    private final int BTreeNodeSize;

    public BTree(int degree){

        BTreeNodeSize = 32*degree-3;
        placeToInsert = rootOffset + BTreeNodeSize;
        this.degree = degree;
        BTreeNode x = new BTreeNode();
        root = x;
        root.setOffset(rootOffset);
        x.setIsLeaf(true);
        x.setN(0);
        try {
            myfile = new File("test");
            raf = new RandomAccessFile(myfile, "rw");
        }
        catch (FileNotFoundException fnfe){
            System.err.println("file is corrupt or missing!");
            System.exit(-1);
        }
        writeTreeMetadata();
    }
    public void insert(long k){
        BTreeNode r = root;
        int i = r.getN();
        System.out.println("inserting: " + k);
        if (i == (2 * degree - 1)){
            TreeObject obj = new TreeObject(k);
            while (i > 0 && obj.compareTo(r.getKey(i-1)) < 0){
                i--;
            }
            if (i < r.getN()){
            System.out.println("r: " + r.getKey(i));
            System.out.println("obj: " + obj);
            }
            if (i > 0 && obj.compareTo(r.getKey(i-1)) == 0)
                r.getKey(i-1).increaseFrequency();
            else {
                BTreeNode s = new BTreeNode();
                s.setOffset(r.getOffset());
                root = s;
                r.setOffset(placeToInsert);
                r.setParent(s.getOffset());
                s.setIsLeaf(false);
                s.addChild(r.getOffset());
                splitChild(s, 0, r);
                insertNonfull(s,k);
            }
        }
        else
            insertNonfull(r,k);
    }
    public void insertNonfull(BTreeNode x, long k){
        int i = x.getN();
        TreeObject obj = new TreeObject(k);
        System.out.println(x + ": " + x.isLeaf());
        if (x.isLeaf()){
            if (x.getN() != 0) {
                System.out.println("x.getN(): " + x.getN());
                while (i > 0 && obj.compareTo(x.getKey(i-1)) < 0){
                    System.out.println("comparing " + x.getKey(i-1)
                    + " with " + obj);
                    i--;
                }
            }
            if (i > 0 && obj.compareTo(x.getKey(i-1)) == 0){
                x.getKey(i-1).increaseFrequency();
            }
            else {
                System.out.println("adding " + k);
                x.addKey(obj,i);
                System.out.println("x.getKeys(): " + x.getKeys());
                x.setN(x.getN()+1);
                System.out.println(x.getN());
            }
            writeNode(x,x.getOffset());
        }
        else {
            while (i > 0 && (obj.compareTo(x.getKey(i-1)) < 0)){
                i--;
            }
            if (i > 0 && obj.compareTo(x.getKey(i-1)) == 0){
                x.getKey(i-1).increaseFrequency();
                writeNode(x,x.getOffset());
            }
            int offset = x.getChild(i);
            BTreeNode y = readNode(offset);
            System.out.println("y: " + y);
            System.out.println("y.getN(): " + y.getN());
            System.out.println("y.getChildren(): " + y.getChildren());
            if (y.getN() == 2 * degree - 1){
                i = y.getN();
                while (i > 0 && obj.compareTo(y.getKey(i-1)) < 0){
                    System.out.println("obj: " + obj);
                    System.out.println("checking: " + y.getKey(i-1));
                    i--;
                } 
                System.out.println("obj: " + obj);
                System.out.println("checking: " + y.getKey(i-1));
                if (i > 0 && obj.compareTo(y.getKey(i-1)) == 0){
                    System.out.println("increasing freq");
                    y.getKey(i-1).increaseFrequency();
                    System.out.println(y.getKey(i-1));
                    writeNode(y,y.getOffset());
                    return;
                }
                else {
                    i = x.getN();
                    splitChild(x, i, y);
                        if (obj.compareTo(x.getKey(i)) > 0) {
                            i++;
                        }
                }
            }
            System.out.print("Going to child("+i+")" + x.getChild(i));
            offset = x.getChild(i);
            BTreeNode child = readNode(offset);
            System.out.println(": " + child);
            insertNonfull(child,k);
        }
    }
    public void splitChild(BTreeNode x, int i, BTreeNode y){
        BTreeNode z = new BTreeNode();
        System.out.println("in splitChild()");
        z.setIsLeaf(y.isLeaf());
        z.setParent(y.getParent());
        for (int j = 0; j < degree - 1; j++){
            System.out.println("moving y.getKey(" + degree + "): " + y.getKey(degree) 
                    + " to z");
            z.addKey(y.removeKey(degree));
            System.out.println("z: " + z);
            System.out.println("y: " + y);
            z.setN(z.getN()+1);
            System.out.println("z.getN(): " + z.getN());
            y.setN(y.getN()-1);
            System.out.println("y.getN(): " + y.getN());

        }
        if (!y.isLeaf()){
            for (int j = 0; j < degree; j++){
            System.out.println("moving y.getChild(" + degree + "): " + 
                y.getChild(degree) + " to z");
                z.addChild(y.removeChild(degree));
                System.out.println("z: " + z); 
                System.out.println("z.getChildren(): " + z.getChildren());
                System.out.println("y: " + y);
                System.out.println("z.getChildren(): " + z.getChildren());
            }
        }
        
        System.out.println("moving y.getKey(" + (degree - 1) + "): " + y.getKey(degree - 1) +
                "to x");
        System.out.println("x: " + x.getKeys());
        x.addKey(y.removeKey(degree - 1), i);
        System.out.println("x: " + x);
        x.setN(x.getN()+1);
        y.setN(y.getN()-1);
        if (x == root){
            writeNode(y,placeToInsert);
            placeToInsert += BTreeNodeSize;
            z.setOffset(placeToInsert);
            x.addChild(z.getOffset(),i+1);
            writeNode(z,placeToInsert);
            writeNode(x,rootOffset);
        }
        else{
            writeNode(x,x.getOffset());
            writeNode(y,y.getOffset());
            z.setOffset(placeToInsert);
            writeNode(z,placeToInsert);
            placeToInsert += BTreeNodeSize;
        }
    }
    public TreeObject search(BTreeNode x, long k){
        int i = 0;
        TreeObject obj = new TreeObject(k);
        System.out.println("searching for: " + obj);
        System.out.println(x.getKeys());
        System.out.println(x.getN());
        while (i < x.getN() && (obj.compareTo(x.getKey(i)) > 0)){
            System.out.println(x.getKey(i));
            i++;
        }
        if (i < x.getN() && obj.compareTo(x.getKey(i)) == 0){
            return x.getKey(i);
        }
        if (x.isLeaf()){
            return null;
        }
        else {
            int offset = x.getChild(i);
            BTreeNode y = readNode(offset);
            return search(y,k);
        }
    }
    public void inOrderPrint(BTreeNode n){
        TreeObject obj = null;
        System.out.println(n);
        if (n.isLeaf() == true){
            for (int i = 0; i < n.getN(); i++){
                System.out.println(n.getKey(i));
            }
            return;
        }
        for (int i = 0; i < n.getN() + 1; ++i){
            int offset = n.getChild(i);
            BTreeNode y = readNode(offset);
            inOrderPrint(y);
            if (i < n.getN())
                System.out.println(n.getKey(i));
        }
    }
    public BTreeNode getRoot(){
        return root;
    }
    public void writeNode(BTreeNode n, int offset){
            int i = 0;
            System.out.println("in writeNode()");
            System.out.println("writing " + n + " to " + offset);
            try {
                writeNodeMetadata(n,n.getOffset());
                System.out.println("writing isLeaf: " + n.isLeaf());
                System.out.println("writing n: " + n.getN());
                System.out.println("writing parent: " + n.getParent());
                raf.writeInt(n.getParent());
                for (i = 0; i < 2 * degree - 1; i++){
                    if (i < n.getN() + 1 && !n.isLeaf()){
                        System.out.println("i: " + i);
                        raf.writeInt(n.getChild(i));
                        System.out.println("writing " + n.getChild(i));
                    }
                    else if (i >= n.getN() + 1 || n.isLeaf()){
                        System.out.println("writing child 0");
                        raf.writeInt(0);
                    }
                    if (i < n.getN()){
                        long data = n.getKey(i).getData();
                        raf.writeLong(data);
                        System.out.println("writing " + data);
                        int frequency = n.getKey(i).getFrequency();
                        raf.writeInt(frequency);
                        System.out.println("writing " + frequency);
                    }
                    else if (i >= n.getN() || n.isLeaf()){
                        raf.writeLong(0);
                        System.out.println("writing obj 0");
                    }
                }
                    //System.out.println("i: " + i);
                    System.out.println("n.getN(): " + n.getN());
                    System.out.println(2 * degree - 1);
                if (n.getN() == (2 * degree - 1) && !n.isLeaf()){
                    System.out.println("writing last child: " + n.getChild(i-1));
                    raf.writeInt(n.getChild(i-1));
                }
                else {
                    raf.writeInt(0);
                    System.out.println("writing child 0");
                }
            }
            catch (IOException ioe){
                System.err.println("IO Exception occurred!");
                System.exit(-1);
            }
    }
    public BTreeNode readNode(int offset){
        BTreeNode y = new BTreeNode();
        System.out.println("in readNode()");
        System.out.println("reading from " + offset);
        TreeObject obj = null;
        y.setOffset(offset);
        int k = 0;
        try {
            raf.seek(offset);
            boolean isLeaf = raf.readBoolean();
            y.setIsLeaf(isLeaf);
            System.out.println("isLeaf: " + isLeaf);
            int n = raf.readInt();
            y.setN(n);
            int parent = raf.readInt();
            y.setParent(parent);
            System.out.println("y.n " + n);
            for (k = 0; k < 2 * degree - 1; k++){
                if (k < y.getN() + 1 && !y.isLeaf()){
                    int child = raf.readInt();
                    y.addChild(child);
                    System.out.println("k: " + k);
                    System.out.println("reading child: " + child);
                }
                else if (k >= y.getN() || y.isLeaf()){
                    raf.seek(raf.getFilePointer() + 4);
                }
                if (k < y.getN()){
                    long value = raf.readLong();
                    int frequency = raf.readInt();
                    obj = new TreeObject(value,frequency);
                    y.addKey(obj);
                    System.out.println("reading key: " + obj);
                }
            }
            if (y.getN() == (2 * degree - 1) && !y.isLeaf()){
                int child = raf.readInt();
                y.addChild(child);
                System.out.println("reading last child: " + child);
            }
            else if (k > y.getN()){
                raf.seek(raf.getFilePointer() + 4);
            }
        }
        catch (IOException ioe){
            System.err.println(ioe.getMessage());
            System.exit(-1);
        }
        return y;
    }
    public void writeTreeMetadata(){
        try {
            raf.seek(0);
            raf.writeInt(degree);
            raf.writeInt(32*degree-3);
            raf.writeInt(12);
        }
        catch (IOException ioe){
            System.err.println("IO Exception occurred!");
            System.exit(-1);
        }
    }
    public void writeNodeMetadata(BTreeNode x, int offset){
        try {
            raf.seek(offset);
            raf.writeBoolean(x.isLeaf());
            raf.writeInt(x.getN());
        }
        catch (IOException ioe){
            System.err.println("IO Exception occurred!");
            System.exit(-1);
        }
    }
}
