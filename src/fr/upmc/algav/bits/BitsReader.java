package fr.upmc.algav.bits;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BitsReader {
	private FileInputStream fis;
	private BufferedInputStream bis;
	private DataInputStream dis;
	private byte currentBuffer;
	private short bufferPosition;
	
	public BitsReader(File file) {
		try {
			this.fis = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			System.out.println("The file does not exist.");
			System.exit(1);
		}
		this.bis = new BufferedInputStream(fis);
		this.dis = new DataInputStream(bis);
		this.bufferPosition = 0;
	}
	
	public byte readNextChar() throws EndException {
		if (this.bufferPosition == 0){
			try {
				this.currentBuffer = this.dis.readByte();
				return this.currentBuffer;
			} catch (EOFException e){
				throw new EndException();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		} else {
			// we retrieve the part we need in the current buffer
			byte returnValue = this.currentBuffer;
			returnValue = (byte)(returnValue << this.bufferPosition);
			
			// we get the next buffer
			try {
				this.currentBuffer = this.dis.readByte();
			} catch (EOFException e) {
				throw new EndException();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			// we retrieve the part we need in the new buffer
			byte secondPart = this.currentBuffer;
			secondPart = (byte)((secondPart & 0xff) >>> (8 - this.bufferPosition));
									
			// we merge both parts
			returnValue = (byte)(returnValue | secondPart);			
			return returnValue;
		}

		throw new EndException();
	}
	
	public byte readNextBit() throws EndException {
		byte returnValue = this.currentBuffer;
		if (this.bufferPosition == 0){
			try {
				this.currentBuffer = this.dis.readByte();
				returnValue = this.currentBuffer;
			} catch (EOFException e){
				throw new EndException();
			} catch (IOException e) {
				System.out.println("Something went wrong while reading some bits of the file...");
				System.exit(1);
			}
			returnValue = (byte)(((returnValue & 0xff) >>> (7-this.bufferPosition)) & 1);
		} else {
			returnValue = (byte)(((returnValue & 0xff) >>> (7-this.bufferPosition)) & 1);
		}
		this.bufferPosition = (short)((this.bufferPosition+1)%8);
		return returnValue;
	}
	
	public void close() throws IOException{
		this.dis.close();
		this.bis.close();
		this.fis.close();
	}
	
}
