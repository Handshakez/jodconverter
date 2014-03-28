package com.mydocket.pdfmaker;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
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
		StringBuffer value = new StringBuffer();
		int next = -1;
		boolean okay = true;
		while (okay) {
			next = input.read();
			if (next == -1 || next == newline) {
				okay = false;
			} else {
				value.append((char) next);
			}
		}
		return value.toString();
	}
	
	
}
