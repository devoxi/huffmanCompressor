package fr.upmc.algav.bits;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitsWriter {
	private FileOutputStream fos;
	private BufferedOutputStream bos;
	private DataOutputStream dos;
	private byte buffer;
	private int bufferPosition;
	
	public BitsWriter(File file) {
		try {
			this.fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			System.out.println("Something went wrong with the output file...");
			System.exit(1);
		}
		this.bos = new BufferedOutputStream(fos);
		this.dos = new DataOutputStream(bos);
		this.buffer = 0;
		this.bufferPosition = 0;
	}
	
	public boolean writeChar(byte c) {
		if (bufferPosition == 0){
			try {
				this.dos.writeByte((int) c);
			} catch (IOException e) {
				System.out.println("An error occured when writing the output file...");
				System.exit(1);
			}
		} else {
			byte writeValue = (byte)(this.buffer | ((c & 0xff) >>> this.bufferPosition));
			try {
				this.dos.write((int)writeValue);
			} catch (IOException e) {
				System.out.println("An error occured when writing the output file...");
				System.exit(1);
			}
			this.buffer = (byte)(c << (8 - this.bufferPosition));
		}
		return true;
	}
	
	public boolean writeBit(byte b) {
		this.buffer = (byte)(this.buffer | (b << (7-this.bufferPosition)));
		if ((++this.bufferPosition) == 8){
			try {
				this.dos.writeByte((int)this.buffer);
				this.bufferPosition = 0;
				this.buffer = 0;
			} catch (IOException e) {
				System.out.println("An error occured when writing the output file...");
				System.exit(1);
			}
		}
		return true;
	}
	
	public boolean fillLastByte(byte code[]){
		int i, j;
		if (this.bufferPosition == 0) return true;
		j = 0;
		int max = this.bufferPosition;
		for (i=0; i<(8-max); i++){
			this.writeBit(code[j]);
			j = (j+1)%code.length;
		}
		return true;
	}
	
	
	public void close() throws IOException{
		this.dos.close();
		this.bos.close();
		this.fos.close();
	}
	
}
