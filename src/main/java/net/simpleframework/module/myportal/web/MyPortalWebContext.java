package net.simpleframework.module.myportal.web;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ctx.Module;
import net.simpleframework.module.myportal.impl.MyPortalContext;
import net.simpleframework.module.myportal.web.page.MyPortalPage;
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

	public static WebModuleFunction MY_PORTAL_FUNCTION = (WebModuleFunction) new WebModuleFunction(
			MyPortalPage.class).setName(MODULE_NAME + "-MyPortalPage")
			.setText($m("MyPortalContext.0")).setDisabled(true);

	@Override
	protected Module createModule() {
		return super.createModule().setDefaultFunction(MY_PORTAL_FUNCTION);
	}

	@Override
	public AbstractElement<?> toMyPortalElement(final PageParameter pp) {
		return new LinkElement(MY_PORTAL_FUNCTION.getText()).setHref(MY_PORTAL_FUNCTION.getUrl());
	}
}
