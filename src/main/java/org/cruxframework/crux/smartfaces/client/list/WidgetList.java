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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A list of widgets
 * @author Thiago da Rosa de Bustamante
 */
public class WidgetList<T> extends AbstractPageable<T, FlowPanel>
{
	private static final String DEFAULT_STYLE_NAME = "faces-WidgetList";
	private static final String PAGE_PANEL_STYLE_NAME = "page";
	
	protected SimplePanel contentPanel = new SimplePanel();
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
		FlowPanel pagePanel = getPagePanel();
		if (pagePanel != null)
		{
			int widgetIndex = pagePanel.getWidgetIndex(w);
			if (widgetIndex >= 0)
			{
				if (pager != null && !pager.supportsInfiniteScroll())
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
		FlowPanel pagePanel = getPagePanel();
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
		FlowPanel pagePanel = getPagePanel();
		if (pagePanel != null)
		{
			pagePanel.clear();
		}
	}
	
	@Override
	protected void clearRange(int start)
	{
		FlowPanel pagePanel = getPagePanel();
		if (pagePanel != null)
		{
			while (pagePanel.getWidgetCount() > start)
			{
				pagePanel.remove(start);
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
	
	protected String getPagePanelStyleName()
    {
	    return PAGE_PANEL_STYLE_NAME;
    }

	@Override
    protected FlowPanel initializePagePanel()
    {
		FlowPanel pagePanel = new FlowPanel();
		pagePanel.setStyleName(getPagePanelStyleName());
		return pagePanel;
    }

	@Override
	protected void onDataSelected(T recordObject, boolean selected)
	{
	    // TODO This widget does not support selection
	}	
}
