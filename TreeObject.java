public class TreeObject implements Comparable<TreeObject>{
    private int frequency;
    private long data;

    public TreeObject(long data, int frequency){
        this.data = data;
        this.frequency = frequency;
    }
    public TreeObject(long data){
        this.data = data;
        this.frequency = 1;
    }
    public void increaseFrequency(){
        this.frequency++;
    }
    public int getFrequency(){
        return frequency;
    }
    public long getData(){
        return data;
    }
    public int compareTo(TreeObject that){
        if (this.data < that.data)
            return -1;
        else if (this.data > that.data)
            return 1;
        else
            return 0;
    }
    public String toString(){
        String s = new String();
        s += "key: " + data + " frequency: " + frequency;
        return s;
    }
    public String toDNAString(){
    	return "key: " + longToSequence(data) + " frequency: " + frequency;
    }
    private String longToSequence(long l) {
    	StringBuilder s = new StringBuilder();
    	for (int i = 62; i >= 0; i-=2) {
    		switch ((int)((l>>i) & 0b11)) {
    			case (int)GeneBankCreateBTree.CODE_A:
    				s.append('a');
    				break;
    			case (int)GeneBankCreateBTree.CODE_C:
    				s.append('c');
    				break;
    			case (int)GeneBankCreateBTree.CODE_T:
    				s.append('t');
    				break;
    			case (int)GeneBankCreateBTree.CODE_G:
    				s.append('g');
    				break;
    		}
    	}
    	return s.toString();
    }
}
