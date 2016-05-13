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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.smartfaces.client.panel.SelectablePanel;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 */
abstract class StoryboardPanel extends Composite
{
	private static final String STORYBOARD_ITEM_STYLE_NAME = "item";
	
	protected boolean fixedHeight = true;
	protected boolean fixedWidth = true;
	protected String itemHeight;
	protected String itemWidth;
	protected FlowPanel mainPanel;
	protected Storyboard storyboard; 
	
	protected StoryboardPanel()
    {
		this.mainPanel = new FlowPanel();
		initWidget(mainPanel);
		
		this.itemHeight = getItemHeight();
		this.itemWidth = getItemWidth();
    }

	public void add(Widget widget)
	{
		mainPanel.add(createClickablePanelForCell(widget));
	} 
	
	public void clear() 
	{
		mainPanel.clear();
	} 
	
	public Widget getWidget(int index)
	{
		return ((SelectablePanel)mainPanel.getWidget(index)).getChildWidget();
	}
	
	public int getWidgetCount() 
    {
    	return mainPanel.getWidgetCount();
    }

	public int getWidgetIndex(Widget child)
	{
		int count = getWidgetCount();
		for (int i=0; i< count; i++)
		{
			if (getWidget(i).equals(child))
			{
				return i;
			}
		}
		return -1;
	}
	
	public Iterator<Widget> iterator()
    {
	    return new Iterator<Widget>()
		{
	    	private int index = -1;
	    	
			@Override
            public boolean hasNext()
            {
			      return index < (getWidgetCount() - 1);
            }

			@Override
            public Widget next()
			{
				if (index >= getWidgetCount()) 
				{
					throw new NoSuchElementException();
				}
				return getWidget(++index);
            }

			@Override
            public void remove()
            {
				if ((index < 0) || (index >= getWidgetCount())) 
				{
					throw new IllegalStateException();
				}
				mainPanel.remove(index--);
            }

		};
    }

	public boolean remove(int index) 
	{
		return mainPanel.remove(index);
	}

    public boolean remove(Widget w)
    {
	    int index = getWidgetIndex(w);
	    if (index >= 0)
	    {
	    	return mainPanel.remove(index);
	    }
	    return false;
    }
    
    public void setFixedHeight(boolean fixedHeight) 
	{
		this.fixedHeight = fixedHeight;
	}
    
    public void setFixedWidth(boolean fixedWidth) 
	{
		this.fixedWidth = fixedWidth;
	}

	public void setHorizontalAlignment(HorizontalAlignmentConstant value)
    {
    }

    public void setHorizontalAlignment(IsWidget child, HorizontalAlignmentConstant value)
    {
		child.asWidget().getParent().getElement().getStyle().setProperty("textAlign", value.getTextAlignString());
    }

    public void setLargeDeviceItemHeight(IsWidget child, String height)
	{
	}

    public void setLargeDeviceItemHeight(String height)
    {
    }

    public void setLargeDeviceItemWidth(IsWidget child, String width)
    {
    }

    public void setLargeDeviceItemWidth(String width)
    {
    }

    public void setSmallDeviceItemHeight(IsWidget child, String height)
    {
    }

	public void setSmallDeviceItemHeight(String height)
    {
    }
	
    public void setVerticalAlignment(IsWidget child, VerticalAlignmentConstant value)
    {
		child.asWidget().getParent().getElement().getStyle().setProperty("verticalAlign", value.getVerticalAlignString());
    }

    public void setVerticalAlignment(VerticalAlignmentConstant value)
    {
    	storyboard.getElement().getStyle().setProperty("verticalAlign", value.getVerticalAlignString());
    }

    protected void configHeightWidth(final Widget panel) 
	{
		if (!StringUtils.isEmpty(itemHeight))
		{
			if(fixedHeight)
			{
				panel.setHeight(itemHeight);
			}
			else
			{
				panel.getElement().getStyle().setProperty("minHeight", itemHeight);
			}
		}
		
		if (!StringUtils.isEmpty(itemWidth))
		{
			if(fixedWidth)
			{
				panel.setWidth(itemWidth);
			}
			else
			{
				panel.getElement().getStyle().setProperty("minWidth", itemWidth);
			}
		}
	}

	protected Widget createClickablePanelForCell(Widget widget)
	{
		final SelectablePanel panel = new SelectablePanel();
		panel.add(widget);
		panel.setStyleName(STORYBOARD_ITEM_STYLE_NAME);
		configHeightWidth(panel);
		
		panel.addSelectHandler(new SelectHandler()
		{
			@Override
			public void onSelect(SelectEvent event)
			{
				int index = mainPanel.getWidgetIndex(panel);
			    SelectionEvent.fire(storyboard, index);
			    setSelected(!isSelected(index), index);
			}
		});
		return panel;
	}

	protected abstract String getItemHeight();

	protected abstract String getItemWidth();

	protected void setStoryboard(Storyboard storyboard)
	{
		this.storyboard = storyboard;
	}

	public void setSelected(boolean selected, int index)
    {
		if (selected != isSelected(index))
		{
			if (selected)
			{
				mainPanel.getWidget(index).addStyleDependentName("selected");
			}
			else
			{
				mainPanel.getWidget(index).removeStyleDependentName("selected");
			}
		}
    }

	public boolean isSelected(int index)
    {
	    return mainPanel.getWidget(index).getStyleName().contains("-selected");
    }		
}