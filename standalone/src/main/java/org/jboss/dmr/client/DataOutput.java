package org.jboss.dmr.client;

import java.io.IOException;

public class DataOutput {
	
	private byte[] bytes;
	private int pos;
	
	public DataOutput() {
		bytes = new byte[50];
	}
	
	public String getEncoded() {
		return Base64.encodeBytes(bytes, 0, pos);
	}
	
	public byte[] getBytes() {
		byte[] array = new byte[pos];
		for (int i = 0; i < pos; i++)
			array[i] = bytes[i];
		
		return array;
	}
	
	private void growToFit(int size) {
		if (pos + size >= bytes.length) {
			byte[] array = new byte[bytes.length + 50];
			for (int i = 0; i < bytes.length; i++) {
				array[i] = bytes[i];
			}
			
			bytes = array;
		}
	}
	
	public void writeBoolean(boolean v) throws IOException {
		growToFit(1);
		bytes[pos++] = v ? (byte)1 : (byte)0;
	}

	public void writeByte(int v) throws IOException {
		growToFit(1);
		bytes[pos++] = (byte)v;

	}

	public void writeShort(int v) throws IOException {
		growToFit(2);
		bytes[pos++] = (byte)(v >>> 8);
		bytes[pos++] = (byte)(v & 0xFF);

	}

	public void writeChar(int v) throws IOException {
		growToFit(2);
		bytes[pos++] = (byte)(v >>> 8);
		bytes[pos++] = (byte)(v & 0xFF);
	}

	public void writeInt(int v) throws IOException {
		growToFit(4);
		bytes[pos++] = (byte) (v >>> 24);
		bytes[pos++] = (byte)((v >>> 16) & 0xFF);
		bytes[pos++] = (byte)((v >>> 8) & 0xFF);
		bytes[pos++] = (byte) (v & 0xFF);
	}

	public void writeLong(long v) throws IOException {
		growToFit(8);
		bytes[pos++] = (byte) (v >>> 56);
		bytes[pos++] = (byte)((v >>> 48) & 0xFF);
		bytes[pos++] = (byte)((v >>> 40) & 0xFF);
		bytes[pos++] = (byte)((v >>> 32) & 0xFF);
		bytes[pos++] = (byte)((v >>> 24) & 0xFF);
		bytes[pos++] = (byte)((v >>> 16) & 0xFF);
		bytes[pos++] = (byte)((v >>> 8) & 0xFF);
		bytes[pos++] = (byte) (v & 0xFF);
	}

	public void writeFloat(float v) throws IOException {
		write(IEEE754.fromFloat(v));
	}

	public void writeDouble(double v) throws IOException {
		write(IEEE754.fromDouble(v));
	}

	public void writeUTF(String s) throws IOException {
		byte[] encoded = s.getBytes("UTF-8");
		writeShort(encoded.length);
		write(encoded);
	}

	public void write(byte[] bits) {
		growToFit(bits.length);
		for (int i = 0; i < bits.length; i++)
			bytes[pos++] = bits[i];
	}
}
