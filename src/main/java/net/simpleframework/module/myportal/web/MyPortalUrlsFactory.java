package net.simpleframework.module.myportal.web;

import net.simpleframework.module.myportal.web.page.MyPortalTPage;
import net.simpleframework.module.myportal.web.page.t2.MyPortalPage;
import net.simpleframework.mvc.common.UrlsCache;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyPortalUrlsFactory extends UrlsCache {

	public MyPortalUrlsFactory() {
		put(MyPortalTPage.class, MyPortalPage.class);
	}
}
