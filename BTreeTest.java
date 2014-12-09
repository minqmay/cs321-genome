import java.util.Random;
public class BTreeTest{
    private static Random numGen;
    public static void main(String[] args){
        BTree mbt = new BTree(128,"test1",true,100);
        numGen = new Random();
        for (int i = 1; i < 1000; i++){
            mbt.insert(i);
            mbt.inOrderPrint(mbt.getRoot());
            System.out.println();
            mbt.insert(i);
            mbt.inOrderPrint(mbt.getRoot());
            System.out.println();
        }
        for (int i = 1; i < 1000; i++) {
        TreeObject a = mbt.search(mbt.getRoot(),i);
        if (a == null || a.compareTo(new TreeObject(i)) != 0)
            System.err.println("search failed!");
        }
        long[] keys = new long[100];
        mbt = new BTree(2,"test2",true,100);
        for (int i = 0; i < 100; i++){
            int num = numGen.nextInt(15);
            mbt.insert(num);
            keys[i] = num;
            mbt.inOrderPrint(mbt.getRoot());
            System.out.println();
            mbt.insert(num);
            mbt.inOrderPrint(mbt.getRoot());
            System.out.println();
        }
        for (int i = 0; i < 100; i++) {
        
        TreeObject a = mbt.search(mbt.getRoot(),keys[i]);
        if (a == null || a.compareTo(new TreeObject(keys[i])) != 0)
            System.err.println("search failed!");
        }
    }
}
