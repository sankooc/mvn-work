package com.hongbo.common.process;

import com.hongbo.common.process.win32.Win32ProcessManager;

public class ProcessManagerFactory {

	public enum OpType {
		WIN32, LINUX32, FREEBSD64;
	}

	public static IProcessManager createProccessFactory() {
		String osName = System.getProperty("os.name");
		String arch = System.getProperty("os.arch");
		String username = System.getProperty("user.name");
		OpType type = null;
		if (osName.startsWith("Windows") && arch.equalsIgnoreCase("x86")) {
			type = OpType.WIN32;
		}else if("Mac OS X".equalsIgnoreCase(osName) && "x86_64".equalsIgnoreCase(arch)){
			type = OpType.FREEBSD64;
		}

		switch (type) {
		case WIN32 : 
			Win32ProcessManager manager = new Win32ProcessManager();
			manager.setUsername(username);
			return manager;
		case FREEBSD64:
			
		default:
			throw new RuntimeException("unsupported optype");
		}
	}

}
