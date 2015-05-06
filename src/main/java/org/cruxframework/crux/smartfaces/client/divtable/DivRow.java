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
package org.cruxframework.crux.smartfaces.client.divtable;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;

/**
 * A panel that shows its items as an ordered list (using {@code <ol> and <li>} tags)
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 */
public class DivRow extends FlowPanel// extends AbstractDivRow 
{
	public DivRow()
	{
		setStyleName("row");
	}
	
	public void add(IsWidget widget) 
	{
		FlowPanel column = new FlowPanel();
		column.add(widget);
		add(column);
		setStyleProperties(widget, column);
	}
	
	private void setStyleProperties(final IsWidget widget, final FlowPanel column)
    {
		((UIObject) widget).getElement().getStyle().setProperty("width", "100%");
		
//		column.getElement().getStyle().setProperty("order", String.valueOf(getWidgetIndex(widget)));
		column.getElement().setClassName("column");
    }

	public void insert(IsWidget widget, int columnIndex) 
	{
		FlowPanel column = null;
		try
		{
			column = (FlowPanel) getWidget(columnIndex);
			column.clear();
			column.add(widget);
		} catch (IndexOutOfBoundsException e)
		{
			add(widget);
		}
	}	
	
//	public DivRow()
//    {
//    }
//
//	public DivRow(String styleName)
//	{
//		super(styleName);
//	}
//
//	@Override
//	protected Element createElement() 
//	{
//		return DOM.createElement("div");	
//	}
//
//	@Override
//	protected String getDefaultClassName() 
//	{
//		return "row";
//	}
}
