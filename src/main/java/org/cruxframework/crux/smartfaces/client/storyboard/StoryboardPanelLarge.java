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
package org.cruxframework.crux.smartfaces.client.storyboard;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class StoryboardPanelLarge extends StoryboardPanel 
{
	@Override
    public void setHorizontalAlignment(HorizontalAlignmentConstant value)
    {
    	storyboard.getElement().getStyle().setProperty("textAlign", value.getTextAlignString());
    }
	
	@Override
	public void setLargeDeviceItemHeight(IsWidget child, String height)
	{
		assert(child.asWidget().getParent() != null);
		child.asWidget().getParent().setHeight(height);
	}

	@Override
    public void setLargeDeviceItemHeight(String height)
    {
		this.itemHeight = height;
    }

	@Override
    public void setLargeDeviceItemWidth(IsWidget child, String width)
    {
		assert(child.asWidget().getParent() != null);
		child.asWidget().getParent().setWidth(width);
    }

	@Override
    public void setLargeDeviceItemWidth(String width)
    {
		this.itemWidth = width;
    }

	@Override
	protected Widget createClickablePanelForCell(Widget widget)
	{
	    final Widget panel = super.createClickablePanelForCell(widget);
	    panel.getElement().getStyle().setProperty("display", "inline-table");
		return panel;
	}
	
	@Override
	protected String getItemHeight() 
	{
		return "200px";
	}
	
	@Override
	protected String getItemWidth() 
	{
		return "200px";
	}
	
	@Override
	protected void setStoryboard(Storyboard storyboard) 
	{
		super.setStoryboard(storyboard);
		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	}
}
