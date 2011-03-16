package org.jboss.dmr.client;

import java.io.IOException;

public class DataInput {
	private int pos = 0;
	private byte[] bytes;
	
	public DataInput(byte[] bytes) {
		this.bytes = bytes;
	}

	public int skipBytes(int n) throws IOException {
		return pos += n;
	}

	public boolean readBoolean() throws IOException {
		return bytes[pos++] == 1;
	}

	public byte readByte() throws IOException {
		return bytes[pos++];
	}

	public int readUnsignedByte() throws IOException {
		return readByte() & 0xFF;
	}

	public short readShort() throws IOException {
		return (short)(bytes[pos++] << 8 | bytes[pos++]);
	}

	public int readUnsignedShort() throws IOException {
		return bytes[pos++] << 8 | bytes[pos++];
	}

	public char readChar() throws IOException {
		return (char)(bytes[pos++] << 8 | bytes[pos++]);
	}

	public int readInt() throws IOException {
		return bytes[pos++] << 24 | bytes[pos++] << 16 | bytes[pos++] << 8 | bytes[pos++];
	}

	public long readLong() throws IOException {
		return (long)(bytes[pos++] << 56) | 
		       (long)(bytes[pos++] << 48) | 
		       (long)(bytes[pos++] << 40) | 
		       (long)(bytes[pos++] << 32) |
		              bytes[pos++] << 24  |
		              bytes[pos++] << 16  |
		              bytes[pos++] << 8   |
		              bytes[pos++];
		              
	}

	public float readFloat() throws IOException {
		// TODO Auto-generated method stub
		readInt();
		return 0;
	}

	public double readDouble() throws IOException {
		// TODO Auto-generated method stub
		readLong();
		return 0;
	}

	public String readLine() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public String readUTF() throws IOException {
		int len = readUnsignedShort();
		String string = new String(bytes, pos, len, "UTF-8");
		pos += len;
		return string;
	}

	public void readFully(byte[] b) {
		for (int i = 0; i < b.length; i++) {
			b[i] = bytes[pos++];
		}
	}

}
