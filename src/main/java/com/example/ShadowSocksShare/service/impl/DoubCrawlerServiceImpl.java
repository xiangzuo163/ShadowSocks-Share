package com.example.ShadowSocksShare.service.impl;

import com.example.ShadowSocksShare.domain.ShadowSocksDetailsEntity;
import com.example.ShadowSocksShare.service.ShadowSocksCrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * doub
 * https://doub.io
 */
@Slf4j
@Service
public class DoubCrawlerServiceImpl extends ShadowSocksCrawlerService {
	// 目标网站 URL
	private static final String TARGET_URL = "https://doub.io/sszhfx/";

	/**
	 * 网页内容解析 ss 信息
	 */
	@Override
	protected Set<ShadowSocksDetailsEntity> parse(Document document) {
		Elements ssList = document.select("a.dl1");

		Set<ShadowSocksDetailsEntity> set = new HashSet(ssList.size());
		for (int i = 0; i < ssList.size(); i++) {
			try {
				Element element = ssList.get(i);
				if (element.hasText() && element.text().equalsIgnoreCase("ssr")) {
					String ssrHtml = element.attributes().get("href");
					String ssrLink = ssrHtml.replace("http://doub.pw/qr/qr.php?text=", "");

					ShadowSocksDetailsEntity ss = parseLink(ssrLink);
					ss.setValid(false);
					ss.setValidTime(new Date());
					ss.setTitle(document.title());
					ss.setRemarks(TARGET_URL);
					ss.setGroup("ShadowSocks-Share");

					// 测试网络
					if (isReachable(ss))
						ss.setValid(true);

					// 无论是否可用都入库
					set.add(ss);

					log.debug("*************** 第 {} 条 ***************{}{}", i + 1, System.lineSeparator(), ss);
					// log.debug("{}", ss.getLink());
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return set;
	}

	/**
	 * 目标网站 URL
	 */
	@Override
	protected String getTargetURL() {
		return TARGET_URL;
	}
}
