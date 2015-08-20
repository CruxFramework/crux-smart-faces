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
package org.cruxframework.crux.smartfaces.client.tabviewcontainer;

import org.cruxframework.crux.core.client.event.focusblur.BeforeBlurHandler;
import org.cruxframework.crux.core.client.event.focusblur.BeforeFocusHandler;
import org.cruxframework.crux.core.client.event.focusblur.HasBeforeFocusAndBeforeBlurHandlers;
import org.cruxframework.crux.smartfaces.client.tab.TabPanel;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Bruno Medeiros (bruno@triggolabs.com)
 *
 */
public class Tab extends Composite implements HasBeforeFocusAndBeforeBlurHandlers
{
	private SimplePanel containerPanel;
	private final Flap flap;
	private final String viewId;
	private TabPanel tabPanel;

	Tab(TabPanel tabPanel, Flap flap, String viewId)
    {
		this.tabPanel = tabPanel;
		this.flap = flap;
		this.viewId = viewId;
		this.containerPanel = new SimplePanel();
		initWidget(containerPanel);
    }

	@Override
    public HandlerRegistration addBeforeBlurHandler(BeforeBlurHandler handler)
    {
	    return flap.addBeforeBlurHandler(handler);
    }

	@Override
    public HandlerRegistration addBeforeFocusHandler(BeforeFocusHandler handler)
    {
	    return flap.addBeforeFocusHandler(handler);
    }
	
	public String getLabel()
	{
		return flap.getLabel();
	}
	
	public String getViewId()
	{
		return viewId;
	}
	
	public boolean isCloseable()
	{
		return flap.isCloseable();
	}
	
	public boolean isFlapVisible()
	{
	    return flap.isVisible();
	}
	
	public void setFlapVisible(boolean visible)
	{
	    flap.setVisible(visible);
	    int index = tabPanel.getWidgetIndex(this);
	    if (index >= 0)
	    {
	    	tabPanel.setTabVisible(index, visible);
	    }
	}
	
	public void setLabel(String label)
	{
		flap.setLabel(label);
	}

	Panel getContainerPanel()
	{
		return containerPanel;
	}
	
	Flap getFlap()
	{
		return flap;
	}
}
