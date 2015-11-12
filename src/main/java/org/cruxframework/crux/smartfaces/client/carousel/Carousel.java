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
package org.cruxframework.crux.smartfaces.client.carousel;

import org.cruxframework.crux.core.client.dataprovider.DataProvider;
import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPageable;
import org.cruxframework.crux.core.client.dto.DataObject;
import org.cruxframework.crux.core.client.factory.WidgetFactory;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.storyboard.Storyboard;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A carousel of items that support {@link DataProvider}s to provide a collection of data.
 * @author Thiago da Rosa de Bustamante
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
public class Carousel<T> extends AbstractPageable<T, Storyboard>
{
	public static final String DEFAULT_STYLE_NAME = "faces-Carousel";
	public static final String PAGE_PANEL_STYLE_NAME = "page";

	protected SimplePanel contentPanel = new SimplePanel();
	protected boolean fixedHeight = true;
	protected boolean fixedWidth = true;
	protected HorizontalAlignmentConstant horizontalAlignment;
	protected String largeDeviceItemHeight;
	protected String largeDeviceItemWidth;
	protected String smallDeviceItemHeight;
	protected VerticalAlignmentConstant verticalAlignment;
	protected WidgetFactory<T> widgetFactory;

	/**
	 * Constructor
	 * @param widgetFactory a factory to create widgets for each {@link DataObject} 
	 * provided by the {@link DataProvider}
	 */
	public Carousel(WidgetFactory<T> widgetFactory) 
	{
		assert(widgetFactory != null);
		this.widgetFactory = widgetFactory;
		initWidget(contentPanel);
		setStyleName(DEFAULT_STYLE_NAME);
	}
	
	@Override
	public void reset(boolean reloadData)
	{
		clear();
		super.reset(reloadData);
	}
	
	/**
	 * If this is set to true, the item height will be fixed to the provided value through the methods
	 * setSmallDeviceItemHeight and setLargeDeviceItemHeight. If false, the value provided to this method
	 * will be the minimun height for the item. If the content is bigger than the height provided, the 
	 * item will expand to the content height.
	 * @param fixedHeight true to fix the height
	 */
	public void setFixedHeight(boolean fixedHeight)
	{
		this.fixedHeight = fixedHeight;
		if (getPagePanel() != null)
		{
			getPagePanel().setFixedHeight(fixedHeight);
		}
	}
	
	/**
	 * If this is set to true, the item width will be fixed to the provided value through the methods
	 * setLargeDeviceItemWidth. If false, the value provided to this method
	 * will be the minimun width for the item. If the content is bigger than the width provided, the 
	 * item will expand to the content width.
	 * @param fixedHeight true to fix the width
	 */
	public void setFixedWidth(boolean fixedWidth)
	{
		this.fixedWidth = fixedWidth;
		if (getPagePanel() != null)
		{
			getPagePanel().setFixedWidth(fixedWidth);
		}
	}
	
	/**
	 * Set the horizontal alignment for the items on this carousel. It only affects large devices, 
	 * as on small devices, the items fills all the space available horizontally.
	 * @param value alignment
	 */
	public void setHorizontalAlignment(HasHorizontalAlignment.HorizontalAlignmentConstant value)
	{
		this.horizontalAlignment = value;
		if (getPagePanel() != null)
		{
			getPagePanel().setHorizontalAlignment(horizontalAlignment);
		}
		
	}

	/**
	 * Set the height of each item on this carousel, when displaying on a large device. 
	 * @param height item height.
	 */
	public void setLargeDeviceItemHeight(String height)
	{
		this.largeDeviceItemHeight = height;
		if (getPagePanel() != null)
		{
			getPagePanel().setLargeDeviceItemHeight(largeDeviceItemHeight);
		}
	}
	
	/**
	 * Set the width of each item on this carousel, when displaying on a large device. 
	 * @param width item width.
	 */
	public void setLargeDeviceItemWidth(String width)
	{
		this.largeDeviceItemWidth = width;
		if (getPagePanel() != null)
		{
			getPagePanel().setLargeDeviceItemWidth(largeDeviceItemWidth);
		}
	}
	
	/**
	 * Set the height of each item on this carousel, when displaying on a small device. 
	 * @param height item height.
	 */
	public void setSmallDeviceItemHeight(String height)
	{
		this.smallDeviceItemHeight = height;
		if (getPagePanel() != null)
		{
			getPagePanel().setSmallDeviceItemHeight(smallDeviceItemHeight);
		}
	}
	
	/**
	 * Set the vertical alignment for items on this carousel.
	 * @param value alignment
	 */
	public void setVerticalAlignment(HasVerticalAlignment.VerticalAlignmentConstant value)
	{
		this.verticalAlignment = value;
		if (getPagePanel() != null)
		{
			getPagePanel().setVerticalAlignment(verticalAlignment);
		}
	}

	@Override
	protected void clear() 
	{
		if (getPagePanel() != null)
		{
			getPagePanel().clear();
		}
	}

	@Override
	protected void clearRange(int startRecord) 
	{
		Storyboard pagePanel = getPagePanel();
		if (pagePanel != null)
		{
			while (pagePanel.getWidgetCount() > startRecord)
			{
				pagePanel.remove(startRecord);
			}
		}
	}

	@Override
	protected Panel getContentPanel() 
	{
		return contentPanel;
	}

	@Override
	protected DataProvider.DataReader<T> getDataReader()
	{
	    return new DataProvider.DataReader<T>()
	    {
			@Override
            public void read(T value, int index)
            {
				IsWidget widget = widgetFactory.createWidget(value);
				getPagePanel().add(widget);
            }
	    };
	}

	@Override
    protected Storyboard initializePagePanel()
    {
		Storyboard pagePanel = new Storyboard();
		pagePanel.setStyleName(PAGE_PANEL_STYLE_NAME);
		pagePanel.setFixedHeight(fixedHeight);
		pagePanel.setFixedWidth(fixedWidth);
		if (horizontalAlignment != null)
		{
			pagePanel.setHorizontalAlignment(horizontalAlignment);
		}
		if (largeDeviceItemHeight != null)
		{
			pagePanel.setLargeDeviceItemHeight(largeDeviceItemHeight);
		}
		if (largeDeviceItemWidth != null)
		{
			pagePanel.setLargeDeviceItemWidth(largeDeviceItemWidth);
		}
		if (smallDeviceItemHeight != null)
		{
			pagePanel.setSmallDeviceItemHeight(smallDeviceItemHeight);
		}
		if (verticalAlignment != null)
		{
			pagePanel.setVerticalAlignment(verticalAlignment);
		}
		return pagePanel;
    }
}
