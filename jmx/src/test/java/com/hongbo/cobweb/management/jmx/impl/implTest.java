package com.hongbo.cobweb.management.jmx.impl;

import junit.framework.Assert;

import org.apache.karaf.management.mbeans.bundles.BundlesMBean;
import org.apache.karaf.management.mbeans.config.ConfigMBean;
import org.apache.karaf.management.mbeans.dev.DevMBean;
import org.apache.karaf.management.mbeans.log.LogMBean;
import org.apache.karaf.management.mbeans.packages.PackagesMBean;
import org.apache.karaf.management.mbeans.services.ServicesMBean;
import org.apache.karaf.management.mbeans.system.SystemMBean;
import org.junit.Test;

public class implTest {

	@Test
	public void mapTest() {
		String value = KarafManager.maps.get(DevMBean.class);
		Assert.assertEquals(value, "dev");
		
		value = KarafManager.maps.get(BundlesMBean.class);
		Assert.assertEquals(value, "bundles");
		
		value = KarafManager.maps.get(LogMBean.class);
		Assert.assertEquals(value, "log");
		
		value = KarafManager.maps.get(ConfigMBean.class);
		Assert.assertEquals(value, "config");
		
		value = KarafManager.maps.get(SystemMBean.class);
		Assert.assertEquals(value, "system");
		
		value = KarafManager.maps.get(ServicesMBean.class);
		Assert.assertEquals(value, "services");
		
		value = KarafManager.maps.get(PackagesMBean.class);
		Assert.assertEquals(value, "packages");
		
	}
}
