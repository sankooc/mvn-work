/**
 * 
 */
package com.hongbo.cobweb.management.jmx.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.TabularData;
import javax.management.remote.JMXConnector;

import org.apache.karaf.management.mbeans.bundles.BundlesMBean;
import org.apache.karaf.management.mbeans.config.ConfigMBean;
import org.apache.karaf.management.mbeans.dev.DevMBean;
import org.apache.karaf.management.mbeans.log.LogMBean;
import org.apache.karaf.management.mbeans.packages.PackagesMBean;
import org.apache.karaf.management.mbeans.services.ServicesMBean;
import org.apache.karaf.management.mbeans.system.SystemMBean;

import com.hongbo.cobweb.management.admin.ServiceDomainAdminMBean;
import com.hongbo.cobweb.management.jmx.IKarafManageService;
import com.hongbo.cobweb.store.entity.sd.ServiceDomain;

/**
 * @author sankooc
 * 
 */
public class KarafManager implements IKarafManageService {

	private final JMXConnector connector;
	private final MBeanServerConnection connection;
	private final String karafName;
	final static Map<Class<?>, String> maps = new HashMap<Class<?>, String>();
	static {
		maps.put(DevMBean.class, "dev");
		maps.put(BundlesMBean.class, "bundles");
		maps.put(LogMBean.class, "log");
		maps.put(ConfigMBean.class, "config");
		maps.put(SystemMBean.class, "system");
		maps.put(ServicesMBean.class, "services");
		maps.put(PackagesMBean.class, "packages");
	}

	public KarafManager(JMXConnector jmxc, MBeanServerConnection connection,
			String name) {
		this.connector = jmxc;
		this.connection = connection;
		this.karafName = name;
	}

	public KarafManager(JMXConnector jmxc, String karafName) throws IOException {
		this(jmxc, jmxc.getMBeanServerConnection(), karafName);
	}

	<T> T getProxy(ObjectName name, Class<T> clz) {
		T t = javax.management.JMX.newMBeanProxy(connection, name, clz, false);
		return t;
	}

	<T> T getProxy(String name, Class<T> clz) {
		try {
			ObjectName on = new ObjectName(name);
			return getProxy(on, clz);
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	<T> T getKarafProxy(Class<T> clz) {
		String clzName = clz.getSimpleName();
		if (clzName.endsWith("MBean")) {
			String name = clzName.substring(0, clzName.length() - 5)
					.toLowerCase();
			return getKarafProxy(name, clz);
		} else {
			return getKarafProxy(maps.get(clz), clz);
		}
	}

	<T> T getKarafProxy(String type, Class<T> clz) {
		ObjectName name;
		try {
			name = getBeanName(type);
			return getProxy(name, clz);
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	ObjectName getBeanName(String type) throws MalformedObjectNameException,
			NullPointerException {
		String str = "org.apache.karaf:type=" + type + ",name=" + karafName;
		return new ObjectName(str);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hongbo.cobweb.management.jmx.IKarafManageService#framework()
	 */
	public String framework() throws Exception {
		DevMBean bean = getKarafProxy(DevMBean.class);
		return bean.framework();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#frameworkOptions
	 * (boolean, java.lang.String)
	 */
	public void frameworkOptions(boolean debug, String framework)
			throws Exception {
		DevMBean bean = getKarafProxy(DevMBean.class);
		bean.frameworkOptions(debug, framework);
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#restart(boolean)
	 */
	public void restart(boolean clean) throws Exception {
		DevMBean bean = getKarafProxy(DevMBean.class);
		bean.restart(clean);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hongbo.cobweb.management.jmx.IKarafManageService#shutdown()
	 */
	public void shutdown() throws Exception {
		SystemMBean bean = getKarafProxy(SystemMBean.class);
		assert (null != bean);
		bean.shutdown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#set(java.lang.String
	 * )
	 */
	public void set(String level) throws Exception {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#set(java.lang.String
	 * , java.lang.String)
	 */
	public void set(String level, String logger) throws Exception {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hongbo.cobweb.management.jmx.IKarafManageService#get()
	 */
	public String get() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#get(java.lang.String
	 * )
	 */
	public String get(String logger) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hongbo.cobweb.management.jmx.IKarafManageService#list()
	 */
	public List<String> list() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#create(java.lang
	 * .String)
	 */
	public void create(String pid) throws Exception {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#delete(java.lang
	 * .String)
	 */
	public void delete(String pid) throws Exception {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#proplist(java.lang
	 * .String)
	 */
	public Map<String, String> proplist(String pid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#propdel(java.lang
	 * .String, java.lang.String)
	 */
	public void propdel(String pid, String key) throws Exception {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#propappend(java.
	 * lang.String, java.lang.String, java.lang.String)
	 */
	public void propappend(String pid, String key, String value)
			throws Exception {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#propset(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	public void propset(String pid, String key, String value) throws Exception {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hongbo.cobweb.management.jmx.IKarafManageService#serviceList()
	 */
	public TabularData serviceList() throws Exception {
		ServicesMBean bean = getKarafProxy(ServicesMBean.class);
		assert(bean != null);
		return bean.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#serviceList(boolean)
	 */
	public TabularData serviceList(boolean inUse) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#serviceList(long)
	 */
	public TabularData serviceList(long bundleId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#serviceList(long,
	 * boolean)
	 */
	public TabularData serviceList(long bundleId, boolean inUse)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#exportedPackages()
	 */
	public List<String> exportedPackages() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#exportedPackages
	 * (long)
	 */
	public List<String> exportedPackages(long bundleId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#importedPackages()
	 */
	public List<String> importedPackages() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hongbo.cobweb.management.jmx.IKarafManageService#importedPackages
	 * (long)
	 */
	public List<String> importedPackages(long bundleId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public JMXConnector getConnector() {
		return connector;
	}

	public MBeanServerConnection getConnection() {
		return connection;
	}

	public Collection<ServiceDomain> getServiceDomains() throws Exception {
		ServiceDomainAdminMBean bean = getKarafProxy(ServiceDomainAdminMBean.class);
		assert(null != bean);
		return bean.getServiceDomains();
	}

}
