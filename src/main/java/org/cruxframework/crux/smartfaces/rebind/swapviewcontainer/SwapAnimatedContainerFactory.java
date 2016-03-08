/*
 * Copyright 2016 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.rebind.swapviewcontainer;

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.views.ChangeViewEvtBind;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasViewHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.smartfaces.client.swappanel.SwapAnimation;
import org.cruxframework.crux.smartfaces.client.swappanel.SwapViewContainer.Direction;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
@TagAttributes({
	@TagAttribute(value="autoRemoveInactiveViews", type=Boolean.class, defaultValue="false"), 
	@TagAttribute(value="animationEnabledForLargeDevices", type=Boolean.class, defaultValue="true"), 
	@TagAttribute(value="animationEnabledForSmallDevices", type=Boolean.class, defaultValue="true"),
	@TagAttribute(value="defaultDirection", type=Direction.class, defaultValue="BACKWARDS"),
	@TagAttribute(value="animationForward", type=SwapAnimatedContainerFactory.Animations.class, required=true, 
				processor=SwapAnimatedContainerFactory.AnimationForwardProcessor.class , widgetType=SwapAnimation.class,
				description="Defines the type of animation to be executed to advance the swap of view."),
	@TagAttribute(value="animationBackward",type=SwapAnimatedContainerFactory.Animations.class, required=true, 
				processor=SwapAnimatedContainerFactory.AnimationBackwardProcessor.class, widgetType=SwapAnimation.class,
				description="Defines the type of animation to be executed to back the swap of view."),
	@TagAttribute(value="defaultAnimation", type=SwapAnimatedContainerFactory.Animations.class, 
				processor=SwapAnimatedContainerFactory.DefaultAnimationProcessor.class, widgetType=SwapAnimation.class,   
			    description="The default animation to be aplied when the panel changes its content."),
	@TagAttribute(value="animationDuration",  type=Double.class,   
				description="The duration for the animation to be aplied when the panel changes its content."),
	
})
@TagEvents({
	@TagEvent(ChangeViewEvtBind.class)
})
public interface SwapAnimatedContainerFactory<C extends WidgetCreatorContext> extends HasViewHandlersFactory<SwapContainerContext>
{
	public static enum Animations{bounce, bounceUpDown, bounceLeft, bounceRight, bounceDownUp, fade, fadeDownUp, 
		fadeUpDown, fadeLeft, fadeRight, fadeDownUpBig, fadeUpDownBig, fadeLeftBig, fadeRightBig, flipX, flipY, lightSpeed, 
		rotate, rotateDownLeft, rotateDownRight, rotateUpLeft, rotateUpRight, roll, bounceUpward, bounceDownward,
		bounceForward, bounceBackward, fadeForward,fadeBackward, fadeUpward, fadeDownward}
	
	public static class AnimationForwardProcessor extends AttributeProcessor<WidgetCreatorContext>
    {
		public AnimationForwardProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
	        out.println(context.getWidget()+".setAnimationForward("+SwapAnimation.class.getCanonicalName()+"."+attributeValue+");");
        }
    }
	
	public static class AnimationBackwardProcessor extends AttributeProcessor<WidgetCreatorContext>
    {
		public AnimationBackwardProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
	        out.println(context.getWidget()+".setAnimationBackward("+SwapAnimation.class.getCanonicalName()+"."+attributeValue+");");
        }
    }
	
	public static class DefaultAnimationProcessor extends AttributeProcessor<WidgetCreatorContext>
    {
		public DefaultAnimationProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
	        out.println(context.getWidget()+".setDefaultAnimation("+SwapAnimation.class.getCanonicalName()+"."+attributeValue+");");
        }
    }
	
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="view", 
		description="A view to be rendered into this view container.")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="id", description="The view identifier."),
		@TagAttributeDeclaration(value="name", required=true, description="The name of the view."),
		@TagAttributeDeclaration(value="active", type=Boolean.class, defaultValue="false")
	})
	public static class ViewProcessor extends WidgetChildProcessor<SwapContainerContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, SwapContainerContext context) throws CruxGeneratorException
		{
			String activeProperty = context.readChildProperty("active");
			boolean active = false;
			if (!StringUtils.isEmpty(activeProperty))
			{
				active = Boolean.parseBoolean(activeProperty);
			}
			String viewId = context.readChildProperty("id");
			String viewName = context.readChildProperty("name");

			if (StringUtils.isEmpty(viewId))
			{
				viewId = viewName;
			}
			if (active)
			{
				if (context.hasActiveView)
				{
					throw new CruxGeneratorException("SwapViewContainer ["+context.getWidgetId()+"], declared on view ["+getWidgetCreator().getView().getId()+"], declares more than one active View. Only one active view is allowed by the container.");
				}
				context.hasActiveView = true;
			}
			out.println(context.getWidget()+".loadView("+EscapeUtils.quote(viewName)+", "+EscapeUtils.quote(viewId)+", "+active+");");
		}
	}	
}
