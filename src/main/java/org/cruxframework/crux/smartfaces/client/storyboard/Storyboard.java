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

import org.cruxframework.crux.core.shared.Experimental;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel that distribute a collection of widgets along the screen.
 * @author Thiago da Rosa de Bustamante.
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
public class Storyboard extends Composite implements HasWidgets.ForIsWidget, IndexedPanel.ForIsWidget, 
								HasSelectionHandlers<Integer> 
{
	public static final String DEFAULT_STYLE_NAME = "faces-Storyboard";
	
	private StoryboardPanel storyboardPanel;

	/**
	 * Constructor
	 */
	public Storyboard() 
	{
		storyboardPanel = GWT.create(StoryboardPanel.class);
		initWidget(storyboardPanel);
		storyboardPanel.setStoryboard(this);
		setStyleName(DEFAULT_STYLE_NAME);
	}
	
	@Override
	public void add(IsWidget w) 
	{
		storyboardPanel.add(w.asWidget());
	}

	@Override
	public void add(Widget w) 
	{
		storyboardPanel.add(w);		
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler) 
	{
		 return addHandler(handler, SelectionEvent.getType());
	}

	@Override
	public void clear() 
	{
		storyboardPanel.clear();
	}

	@Override
	public Widget getWidget(int index) 
	{
		return storyboardPanel.getWidget(index);
	}

	@Override
	public int getWidgetCount() 
	{
		return storyboardPanel.getWidgetCount();
	}

	@Override
	public int getWidgetIndex(IsWidget child) 
	{
		return storyboardPanel.getWidgetIndex(child.asWidget());
	}

	@Override
	public int getWidgetIndex(Widget child) 
	{
		return storyboardPanel.getWidgetIndex(child);
	}

	@Override
	public Iterator<Widget> iterator() 
	{
		return storyboardPanel.iterator();
	}

	@Override
	public boolean remove(int index) 
	{
		return storyboardPanel.remove(index);
	}

	@Override
	public boolean remove(IsWidget w) 
	{
		return storyboardPanel.remove(w.asWidget());
	}

	@Override
	public boolean remove(Widget w) 
	{
		return storyboardPanel.remove(w);
	}
	
	/**
	 * It this is set to true, the item height will be fixed to the provided value through the methods
	 * setSmallDeviceItemHeight and setLargeDeviceItemHeight. If false, the value provided to this method
	 * will be the minimum height for the item. If the content is bigger than the height provided, the 
	 * item will expand to the content height.
	 * @param fixedHeight true to fix the height
	 */
	public void setFixedHeight(boolean fixedHeight)
	{
		storyboardPanel.setFixedHeight(fixedHeight);
	}
	
	/**
	 * It this is set to true, the item width will be fixed to the provided value through the methods
	 * setLargeDeviceItemWidth. If false, the value provided to this method
	 * will be the minimum width for the item. If the content is bigger than the width provided, the 
	 * item will expand to the content width.
	 * @param fixedHeight true to fix the width
	 */
	public void setFixedWidth(boolean fixedWidth)
	{
		storyboardPanel.setFixedWidth(fixedWidth);
	}
	
	/**
	 * Set the horizontal alignment for the items on this Storyboard. It only affects large devices, 
	 * as on small devices, the items fills all the space available horizontally.
	 * @param value alignment
	 */
	public void setHorizontalAlignment(HasHorizontalAlignment.HorizontalAlignmentConstant value)
	{
		storyboardPanel.setHorizontalAlignment(value);
	}
	
	/**
	 * Set the horizontal alignment for the given item on this Storyboard. It only affects large devices, 
	 * as on small devices, the items fills all the space available horizontally.
	 * @param child the item
	 * @param value alignment
	 */
	public void setHorizontalAlignment(IsWidget child, HasHorizontalAlignment.HorizontalAlignmentConstant value)
	{
		storyboardPanel.setHorizontalAlignment(child, value);
	}

	/**
	 * Set the height of the given item on this Storyboard, when displaying on a large device. 
	 * @param child the item
	 * @param height item height.
	 */
	public void setLargeDeviceItemHeight(IsWidget child, String height)
	{
		storyboardPanel.setLargeDeviceItemHeight(child, height);
	}
	
	/**
	 * Set the height of each item on this Storyboard, when displaying on a large device. 
	 * @param height item height.
	 */
	public void setLargeDeviceItemHeight(String height)
	{
		storyboardPanel.setLargeDeviceItemHeight(height);
	}
	
	/**
	 * Set the width of the given item on this Storyboard, when displaying on a large device. 
	 * @param child the item
	 * @param width item width.
	 */
	public void setLargeDeviceItemWidth(IsWidget child, String width)
	{
		storyboardPanel.setLargeDeviceItemWidth(child, width);
	}
	
	/**
	 * Set the width of each item on this Storyboard, when displaying on a large device. 
	 * @param width item width.
	 */
	public void setLargeDeviceItemWidth(String width)
	{
		storyboardPanel.setLargeDeviceItemWidth(width);
	}
	
	/**
	 * Set the height of the given item on this Storyboard, when displaying on a small device. 
	 * @param child the item
	 * @param height item height.
	 */
	public void setSmallDeviceItemHeight(IsWidget child, String height)
	{
		storyboardPanel.setSmallDeviceItemHeight(child, height);
	}
	
	/**
	 * Set the height of each item on this Storyboard, when displaying on a small device. 
	 * @param height item height.
	 */
	public void setSmallDeviceItemHeight(String height)
	{
		storyboardPanel.setSmallDeviceItemHeight(height);
	}
	
	/**
	 * Set the vertical alignment for items on this Storyboard.
	 * @param value alignment
	 */
	public void setVerticalAlignment(HasVerticalAlignment.VerticalAlignmentConstant value)
	{
		storyboardPanel.setVerticalAlignment(value);
	}
	
	/**
	 * Set the vertical alignment for the given item on this Storyboard.
	 * @param child the item
	 * @param value alignment
	 */
	public void setVerticalAlignment(IsWidget child, HasVerticalAlignment.VerticalAlignmentConstant value)
	{
		storyboardPanel.setVerticalAlignment(child, value);
	}
}
