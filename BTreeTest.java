public class BTreeTest{

    public static void main(String[] args){
        BTree mbt = new BTree(2);
        for (int i = 1; i < 11; i++){
            mbt.insert(i);
            mbt.inOrderPrint(mbt.getRoot());
            mbt.insert(i);
            mbt.inOrderPrint(mbt.getRoot());
        }
        for (int i = 1; i < 11; i++) {
        TreeObject a = mbt.search(mbt.getRoot(),i);
        if (a.compareTo(new TreeObject(i)) != 0)
            System.err.println("search failed!");
        }
    }
}
