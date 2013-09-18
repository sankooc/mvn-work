/*
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.talend.studio;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.conn.ApacheConnector;
import org.talend.job.AbstractJob;
import org.talend.job.Configer;
import org.talend.utils.BuildUtil;

/**
 * @author sankooc
 */
public class StudioInstaller extends AbstractJob {

	private static Logger logger = LoggerFactory
			.getLogger(StudioInstaller.class);

	private ApacheConnector connector = ApacheConnector.getInstance();

	public File installStudio(String mVersion, String bVersion) {
		String fileName = String.format(Configer.getStudioFileFormat(), bVersion,
				mVersion);

		File target = new File(Configer.getDataFolder());
		if (new File(target, fileName.substring(0, fileName.length() - 4))
				.exists()) {
			return target;
		}

		File source = download(mVersion, bVersion);
		target.mkdirs();
		if (null != source) {
			BuildUtil.unzip(source, target);
			return target;
		}
		return null;
	}

	public File download(String mVersion, String bVersion) {
		String plainversion = mVersion.replaceAll("\\.", "");
		String target = String.format(Configer.getStudioURLFormat(), mVersion,
				bVersion, mVersion, plainversion, bVersion, mVersion);

		String md5URL = Configer.getBuildURL() + target + ".MD5";
		logger.debug("md5 url {} ", md5URL);
		if (!connector.doTest(md5URL)) {
			logger.error("no resource m:{} b:{}", mVersion, bVersion);
			return null;
		}
		final byte[] md = connector.doGet(md5URL);
		File file = new File(Configer.getDataFolder()
				+ Configer.getStudioFolder()
				+ String.format(Configer.getStudioFileFormat(), bVersion,
						mVersion));
		if (file.exists()) {
			if (checkFile(file, new String(md))) {
				return file;
			} else {
				file.delete();
			}
		} else {
			file.getParentFile().mkdirs();
		}

		String jarUrl = Configer.getBuildURL() + target;
//		if (!connector.doTest(jarUrl)) {
//			logger.error("no resource m:{} b:{}", mVersion, bVersion);
//			return null;
//		}
		try {
			connector.download(jarUrl, -1, file);
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	boolean checkFile(File file, String data) {
		FileInputStream fis = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			fis = new FileInputStream(file);
			FileChannel fc = fis.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (true) {
				int length = fc.read(buffer);
				if (length < 1) {
					break;
				}
				buffer.flip();
				md5.update(buffer);
				buffer.clear();
			}
			byte[] ret = md5.digest();
			return BuildUtil.toHexString(ret).equals(data.trim().toUpperCase());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fis);
		}
		return false;
	}

	public void getSpecalVersion(String mVersion, String bVersion) throws Exception {
		File file = installStudio(mVersion, bVersion);
		if (null == file) {
			throw new Exception("cannot install studio : " + mVersion + "-"
					+ bVersion);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StudioInstaller installer = new StudioInstaller();
	    installer.getLastVersion();
	}
}
