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
        writeTreeMetaData();
    }
    public void insert(long k){
        BTreeNode r = root;
        int i = r.getN();
        if (i == (2 * degree - 1)){
            TreeObject obj = new TreeObject(k);
            while (i >= 1 && obj.compareTo(r.getKey(i-1)) < 0){
                i--;
            }
            if (obj.compareTo(r.getKey(i-1)) == 0)
                r.getKey(i-1).increaseFrequency();
            else {
                BTreeNode s = new BTreeNode();
                s.setOffset(r.getOffset());
                root = s;
                r.setOffset(placeToInsert);
                r.setParent(s.getOffset());
                s.setIsLeaf(false);
                System.out.println(r.isLeaf());
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
                while (i >= 1 && obj.compareTo(x.getKey(i-1)) < 0){
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
            try {
                raf.writeBoolean(x.isLeaf());
                raf.writeInt(x.getN());
                if (x != root)
                    raf.writeInt(x.getParent());
                else
                    raf.skipBytes(8);
                for (i = 0; i < x.getN(); i++){
                    long data = x.getKey(i).getData();
                    raf.writeLong(data);
                    int frequency = x.getKey(i).getFrequency();
                    raf.writeInt(frequency);
                    raf.skipBytes(4);
                }
            }
            catch (IOException ioe){
                System.err.println("IO Exception occurred!");
                System.exit(-1);
            }
            //disk_write(x);
        }
        else {
            while (i >= 1 && (obj.compareTo(x.getKey(i-1)) < 0)){
                i--;
            }
            if (i > 0 && obj.compareTo(x.getKey(i-1)) == 0){
                x.getKey(i-1).increaseFrequency();
            }
            //disk_read(getChild(i);
            System.out.println(x + " " + x.isLeaf());
            int offset = x.getChild(i);
            BTreeNode y = readNode(offset);
            if (y.getN() == 2 * degree - 1){
                i = x.getN();
                while (i >= 1 && obj.compareTo(x.getKey(i-1)) < 0){
                    i--;
                }
                if (i > 0 && obj.compareTo(x.getKey(i-1)) == 0){
                    x.getKey(i-1).increaseFrequency();
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
        x.addChild(z.getOffset(), i+1);
        x.addKey(y.removeKey(degree - 1), i);
        x.setN(x.getN()+1);
        y.setN(y.getN()-1);
        if (x == root){
            writeNode(x,rootOffset);
            writeNode(y,placeToInsert);
            placeToInsert += BTreeNodeSize;
            writeNode(z,placeToInsert);
            placeToInsert += BTreeNodeSize;
        }
        else{
            writeNode(x,x.getOffset());
            writeNode(y,y.getOffset());
            writeNode(z,placeToInsert);
            placeToInsert += BTreeNodeSize;
        }
        //disk_write(y);
        //disk_write(z);
        //disk_write(x);
    }
    public TreeObject search(BTreeNode x, long k){
        int i = 0;
        TreeObject obj = new TreeObject(k);
        System.out.println("searching for: " + obj);
        System.out.println(x.getKeys());
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
            System.out.println("y: " + y);
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
                raf.seek(offset);
                raf.writeBoolean(n.isLeaf());
                raf.writeInt(n.getN());
                raf.writeInt(n.getParent());
                for (i = 0; i < n.getN(); i++){
                    if (!n.isLeaf()){
                        raf.writeInt(n.getChild(i));
                    }
                    else
                        raf.skipBytes(4);
                    long data = n.getKey(i).getData();
                    raf.writeLong(data);
                    int frequency = n.getKey(i).getFrequency();
                    raf.writeInt(frequency);
                }
                if (!n.isLeaf())
                    raf.writeInt(n.getChild(i));
            }
            catch (IOException ioe){
                System.err.println("IO Exception occurred!");
                System.exit(-1);
            }
    }
    public BTreeNode readNode(int offset){
        BTreeNode y = new BTreeNode();
        TreeObject obj = null;
        try {
            raf.seek(offset);
            y.setIsLeaf(raf.readBoolean());
            y.setN(raf.readInt());
            int n = y.getN();
            y.setParent(raf.readInt());
            for (int k = 0; k < y.getN(); k++){
                y.addChild(raf.readInt());
                long value = raf.readLong();
                int frequency = raf.readInt();
                obj = new TreeObject(value,frequency);
                y.addKey(obj); 
            }
            y.addChild(raf.readInt());
        }
        catch (IOException ioe){
            System.err.println(ioe.getMessage());
            System.exit(-1);
        }
        return y;
    }
    public void writeTreeMetaData(){ 
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
}
