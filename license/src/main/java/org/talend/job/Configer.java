package org.talend.job;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configer {
	static XPathFactory xfactory = javax.xml.xpath.XPathFactory.newInstance();
	static XPath xpath = xfactory.newXPath();
	static Logger logger = LoggerFactory.getLogger(Configer.class);

	static String TEMP_FOLDER = System.getProperty("os.name").startsWith("Windows") ? "d:/temp/" : System.getProperty("user.home")
			+ File.separator + "talend";
	static String BUILD_URL = "http://192.168.30.4/builds/";
	static String BUILD_RELEASE = "^V(\\d.\\d.\\d)NB_(r\\d+)/$";

	// V5.4.0NB_r108102/all/V5.4.0NB/all_540/Talend-Studio-r108102-V5.4.0NB.zip
	static String STUDIO_FOLDER = "studio/";
	static String STUDIO_URL_FORMAT = "V%sNB_%s/all/V%sNB/all_%s/Talend-Studio-%s-V%sNB.zip";
	static String STUDIO_FILE_FORMAT = "Talend-Studio-%s-V%sNB.zip";

	// mdm
	static String MDMSERVER_FOLDER = "mdmserver/";
	static String MDMSERVER_URL_FORMAT = "V%sNB_%s/tmdmee/V%sNB/tmdmee_%s_ee_mpx/Talend-MDMServer-%s-V%sNB.jar";
	static String MDMSERVER_FILE_FORMAT = "Talend-MDMServer-%s-V%sNB";

	// license
	static String LICENSE_URL_FORMAT = "GENERATED_%s_LICENSES/";
	static String LICENSE_ITEM_FORMAT = "licenses_%s_\\d+days_\\d+users_20(\\d+)_to_20(\\d+).zip";
	static String LICENSE_FILE_FORMAT = "(CLOUD_EE_MPX_\\d+users_20\\d+_%version.license)|(CLOUD_BPM_EE_MPX_\\d+users_20\\d+_%version.license)";

	static org.w3c.dom.Document loadDocument() {
		return null;// TODO
	}

	static DocumentBuilder builder;

	static org.w3c.dom.Document loadDocument(InputStream stream) {
		try {
			if (null == builder) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				builder = factory.newDocumentBuilder();
			}
			return builder.parse(stream);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	static String getStringValue(String path, String dv) {
		org.w3c.dom.Document doc = loadDocument();
		if (null != doc) {
			try {
				XPathExpression x_path = xpath.compile(path);
				String value = (String) x_path.evaluate(doc, XPathConstants.STRING);
				if (StringUtils.isEmpty(value)) {
					return dv;
				}
				return value;
			} catch (XPathExpressionException e) {
				logger.error(e.getMessage());
			}
		}
		return dv;
	}

	public static String getDataFolder() {
		return getStringValue("/root/@folder", TEMP_FOLDER);
	}

	public static String getBuildURL() {
		return getStringValue("/root/@url", BUILD_URL);
	}

	public static String getBuildRelease() {
		return getStringValue("/root/@release", BUILD_RELEASE);
	}

	// studio
	public static String getStudioFolder() {
		return getStringValue("/root/studio/@folder", STUDIO_FOLDER);
	}

	public static String getStudioURLFormat() {
		return getStringValue("/root/studio/@url", STUDIO_URL_FORMAT);
	}

	public static String getStudioFileFormat() {
		return getStringValue("/root/studio/@file", STUDIO_FILE_FORMAT);
	}

	// mdm
	public static String getMdmServerFolder() {
		return getStringValue("/root/mdm/@folder", MDMSERVER_FOLDER);
	}

	public static String getMdmServerURLFormat() {
		return getStringValue("/root/mdm/@url", MDMSERVER_URL_FORMAT);
	}

	public static String getMdmServerFileFormat() {
		return getStringValue("/root/mdm/@file", MDMSERVER_FILE_FORMAT);
	}

	// license
	public static String getLicenseURL() {
		return getStringValue("/root/license/@url", LICENSE_URL_FORMAT);
	}

	public static String getLicenseItem() {
		return getStringValue("/root/license/@item", LICENSE_ITEM_FORMAT);
	}

	public static String getLicenseFile() {
		return getStringValue("/root/license/@file", LICENSE_FILE_FORMAT);
	}
}
