package net.simpleframework.module.myportal.web.page;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.IoUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.common.xml.XmlDocument;
import net.simpleframework.ctx.common.xml.XmlElement;
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.module.myportal.ILayoutLobService;
import net.simpleframework.module.myportal.IMyPortalContext;
import net.simpleframework.module.myportal.IMyPortalContextAware;
import net.simpleframework.module.myportal.ITabService;
import net.simpleframework.module.myportal.LayoutLobBean;
import net.simpleframework.module.myportal.TabBean;
import net.simpleframework.module.myportal.web.IMyPortalWebContext;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.SessionCache;
import net.simpleframework.mvc.component.AbstractComponentRegistry;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.portal.ColumnBean;
import net.simpleframework.mvc.component.portal.DefaultPortalHandler;
import net.simpleframework.mvc.component.portal.PortalBean;
import net.simpleframework.mvc.component.portal.PortalRegistry;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MyPortalHandle extends DefaultPortalHandler implements IMyPortalContextAware {

	@Override
	public Collection<ColumnBean> getPortalColumns(final ComponentParameter cp) {
		final ColumnCache columnCache = getColumnCache(cp, getHomeTab(cp));
		return columnCache != null ? columnCache.columns : null;
	}

	protected ColumnCache getColumnCache(final ComponentParameter cp, final TabBean homeTab) {
		if (homeTab == null) {
			return null;
		}

		final ID tabId = homeTab.getId();
		ColumnCache columnCache = (ColumnCache) SessionCache.lget(tabId);
		if (columnCache != null) {
			return columnCache;
		}

		try {
			final LayoutLobBean homeLayout = myPortalContext.getLayoutLobService()
					.getLayoutLob(homeTab);
			final XmlDocument doc = new XmlDocument(
					IoUtils.getStringFromReader(homeLayout.getLayoutLob()));
			final List<ColumnBean> columns = ((PortalRegistry) AbstractComponentRegistry
					.getComponentRegistry(PortalBean.class)).loadBean((PortalBean) cp.componentBean,
							cp.getScriptEval(), doc.getRoot());
			columnCache = new ColumnCache(doc, columns);
			SessionCache.lput(tabId, columnCache);
		} catch (final IOException e) {
			getLog().warn(e);
		}
		return columnCache;
	}

	@Transaction(context = IMyPortalContext.class)
	@Override
	public void updatePortal(final ComponentParameter cp, final Collection<ColumnBean> columns,
			final boolean draggable) {
		final TabBean homeTab = getHomeTab(cp);
		if (homeTab == null) {
			return;
		}
		final ID tabId = homeTab.getId();

		final ILayoutLobService service = myPortalContext.getLayoutLobService();
		final LayoutLobBean homeLayout = service.getBean(tabId);
		final ColumnCache columnCache = (ColumnCache) SessionCache.lget(tabId);
		if (columnCache == null) {
			return;
		}
		final XmlElement root = columnCache.document.getRoot();
		root.clearContent();
		root.addAttribute("draggable", String.valueOf(draggable));
		columnCache.columns = columns;
		for (final ColumnBean column : columns) {
			column.syncElement();
			root.addElement(column.getElement());
		}
		homeLayout.setLayoutLob(new StringReader(columnCache.document.toString()));
		service.update(homeLayout);
	}

	private TabBean getHomeTab(final ComponentParameter cp) {
		final String tabId = cp.getParameter(TAB_ID);
		final ITabService service = myPortalContext.getTabService();
		TabBean homeTab = service.getBean(tabId);
		if (homeTab == null) {
			homeTab = service.homeTab(cp.getLoginId());
		}
		return homeTab;
	}

	private class ColumnCache {
		XmlDocument document;

		Collection<ColumnBean> columns;

		ColumnCache(final XmlDocument document, final Collection<ColumnBean> columns) {
			this.document = document;
			this.columns = columns;
		}
	}

	@Override
	public Object getBeanProperty(final ComponentParameter cp, final String beanProperty) {
		if ("roleManager".equals(beanProperty)) {
			return PermissionConst.ROLE_ALL_ACCOUNT;
		} else if ("draggable".equals(beanProperty)) {
			final ColumnCache columnCache = getColumnCache(cp, getHomeTab(cp));
			if (columnCache == null) {
				return false;
			}
			return Convert.toBool(columnCache.document.getRoot().attributeValue("draggable"));
		}
		return super.getBeanProperty(cp, beanProperty);
	}

	@Override
	public Map<String, Object> getFormParameters(final ComponentParameter cp) {
		return ((KVMap) super.getFormParameters(cp)).add(TAB_ID, cp.getParameter(TAB_ID));
	}

	static final String TAB_ID = "tabid";

	public static String getTabUrl(final PageParameter pp, final Object tabId) {
		return ((IMyPortalWebContext) myPortalContext).getUrlsFactory().getUrl(pp,
				MyPortalTPage.class, TAB_ID + "=" + tabId);
	}
}
