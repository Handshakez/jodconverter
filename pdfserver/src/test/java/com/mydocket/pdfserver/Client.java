package com.mydocket.pdfserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

	private static final int newline = 13;
	private Socket socket = null;
	
	public OutputStream output;
	public InputStream input;
	
	public Client(){
	}
	public Client(int port) throws Exception {
		this.open(port);
	}
	
	public void open(int port) throws Exception {
		if (socket != null) {
			this.close();
		}
		
		socket = new Socket("localhost", port);
		
		// we write to the 'output' stream of the socket
		// and read from the 'input' stream
		// I find this quite confusing
		output = socket.getOutputStream();
		input  = socket.getInputStream();
	}
	
	public void close() throws Exception {
		// java 1.6 doesn't implement closeable for socket :\
		//Closeable[] cs = new Closeable[] {output, input, socket};
		output.close();
		input.close();
		socket.close();
	}
	
	public void sendLine(String msg) throws Exception {
		PrintWriter pw = null;
		pw = new PrintWriter(output);
		pw.print(msg);
		pw.write(newline);
		pw.flush();
	}

	
	public String readLine() throws Exception {
		return readSome(true);
	}
	public String readSome(boolean considerNewline) throws Exception {
		StringBuffer value = new StringBuffer();
		int next = -1;
		boolean reading = true;
		while (reading) {
			next = input.read();
			if (next == -1 || (considerNewline && (next == newline))) {
				reading = false;
			} else {
				value.append((char) next);
			}
		}
		return value.toString();
	}
	
	public String readRest() throws Exception {
		return readSome(false);
	}
	
	public void readSizedContent(OutputStream stream) throws Exception {
		String sSize = readLine();
		long size    = Long.parseLong(sSize);
		
		// TODO: more efficient?
		for (long i = 0; i < size; i++) {
			stream.write(input.read());
		}
		stream.close();
	}
	
	
}
