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
package org.cruxframework.crux.smartfaces.rebind.pager;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.AbstractHasPageableFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.pager.SwapPager;
import org.cruxframework.crux.smartfaces.client.swappanel.SwapAnimation;
import org.cruxframework.crux.smartfaces.rebind.Constants;
import org.cruxframework.crux.smartfaces.rebind.swappanel.HasSwapAnimationFactory;

/**
 * @author Thiago da Rosa de Bustamante
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
@DeclarativeFactory(id="swapPager", library=Constants.LIBRARY_NAME, targetWidget=SwapPager.class,
description="A pager widget that can predict the datasource size at the load instant and changes the pages using swap animations.")
@TagAttributes({
	@TagAttribute(value="navigationButtonsVisible", type=Boolean.class, defaultValue="true",
				  description="If this is true, the next and back buttons are visible."),
	@TagAttribute(value="circularPaging", type=Boolean.class, defaultValue="true",
				  description="If this property is true, the pager will start at the first position "
				  	+ "after the last page is reached during paginations."),
	@TagAttribute(value = "animationBackward", processor = SwapPagerFactory.BackAnimationProcessor.class, 
			      type = HasSwapAnimationFactory.SwapAnimations.class, 
			      description="The animation to be aplied when the panel swaps to backward."), 
	@TagAttribute(value = "animationBackward", processor = SwapPagerFactory.ForwardAnimationProcessor.class, 
    			  type = HasSwapAnimationFactory.SwapAnimations.class, 
    			  description="The animation to be aplied when the panel swaps to forward.") 
})

public class SwapPagerFactory extends AbstractHasPageableFactory<WidgetCreatorContext> implements HasAnimationFactory<WidgetCreatorContext> 
{
	@Override
	public WidgetCreatorContext instantiateContext()
	{
		return new WidgetCreatorContext();
	}
	
	public static class BackAnimationProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public BackAnimationProcessor(WidgetCreator<?> widgetCreator)
		{
			super(widgetCreator);
		}

		@Override
		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
		{
			out.println(context.getWidget() + ".setAnimationBackward(" + SwapAnimation.class.getCanonicalName() + "." + attributeValue + ");");
		}
	}

	public static class ForwardAnimationProcessor extends AttributeProcessor<WidgetCreatorContext>
	{
		public ForwardAnimationProcessor(WidgetCreator<?> widgetCreator)
		{
			super(widgetCreator);
		}

		@Override
		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
		{
			out.println(context.getWidget() + ".setAnimationForward(" + SwapAnimation.class.getCanonicalName() + "." + attributeValue + ");");
		}
	}
}
