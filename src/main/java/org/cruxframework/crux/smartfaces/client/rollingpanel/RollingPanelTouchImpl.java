/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.client.rollingpanel;

import java.util.HashMap;

import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 * @author Samuel Almeida Cardoso
 *
 */
class RollingPanelTouchImpl extends ScrollPanel implements RollingPanel.PanelImplementation
{
	static final String DEFAULT_ITEM_STYLE_NAME = "faces-itemRollingPanel";
	static final String DEFAULT_ITEM_WRAPPER_STYLE_NAME = "faces-itemWrapperRollingPanel";
	
	private HashMap<Widget,WrappedWidget> items = new HashMap<Widget,WrappedWidget>();
	
	private FlowPanel itemsContainer = new FlowPanel();
	private boolean scrollToAddedWidgets = false;
	public RollingPanelTouchImpl()
    {
		itemsContainer.setStyleName(DEFAULT_ITEM_WRAPPER_STYLE_NAME);
		itemsContainer.addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().flexBoxHorizontalContainer());
		super.add(itemsContainer);
    }

	@Override
	public void add(final Widget child)
	{
		itemsContainer.add(createItem(child));
		if (scrollToAddedWidgets)
		{
			Scheduler.get().scheduleDeferred(new ScheduledCommand()
			{
				public void execute()
				{
					scrollToWidget(child);
				}
			});
		}
	}
	
	@Override
	public int getScrollPosition()
	{
		return getElement().getScrollLeft();
	}
	
	@Override
	public Widget getWidget(int index) 
	{
		if(items.values() != null)
		{
			for (WrappedWidget wrappedWidget : items.values())
			{
				if(wrappedWidget.index == index)
				{
					return wrappedWidget.widget;
				}
			}
		}
		return null;
	}

	@Override
	public int getWidgetCount() 
	{
		return items.size();
	}
	
	@Override
	public int getWidgetIndex(Widget child) 
	{
		return items.get(child).index;
	}
	
	@Override
	public void insert(final Widget widget, int i)
    {
		itemsContainer.insert(createItem(widget), i);
		if (scrollToAddedWidgets)
		{
			Scheduler.get().scheduleDeferred(new ScheduledCommand()
			{
				public void execute()
				{
					scrollToWidget(widget);
				}
			});
		}
    }

	@Override
	public boolean isScrollToAddedWidgets()
    {
    	return scrollToAddedWidgets;
    }
	
	@Override
	public boolean remove(int index)
    {
		items.remove(index);
		return itemsContainer.remove(index);
    }

	@Override
	public boolean remove(Widget w) 
	{
		items.remove(w);
		return itemsContainer.remove(w);
	}
	
	@Override
	public void scrollToWidget(Widget widget)
	{
		ensureVisible(items.get(widget).wrappedWidget);
	}
	
	@Override
	public void setScrollPosition(int position)
	{
		setHorizontalScrollPosition(position);
	}
	
	@Override
	public void setScrollToAddedWidgets(boolean scrollToAddedWidgets)
    {
    	this.scrollToAddedWidgets = scrollToAddedWidgets;
    }

	private FlowPanel createItem(Widget child)
	{
		FlowPanel wrapper = new FlowPanel();
		wrapper.setStyleName(DEFAULT_ITEM_STYLE_NAME);
		wrapper.add(child);
		items.put(child,new WrappedWidget(child, wrapper, itemsContainer.getWidgetCount()));
		return wrapper;
	}

	private static class WrappedWidget
	{
		protected int index;
		protected Widget widget;
		protected FlowPanel wrappedWidget;
		
		public WrappedWidget(Widget widget, FlowPanel wrappedWidget, int index)
		{
			this.widget = widget;
			this.index = index;
			this.wrappedWidget = wrappedWidget;
		}

		@Override
		public boolean equals(Object obj) 
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			WrappedWidget other = (WrappedWidget) obj;
			if (widget == null) {
				if (other.widget != null)
					return false;
			} else if (!widget.equals(other.widget))
				return false;
			return true;
		}

		@Override
		public int hashCode() 
		{
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((widget == null) ? 0 : widget.hashCode());
			return result;
		}
	}
}
