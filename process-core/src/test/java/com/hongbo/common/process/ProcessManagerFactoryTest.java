package com.hongbo.common.process;

import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author sankooc
 * 
 *         2012-8-31
 */
public class ProcessManagerFactoryTest {

	@Test
	public void common() {
		IProcessManager manager = ProcessManagerFactory.createProccessFactory();
		Assert.assertNotNull(manager);
		
		Collection<IProcess> processes = manager.getCurrentProcess();
		Assert.assertNotNull(processes);
		Assert.assertFalse(processes.isEmpty());
		
		processes = manager.getProcess("java.exe");
		Assert.assertNotNull(processes);
		Assert.assertFalse(processes.isEmpty());
		
		Collection<INetwork> networks = manager.getNetworks(processes.iterator().next());
		Assert.assertNotNull(networks);
		Assert.assertFalse(networks.isEmpty());
		for(INetwork net : networks){
			System.out.println(net.getPort());
		}
		
	}

	
//	public void intTest(){
//		
//	}
}
