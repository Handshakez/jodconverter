package com.mydocket.pdfserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * If you get an error that you can't find 'convert', make sure to add /usr/local/bin to the environment where
 * you're running the test.
 * 
 * @author aarvesen
 *
 */
@RunWith(JUnit4.class)
public class TestServer {
		
	private int port = 7002;
	private Server server;
	private static OfficeManager manager;
	
	@BeforeClass
	public static void startManager() throws Exception {
		// load our properties
		File fProps = findFile("test.properties");
		Properties props = new Properties();
		FileInputStream fis = new FileInputStream(fProps);
		props.load(fis);
		fis.close();
		
		System.out.println("Path:" + System.getProperty("java.library.path"));
		
		// now that's out of the way....
		
		DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
		configuration.setPortNumber(7007);
		configuration.setOfficeHome((String) props.getProperty("office.home", null));
		manager = configuration.buildOfficeManager();
		manager.start();
	}
	
	@AfterClass
	public static void stopManager() throws Exception {
		if (manager != null) {
			manager.stop();
		}
	}
	
	
	
	@Rule
	public ExternalResource start_server = new ExternalResource() {
		private Thread thread;
		
		@Override
		protected void before() throws Throwable {
			server = new Server(port, manager);
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
			thread =  new Thread(r);
			thread.start();
			while (! server.running) {
				sleep("staring");
			}
		}
		
		@SuppressWarnings("deprecation")
		@Override
		protected void after() {
			thread.stop();
			while (server.running) {
				sleep("stopping");
			}
		}
		
		private void sleep(String action) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				fail("Exception while " + action + " server: " + e.getMessage());
			}
		}
	};
	
	
	@Test
	public void testServer() throws Exception {
		assertTrue("This is true", true);
	}
	
	@Test
	public void testHello() throws Exception {
		
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
	}
	
	// this annoyance since running from inside of Eclipse has a different path
	// than running from mvn command line.  Bah.
	private static File findFile(String fname) {
		String[] paths = {
			"pdfserver/src/test/resources/",
			"src/test/resources/",
		};
		
		File found = null;
		for (String p : paths) {
			File f = new File(p + fname);
			if (f.exists()) {
				found = f;
				break;
			}
		}
		return found;
	}
	
	
	@Test
	public void testFileUpload() throws Exception {
		String fname = "Handshakez Salesforce API.docx";
		File docx = findFile(fname);
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
		
		assertTrue("Thumbnail failed to read", c.readSizedContent(new FileOutputStream("/tmp/thumbnail.png")));
		assertTrue("Preview failed to read", c.readSizedContent(new FileOutputStream("/tmp/converted.pdf")));
		
		
//		c.sendLine("STOP");
//		msg = c.readLine();
//		System.out.println(msg);
//		assertEquals("Goodbye.", msg);
		c.close();
	}
	
}
