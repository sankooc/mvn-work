package com.hongbo.common.process.bsd64;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;

import com.hongbo.common.process.INetwork;
import com.hongbo.common.process.IProcess;
import com.hongbo.common.process.IProcessManager;

public class BSD64ProcessManager implements IProcessManager {

	String username;
	DefaultExecutor excuter = new DefaultExecutor();
	public BSD64ProcessManager(){
//		excuter.getStreamHandler().;
//		BufferedReader reader = new BufferedReader(new InputStreamReader());
	}
	
	public Collection<IProcess> getCurrentProcess() {
		CommandLine command = new CommandLine("ls");
		ExecuteStreamHandler streamHandler = new PumpStreamHandler();
		excuter.setStreamHandler(streamHandler);
//		excuter.getProcessDestroyer().
	    try {
			excuter.execute(command);
			
		} catch (ExecuteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<IProcess> getProcess(String processName) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<INetwork> getNetworks(IProcess process) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUsername() {
		return username;
	}

}
