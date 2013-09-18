package org.talend.job;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.utils.BuildUtil;

public abstract class AbstractJob implements IDailyJob {
	private static Logger logger = LoggerFactory.getLogger(AbstractJob.class);

	public void getLastVersion() {
		Iterator<String[]> ite = BuildUtil.getRecentBuildVersionIterator();
		while (ite.hasNext()) {
			String[] versions = ite.next();
			if (null != versions) {
				String mVersion = versions[0];
				String bVersion = versions[1];
				try {
					getSpecalVersion(mVersion, bVersion);
					break;
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
	}
	abstract protected void getSpecalVersion(String mVersion, String bVersion) throws Exception;
}
