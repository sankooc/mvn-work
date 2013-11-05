package com.hongbo.common.process.win32;

import com.hongbo.common.process.IProcess;

/**
 * @author sankooc
 *
 * 2012-8-31
 */
public class Win32Process implements IProcess {
	private String name;
	private int pid;
	private String sessionName;
	private String sessionSym;
	private long memory;
	private String status;
	private String user;
	private String cpuTime;
	private String windowTitle;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public String getSessionSym() {
		return sessionSym;
	}
	public void setSessionSym(String sessionSym) {
		this.sessionSym = sessionSym;
	}
	public long getMemory() {
		return memory;
	}
	public void setMemory(long memory) {
		this.memory = memory;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getCpuTime() {
		return cpuTime;
	}
	public void setCpuTime(String cpuTime) {
		this.cpuTime = cpuTime;
	}
	public String getWindowTitle() {
		return windowTitle;
	}
	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}
	
	
	
}
