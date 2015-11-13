/*
 * Copyright 2015 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.smartfaces.rebind.breadcrumb;

import org.cruxframework.crux.core.client.screen.views.ViewContainer;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.event.SelectEvtBind;
import org.cruxframework.crux.core.rebind.screen.Widget;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.ExpressionDataBinding;
import org.cruxframework.crux.core.rebind.screen.widget.PropertyBindInfo;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasEnabledFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.HasPostProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.ProcessingTime;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEventsDeclaration;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.breadcrumb.Breadcrumb;
import org.cruxframework.crux.smartfaces.client.breadcrumb.BreadcrumbItem;
import org.cruxframework.crux.smartfaces.client.image.Image;
import org.cruxframework.crux.smartfaces.rebind.Constants;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
@DeclarativeFactory(library = Constants.LIBRARY_NAME, id = "breadcrumb", targetWidget = Breadcrumb.class, 
					description = "A Breadcrumb component. Help to guide the user navigation.")
@TagAttributes({
		@TagAttribute(value = "activeIndex", type = Integer.class, 
					 description = "The index of the current active item on this Breadcrumb.",
					 processingTime=ProcessingTime.afterAllWidgetsOnView),
		@TagAttribute(value = "dividerImage", supportsResources=true, 
					 description = "The image used as divider between items on this Breadcrumb.",
					 processor=BreadcrumbFactory.DividerImageAttributeProcessor.class), 
		@TagAttribute(value = "viewContainer", supportsI18N=true, 
		 			 description = "A ViewContainer to open views as items are selected.", 
		 			 processor=BreadcrumbFactory.ViewContainerAttributeProcessor.class),
		@TagAttribute(value = "removeInactiveItems", type=Boolean.class, defaultValue="false", 
					property="removeInactiveItemsEnabled", 
		 			description = "When this property is true, the Breadcrumb automatically remove the items that are not active anymore."),
		@TagAttribute(value = "singleActivationModeEnabled", type=Boolean.class, defaultValue="false", 
					description = "When this property is true, the Breadcrumb will keep only one item activated at a time. "
							    + "If false, all previous items are also activated."),
		@TagAttribute(value = "activateItemsOnSelectionEnabled", type=Boolean.class, defaultValue="true", 
					description = "When this property is true, the Breadcrumb will set the active index for an "
							+ "item when it is selected by the user."),
		@TagAttribute(value = "updateOnViewChangeEnabled", type=Boolean.class, defaultValue="true", 
					description = "When this property is true, the Breadcrumb will set the active index for an "
							+ "item when it is bound to a view that is activated on the Breadcrumb's viewContainer.")
})
@TagChildren({
	@TagChild(BreadcrumbFactory.BreadcrumbItemProcessor.class) 
})
public class BreadcrumbFactory extends WidgetCreator<BreadcrumbContext> implements HasEnabledFactory<BreadcrumbContext>
{
	@Override
	public BreadcrumbContext instantiateContext() 
	{
		return new BreadcrumbContext();
	}
		
	@TagChildren({
		@TagChild(BreadcrumbItemLabelTextProcessor.class),
		@TagChild(BreadcrumbItemLabelWidgetProcessor.class)
	})
	public static class BreadcrumbItemChildrenProcessor extends ChoiceChildProcessor<BreadcrumbContext> {}

	@TagConstraints(tagName="labelText")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="text", required=true, supportsI18N=true)
	})
	public static class BreadcrumbItemLabelTextProcessor extends WidgetChildProcessor<BreadcrumbContext>
	{
		@Override
		public void processChildren(SourcePrinter out, BreadcrumbContext context) throws CruxGeneratorException 
		{
			String text = context.readChildProperty("text");
			if (text != null && text.length() > 0)
			{
				boolean isBindingExpression = false;
				
				String widgetPropertyPath = "text";
				String getUiObjectExpression = "getItem(\""+context.itemName+"\")";
				String uiObjectClassName = BreadcrumbItem.class.getCanonicalName();
				
				PropertyBindInfo binding = getWidgetCreator().getObjectDataBinding(text, getWidgetCreator().getWidgetClassName(), 
																	widgetPropertyPath, true, uiObjectClassName, getUiObjectExpression, 
																	context.getDataBindingProcessor());
				if (binding != null)
				{
					context.registerObjectDataBinding(binding);
					isBindingExpression = true;
				}
				else
				{
					ExpressionDataBinding expressionBinding = getWidgetCreator().getExpressionDataBinding(text, getWidgetCreator().getWidgetClassName(), 
														widgetPropertyPath, uiObjectClassName, getUiObjectExpression, context.getDataBindingProcessor(), null);
					if (expressionBinding != null)
					{
						context.registerExpressionDataBinding(expressionBinding);
						isBindingExpression = true;
					}
				}
				if (!isBindingExpression)
				{
					out.println(context.itemVariable + ".setText(" + getWidgetCreator().getDeclaredMessage(text) + ");");
				}
			}
		}
	}
	
	@TagConstraints(tagName="labelWidget")
	@TagChildren({
		@TagChild(BreadcrumbItemWidgetProcessor.class)
	})
	public static class BreadcrumbItemLabelWidgetProcessor extends WidgetChildProcessor<BreadcrumbContext> {}

	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="breadcrumbItem", applyDeviceFilters=true)
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="name", required=true, supportsI18N=true),
		@TagAttributeDeclaration("viewName"),
		@TagAttributeDeclaration("viewId"),
		@TagAttributeDeclaration(value="enabled", type=Boolean.class),
		@TagAttributeDeclaration("editPermission"), 
		@TagAttributeDeclaration("viewPermission") 
	})
	@TagEventsDeclaration({
		@TagEventDeclaration(value="onSelect")
	})
	@TagChildren({
		@TagChild(BreadcrumbItemChildrenProcessor.class)
	})
	public static class BreadcrumbItemProcessor extends WidgetChildProcessor<BreadcrumbContext> 
												implements HasPostProcessor<BreadcrumbContext>
	{
		@Override
		public void postProcessChildren(SourcePrinter out, BreadcrumbContext context) throws CruxGeneratorException 
		{
			context.itemVariable = null;
		}
		
		@Override
		public void processChildren(SourcePrinter out, BreadcrumbContext context) throws CruxGeneratorException 
		{
			context.itemVariable = getWidgetCreator().createVariableName("item");
			String itemClassName = BreadcrumbItem.class.getCanonicalName();
			context.itemName = context.readChildProperty("name");
			out.println(itemClassName + " " + context.itemVariable + " = new " +itemClassName + "(" + 
						EscapeUtils.quote(context.itemName) + ");");
			
			String viewName = context.readChildProperty("viewName");
			if (viewName != null && viewName.length() > 0)
			{
				String viewId = context.readChildProperty("viewId", viewName);
				out.println(context.itemVariable + ".setView(" + EscapeUtils.quote(viewName) + ", " + EscapeUtils.quote(viewId) + ");");
			}

			boolean enabled = context.readBooleanChildProperty("enabled", true);
			if (!enabled)
			{
				out.println(context.itemVariable + ".setEnabled(" + enabled + ");");
			}

			String viewPermission = context.readChildProperty("viewPermission");
			if (viewPermission != null && viewPermission.length() > 0)
			{
				out.println(context.itemVariable + ".checkViewPermission(" + EscapeUtils.quote(viewPermission) + ");");
			}
			String editPermission = context.readChildProperty("editPermission");
			if (editPermission != null && editPermission.length() > 0)
			{
				out.println(context.itemVariable + ".checkEditPermission(" + EscapeUtils.quote(editPermission) + ");");
			}
			String onSelect = context.readChildProperty("onSelect");
			if (onSelect != null && onSelect.length() > 0)
			{
				new SelectEvtBind(getWidgetCreator()).processEvent(out, onSelect, context.itemVariable, context.getWidgetId());
			}
			out.println(context.getWidget() + ".add(" + context.itemVariable + ");");
		}
	} 

	
	@TagConstraints(type=AnyWidget.class)
	public static class BreadcrumbItemWidgetProcessor extends WidgetChildProcessor<BreadcrumbContext>
	{
		@Override
		public void processChildren(SourcePrinter out, BreadcrumbContext context) throws CruxGeneratorException 
		{
			String childWidget = getWidgetCreator().createChildWidget(out, context.getChildElement(), context);
			if (childWidget != null && childWidget.length() > 0)
			{
				out.println(context.itemVariable + ".setWidget(" + childWidget + ");");
			}
		}
	}
	
	public static class DividerImageAttributeProcessor extends AttributeProcessor<BreadcrumbContext>
	{
		public DividerImageAttributeProcessor(WidgetCreator<?> widgetCreator) 
		{
			super(widgetCreator);
		}

		@Override
		public void processAttribute(SourcePrinter out, BreadcrumbContext context, String attributeValue) 
		{
			if (getWidgetCreator().isResourceReference(attributeValue))
			{
				String expression = getWidgetCreator().getResourceAccessExpression(attributeValue);
				out.println(context.getWidget()+".setDividerImage(new "+Image.class.getCanonicalName()+"("+expression+"));");
			}
			else
			{
				out.println(context.getWidget()+".setDividerImage("+EscapeUtils.quote(attributeValue)+");");
			}
		}
	}
	
	public static class ViewContainerAttributeProcessor extends AttributeProcessor<BreadcrumbContext>
	{
		public ViewContainerAttributeProcessor(WidgetCreator<?> widgetCreator) 
		{
			super(widgetCreator);
		}

		@Override
		public void processAttribute(SourcePrinter out, BreadcrumbContext context, String attributeValue) 
		{
			Widget widget = getWidgetCreator().getView().getWidget(attributeValue);
			if (widget == null)
			{
				throw new CruxGeneratorException("There is no viewContainer named ["+attributeValue+
												"] on the view ["+getWidgetCreator().getView().getId()+"]");
			}
			
			printlnPostProcessing(context.getWidget()+".setViewContainer(("+
								ViewContainer.class.getCanonicalName()+")"+getViewVariable()+".getWidget("+
								EscapeUtils.quote(attributeValue)+"));");
		}
	}
}

class BreadcrumbContext extends WidgetCreatorContext
{
	public String itemName;
	String itemVariable;
}
