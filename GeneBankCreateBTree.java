import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Create a BTree from a gbk file.
 * 
 * @author andrew
 *
 */
public class GeneBankCreateBTree {
	
	public static final int CODE_A = 0b00;
	public static final int CODE_T = 0b11;
	public static final int CODE_C = 0b01;
	public static final int CODE_G = 0b10;
	
	public static final int MAX_SEQUENCE_LENGTH = 31;
	public static final int MAX_DEBUG_LEVEL = 1;
	
	public static final int DEFAULT_DEGREE = 80; // TODO: determine optimal degree

	public static void main(String[] args) throws IOException {
		if (args.length < 4) {
			badUsage();
		}
		
		// <cache>
		boolean useCache = false;
		
		try {
			int c = Integer.parseInt(args[0]);
			if (c == 0) useCache = false;
			else if (c == 1) useCache = true;
			else badUsage();
		} catch (NumberFormatException e) {
			badUsage();
		}
		
		// <degree>
		int BTreeDegree = 0;
		
		try {
			int deg = Integer.parseInt(args[1]);
			if (deg < 0) badUsage();
			else if (deg == 0) BTreeDegree = getOptimalDegree();
			else BTreeDegree = deg;
		} catch (NumberFormatException e) {
			badUsage();
		}
		int sequenceLength = 0;
		
		// <sequence length>
		try {
			int len = Integer.parseInt(args[3]);
			if (len < 1 || len > MAX_SEQUENCE_LENGTH) badUsage();
			else sequenceLength = len;
		} catch (NumberFormatException e) {
			badUsage();
		}
		
		// <gbk file>
		File gbk = new File(args[2]);

		// XXX: there's got to be a better class for this.
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(gbk));
		} catch (FileNotFoundException e) {
			System.err.println("File not found: "+gbk.getPath());
		}
		
		BTree tree = new BTree(BTreeDegree,"test");
		
		String line = null;
		line = in.readLine();
		boolean inSequence = false;
		int sequencePosition = 0;
		int charPosition = 0;
		long sequence = 0L;
		while (line != null) { // tried to optimize this, mostly made it ugly
			if (inSequence) {
				if (line.startsWith("//")) { // marks the end of the sequence
					inSequence = false;
				} else {
					try {
						while (sequencePosition < sequenceLength) {
							char c = line.charAt(charPosition++);
                            System.out.println("c: " + c);
							switch (c) {
							case 'a':
								sequence = ((sequence<<2) | CODE_A);
								sequencePosition++;
								break;
							case 't':
								sequence = ((sequence<<2) | CODE_T);
								sequencePosition++;
								break;
							case 'c':
								sequence = ((sequence<<2) | CODE_C);
								sequencePosition++;
								break;
							case 'g':
								sequence = ((sequence<<2) | CODE_G);
								sequencePosition++;
								break;
							default: // space or number, not part of sequence
								break;
							}
						}
						// "reverse normalize" the sequence; start at the leftmost bit
						//sequence = sequence<<(64-(sequenceLength<<1));
						/* Since we have a max length of 31 we could encode the
						 * length of the sequence in the long itself, e.g. you
						 * could do this:
						 * - if last two bits are 11, 10, or 01, sequence length
						 *   is 31, 30, or 29
						 * - if last two bits are 00, sequence length is encoded
						 *   in the previous 6 bits (which we know are free
						 *   because the length is shorter than 29)
						 *   
						 * In the context of this assignment there is not really
						 * any reason to do that since all the sequences in a
						 * tree are the same length - and bit manipulation on
						 * longs in Java is crappy performance-wise.
						 */
						tree.insert(sequence);
						// TODO: write to disk.
						sequencePosition = 0;
						
						
					} catch (IndexOutOfBoundsException e) {
						charPosition = 0;
						// just read the next line
						// (this is supposed to be an optimization)
					}
				}
			} else if (line.startsWith("ORIGIN")) { // marks the beginning of a sequence
				inSequence = true;
			}
			// not bothering with the rest of the fields for now
			line = in.readLine();
		}
		
		in.close();
	}
	
	private static void badUsage() {
		System.err.println("Usage: java GeneBankCreateBTree <cache> <degree> <gbk file> <sequence length> [<cache size>] [<debuglevel>]");
		System.err.println("<cache>: 1 to use cache, 0 for no cache");
		System.err.println("<degree>: degree of the BTree (0 for default)");
		System.err.println("<gbk file>: file with sequence data");
		System.err.println("<sequence length>: length of a sequence (1-31)");
		System.err.println("[<cache size>]: if cache enabled, size of cache");
		System.err.println("[<debug level>]: debugging level (0-1)");
		System.exit(1);
    }
	public static int getOptimalDegree(){
        double optimum;
        int sizeOfPointer = 4;
        int sizeOfObject = 12;
        int sizeOfMetadata = 5;
        double diskBlockSize = optimum = 4096;
        optimum += sizeOfObject;
        optimum -= sizeOfPointer;
        optimum -= sizeOfMetadata;
        optimum /= (2 * (sizeOfObject + sizeOfPointer));
        return (int) Math.floor(optimum);
    }
}


