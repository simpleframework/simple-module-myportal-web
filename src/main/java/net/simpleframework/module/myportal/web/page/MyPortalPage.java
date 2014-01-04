package net.simpleframework.module.myportal.web.page;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.common.Convert;
import net.simpleframework.ctx.permission.IPermissionConst;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.module.myportal.IMyPortalContext;
import net.simpleframework.module.myportal.IMyPortalContextAware;
import net.simpleframework.module.myportal.IPortalTabService;
import net.simpleframework.module.myportal.PortalTabBean;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ETabMatch;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.portal.PortalBean;
import net.simpleframework.mvc.component.ui.menu.EMenuEvent;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.tooltip.TooltipBean;
import net.simpleframework.mvc.component.ui.window.WindowBean;
import net.simpleframework.mvc.template.struct.NavigationButtons;
import net.simpleframework.mvc.template.t2.T2TemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyPortalPage extends T2TemplatePage implements IMyPortalContextAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		pp.addImportCSS(MyPortalPage.class, "/my_portal.css");

		addComponentBean(pp, PortalBean.class, MyPortalHandle.class).setContainerId(
				"MyPortalPage_layout");

		addAjaxRequest(pp, "MyPortalPage_tabPage", PortalTabEditPage.class);
		addComponentBean(pp, "MyPortalPage_addTab", WindowBean.class)
				.setContentRef("MyPortalPage_tabPage").setTitle($m("MyPortalPage.0")).setHeight(250)
				.setWidth(400);

		addAjaxRequest(pp, "MyPortalPage_tabDelete").setConfirmMessage($m("MyPortalPage.1"))
				.setHandleMethod("doTabDelete");

		addComponentBean(pp, "MyPortalPage_tooltip", TooltipBean.class);

		final MenuBean menu = (MenuBean) addComponentBean(pp, "MyPortalPage_menu", MenuBean.class)
				.setMenuEvent(EMenuEvent.click).setSelector(".MyPortalPage .tmenu");
		menu.addItem(MenuItem.itemEdit().setOnclick(
				"$Actions['MyPortalPage_addTab']('tab_id=' + $Target(item).id.substring(1));"));
		menu.addItem(MenuItem.sep());
		menu.addItem(MenuItem.itemDelete().setOnclick(
				"$Actions['MyPortalPage_tabDelete']('tab_id=' + $Target(item).id.substring(1));"));
	}

	@Override
	public String getRole(final PageParameter pp) {
		return IPermissionConst.ROLE_ALL_ACCOUNT;
	}

	@Transaction(context = IMyPortalContext.class)
	public IForward doTabDelete(final ComponentParameter cp) {
		final IPortalTabService service = context.getPortalTabService();
		final PortalTabBean homeTab = service.getBean(cp.getParameter("tab_id"));
		final PortalTabBean firstHomeTab = service.homeTab(cp.getLoginId());
		final JavascriptForward js = new JavascriptForward();
		if (firstHomeTab.getId().equals(homeTab.getId())) {
			js.append("alert('").append($m("MyPortalPage.3")).append("');");
		} else {
			service.delete(homeTab.getId());
			js.append("$Actions.loc(\"");
			js.append(MyPortalHandle.getTabUrl(homeTab.getId())).append("\");");
		}
		return js;
	}

	@Override
	public NavigationButtons getNavigationBar(final PageParameter pp) {
		return super.getNavigationBar(pp).append(
				new LinkElement($m("MyPortalContext.0")).setHref(url(MyPortalPage.class)));
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String variable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='MyPortalPage'>");
		sb.append("  <div class='right_bar'>");
		sb.append("    <a onclick=\"_lo_fireMenuAction(")
				.append("$('MyPortalPage_layout').down('.pagelet'), 'layoutModulesWindow');\">")
				.append($m("MyPortalPage.2")).append("</a>");
		sb.append("  </div>");
		sb.append("  <div class='tabs_icon'></div>");
		sb.append("  <div class='tabs'>");
		final TabButtons btns = TabButtons.of();
		for (final PortalTabBean homeTab : context.getPortalTabService().queryTabs(pp.getLoginId())) {
			btns.add(new TabButton(homeTab.getTabText(), MyPortalHandle.getTabUrl(homeTab.getId()))
					.setTabMatch(ETabMatch.params).setId(Convert.toString(homeTab.getId()))
					.setMenuIcon(true).setTooltip(homeTab.getDescription()));
		}
		sb.append(btns.toString(pp));
		sb.append("    <a class='addtab' onclick=\"$Actions['MyPortalPage_addTab']();\">")
				.append($m("MyPortalPage.0")).append("</a>");
		sb.append("  </div>");
		sb.append("</div>");
		sb.append("<div id='MyPortalPage_layout'></div>");
		return sb.toString();
	}
}
