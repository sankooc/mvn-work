package org.talend.mdm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.conn.ApacheConnector;
import org.talend.job.AbstractJob;
import org.talend.job.Configer;

public class MdmServerInstaller extends AbstractJob {
	ApacheConnector connector = ApacheConnector.getInstance();

	static String install_script;
	static {
		InputStream stream = MdmServerInstaller.class.getClassLoader()
				.getResourceAsStream("silentinstall.xml");
		try {
			install_script = IOUtils.toString(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// static String suffix =
	// "V%sNB_%s/tmdmee/V%sNB/tmdmee_%s_ee_mpx/Talend-MDMServer-%s-V%sNB.jar";
//	static String fileformat = "Talend-MDMServer-%s-V%sNB";
	static Logger logger = LoggerFactory.getLogger(MdmServerInstaller.class);
	boolean flag;

	synchronized boolean installMdmServer(String mVersion, String bVersion,
			File file) {
		logger.info("install mdm server major: {}  build:{}", mVersion,
				bVersion);
		String urlFormat = Configer.getBuildURL()
				+ Configer.getMdmServerURLFormat();
		String fileName = String.format(Configer.getMdmServerFileFormat(), bVersion, mVersion);
		File installTarget = new File(file, fileName);
		if (installTarget.exists()) {
			return true;
		}
		String plainversion = mVersion.replaceAll("\\.", "");
		String url = String.format(urlFormat, mVersion, bVersion, mVersion,
				plainversion, bVersion, mVersion);
		logger.info("resource url : {}", url);
		if (!connector.doTest(url)) {
			logger.error("no resource url : {}", url);
			return false;
		}
		File target = new File(file, Configer.getMdmServerFolder()+fileName + ".jar");
		if (target.exists()) {
			
		} else {
			try {
				connector.download(url, -1, target);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				target.deleteOnExit();
				return false;
			}
		}
		File script = createScript(installTarget.getPath(), file);
		if (null == script) {
			return false;
		}

		try {
			Runtime runtime = Runtime.getRuntime();
			final Process process = runtime.exec(
					"java -jar " + target.getCanonicalPath() + " "
							+ script.getName(), null, file);
			Thread thread = new Thread(new Runnable() {
				public void run() {
					InputStream stream = process.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(stream));
					while (flag) {
						try {
							String str = reader.readLine();
							if (null == str) {
								Thread.sleep(700);
							}
							logger.info(str);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				}
			});
			thread.setDaemon(true);
			flag = true;
			thread.start();
			int ret = process.waitFor();
			logger.info("install complete with {}", ret);
			flag = false;
			udpateDatasteward(installTarget);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	void udpateDatasteward(File installTarget) {
		File datasteward = new File(
				installTarget,
				"jboss-4.2.2.GA/server/default/deploy/org.talend.datastewardship.war/WEB-INF/classes/tdsc-database.properties");
		if (datasteward.exists()) {
			InputStream stream = null;
			Properties pro = new Properties();
			try {
				stream = new FileInputStream(datasteward);
				pro.load(stream);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(stream);
			}

			pro.setProperty("tdsc.database.driverClassName", "org.h2.Driver");
			pro.setProperty("tdsc.database.url",
					"jdbc:h2://" + installTarget.getPath()
							+ "jboss-4.2.2.GA/server/default/data/TDSC/tdsc_db");
			pro.setProperty("tdsc.database.username", "sa");
			pro.setProperty("tdsc.database.password", "");
			pro.setProperty("tdsc.database.dialect",
					"org.hibernate.dialect.H2Dialect");
			pro.setProperty("tdsc.database.connectionProperties", "");

			OutputStream out = null;
			try {
				out = new FileOutputStream(datasteward);
				pro.store(out, null);
				logger.info("update config file : {}", datasteward.getPath());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(out);
			}
		}
	}

	File createScript(String fileName, File file) {
		String scriptFile = "script.xml";
		File script = new File(file, scriptFile);
		fileName = fileName.replaceAll("\\\\", "\\/");
		String content = install_script.replaceAll("\\$\\{dir\\}", fileName);
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(script);
			IOUtils.write(content, stream);
			return script;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return null;
	}

	public static void main(String[] args) {
		 MdmServerInstaller mm = new MdmServerInstaller();
		 mm.getLastVersion();
		// mm.installNewestVersion(new File("d:/temp/"));
	}

	public void getSpecalVersion(String mVersion, String bVersion)
			throws Exception {
		File file = new File(Configer.getDataFolder());
		if (!installMdmServer(mVersion, bVersion, file)) {
			throw new Exception("fail to install mdmserver :" + mVersion + "-"
					+ bVersion);
		}

	}
}
