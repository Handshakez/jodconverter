package com.mydocket.pdfserver;

import org.artofsolving.jodconverter.office.OfficeManager;

import com.mydocket.pdfserver.Console.HarmlessException;
import com.mydocket.pdfserver.Console.QuietException;

// This class is to run the server via jsvc process
// http://commons.apache.org/proper/commons-daemon/jsvc.html
public class Daemon {

	private String[] args = null;
	private OfficeManager manager;
	private Server server;
	
	
	public void init(String[] args) {
		//not much.  Save the args for the start method.
		this.args = args;
	}
	
	public void start() throws Exception {
		Console c = new Console();
		c.execute(this.args);
//		} catch (HarmlessException e) {
//			// nothing
////			if (!( e instanceof HarmlessException)) {
////				if (! (e instanceof QuietException)) {
////					e.printStackTrace();
////				}
////			}
//		}
	}
	
	protected void execute(OfficeManager manager, int serverPort) throws Exception {
		this.manager = manager;
		this.server  = new Server(serverPort, manager);
		this.server.run();
	}
	
	
	void stop() throws Exception {
		this.manager.stop();
		this.server.stop();
	}
	
	void destroy() {
		// nothing?
	}
}
