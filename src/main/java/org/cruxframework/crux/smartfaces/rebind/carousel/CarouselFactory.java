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
package org.cruxframework.crux.smartfaces.rebind.carousel;

import org.cruxframework.crux.core.client.factory.WidgetFactory;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.AbstractPageableFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.HorizontalAlignment;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.HorizontalAlignmentAttributeParser;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.VerticalAlignment;
import org.cruxframework.crux.core.rebind.screen.widget.creator.align.VerticalAlignmentAttributeParser;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.smartfaces.client.carousel.Carousel;
import org.cruxframework.crux.smartfaces.rebind.Constants;
import org.json.JSONObject;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(library=Constants.LIBRARY_NAME, id="carousel", targetWidget=Carousel.class)
@TagAttributes({
	@TagAttribute(value="largeDeviceItemWidth", supportedDevices={Device.largeDisplayArrows, Device.largeDisplayMouse, Device.largeDisplayTouch}),
	@TagAttribute(value="smallDeviceItemHeight", supportedDevices={Device.smallDisplayArrows, Device.smallDisplayTouch}),
	@TagAttribute(value="smallDeviceItemWidth", supportedDevices={Device.smallDisplayArrows, Device.smallDisplayTouch}),
	@TagAttribute(value="largeDeviceItemHeight", supportedDevices={Device.largeDisplayArrows, Device.largeDisplayMouse, Device.largeDisplayTouch}),
	@TagAttribute(value="horizontalAlignment", type=HorizontalAlignment.class, widgetType=HorizontalAlignmentConstant.class, 
		    processor=HorizontalAlignmentAttributeParser.class, defaultValue="center"),
	@TagAttribute(value="verticalAlignment", type=VerticalAlignment.class, widgetType=VerticalAlignmentConstant.class, 
			processor=VerticalAlignmentAttributeParser.class, defaultValue="middle"), 
	@TagAttribute(value="fixedWidth", type=Boolean.class, defaultValue="true"), 
	@TagAttribute(value="fixedHeight", type=Boolean.class, defaultValue="true") 
})
@TagChildren({
	@TagChild(value=CarouselFactory.WidgetChildCreator.class, autoProcess=false)
})
public class CarouselFactory extends AbstractPageableFactory<WidgetCreatorContext>
{
	@Override
	public WidgetCreatorContext instantiateContext() 
	{
		return new WidgetCreatorContext();
	}
	
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		JSONObject widgetCreatorChild = ensureFirstChild(context.getWidgetElement(), false, context.getWidgetId());;
		
		JClassType dataObject = getDataObject(context);
		String dataObjectName = dataObject.getParameterizedQualifiedSourceName();
		String className = getWidgetClassName()+"<"+dataObjectName+">";

		String widgetFactory = createVariableName("widgetFactory");
		String widgetFactoryClassName = WidgetFactory.class.getCanonicalName()+"<"+dataObjectName+">";
		
		out.print("final " + widgetFactoryClassName + " " + widgetFactory + " = ");
		
		if (!generateWidgetCreationForCell(out, context, widgetCreatorChild, dataObject))
		{
        	throw new CruxGeneratorException("Invalid child tag on widget ["+context.getWidgetId()+"]. View ["+getView().getId()+"]");
		}

		out.println("final "+className + " " + context.getWidget()+" = new "+className+"("+widgetFactory+");");
	}
}
