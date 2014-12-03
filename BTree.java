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

    public BTree(int degree, String fileName){

        BTreeNodeSize = 32*degree-3;
        placeToInsert = rootOffset + BTreeNodeSize;
        this.degree = degree;
        BTreeNode x = new BTreeNode();
        root = x;
        root.setOffset(rootOffset);
        x.setIsLeaf(true);
        x.setN(0);
        try {
            myfile = new File(fileName);
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
        if (i == (2 * degree - 1)){
            TreeObject obj = new TreeObject(k);
            while (i > 0 && obj.compareTo(r.getKey(i-1)) < 0){
                i--;
            }
            if (i < r.getN()){
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
        if (x.isLeaf()){
            if (x.getN() != 0) {
                while (i > 0 && obj.compareTo(x.getKey(i-1)) < 0){
                    i--;
                }
            }
            if (i > 0 && obj.compareTo(x.getKey(i-1)) == 0){
                x.getKey(i-1).increaseFrequency();
            }
            else {
                x.addKey(obj,i);
                x.setN(x.getN()+1);
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
                return;
            }
            int offset = x.getChild(i);
            BTreeNode y = readNode(offset);
            if (y.getN() == 2 * degree - 1){
                int j = y.getN();
                while (j > 0 && obj.compareTo(y.getKey(j-1)) < 0){
                    j--;
                } 
                if (j > 0 && obj.compareTo(y.getKey(j-1)) == 0){
                    y.getKey(j-1).increaseFrequency();
                    writeNode(y,y.getOffset());
                    return;
                }
                else {
                    splitChild(x, i, y);
                        if (obj.compareTo(x.getKey(i)) > 0) {
                            i++;
                        }
                }
            }
            offset = x.getChild(i);
            BTreeNode child = readNode(offset);
            insertNonfull(child,k);
        }
    }
    public void splitChild(BTreeNode x, int i, BTreeNode y){
        BTreeNode z = new BTreeNode();
        z.setIsLeaf(y.isLeaf());
        z.setParent(y.getParent());
        for (int j = 0; j < degree - 1; j++){
            z.addKey(y.removeKey(degree));
            z.setN(z.getN()+1);
            y.setN(y.getN()-1);

        }
        if (!y.isLeaf()){
            for (int j = 0; j < degree; j++){
                z.addChild(y.removeChild(degree));
            }
        }
        x.addKey(y.removeKey(degree - 1), i);
        x.setN(x.getN()+1);
        y.setN(y.getN()-1);
        if (x == root){
            writeNode(y,placeToInsert);
            placeToInsert += BTreeNodeSize;
            z.setOffset(placeToInsert);
            x.addChild(z.getOffset(),i+1);
            writeNode(z,placeToInsert);
            writeNode(x,rootOffset);
            placeToInsert += BTreeNodeSize;
        }
        else{
            writeNode(y,y.getOffset());
            z.setOffset(placeToInsert);
            writeNode(z,placeToInsert);
            x.addChild(z.getOffset(),i+1);
            writeNode(x,x.getOffset());
            placeToInsert += BTreeNodeSize;
        }
    }
    public TreeObject search(BTreeNode x, long k){
        int i = 0;
        TreeObject obj = new TreeObject(k);
        while (i < x.getN() && (obj.compareTo(x.getKey(i)) > 0)){
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
            try {
                writeNodeMetadata(n,n.getOffset());
                raf.writeInt(n.getParent());
                for (i = 0; i < 2 * degree - 1; i++){
                    if (i < n.getN() + 1 && !n.isLeaf()){
                        raf.writeInt(n.getChild(i));
                    }
                    else if (i >= n.getN() + 1 || n.isLeaf()){
                        raf.writeInt(0);
                    }
                    if (i < n.getN()){
                        long data = n.getKey(i).getData();
                        raf.writeLong(data);
                        int frequency = n.getKey(i).getFrequency();
                        raf.writeInt(frequency);
                    }
                    else if (i >= n.getN() || n.isLeaf()){
                        raf.writeLong(0);
                    }
                }
                if (i == n.getN() && !n.isLeaf()){
                    raf.writeInt(n.getChild(i));
                }
            }
            catch (IOException ioe){
                System.err.println("IO Exception occurred!");
                System.exit(-1);
            }
    }
    public BTreeNode readNode(int offset){
        BTreeNode y = new BTreeNode();
        TreeObject obj = null;
        y.setOffset(offset);
        int k = 0;
        try {
            raf.seek(offset);
            boolean isLeaf = raf.readBoolean();
            y.setIsLeaf(isLeaf);
            int n = raf.readInt();
            y.setN(n);
            int parent = raf.readInt();
            y.setParent(parent);
            for (k = 0; k < 2 * degree - 1; k++){
                if (k < y.getN() + 1 && !y.isLeaf()){
                    int child = raf.readInt();
                    y.addChild(child);
                }
                else if (k >= y.getN() + 1 || y.isLeaf()){
                    raf.seek(raf.getFilePointer() + 4);
                }
                if (k < y.getN()){
                    long value = raf.readLong();
                    int frequency = raf.readInt();
                    obj = new TreeObject(value,frequency);
                    y.addKey(obj);
                }
            }
            if (k == y.getN() && !y.isLeaf()){
                int child = raf.readInt();
                y.addChild(child);
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
