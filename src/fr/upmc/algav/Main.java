package fr.upmc.algav;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.upmc.algav.bits.BitsReader;
import fr.upmc.algav.bits.BitsWriter;
import fr.upmc.algav.bits.EndException;
import fr.upmc.algav.huffman.HuffmanLeaf;
import fr.upmc.algav.huffman.HuffmanNode;
import fr.upmc.algav.huffman.HuffmanTree;

public class Main {

	public static void main(String[] args) {
		
		// execution time
		long startTime = System.currentTimeMillis();
		Runtime runtime = Runtime.getRuntime();
		int mb = 1024*1024;
		
		// we check if we have the correct number of arguments
		if (args.length != 2) {
			System.out.println("Arguments: <c/d> <file>");
			System.exit(1);
		}
				
		// if we want to compress 
		if (args[0].equals("compress") || args[0].equals("c")) {
			compress(args[1]);
		// if we want to decompress
		} else if (args[0].equals("decompress") || args[0].equals("d")) {
			decompress(args[1]);
		// if the user made a mistake in the first argument
		} else {
			System.out.println("Help: al <c/d> <file>");
			System.exit(1);
		}
		
		System.out.println("Done in: " + (System.currentTimeMillis() - startTime)+"ms");
		System.out.println("Allocated memory: "+(runtime.totalMemory() / mb)+"mb");
		System.out.println("Used memory: "+((runtime.totalMemory() - runtime.freeMemory()) / mb)+"mb");
		System.exit(0);
		
	}
	
	public static int compress(String f) {
		File file = new File(f);
		try {
			// we open the files
			BitsReader br = new BitsReader(file);
			BitsWriter bw = new BitsWriter(new File(f+".compressed"));
			
			// we create the root
			HuffmanNode tree = new HuffmanNode();
			tree.setHeight(0);
			tree.setWeight(0);
			// we create a hashmap referencing every leafs
			HashMap<Integer, HuffmanTree> hm = new HashMap<Integer, HuffmanTree>();
			HuffmanNode spec = tree;
			
			// we process the input file
			try {
				while (true) {
					byte ch = br.readNextChar();
		            if (hm.get((int)ch) == null) {
		            	// the letter isn't in the tree
		            	if (spec.isRoot()) {
		            		// # is root
		            		tree = spec.addLetter(ch, hm);
		            		bw.writeChar(ch);
		            	} else {
		            		// # is not root
		            		writeCodeOf(spec, bw);
		            		bw.writeChar(ch);
		            		HuffmanNode q = spec.addLetter(ch, hm);
		            		HuffmanTree.updateTree(tree, spec, q);
		            	}
		            } else {
		            	// letter is in the tree
		            	HuffmanLeaf l = (HuffmanLeaf) hm.get((int)ch);
		            	writeCodeOf(l, bw);
		            	HuffmanTree.updateTree(tree, spec, l);
		            }
		        }
			} catch (EndException e){}
			
			// we fill the last byte with the code of the # leaf
			writeEndOfByte(spec, bw);
			
			// ---- DEBUG ----
//			System.out.println("##################################");
//			System.out.print("Id: 0 -- Value: ");
//        	spec.printCode();
//        	System.out.print(" -- weight: "+spec.getWeight()+" -- height: "+spec.getHeight());
//        	System.out.println();
//            Iterator it = hm.entrySet().iterator();
//            while (it.hasNext()){
//            	Map.Entry pairs = (Map.Entry)it.next();
//            	byte k = ((Integer)pairs.getKey()).byteValue();
//            	HuffmanTree v = (HuffmanTree)pairs.getValue();
//            	System.out.print("Id:  -- Key: "+k+" -- Char: "+(char)k+" -- Value: ");
//            	v.printCode();
//            	System.out.print(" -- weight: "+v.getWeight()+" -- height: "+v.getHeight());
//            	System.out.println();
//            }
//            System.out.println("##################################");
			
			// we close the files
			br.close();
			bw.close();
		} catch (IOException e) {
			System.out.println("Error while trying to open the file!");
			System.exit(1);
		}
		return 0;
	}
	
	public static int decompress(String f) {
		File file = new File(f);
		try {
			// we open the files
			BitsReader br = new BitsReader(file);
			BitsWriter bw = new BitsWriter(new File(f+".uncompressed"));
			
			// we create the root
			HuffmanNode tree = new HuffmanNode();
			tree.setHeight(0);
			tree.setWeight(0);
			// we create a hashmap referencing every leafs
			HashMap<Integer, HuffmanTree> hm = new HashMap<Integer, HuffmanTree>();
			HuffmanNode spec = tree;
			
			// we process the file
			try {
				// we take care of the first char
				byte ch = br.readNextChar();
        		tree = spec.addLetter(ch, hm);
           		bw.writeChar(ch);
           		// we process the rest of the file
				while (true){
					HuffmanTree t = tree;
					while (!t.isLeaf() && t.getWeight() > 0){
						byte b = br.readNextBit();
						t = ((HuffmanNode)t).getByBit(b);
					}
					if (t == spec) {
						// the # leaf is found, then we read an ASCII char
						byte ch2 = br.readNextChar();
						HuffmanNode q = spec.addLetter(ch2, hm);
	            		HuffmanTree.updateTree(tree, spec, q);
						bw.writeChar(ch2);
					} else {
						// we write  the char matching the leaf found
						byte ch2 = ((HuffmanLeaf)t).getLetter();
		            	HuffmanTree.updateTree(tree, spec, t);
						bw.writeChar(ch2);
					}
				}
			} catch (EndException e) {}
			
			// ----- DEBUG ------
//			System.out.println("##################################");
//			System.out.print("Id: 0 -- Value: ");
//        	spec.printCode();
//        	System.out.print(" -- weight: "+spec.getWeight()+" -- height: "+spec.getHeight());
//        	System.out.println();
//            Iterator it = hm.entrySet().iterator();
//            while (it.hasNext()){
//            	Map.Entry pairs = (Map.Entry)it.next();
//            	byte k = ((Integer)pairs.getKey()).byteValue();
//            	HuffmanTree v = (HuffmanTree)pairs.getValue();
//            	System.out.print("Id:  -- Key: "+k+" -- Char: "+(char)k+" -- Value: ");
//            	v.printCode();
//            	System.out.print(" -- weight: "+v.getWeight()+" -- height: "+v.getHeight());
//            	System.out.println();
//            }
//            System.out.println("##################################");
			
			// we close the files
			br.close();
			bw.close();
			
		} catch (IOException e) {
			System.out.println("Error while trying to open the file!");
			System.exit(0);
		}
		return 0;
	}
	
	// writes the code of a leaf in the file
	public static boolean writeCodeOf(HuffmanTree l, BitsWriter bw){
		byte code[] = l.getCode();
		int i;
		for (i=0; i<l.getHeight(); i++) {
			bw.writeBit(code[i]);
		}
		return true;
	}
	
	// fill the buffer of the last byte with the code of the # leaf
	public static boolean writeEndOfByte(HuffmanNode spec, BitsWriter bw){
		byte code[] = spec.getCode();
		bw.fillLastByte(code);
		return true;
	}
}
