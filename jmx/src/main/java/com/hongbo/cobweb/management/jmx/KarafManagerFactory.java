/**
 * 
 */
package com.hongbo.cobweb.management.jmx;

import java.util.HashMap;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.hongbo.cobweb.management.jmx.impl.KarafManager;

/**
 * @author sankooc
 * 
 */
public class KarafManagerFactory {

	public static IKarafManageService createManagerService() {
		return null;
	}

	public static IKarafManageService createManagerService(String host,
			int port, String karafName, String username, String password)
			throws Exception {
		if (null == host) {
			host = "localhost";
		}
		if (null == karafName) {
			karafName = "root";
		}
		HashMap<String, String[]> environment = new HashMap<String, String[]>();
		if (null != username && !username.isEmpty()) {
			String[] credentials = new String[] { username, password };
			environment.put("jmx.remote.credentials", credentials);
		}
		String jmxUr = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port
				+ "/karaf-" + karafName;
		JMXServiceURL url = new JMXServiceURL(jmxUr);
		JMXConnector jmxc = JMXConnectorFactory.connect(url, environment);
		MBeanServerConnection connect = jmxc.getMBeanServerConnection();

		return new KarafManager(jmxc, connect, karafName);
	}
}
