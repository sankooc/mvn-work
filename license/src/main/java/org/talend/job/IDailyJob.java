package org.talend.job;

public interface IDailyJob {
//	String TEMP_FOLDER = "d:/temp/";
//	String BUILD_URL = "http://192.168.30.4/builds/";
//	String BUILD_RELEASE = "^V(\\d.\\d.\\d)NB_(r\\d+)/$";
//
//	// V5.4.0NB_r108102/all/V5.4.0NB/all_540/Talend-Studio-r108102-V5.4.0NB.zip
//	String STUDIO_FOLDER = "studio/";
//	String STUDIO_URL_FORMAT = "V%sNB_%s/all/V%sNB/all_%s/Talend-Studio-%s-V%sNB.zip";
//	String STUDIO_FILE_FORMAT = "Talend-Studio-%s-V%sNB.zip";
//
//	String MDMSERVER_FOLDER = "mdmserver";
//	String MDMSERVICE_URL_FORMAT = "V%sNB_%s/tmdmee/V%sNB/tmdmee_%s_ee_mpx/Talend-MDMServer-%s-V%sNB.jar";
//
//	String MDMSERVICE_FILE_FORMAT ="Talend-MDMServer-%s-V%sNB";
	
	void getLastVersion();

//	void getSpecalVersion(String mVersion, String bVersion) throws Exception;
	
}
