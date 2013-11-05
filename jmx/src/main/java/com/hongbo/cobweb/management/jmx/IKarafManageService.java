package com.hongbo.cobweb.management.jmx;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.management.openmbean.TabularData;

import com.hongbo.cobweb.store.entity.sd.ServiceDomain;

/**
 * 
 */

/**
 * @author sankooc
 *
 */
public interface IKarafManageService {
	
	//system & dev
	 /**
     * Get the current OSGi framework in use.
     *
     * @return the name of the OSGi framework in use.
     * @throws Exception
     */
    String framework() throws Exception;

    /**
     * OSGi framework options.
     *
     * @param debug enable debug of the OSGi framework to use.
     * @param framework name of the OSGI framework to use.
     * @throws Exception
     */
    void frameworkOptions(boolean debug, String framework) throws Exception;

    /**
     * Restart Karaf, with eventually a cleanup.
     *
     * @param clean if true, Karaf is cleanup, false else.
     * @throws Exception
     */
    void restart(boolean clean) throws Exception;
    
    void shutdown() throws Exception;
    
	
	//log
	void set(String level) throws Exception;
    void set(String level, String logger) throws Exception;

    String get() throws Exception;
    String get(String logger) throws Exception;
    
    //config
    /**
     * Get the list of all configuration PIDs.
     *
     * @return the list of all configuration PIDs.
     * @throws Exception
     */
    List<String> list() throws Exception;

    /**
     * Create a new configuration for the given PID.
     *
     * @param pid the configuration PID.
     * @throws Exception
     */
    void create(String pid) throws Exception;

    /**
     * Delete a configuration identified by the given PID.
     *
     * @param pid the configuration PID to delete.
     * @throws Exception
     */
    void delete(String pid) throws Exception;

    /**
     * Get the list of properties for a configuration PID.
     *
     * @param pid the configuration PID.
     * @return the list of properties.
     * @throws Exception
     */
    Map<String, String> proplist(String pid) throws Exception;

    /**
     * Remove the configuration property identified by the given key.
     *
     * @param pid the configuration PID.
     * @param key the property key.
     * @throws Exception
     */
    void propdel(String pid, String key) throws Exception;

    /**
     * Append (or add) a value for the given configuration key.
     *
     * @param pid the configuration PID.
     * @param key the property key.
     * @param value the value to append to the current property value.
     * @throws Exception
     */
    void propappend(String pid, String key, String value) throws Exception;

    /**
     * Set a configuration property.
     *
     * @param pid the configuration PID.
     * @param key the property key.
     * @param value the property value.
     * @throws Exception
     */
    void propset(String pid, String key, String value) throws Exception;
    
    //services
    TabularData serviceList() throws Exception;
    TabularData serviceList(boolean inUse) throws Exception;
    TabularData serviceList(long bundleId) throws Exception;
    TabularData serviceList(long bundleId, boolean inUse) throws Exception;
    
    //packages
    List<String> exportedPackages() throws Exception;
    List<String> exportedPackages(long bundleId) throws Exception;
    List<String> importedPackages() throws Exception;
    List<String> importedPackages(long bundleId) throws Exception;
    
    
    Collection<ServiceDomain> getServiceDomains() throws Exception;
    
}
