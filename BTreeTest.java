import java.util.Random;
public class BTreeTest{
    private static Random numGen;
    public static void main(String[] args){
        BTree mbt = new BTree(2,"test");
        numGen = new Random();
        for (int i = 1; i < 11; i++){
            mbt.insert(i);
            mbt.inOrderPrint(mbt.getRoot());
            System.out.println();
            mbt.insert(i);
            mbt.inOrderPrint(mbt.getRoot());
            System.out.println();
        }
        for (int i = 1; i < 11; i++) {
        TreeObject a = mbt.search(mbt.getRoot(),i);
        if (a == null || a.compareTo(new TreeObject(i)) != 0)
            System.err.println("search failed!");
        }
        mbt = new BTree(2,"test");
        for (int i = 1; i < 11; i++){
            int num = numGen.nextInt(15);
            mbt.insert(num);
            mbt.inOrderPrint(mbt.getRoot());
            System.out.println();
            mbt.insert(num);
            mbt.inOrderPrint(mbt.getRoot());
            System.out.println();
        }
    }
}
