package com.hongbo.common.process;

import java.util.Collection;

/**
 * @author sankooc
 *
 * 2012-8-31
 */
public interface IProcessManager {
	
	Collection<IProcess> getCurrentProcess();
	
	Collection<IProcess> getProcess(String processName);
	
	Collection<INetwork> getNetworks(IProcess process);
	
	String getUsername();
	
}
