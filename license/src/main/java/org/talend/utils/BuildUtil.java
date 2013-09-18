package org.talend.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.conn.ApacheConnector;
import org.talend.job.Configer;

public class BuildUtil {
	private static ApacheConnector connector = ApacheConnector.getInstance();
	private static Logger logger = LoggerFactory.getLogger(BuildUtil.class);

	public static Document getRecentBuildPage() {
		String url = Configer.getBuildURL()+ "?C=M;O=D";
		if (connector.doTest(url)) {
			return connector.getPage(url);
		}
		return null;
	}

	static char[] tokens = new char[] { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String toHexString(byte[] contents) {
		if (contents == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for (int j = 0; j < contents.length; j++) {
			int val = 0xff & contents[j];
			builder.append(tokens[val >>> 4]);
			builder.append(tokens[val & 0x0f]);
		}
		return builder.toString();
	}

	
	public static void unzip(File source, File target) {
		ZipInputStream stream = null;
		try {
			stream = new ZipInputStream(new FileInputStream(source));
			ZipEntry entry = null;
			while (true) {
				entry = stream.getNextEntry();
				if (null == entry) {
					break;
				}
				File file = new File(target, entry.getName());
				if (entry.isDirectory()) {
					file.mkdirs();
					continue;
				} else {
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					OutputStream out = new FileOutputStream(file);
					IOUtils.copy(stream, out);
					out.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(stream);
		}

	}
	
	public static String[] getRecentBuildVersion() {
		Document doc = getRecentBuildPage();
		Elements eles = doc.getElementsMatchingOwnText(Configer.getBuildRelease());
		Element ele = eles.get(0);
		String text = ele.text();
		Matcher matcher = Pattern.compile(Configer.getBuildRelease())
				.matcher(text);
		if (matcher.find()) {
			return new String[] { matcher.group(1), matcher.group(2) };
		}
		logger.error("cannot find build");
		return null;
	}

	public static Iterator<String[]> getRecentBuildVersionIterator() {
		Document doc = getRecentBuildPage();
		if(null == doc){
			return null;
		}
		Elements eles = doc.getElementsMatchingOwnText(Configer.getBuildRelease());
		final Iterator<Element> ite = eles.iterator();
		return new Iterator<String[]>() {
			public boolean hasNext() {
				return ite.hasNext();
			}

			public String[] next() {
				Element ele = ite.next();
				String text = ele.text();
				Matcher matcher = Pattern.compile(Configer.getBuildRelease())
						.matcher(text);
				if (matcher.find()) {
					return new String[] { matcher.group(1), matcher.group(2) };
				}
				return null;
			}

			public void remove() {
			}
		};
	}
}
