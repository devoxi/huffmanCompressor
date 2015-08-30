package fr.upmc.algav.huffman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class HuffmanTree implements Cloneable {
	
	private int height;
	private int weight;
	private HuffmanNode parent;
	private HuffmanTree next;
	private HuffmanTree prev;
	
	// constructors
	public HuffmanTree() {

	}

	// miscellanous functions
	public abstract boolean isLeaf();
	
	public boolean isRoot() {
		if (this.height == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public byte[] getCode(){
		HuffmanTree t = this;
		byte code[] = new byte[t.getHeight()];
		int i = t.getHeight() - 1;
		do {
			HuffmanTree s = t;
			t = t.getParent();
			if (s == ((HuffmanNode)t).getGauche()) {
				code[i] = 0;
			} else {
				code[i] = 1;
			}
			i--;
		} while (!t.isRoot());
		return code;
	}
	
	public int getCodeForOrder(){
		HuffmanTree t = this;
		int code = 0;
		// use a long instead of an int for i if the tree gets too big
		int i = 0;
		do {
			HuffmanTree s = t;
			t = t.getParent();
			if (s == ((HuffmanNode)t).getDroit()) {
				code = (int) (code + Math.pow(2, i));
			}
			i++;
		} while (!t.isRoot());
		return code;
	}
	
	// TODO: used only for debug
	public void printCode(){
		HuffmanTree t = this;
		byte code[] = new byte[t.getHeight()];
		int i = t.getHeight() - 1;
		do {
			HuffmanTree s = t;
			t = t.getParent();
			if (s == ((HuffmanNode)t).getGauche()) {
				code[i] = 0;
			} else {
				code[i] = 1;
			}
			i--;
		} while (!t.isRoot());
		for (i=0; i<this.getHeight(); i++) {
			System.out.print(code[i]);
		}
	}
	
	// getters & setters
	public int getHeight(){
		return this.height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public HuffmanNode getParent() {
		if (this.height == 0) {
			return null;
		} else {
			return parent;
		}
	}

	public void setParent(HuffmanNode parent) {
		this.parent = parent;
	}
	
	public HuffmanTree getNext() {
		return next;
	}

	public void setNext(HuffmanTree next) {
		this.next = next;
	}

	public HuffmanTree getPrev() {
		return prev;
	}

	public void setPrev(HuffmanTree prev) {
		this.prev = prev;
	}
	
	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public static boolean updateTree(HuffmanTree root, HuffmanTree spec,HuffmanTree q) {
		HuffmanTree t = canBeIncremented(q);
		if (t == root) {
			// the whole path can be incremented
			q.setWeight(q.getWeight() + 1);
			if (q.isRoot()) return true;
			do {
				q = q.getParent();
				q.setWeight(q.getWeight() + 1);
			} while(!q.isRoot());
			root.setWeight(root.getWeight() + 1);
			return true;
		} else {
			// not the whole path can be incremented
			if (q.getPrev().getWeight() == 0 && t == q.getParent()) {
				 // Q & # are brothers
				 HuffmanTree bEnd = getBlockEnd(q);
				 switchSubTrees(q, bEnd);
				 q.setWeight(q.getWeight() + 1);
				 // rebuild GDBH
				 if (q.getNext() == bEnd || q.getPrev() == bEnd) {
					 if (bEnd.isLeaf() || q.isLeaf()) {
						 // nothing
					 } else {
						 rebuildGDBH(root, spec);
					 }
				 } else if (q.isLeaf() && bEnd.isLeaf()) {
					 // nothing
				 } else {
					 rebuildGDBH(root, spec);
				 }
				 return updateTree(root, spec,q.getParent());
			} else {
				HuffmanTree b = getBlockEnd(t);
				while (q != t){
					q.setWeight(q.getWeight() + 1);
					q = q.getParent();
				}
				q.setWeight(q.getWeight() + 1);
				switchSubTrees(q, b);
				// rebuild GDBH
				 if (q.getNext() == b || q.getPrev() == b) {
					 if (b.isLeaf() || q.isLeaf()) {
						 // nothing
					 } else {
						 rebuildGDBH(root, spec);
					 }
				 } else if (q.isLeaf() && b.isLeaf()) {
					 // nothing
				 } else {
					 rebuildGDBH(root, spec);
				 }
				return updateTree(root, spec, q.getParent());
			}
		}
	}
	
	public static HuffmanTree canBeIncremented(HuffmanTree q) {
		HuffmanTree t = q;
		if (t.isRoot())
			return t;
		do {
			// - if the next is root, then we can increment the whole path
			// - if the weight is smaller, then we can increment
		    // - if the previous tree is the # leaf, then we need to return the father
			if (!t.getNext().isRoot()) {
				if (t.getWeight() == t.getNext().getWeight())
					if (t.getPrev().getWeight() > 0)
						return t;
			}
			t = t.getParent();
		} while (!t.isRoot());
		return t;
	}
	
	public static void switchSubTrees(HuffmanTree a, HuffmanTree b) {
		// Step 1: we exchange prev/next between a and b
		HuffmanTree tmp;
		try {
			tmp = (HuffmanTree) a.clone();
			if (a.getNext() == b) {
				// in case b follows a
				a.setNext(b.getNext());
				b.setPrev(a.getPrev());
				a.setPrev(b);
				b.setNext(a);
			} else if (b.getNext() == a) {
				// in case a follows b
				b.setNext(a.getNext());
				a.setPrev(b.getPrev());
				b.setPrev(a);
				a.setNext(b);
			} else {
				// if a & b are not following each other
				a.setNext(b.getNext());
				a.setPrev(b.getPrev());
				b.setNext(tmp.getNext());
				b.setPrev(tmp.getPrev());
			}
			
			// Step 2: we updates next of prev
			a.getPrev().setNext(a);
			b.getPrev().setNext(b);
			
			// Step 3: we updates prev of next
			a.getNext().setPrev(a);
			b.getNext().setPrev(b);
			
			// step 4: we exchange fathers
			a.setParent(b.getParent());
			b.setParent(tmp.getParent());
			
			// step 5: we update the children's fathers
			if (a.getParent().getGauche() == b) {
				a.getParent().setGauche(a);
			} else {
				a.getParent().setDroit(a);
			}	
			if (b.getParent().getGauche() == a) {
				b.getParent().setGauche(b);
			} else {
				b.getParent().setDroit(b);
			}
			
			// step 6: if different height we updates height and their childen's height
			if (a.getHeight() != b.getHeight()){
				a.setHeight(b.getHeight());
				b.setHeight(tmp.getHeight());
				updateDescendingHeight(a);
				updateDescendingHeight(b);
			}
		} catch (CloneNotSupportedException e) {
			System.out.println("ERROR -- HuffmanTree > switchSubTrees : cloneable");
			System.exit(1);
		}
	}
	
	public static boolean updateDescendingHeight(HuffmanTree t){
		if (t.getWeight() == 0 || t.isLeaf()) { return true; }
		HuffmanTree tg = ((HuffmanNode)t).getGauche();
		HuffmanTree td = ((HuffmanNode)t).getDroit();
		td.setHeight(t.getHeight() + 1);
		tg.setHeight(t.getHeight() + 1);
		if (!td.isLeaf()){
			updateDescendingHeight(td);
		}
		if (!tg.isLeaf()){
			updateDescendingHeight(tg);
		}
		return true;
	}
	
	public static HuffmanTree getBlockEnd(HuffmanTree t){
		while (t.getNext().getWeight() == t.getWeight()){
			t = t.getNext();
		}
		return t;
	}
	
	public static boolean rebuildGDBH(HuffmanTree root, HuffmanTree spec){
		// ------- we split nodes by height
		// we create a hashmap referencing all nodes & leafs following their height
		HashMap<Integer, ArrayList<HuffmanTree>> hh = new HashMap<Integer, ArrayList<HuffmanTree>>();	
		addChildsToMap(root, hh);
		
		// ---- we order the nodes of the same height & update their next/prev
		HuffmanTree lastOne = null;
		for (int i=spec.getHeight(); i > 0; i--) {
			ArrayList<HuffmanTree> al = hh.get(i);
			HashMap<Integer, Integer> h2 = new HashMap<Integer, Integer>();
			int[] ar = new int[al.size()];
			for (int j=0; j<al.size(); j++){
				HuffmanTree t = al.get(j);
				h2.put(t.getCodeForOrder(), j);
				ar[j] = t.getCodeForOrder();
			}
			Arrays.sort(ar);
			for (int j=0; j<al.size(); j++){
				HuffmanTree current = al.get(h2.get(ar[j]));
				HuffmanTree next;
				HuffmanTree prev;
				if (j != (al.size()-1)) { next = al.get(h2.get(ar[j+1])); } else { next = null; }
				if (j == 0) { prev = null; } else { prev = al.get(h2.get(ar[j-1])); }
				
				if (i == spec.getHeight() && j == 0){
					current.setNext(next);
				} else if (j == 0) {
					current.setNext(next);
					current.setPrev(lastOne);
					lastOne.setNext(current);
				} else if (j == (al.size() - 1)){
					current.setPrev(prev);
					lastOne = al.get(h2.get(ar[j]));
				} else {
					current.setNext(next);
					current.setPrev(prev);
				}
			}
		}
		root.setPrev(lastOne);
		return true;
	}
	
	public static void addChildsToMap(HuffmanTree node, HashMap<Integer, ArrayList<HuffmanTree>> hh){
		if (!node.isLeaf() && node.getWeight() != 0) {
			ArrayList<HuffmanTree> al;
			if ((al = hh.get(node.getHeight() + 1)) != null){
				al.add(((HuffmanNode)node).getGauche());
				al.add(((HuffmanNode)node).getDroit());
			} else {
				al = new ArrayList<HuffmanTree>();
				hh.put(node.getHeight() + 1, al);
				al.add(((HuffmanNode)node).getGauche());
				al.add(((HuffmanNode)node).getDroit());
			}
			addChildsToMap(((HuffmanNode)node).getDroit(), hh);
			addChildsToMap(((HuffmanNode)node).getGauche(), hh);
		}
	}
}
