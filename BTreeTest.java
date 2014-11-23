public class BTreeTest{

    public static void main(String[] args){
        BTree mbt = new BTree(2);
        mbt.insert(1);
        mbt.inOrderPrint(mbt.getRoot());
        mbt.insert(1);
        mbt.inOrderPrint(mbt.getRoot());
        mbt.insert(2);
        mbt.inOrderPrint(mbt.getRoot());
        mbt.insert(2);
        mbt.inOrderPrint(mbt.getRoot());
        mbt.insert(3);
        mbt.inOrderPrint(mbt.getRoot());
        mbt.insert(3);
        mbt.inOrderPrint(mbt.getRoot());
        mbt.insert(4);
        mbt.inOrderPrint(mbt.getRoot());
        mbt.insert(4);
        mbt.inOrderPrint(mbt.getRoot());
        //mbt.insert(5);
        //mbt.inOrderPrint(mbt.getRoot());
     //   mbt.insert(5);
        //mbt.inOrderPrint(mbt.getRoot());
        //mbt.insert(6);
       // mbt.inOrderPrint(mbt.getRoot());
       // mbt.insert(6);
       // mbt.inOrderPrint(mbt.getRoot());
       // mbt.insert(7);
       // mbt.inOrderPrint(mbt.getRoot());
        //mbt.insert(7);
        //mbt.inOrderPrint(mbt.getRoot());
       // mbt.insert(8);
        //mbt.inOrderPrint(mbt.getRoot());
        //mbt.insert(8);
//        mbt.inOrderPrint(mbt.getRoot());
  //      mbt.insert(9);
        //mbt.inOrderPrint(mbt.getRoot());
        //mbt.insert(9);
    //    mbt.inOrderPrint(mbt.getRoot());
      //  mbt.insert(10);
        //mbt.inOrderPrint(mbt.getRoot());
        //mbt.insert(10);
          //mbt.inOrderPrint(mbt.getRoot());
          TreeObject a = mbt.search(mbt.getRoot(),1);
          TreeObject b = mbt.search(mbt.getRoot(),2);
          TreeObject c = mbt.search(mbt.getRoot(),3);
          TreeObject d = mbt.search(mbt.getRoot(),4);
       // TreeObject e = mbt.search(mbt.getRoot(),5);
 //       TreeObject f = mbt.search(mbt.getRoot(),6);
   //     TreeObject g = mbt.search(mbt.getRoot(),7);
     //   TreeObject h = mbt.search(mbt.getRoot(),8);
       // TreeObject i = mbt.search(mbt.getRoot(),9);
   //     TreeObject j = mbt.search(mbt.getRoot(),10);
     //   if (a.compareTo(new TreeObject(1)) != 0)
       //     System.err.println("search failed!");
  //      if (b.compareTo(new TreeObject(2)) != 0)
    //        System.err.println("search failed!");
      //  if (c.compareTo(new TreeObject(3)) != 0)
        //    System.err.println("search failed!");
 //       if (d.compareTo(new TreeObject(4)) != 0)
   //         System.err.println("search failed!");
     //   if (e.compareTo(new TreeObject(5)) != 0)
       //     System.err.println("search failed!");
//        if (f.compareTo(new TreeObject(6)) != 0)
  //          System.err.println("search failed!");
     //   if (g.compareTo(new TreeObject(7)) != 0)
    //        System.err.println("search failed!");
      //  if (h.compareTo(new TreeObject(8)) != 0)
        //    System.err.println("search failed!");
//        if (i.compareTo(new TreeObject(9)) != 0)
  //          System.err.println("search failed!");
    //    if (j.compareTo(new TreeObject(10)) != 0)
      //      System.err.println("search failed!");
    }
}
