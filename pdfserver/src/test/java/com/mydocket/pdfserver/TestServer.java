package com.mydocket.pdfserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.mydocket.pdfserver.Server;

@RunWith(JUnit4.class)
public class TestServer {

	private int port = 7002;
	
//	@Rule
//	public ExternalResource start_server = new ExternalResource() {
//		@Override
//		protected void before() throws Throwable {
//			
//		}
//		
//		@Override
//		protected void after() {
//			
//		}
//	};
	
	
	@Test
	public void testServer() throws Exception {
		assertTrue("This is true", true);
	}
	
	@Test
	public void testHello() throws Exception {
		final Server server = new Server(port, null);
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					server.run();
				} catch (Exception e) {
					fail("Exception while running server " + e.getMessage());
				}
			}
			
		};
		Thread t = new Thread(r);
		t.start();
		
		while (! server.running) {
			Thread.sleep(100);
		}
		
		String msg = null;
		Client c = new Client(port);
		c.sendLine("HELO");
		msg = c.readLine();
		System.out.println(msg);
		assertEquals("Hi.", msg);
		
		
		c.sendLine("STOP");
		msg = c.readLine();
		System.out.println(msg);
		assertEquals("Goodbye.", msg);
		c.close();
		
		System.out.println("Running? " + server.running);
		// TODO: this sucks, right?
		t.stop();
		while (server.running) {
			Thread.sleep(100);
		}
		System.out.println("Running? " + server.running);
	}
	
	@Test
	public void testFileUpload() throws Exception {
		final Server server = new Server(port, null);
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					server.run();
				} catch (Exception e) {
					fail("Exception while running server " + e.getMessage());
				}
			}
			
		};
		Thread t = new Thread(r);
		t.start();
		
		while (! server.running) {
			Thread.sleep(100);
		}
		
		String fname = "Handshakez Salesforce API.docx";
		File docx = new File("src/test/resources/" + fname);
		long length = docx.length();
		
		String msg = null;
		Client c = new Client(port);
		c.sendLine("PUSH");
		c.sendLine(fname);
		c.sendLine("" + length);
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(docx);
			byte[] buffer = new byte[4096];
			int size = 0;
			while ((size = fis.read(buffer)) != -1) {
				c.output.write(buffer,0, size);
			}
			c.output.flush();
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
		
		msg = c.readLine();
		System.out.println(msg);
		
		c.sendLine("STOP");
		msg = c.readLine();
		System.out.println(msg);
		assertEquals("Goodbye.", msg);
		c.close();
		
		System.out.println("Running? " + server.running);
		// TODO: this sucks, right?
		t.stop();
		while (server.running) {
			Thread.sleep(100);
		}
		System.out.println("Running? " + server.running);
	}
	
}
