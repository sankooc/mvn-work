package com.hongbo.common.process.win32;

import junit.framework.Assert;

import org.junit.Test;

import com.hongbo.common.process.win32.Win32ProcessManager;

public class Win32Test {
	
	@Test
	public void intTest(){
		Win32ProcessManager manager = new Win32ProcessManager();
		int value = manager.getIntValue("1,22,12");
		Assert.assertEquals(value, 12212);
	}
}
