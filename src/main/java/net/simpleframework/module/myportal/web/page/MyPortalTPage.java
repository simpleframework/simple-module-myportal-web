package net.simpleframework.module.myportal.web.page;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.module.myportal.IMyPortalContext;
import net.simpleframework.module.myportal.IMyPortalContextAware;
import net.simpleframework.module.myportal.ITabService;
import net.simpleframework.module.myportal.TabBean;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ETabMatch;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.portal.PortalBean;
import net.simpleframework.mvc.component.ui.menu.EMenuEvent;
import net.simpleframework.mvc.component.ui.menu.MenuBean;
import net.simpleframework.mvc.component.ui.menu.MenuItem;
import net.simpleframework.mvc.component.ui.tooltip.TooltipBean;
import net.simpleframework.mvc.template.AbstractTemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyPortalTPage extends AbstractTemplatePage implements IMyPortalContextAware {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		pp.addImportCSS(MyPortalTPage.class, "/myportal.css");

		addComponentBean(pp, PortalBean.class, MyPortalHandle.class).setContainerId(
				"MyPortalTPage_layout");

		addAjaxRequest(pp, "MyPortalTPage_tabPage", PortalTabEditPage.class);
		addWindowBean(pp, "MyPortalTPage_addTab").setContentRef("MyPortalTPage_tabPage")
				.setTitle($m("MyPortalTPage.0")).setHeight(250).setWidth(400);

		addAjaxRequest(pp, "MyPortalTPage_tabDelete").setConfirmMessage($m("MyPortalTPage.1"))
				.setHandlerMethod("doTabDelete");

		addComponentBean(pp, "MyPortalTPage_tooltip", TooltipBean.class);

		final MenuBean menu = (MenuBean) addComponentBean(pp, "MyPortalTPage_menu", MenuBean.class)
				.setMenuEvent(EMenuEvent.click).setSelector(".MyPortalTPage .tmenu");
		menu.addItem(MenuItem.itemEdit().setOnclick(
				"$Actions['MyPortalTPage_addTab']('tab_id=' + $Target(item).id.substring(1));"));
		menu.addItem(MenuItem.sep());
		menu.addItem(MenuItem.itemDelete().setOnclick(
				"$Actions['MyPortalTPage_tabDelete']('tab_id=' + $Target(item).id.substring(1));"));
	}

	@Transaction(context = IMyPortalContext.class)
	public IForward doTabDelete(final ComponentParameter cp) {
		final ITabService service = myPortalContext.getTabService();
		final TabBean homeTab = service.getBean(cp.getParameter("tab_id"));
		final TabBean firstHomeTab = service.homeTab(cp.getLoginId());
		final JavascriptForward js = new JavascriptForward();
		if (firstHomeTab.equals(homeTab)) {
			js.append("alert('").append($m("MyPortalTPage.3")).append("');");
		} else {
			service.delete(homeTab.getId());
			js.append("$Actions.loc(\"");
			js.append(MyPortalHandle.getTabUrl(cp, homeTab.getId())).append("\");");
		}
		return js;
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String variable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='MyPortalTPage'>");
		sb.append("  <div class='right_bar'>");
		sb.append("    <a onclick=\"_lo_fireMenuAction(")
				.append("$('MyPortalTPage_layout').down('.pagelet'), 'layoutModulesWindow');\">")
				.append($m("MyPortalTPage.2")).append("</a>");
		sb.append("  </div>");
		sb.append("  <div class='tabs_icon'></div>");
		sb.append("  <div class='tabs'>");
		final TabButtons btns = TabButtons.of();
		for (final TabBean homeTab : myPortalContext.getTabService().queryTabs(pp.getLoginId())) {
			btns.add(new TabButton(homeTab.getTabText(), MyPortalHandle.getTabUrl(pp, homeTab.getId()))
					.setTabMatch(ETabMatch.params).setId(homeTab.getId().toString()).setMenuIcon(true)
					.setTooltip(homeTab.getDescription()));
		}
		sb.append(btns.toString(pp));
		sb.append("    <a class='addtab' onclick=\"$Actions['MyPortalTPage_addTab']();\">")
				.append($m("MyPortalTPage.0")).append("</a>");
		sb.append("  </div>");
		sb.append("</div>");
		sb.append("<div id='MyPortalTPage_layout'></div>");
		return sb.toString();
	}
}
