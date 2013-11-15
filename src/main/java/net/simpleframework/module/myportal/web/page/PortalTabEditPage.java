package net.simpleframework.module.myportal.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;
import java.util.Map;

import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.module.myportal.IMyPortalContext;
import net.simpleframework.module.myportal.IMyPortalContextAware;
import net.simpleframework.module.myportal.IPortalTabService;
import net.simpleframework.module.myportal.PortalTabBean;
import net.simpleframework.mvc.IPageHandler.PageSelector;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.RowField;
import net.simpleframework.mvc.common.element.TableRow;
import net.simpleframework.mvc.common.element.TableRows;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.validation.EValidatorMethod;
import net.simpleframework.mvc.component.base.validation.Validator;
import net.simpleframework.mvc.template.lets.FormTableRowTemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class PortalTabEditPage extends FormTableRowTemplatePage implements IMyPortalContextAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		addFormValidationBean(pp).addValidators(
				new Validator().setSelector("#tab_name").setMethod(EValidatorMethod.required));
	}

	@Override
	public void onLoad(final PageParameter pp, final Map<String, Object> dataBinding,
			final PageSelector selector) {
		final PortalTabBean homeTab = context.getPortalTabService()
				.getBean(pp.getParameter("tab_id"));
		if (homeTab != null) {
			dataBinding.put("tab_id", homeTab.getId());
			dataBinding.put("tab_name", homeTab.getTabText());
			dataBinding.put("tab_description", homeTab.getDescription());
		}
	}

	@Transaction(context = IMyPortalContext.class)
	@Override
	public JavascriptForward onSave(final ComponentParameter cp) {
		final String tabText = cp.getParameter("tab_name");
		final String description = cp.getParameter("tab_description");
		final IPortalTabService service = context.getPortalTabService();
		PortalTabBean tab = service.getBean(cp.getParameter("tab_id"));
		if (tab == null) {
			tab = service.createBean();
			tab.setUserId(cp.getLoginId());
			tab.setCreateDate(new Date());
			tab.setTabText(tabText);
			tab.setDescription(description);
			service.insert(tab);
		} else {
			tab.setTabText(tabText);
			tab.setDescription(description);
			service.update(tab);
		}
		final JavascriptForward js = super.onSave(cp);
		js.append("$Actions.loc(\"").append(MyPortalHandle.getTabUrl(tab.getId())).append("\");");
		return js;
	}

	private final InputElement tab_id = InputElement.hidden("tab_id");
	private final InputElement tab_name = new InputElement("tab_name");
	private final InputElement tab_description = InputElement.textarea("tab_description").setRows(6);

	@Override
	protected TableRows getTableRows(final PageParameter pp) {
		final TableRow r1 = new TableRow(new RowField($m("PortalTabEditPage.0"), tab_id, tab_name));
		final TableRow r2 = new TableRow(new RowField($m("PortalTabEditPage.1"), tab_description));
		return TableRows.of(r1, r2);
	}
}
