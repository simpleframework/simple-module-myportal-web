package net.simpleframework.module.myportal.web;

import static net.simpleframework.common.I18n.$m;

import net.simpleframework.ctx.ModuleFunctions;
import net.simpleframework.module.myportal.impl.MyPortalContext;
import net.simpleframework.module.myportal.web.page.MyPortalTPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.ctx.WebModuleFunction;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyPortalWebContext extends MyPortalContext implements IMyPortalWebContext {
	@Override
	public MyPortalUrlsFactory getUrlsFactory() {
		return singleton(MyPortalUrlsFactory.class);
	}

	@Override
	protected ModuleFunctions getFunctions() {
		return ModuleFunctions.of((WebModuleFunction) new WebModuleFunction(this)
				.setUrl(getUrlsFactory().getUrl(null, MyPortalTPage.class))
				.setName(MODULE_NAME + "-MyPortalPage").setText($m("MyPortalContext.0"))
				.setDisabled(true));
	}

	@Override
	public AbstractElement<?> toMyPortalElement(final PageParameter pp) {
		final WebModuleFunction f = (WebModuleFunction) getFunctionByName(
				MODULE_NAME + "-MyPortalPage");
		return new LinkElement(f.getText()).setHref(f.getUrl());
	}

}
