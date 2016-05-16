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

import com.google.gwt.user.client.ui.IsWidget;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
class StoryboardPanelSmall extends StoryboardPanel
{
	public void setSmallDeviceItemHeight(IsWidget child, String height)
    {
		assert(child.asWidget().getParent() != null);
		child.asWidget().getParent().setHeight(height);
    }

	public void setSmallDeviceItemHeight(String height)
    {
		this.itemHeight = height;	    
    }
	
	public void setSmallDeviceItemWidth(IsWidget child, String width)
    {
		assert(child.asWidget().getParent() != null);
		child.asWidget().getParent().setWidth(width);
    }

	public void setSmallDeviceItemWidth(String width)
    {
		this.itemWidth = width;	    
    }
	
    protected String getDefaultItemHeight() 
	{
		return "75px";
	}

    protected String getDefaultItemWidth() 
	{
		return "100%";
	}
}