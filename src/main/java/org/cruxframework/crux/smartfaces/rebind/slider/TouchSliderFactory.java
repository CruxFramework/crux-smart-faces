/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.rebind.slider;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.ProcessingTime;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.slider.TouchSlider;
import org.cruxframework.crux.smartfaces.rebind.Constants;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
@DeclarativeFactory(library=Constants.LIBRARY_NAME, id="touchSlider", targetWidget=TouchSlider.class, 
	description="A slider that shows various widgets, allowing touch slides between them.")
@TagAttributes({
	@TagAttribute(value="circularShowing", type=Boolean.class, defaultValue="false"),
	@TagAttribute(value="slideTransitionDuration", type=Integer.class),
	@TagAttribute(value="showFirstWidget", type=Boolean.class, processingTime=ProcessingTime.afterAllWidgetsOnView,
				  defaultValue="true", processor=TouchSliderFactory.ShowFirstWidgetProcessor.class, supportsDataBinding=false)
})
@TagEvents({
	@TagEvent(value=SlideStartEvtBind.class),
	@TagEvent(value=SlideEndEvtBind.class)
})
	
@TagChildren({
	@TagChild(TouchSliderFactory.SliderChildrenProcessor.class)
})
public class TouchSliderFactory extends WidgetCreator<WidgetCreatorContext>
{
	public static class ShowFirstWidgetProcessor extends AttributeProcessor<WidgetCreatorContext> 
	{
		public ShowFirstWidgetProcessor(WidgetCreator<?> widgetCreator) 
		{
			super(widgetCreator);
		}

		@Override
		public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue) 
		{
			if(Boolean.valueOf(attributeValue))
			{
				out.println(context.getWidget() + ".showFirstWidget();");
			}
		}
	}

	@TagConstraints(minOccurs="0", maxOccurs="unbounded")
	public static class SliderChildrenProcessor extends AnyWidgetChildProcessor<WidgetCreatorContext> {}

	@Override
	public WidgetCreatorContext instantiateContext()
	{
		return new WidgetCreatorContext();
	}
}
