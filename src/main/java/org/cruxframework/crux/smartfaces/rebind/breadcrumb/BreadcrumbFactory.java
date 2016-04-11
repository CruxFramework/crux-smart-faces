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

import org.apache.commons.lang3.StringUtils;
import org.cruxframework.crux.core.client.screen.views.ViewContainer;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.event.SelectEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.ExpressionDataBinding;
import org.cruxframework.crux.core.rebind.screen.widget.PropertyBindInfo;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasEnabledFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasSelectionHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.HasPostProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.ProcessingTime;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute.WidgetReference;
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
import org.cruxframework.crux.smartfaces.client.util.animation.InOutAnimation;
import org.cruxframework.crux.smartfaces.rebind.Constants;
import org.cruxframework.crux.smartfaces.rebind.animation.HasInOutAnimationFactory;
import org.cruxframework.crux.smartfaces.rebind.image.ImageFactory;

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
		@TagAttribute(value = "viewContainer", type=WidgetReference.class, widgetType=ViewContainer.class, 
		 			 processingTime=ProcessingTime.afterAllWidgetsOnView,
					 description = "A ViewContainer to open views as items are selected."),
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
							+ "item when it is bound to a view that is activated on the Breadcrumb's viewContainer."),
		@TagAttribute(value = "collapsible", type=Boolean.class, defaultValue="false", 
					description = "When this property is true, the Breadcrumb will allow that you collapse or expand the breadcrumb "
							+ "selecting the active item."),
		@TagAttribute(value = "collapsed", type=Boolean.class, defaultValue="false", 
					description = "When this property is true, the Breadcrumb will collapse all items "
							+ "keeping only the active item visible.", processingTime=ProcessingTime.afterAllWidgetsOnView),
		@TagAttribute(value="collapseAnimation", processor=BreadcrumbFactory.AnimationProcessor.class, 
					type=HasInOutAnimationFactory.InOutAnimations.class, widgetType=InOutAnimation.class,  
					description="The animation to be aplied when perform collapse operations."),
		@TagAttribute(value="animationDuration",  type=Double.class,   
					description="The duration for the animation to be aplied when perform collapse operations.")
							
})
@TagChildren({
	@TagChild(BreadcrumbFactory.BreadcrumbDividerProcessor.class), 
	@TagChild(BreadcrumbFactory.BreadcrumbItemProcessor.class) 
})
public class BreadcrumbFactory extends WidgetCreator<BreadcrumbContext> implements HasEnabledFactory<BreadcrumbContext>, 
							HasAnimationFactory<WidgetCreatorContext>, HasSelectionHandlersFactory<WidgetCreatorContext>							
{
	public static class AnimationProcessor extends AttributeProcessor<WidgetCreatorContext>
    {
		public AnimationProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
	        out.println(context.getWidget()+".setCollapseAnimation("+InOutAnimation.class.getCanonicalName()+"."+attributeValue+");");
        }
    }

	
	@Override
	public BreadcrumbContext instantiateContext() 
	{
		return new BreadcrumbContext();
	}
		
	@TagConstraints(tagName="divider", minOccurs="0")
	@TagChildren({
		@TagChild(BreadcrumbDividerImageProcessor.class)
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="text")
	})
	public static class BreadcrumbDividerProcessor extends WidgetChildProcessor<BreadcrumbContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, BreadcrumbContext context) throws CruxGeneratorException
		{
			String text = context.readChildProperty("text");
			if (!StringUtils.isEmpty(text))
			{
				out.println(context.getWidget()+".setDividerText("+getWidgetCreator().resolveI18NString(text)+");");
			}
			
		}
	}
		
	@TagConstraints(tagName="image", widgetProperty="dividerImage", type=ImageFactory.class, minOccurs="0", maxOccurs="1")
	public static class BreadcrumbDividerImageProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}
	
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
				String getUiObjectExpression = "{0}.getItem(\""+context.itemName+"\")";
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
														widgetPropertyPath, uiObjectClassName, getUiObjectExpression, context.getDataBindingProcessor(), 
														null, null);
					if (expressionBinding != null)
					{
						context.registerExpressionDataBinding(expressionBinding);
						isBindingExpression = true;
					}
				}
				if (!isBindingExpression)
				{
					out.println(context.itemVariable + ".setText(" + getWidgetCreator().resolveI18NString(text) + ");");
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
			else
			{
				String viewId = context.readChildProperty("viewId");
				if(!StringUtils.isEmpty(viewId))
				{
					out.println(context.itemVariable + ".setView(" + EscapeUtils.quote(viewId) + ");");
				}
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

	
	@TagConstraints(type=AnyWidget.class, autoProcessingEnabled=false)
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
}

class BreadcrumbContext extends WidgetCreatorContext
{
	public String itemName;
	String itemVariable;
}
