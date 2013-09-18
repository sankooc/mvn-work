package org.talend.license;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.conn.ApacheConnector;
import org.talend.job.Configer;
import org.talend.job.IDailyJob;

public class LicenseRetriver implements IDailyJob {
	static Logger logger = LoggerFactory.getLogger(LicenseRetriver.class);

	ApacheConnector connector = ApacheConnector.getInstance();
	Collection<String> versions;

	public LicenseRetriver(org.w3c.dom.Document doc) {
		versions = Arrays.asList(new String[] { "540", "530" });
	}

	public static void main(String[] args) {
//		Properties pro = System.getProperties();
//		for (Object obj : pro.keySet()) {
//			System.out.println(obj + " = " + pro.getProperty(obj.toString()));
//		}
		// LicenseRetriver retriver = new LicenseRetriver(null);
		// retriver.updateLicense();
	}

	public void updateLicense() {
		File userHome = new File(System.getProperty("user.home"));
		File licenseRoot = new File(userHome, "licenses");
		File history = new File(licenseRoot, ".history");
		if (!licenseRoot.exists()) {
			licenseRoot.mkdirs();
			history.mkdirs();
		}
		final Collection<File> files = new ArrayList<File>();
		for (String version : versions) {
			Collection<File> fs = updateLicense(version, licenseRoot);
			if (null != fs)
				files.addAll(fs);
		}
		File[] his = licenseRoot.listFiles(new FileFilter() {
			public boolean accept(File f) {
				return f.isFile() && !files.contains(f);
			}
		});
		if (null != his && his.length > 0) {
			for (File file : his) {
				file.renameTo(new File(history, file.getName()));
			}
		}
	}

	public Collection<File> updateLicense(final String version, final File file) {
		logger.info("start to update {} license ", version);
		String url = String.format(
				Configer.getBuildURL() + Configer.getLicenseURL(), version);

		Document doc = connector.getPage(url);
		if (null == doc) {
			logger.error("no {} license page url:{}", version, url);
			return null;
		}
		String regex = String.format(Configer.getLicenseItem(), version);

		Elements eles = doc.getElementsMatchingOwnText(regex);

		if (eles.isEmpty()) {
			logger.error("no {} license page url:{}", version, url);
			return null;
		}

		final Pattern pattern = Pattern.compile(regex);

		SortedSet<String> set = new TreeSet<String>(new Comparator<String>() {

			public int compare(String o1, String o2) {
				String m1;
				String m2;
				Matcher matcher = pattern.matcher(o1);
				if (matcher.find()) {
					m1 = matcher.group(2);
				} else {
					return 1;
				}
				matcher = pattern.matcher(o2);
				if (matcher.find()) {
					m2 = matcher.group(2);
				} else {
					return -1;
				}
				return m2.compareTo(m1);
			}
		});
		logger.info("there are {} license build", eles.size());
		for (Element ele : eles) {
			String text = ele.text();
			set.add(text);
		}
		if (set.isEmpty()) {
			return null;
		}

		Iterator<String> ite = set.iterator();
		while (ite.hasNext()) {
			String target = ite.next();
			url = url + target;
			logger.info("retrive from newest build {}", url);
			Collection<File> fs = checkout(version, file, url);
			if (!fs.isEmpty()) {
				return fs;
			}
			logger.info("no available license in build");
		}
		logger.error("retrive lecense failed");
		return null;
	}

	Collection<File> checkout(final String version, final File root,
			final String url) {
		try {
			return connector.doGet(url,
					new ResponseHandler<Collection<File>>() {

						public Collection<File> handleResponse(
								HttpResponse response)
								throws ClientProtocolException, IOException {
							Collection<File> files = new LinkedList<File>();
							InputStream stream = response.getEntity()
									.getContent();
							ZipInputStream zip = new ZipInputStream(stream);
							String regex = Configer.getLicenseFile()
									.replaceAll("%version", version);
							Pattern pattern = Pattern.compile(regex);
							while (true) {
								ZipEntry entry = zip.getNextEntry();
								if (null == entry) {
									break;
								}
								try {
									String name = entry.getName();
									Matcher matcher = pattern.matcher(name);
									if (matcher.find()) {
										int count = matcher.groupCount();
										String fname = null;
										for (int i = 1; i <= count; i++) {
											fname = matcher.group(i);
											if (StringUtils.isEmpty(fname)) {
												continue;
											}
											break;
										}

										logger.info(
												"found a available license {}",
												fname);
										File target = new File(root, fname);
										if (target.exists()) {
											files.add(target);// TODO
											continue;
										}
										FileOutputStream fos = new FileOutputStream(
												target);
										IOUtils.copy(zip, fos);
										IOUtils.closeQuietly(fos);
										files.add(target);
									}
								} catch (Exception e) {
									logger.error(e.getMessage());
								}
							}
							return files;
						}
					});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public void getLastVersion() {
		updateLicense();
	}
}
