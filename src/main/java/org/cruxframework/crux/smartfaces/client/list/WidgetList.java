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
package org.cruxframework.crux.smartfaces.client.list;

import org.cruxframework.crux.core.client.dataprovider.DataProvider;
import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPageable;
import org.cruxframework.crux.core.client.factory.WidgetFactory;
import org.cruxframework.crux.core.shared.Experimental;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A list of widgets
 * @author Thiago da Rosa de Bustamante
 *
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
public class WidgetList<T> extends AbstractPageable<T>
{
	private static final String DEFAULT_STYLE_NAME = "faces-WidgetList";
	private static final String PAGE_PANEL_STYLE_NAME = "faces-WidgetList-Page";
	
	protected SimplePanel contentPanel = new SimplePanel();
	protected FlowPanel pagePanel;
	protected final WidgetFactory<T> widgetFactory;

	/**
	 * Constructor
	 * @param widgetFactory
	 */
	public WidgetList(WidgetFactory<T> widgetFactory)
    {
		assert(widgetFactory != null);
		this.widgetFactory = widgetFactory;
		initWidget(contentPanel);
		setStyleName(DEFAULT_STYLE_NAME);
    }
	
	/**
	 * Retrieve the dataObject that is bound to the given widget
	 * @param w
	 * @return
	 */
	public T getDataObject(Widget w)
	{
		if (pagePanel != null)
		{
			int widgetIndex = pagePanel.getWidgetIndex(w);
			if (widgetIndex >= 0)
			{
				if (hasPageable != null && !hasPageable.supportsInfiniteScroll())
				{
				int numPreviousPage = getDataProvider().getCurrentPage() - 1;
					widgetIndex += (numPreviousPage*getPageSize());
				}
					
				return getDataProvider().get(widgetIndex);			
			}
		}
		return null;
	}
	
	/**
	 * Retrieve the widget index
	 * @param w
	 * @return
	 */
	public int getWidgetIndex(Widget w)
	{
		return pagePanel != null ? pagePanel.getWidgetIndex(w) : -1;
	}

	@Override
	public void reset(boolean reloadData)
	{
		clear();
		super.reset(reloadData);
	}
	
	@Override
	protected void clear()
	{
		if (pagePanel != null)
		{
			pagePanel.clear();
		}
	}
	
	@Override
	protected void clearRange(int start)
	{
		if (pagePanel != null)
		{
			while (pagePanel.getWidgetCount() > start)
			{
				pagePanel.remove(start);
			}
		}
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
				pagePanel.add(widget);
            }
	    };
	}
	
	@Override
    protected Panel initializePagePanel()
    {
		pagePanel = new FlowPanel();
		pagePanel.setStyleName(getPagePanelStyleName());
		return pagePanel;
    }

	@Override
	protected IsWidget getPagePanel()
	{
	    return pagePanel;
	}

	protected String getPagePanelStyleName()
    {
	    return PAGE_PANEL_STYLE_NAME;
    }

	@Override
    protected Panel getContentPanel()
    {
	    return contentPanel;
    }	
}
