package net.simpleframework.module.myportal.web;

import net.simpleframework.module.myportal.IMyPortalContext;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IMyPortalWebContext extends IMyPortalContext {

	MyPortalUrlsFactory getUrlsFactory();

	/**
	 * 定义我的搜藏html元素
	 * 
	 * @param pp
	 * @return
	 */
	AbstractElement<?> toMyPortalElement(PageParameter pp);
}
