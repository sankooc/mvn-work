package com.hongbo.common.process.win32;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.mutable.MutableInt;

import com.hongbo.common.process.INetwork;
import com.hongbo.common.process.IProcess;
import com.hongbo.common.process.IProcessManager;

/**
 * @author sankooc
 * 
 *         2012-8-31
 */
public class Win32ProcessManager implements IProcessManager {

	String username;

	final static String taskCommand = "tasklist /V";
	final static String netCommand="netstat -ano";
	static final List<Integer> netLens = new LinkedList<Integer>();
	static {
		netLens.add(7);
		netLens.add(23);
		netLens.add(23);
		netLens.add(16);
		netLens.add(10);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hongbo.common.process.IProcessManager#getCurrentProcess()
	 */
	public Collection<IProcess> getCurrentProcess() {
		return getProcess(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.common.process.IProcessManager#getProcess(java.lang.String)
	 */
	public Collection<IProcess> getProcess(String processName) {
		try {
			BufferedReader reader = getReader(taskCommand);
			List<Integer> len = getHeaderWidth(reader);
			Collection<IProcess> lists = new LinkedList<IProcess>();
			while (true) {
				String line = reader.readLine();
				if (null == line) {
					break;
				}
				if (null == processName || line.startsWith(processName)) {
					Win32Process proc = wrapProcess(line, len);
					lists.add(proc);
				}
			}
			return lists;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	BufferedReader getReader(String command) throws IOException{
		Process process = Runtime.getRuntime().exec(command);
		InputStream stream = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				stream,"UTF-8"));
		return reader;
	}
	
	List<Integer> getHeaderWidth(BufferedReader reader) throws IOException {
		reader.readLine();
		reader.readLine();
		String header = reader.readLine();
		String[] ss = header.split(" ");
		List<Integer> len = new LinkedList<Integer>();
		for (String s : ss) {
			len.add(s.length()+1);
		}
		return len;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.common.process.IProcessManager#getNetworks(com.hongbo.common
	 * .process.IProcess)
	 */
	public Collection<INetwork> getNetworks(IProcess process) {
		int pid = process.getPid();
		return getNetworks(pid);
	}

	public Collection<INetwork> getNetworks(int pid) {
		try {
			BufferedReader reader = getReader(netCommand);
			reader.readLine();
			reader.readLine();
			reader.readLine();
			reader.readLine();
			Collection<INetwork> networks = new LinkedList<INetwork>();
			while(true){
				String line = reader.readLine();
				if(null == line){
					break;
				}
				if(line.trim().endsWith(""+pid)){
					Win32Network network = getNetwork(line);
					networks.add(network);
				}
			}
			return networks;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private Win32Network getNetwork(String line) {
		Iterator<Integer> it = netLens.iterator();
		MutableInt start = new MutableInt(2);
		Win32Network netWork = new Win32Network();
		netWork.setProtocal(getNextStr(line, start, it));
		netWork.setHost(getNextStr(line, start, it));
		netWork.setTarget(getNextStr(line, start, it));
		netWork.setStatus(getNextStr(line, start, it));
		netWork.setPid(getIntValue(line, start, it));
		return netWork;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hongbo.common.process.IProcessManager#getUsername()
	 */
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	private String getNextStr(String str, MutableInt start, Iterator<Integer> it) {
		int length = str.length();
		if (start.intValue() >= length || start.intValue() < 0) {
			return null;
		}
		if (!it.hasNext()) {
			return null;
		}
		int end = start.intValue() + it.next();
		if (end > length) {
			end = length;
		}
		String ret = str.substring(start.intValue(), end);
		start.setValue(end);
		return ret.trim();
	}

	private int getIntValue(String str, MutableInt start, Iterator<Integer> it) {
		String value = getNextStr(str, start, it);
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return 0;
		}
	}

	private long getLongValue(String str, MutableInt start, Iterator<Integer> it) {
		String value = getNextStr(str, start, it);
		if (value.endsWith("K") || value.endsWith("k")) {
			long intValue = getIntValue(value.substring(0, value.length() - 1)
					.trim());
			return intValue * 1024;
		} else {
			return getIntValue(value);
		}
	}

	int getIntValue(String str) {
		String ns = str.replaceAll(",", "");
		return Integer.parseInt(ns);
	}

	private Win32Process wrapProcess(String line, List<Integer> len) {
		Win32Process process = new Win32Process();
		Iterator<Integer> it = len.iterator();
		MutableInt start = new MutableInt(0);
		process.setName(getNextStr(line, start, it));
		process.setPid(getIntValue(line, start, it));
		process.setSessionName(getNextStr(line, start, it));
		process.setSessionSym(getNextStr(line, start, it));
		process.setMemory(getLongValue(line, start, it));
		process.setStatus(getNextStr(line, start, it));
		process.setUser(getNextStr(line, start, it));
		process.setCpuTime(getNextStr(line, start, it));
		process.setWindowTitle(getNextStr(line, start, it));
		return process;
	}

}
