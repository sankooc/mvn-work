/**
 * 
 */
package com.hongbo.cobweb.management.jmx;

import java.util.Collection;

import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularData;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hongbo.cobweb.store.entity.sd.ServiceDomain;

/**
 * @author sankooc
 * 
 */
public class ManagerTest {

	static IKarafManageService service;

	@BeforeClass
	public static void setup() throws Exception {
		service = KarafManagerFactory.createManagerService(null, 1099,
				"default", "karaf", "karaf");
		Assert.assertNotNull(service);
	}

	// @Test
	public void testFactory() {
		try {
			String frameworkStr = service.framework();
			Assert.assertNotNull(frameworkStr);
			System.out.println(frameworkStr);
			// service.restart(true);
			service.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testSDmanager(){
		try {
			Collection<ServiceDomain> lists = service.getServiceDomains();
			
			for(ServiceDomain domain : lists){
				System.out.println(domain.getId());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
//	@Test
	public void testServices() {
		try {
			TabularData datas = service.serviceList();
			Collection<?> lists = datas.values();
			for (Object key : lists) {
				CompositeDataSupport data = (CompositeDataSupport)key;
				String[] intefaces = (String[])data.get("Interfaces");
				String[] properties = (String[])data.get("Properties");
				if(null != properties && properties.length >0){
					for(String str : properties){
						System.out.println(str);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
