package fr.upmc.algav.huffman;

import java.util.HashMap;

public class HuffmanNode extends HuffmanTree {

	private HuffmanTree droit;
	private HuffmanTree gauche;
	
	// constructors
	public HuffmanNode(){
		super();
	}
	
	// Miscellanous functions
	public boolean isLeaf() {
		return false;
	}
	
	// getters & setters
	public HuffmanTree getDroit() {
		return droit;
	}

	public void setDroit(HuffmanTree droit) {
		this.droit = droit;
	}

	public HuffmanTree getGauche() {
		return gauche;
	}

	public void setGauche(HuffmanTree gauche) {
		this.gauche = gauche;
	}
	
	public HuffmanTree getByBit(byte b){
		if (b == 1) {
			return this.droit;
		} else {
			return this.gauche;
		}
	}
	
	public HuffmanNode addLetter(byte c, HashMap<Integer, HuffmanTree> hm) {
		if (this.getWeight() != 0) {
			System.out.println("Something went wrong...");
			System.exit(1);
		}
		HuffmanNode q = new HuffmanNode();
		if (this.isRoot()) {
			// ---- root case ----
			// we take care of the new node and the new leaf
			q.setHeight(0);
			q.setWeight(1);
			q.setGauche(this);
			q.setDroit(new HuffmanLeaf(q, 1, c, this, q));
			q.setPrev(q.getDroit());
			hm.put((int) c, q.getDroit());
			// we update #
			this.setNext(q.getDroit());
			this.setParent(q);
			this.setHeight(1);
			return q;
		} else {
			// ---- not root case ---
			// we take care of the new node and the new leaf
			q.setHeight(this.getHeight());
			q.setWeight(1);
			q.setParent(this.getParent());
			q.setGauche(this);
			q.setDroit(new HuffmanLeaf(q, this.getHeight() + 1, c, this, q));
			q.setNext(this.getNext());
			q.getNext().setPrev(q);
			q.setPrev(q.getDroit());
			hm.put((int) c, q.getDroit());
			// we update the father of q now
			q.getParent().setGauche(q);
			// we update #
			this.setNext(q.getDroit());
			this.setParent(q);
			this.setHeight(this.getHeight() + 1);
			return q.getParent();
		}
	}
}
