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

import java.util.ArrayList;

import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 * 
 * This is a simple table based on divs.
 */
public class DivTable extends Composite
{
	public static final String STYLE_FACES_GRID = "faces-DivTable";
	
	private static final String STYLE_FACES_ROW_EVEN = "rowEven";
	private static final String STYLE_FACES_ROW_ODD = "rowOdd";
	
	private ArrayList<DivRow> rows = new ArrayList<DivRow>();
	private FlowPanel table = new FlowPanel();

	private DivTableHandler tableHandler;

	public DivTable(DivTableHandler tableHandler)
	{
		this.tableHandler = tableHandler;
		initWidget(table);
		setStyleName(STYLE_FACES_GRID);
		addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesDivTableContainer());
	}
	
	public void add(Widget widget)
	{
		rows.get(rows.size()-1).add(widget);
	}

	public void clear()
	{
		rows.clear();
		table.clear();
	}

	public DivRow getRow(int index)
	{
		if(rows == null || rows.isEmpty())
		{
			return null;
		}
		
		return rows.get(index);
	}

	public int getRowCount()
	{
		if(rows == null)
		{
			return 0;
		}
		return rows.size();
	}

	public Widget getWidget(int row, int column)
	{
		if(rows == null)
		{
			return null;
		}

		DivRow divRow = rows.get(row);

		if(divRow == null)
		{
			return null;
		}

		return ((FlowPanel)rows.get(row).getWidget(column)).getWidget(0);
	}

	public void removeRow(int rowIndex)
	{
		rows.remove(rowIndex);
		table.remove(rowIndex);
	}

	public DivRow setWidget(int row, int column, IsWidget widget, String width)
	{
		return setWidget(row, column, widget, null, width);
	}

	public DivRow setWidget(int row, int column, IsWidget widget, String styleName, String width)
	{
		DivRow rowList;

		if(rows.size() <= row)
		{
			rowList = insertRow(row);
		} 
		else
		{
			rowList = rows.get(row);
		}

		rowList.insert(widget, column, styleName, width);

		return rowList;
	}
	
	protected String getColumnClassName(int columnIndex)
    {
		return tableHandler.getColumnClassName(columnIndex);
    }

	protected boolean initClassNameForColumn(int columnIndex)
	{
		return tableHandler.initClassNameForColumn(columnIndex);
	}
	
	protected void injectStyle(String rule)
	{
		tableHandler.injectStyle(rule);
	}
	
	/**
	 * Replaces the current row or creates a new one.
	 * @param index 
	 * @return
	 */
	private DivRow insertRow(int index)
	{
		DivRow row = new DivRow(this);

		if(index%2 == 0)
		{
			row.addStyleName(STYLE_FACES_ROW_EVEN);
		}
		else
		{
			row.addStyleName(STYLE_FACES_ROW_ODD);
		}

		rows.add(index, row);
		table.insert(row, index);
		return row;
	}
	
	public static interface DivTableHandler
	{
		String getColumnClassName(int columnIndex);
		boolean initClassNameForColumn(int columnIndex);
		void injectStyle(String rule);
	}
}
