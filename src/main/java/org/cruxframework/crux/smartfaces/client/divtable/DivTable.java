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

import java.util.ArrayList;

import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class DivTable extends Composite
{
	FlowPanel table = new FlowPanel();
	ArrayList<DivRow> rows = new ArrayList<DivRow>();
	
	public DivTable()
	{
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
		initWidget(table);
		setStyleName("table-container");
	}

	public void add(Widget widget)
	{
		rows.get(rows.size()-1).add(widget);
	}
	
	public void clear()
	{
		rows = new ArrayList<DivRow>();
		table.clear();
	}

	public void removeRow(int row)
	{
		table.remove(row);
	}

	public void setWidget(int row, int column, IsWidget widget)
	{
		DivRow rowList;
		
		if(rows.size() <= row)
		{
			rowList = insertRow(row);
		}
		rowList = rows.get(row);
		
		rowList.insert(widget, column);
	}
	
	public int getRowCount()
	{
		if(rows == null)
		{
			return 0;
		}
		return rows.size();
	}
	
	public DivRow insertRow(int index)
	{
		DivRow row = new DivRow();
		rows.add(index, row);
		table.insert(row, index);
		return row;
	}
	
	public DivRow addRow()
	{
		DivRow row = new DivRow();
		rows.add(row);
		table.add(row);
		return row;
	}
}
