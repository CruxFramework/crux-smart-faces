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
package org.cruxframework.crux.smartfaces.client.divtable;

import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.panel.SelectableFlowPanel;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 * 
 * This is a simple row based in divs.
 */
public class DivRow extends SelectableFlowPanel 
{
	public static final String STYLES_FACES_GRID_COLUMN = "column";
	public static final String STYLES_FACES_GRID_ROW = "row";
	private final DivTable divTable;

	
	protected DivRow(DivTable divTable)
	{
		this.divTable = divTable;
		setStyleName(STYLES_FACES_GRID_ROW);
		addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesDivTableRow());
	}

	public FlowPanel add(IsWidget widget, int columnIndex) 
	{
		FlowPanel column = new FlowPanel();
		column.add(widget);
		add(column);
		return column;
	}

	public int getColumnCount()
	{
		return getChildren().size();
	}
	
	public void insert(IsWidget widget, int columnIndex) 
	{
		insert(widget, columnIndex, null, null);	
	}
	
	public void insert(IsWidget widget, int columnIndex, String styleName, String width) 
	{
		assert(columnIndex >= 0) : "column index has to be positive.";
		FlowPanel column = null;
		if(columnIndex >= getChildren().size())
		{
			//create a new column.
			column = add(widget, columnIndex);
			initColumnStyle(column, columnIndex, styleName, width);	
		}
		else
		{
			//update the column
			column = (FlowPanel) getWidget(columnIndex);
			column.clear();
			column.add(widget);
			initColumnStyle(column, columnIndex, styleName, width);
		}
	}

	public void setColumnOrder(int columnIndex, int order)
	{
		assert(columnIndex < getColumnCount()) : "There is no column ["+columnIndex+"] on this row.";
		String columnName = divTable.getColumnClassName(columnIndex);
		divTable.injectStyle("."+columnName+"{order: " + order + ";}");
	}
	
	public void setColumnWidth(int columnIndex, String width)
	{
		assert(columnIndex < getColumnCount()) : "There is no column ["+columnIndex+"] on this row.";
		String columnName = divTable.getColumnClassName(columnIndex);
		divTable.injectStyle("."+columnName+"{width: " + width + ";}");
	}
	
	private void initColumnStyle(final FlowPanel column, int columnIndex, String styleName, String width)
	{
		Element element = column.getElement();
		if(StringUtils.isEmpty(styleName))
		{
			element.addClassName(STYLES_FACES_GRID_COLUMN);	
		}
		else
		{
			element.addClassName(styleName);
			return;
		}
		
		String columnName = divTable.getColumnClassName(columnIndex);
		element.addClassName(columnName);
		
		
		if (divTable.initClassNameForColumn(columnIndex))
		{
			if (StringUtils.isEmpty(width))
			{
				divTable.injectStyle("."+columnName+"{order: " + columnIndex + ";flex: 1;}");
			}
			else
			{
				divTable.injectStyle("."+columnName+"{order: " + columnIndex + ";width: " + width + ";}");
			}
		}
	}
}
