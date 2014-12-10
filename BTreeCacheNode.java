
public class BTreeCacheNode {
	private BTreeNode data;
	private int offset;
	
	public BTreeCacheNode(BTreeNode data, int offset) {
		this.data = data;
		this.offset = offset;
	}
	
	public BTreeNode getData() {
		return data;
	}
	
	public int getOffset() {
		return offset;
	}
}
