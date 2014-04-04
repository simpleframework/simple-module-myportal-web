package net.simpleframework.module.myportal.web;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ctx.Module;
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
	protected Module createModule() {
		return super.createModule().setDefaultFunction(FUNC_MY_PORTAL);
	}

	@Override
	protected ModuleFunctions getFunctions() {
		return ModuleFunctions.of(FUNC_MY_PORTAL);
	}

	@Override
	public AbstractElement<?> toMyPortalElement(final PageParameter pp) {
		return new LinkElement(FUNC_MY_PORTAL.getText()).setHref(FUNC_MY_PORTAL.getUrl());
	}

	public final WebModuleFunction FUNC_MY_PORTAL = (WebModuleFunction) new WebModuleFunction()
			.setUrl(getUrlsFactory().getUrl(null, MyPortalTPage.class))
			.setName(MODULE_NAME + "-MyPortalPage").setText($m("MyPortalContext.0")).setDisabled(true);
}
