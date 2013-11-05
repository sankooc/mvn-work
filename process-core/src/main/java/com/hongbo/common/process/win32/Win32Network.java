package com.hongbo.common.process.win32;

import com.hongbo.common.process.INetwork;

/**
 * @author sankooc
 * 
 *         2012-8-31
 */
public class Win32Network implements INetwork {
	String protocal;
	String host;
	String target;
	String status;
	int pid;

	public String getProtocal() {
		return protocal;
	}

	public void setProtocal(String protocal) {
		this.protocal = protocal;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getPort() {
		if(null == host){
			return 0;
		}
		int index = host.lastIndexOf(":");
		if(-1 == index){
			return 0;
		}
		String portStr = host.substring(index+1);
		return Integer.parseInt(portStr);
	}

}
