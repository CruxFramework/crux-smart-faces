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
package org.cruxframework.crux.smartfaces.rebind.slider;

import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;

/**
 * 
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 */
public class SlideCloseEvtBind extends EvtProcessor
{
	public SlideCloseEvtBind(WidgetCreator<?> widgetCreator)
    {
	    super(widgetCreator);
    }

	private static final String EVENT_NAME = "onClose";

	public String getEventName()
	{
		return EVENT_NAME;
	}

	@Override
    public Class<?> getEventClass()
    {
	    return CloseEvent.class;
    }

	@Override
    public Class<?> getEventHandlerClass()
    {
	    return CloseHandler.class;
    }		
}
