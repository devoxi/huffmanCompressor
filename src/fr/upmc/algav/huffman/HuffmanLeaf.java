package fr.upmc.algav.huffman;

public class HuffmanLeaf extends HuffmanTree {

	private byte letter;
	
	// constructors
	public HuffmanLeaf(){
		super();
	}
	
	public HuffmanLeaf(HuffmanNode p, int h, byte c, HuffmanTree prev, HuffmanTree next){
		super();
		this.setHeight(h);
		this.setLetter(c);
		this.setParent(p);
		this.setPrev(prev);
		this.setNext(next);
		this.setWeight(1);
	}
	
	// miscellanous functions
	public boolean isLeaf() {
		return true;
	}	
	
	// getters & setters
	public byte getLetter() {
		return letter;
	}

	public void setLetter(byte letter) {
		this.letter = letter;
	}
}
