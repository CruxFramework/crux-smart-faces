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

import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasCloseHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasOpenHandlersFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor.AnyWidget;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvent;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagEvents;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.slider.SlideOutPanel;
import org.cruxframework.crux.smartfaces.client.slider.SlideOutPanel.MenuOrientation;
import org.cruxframework.crux.smartfaces.rebind.Constants;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
@DeclarativeFactory(library=Constants.LIBRARY_NAME, id="slideOutPanel", targetWidget=SlideOutPanel.class, 
	description="A slide out panel that contains a collapsible menu and a main area.")
@TagAttributes({
	@TagAttribute(value="slideTransitionDuration", type=Integer.class, defaultValue="250"),
	@TagAttribute(value="menuOrientation", type=MenuOrientation.class),
	@TagAttribute(value="slideEnabled", type=Boolean.class, defaultValue="true")
})
@TagEvents({
	@TagEvent(value=SlideStartEvtBind.class),
	@TagEvent(value=SlideEndEvtBind.class)
})
	
@TagChildren({
	@TagChild(SlideOutPanelFactory.MenuProcessor.class),
	@TagChild(SlideOutPanelFactory.MainProcessor.class)
})
public class SlideOutPanelFactory extends WidgetCreator<WidgetCreatorContext> 
				implements HasOpenHandlersFactory<WidgetCreatorContext>, HasCloseHandlersFactory<WidgetCreatorContext>
{
	@TagConstraints(tagName="menu")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="width", supportsDataBinding=false)
	})
	@TagChildren({
		@TagChild(SlideOutPanelFactory.MenuWidgetProcessor.class)
	})
	public static class MenuProcessor extends WidgetChildProcessor<WidgetCreatorContext> 
	{
		@Override
		public void processChildren(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
		{
			String width = context.readChildProperty("width");
			if (!StringUtils.isEmpty(width))
			{
				out.println(context.getWidget()+".setMenuWidth("+EscapeUtils.quote(width)+");");
			}
		}
	}

	@TagConstraints(type=AnyWidget.class, autoProcessingEnabled=true, method="setMenuWidget")
	public static class MenuWidgetProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(tagName="main")
	@TagChildren({
		@TagChild(SlideOutPanelFactory.MainWidgetProcessor.class)
	})
	public static class MainProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@TagConstraints(type=AnyWidget.class, autoProcessingEnabled=true, method="setMainWidget")
	public static class MainWidgetProcessor extends WidgetChildProcessor<WidgetCreatorContext> {}

	@Override
	public WidgetCreatorContext instantiateContext()
	{
		return new WidgetCreatorContext();
	}
}
