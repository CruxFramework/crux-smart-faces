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

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

public class DivRow extends FlowPanel 
{
	private static Array<String> columnClasses = CollectionFactory.createArray();
	
	public DivRow()
	{
		setStyleName("row");
	}
	
	public FlowPanel add(IsWidget widget, int columnIndex) 
	{
		FlowPanel column = new FlowPanel();
		column.add(widget);
		add(column);
		return column;
	}
	
	private void setStyleProperties(final FlowPanel column, int columnIndex)
    {
		Element element = column.getElement();
		element.addClassName("column");
		
		String columnName = "column_" + columnIndex;
		int columnClassesIndex = columnClasses.indexOf(columnName);
		if(columnClassesIndex >= 0)
		{
			element.addClassName(columnClasses.get(columnClassesIndex));
		} else
		{
			setClassContent(columnName, "order: " + String.valueOf(columnIndex));
			element.addClassName(columnName);
			columnClasses.insert(columnIndex, columnName);
		}
    }
	
	public static native void setClassContent(String className, String classContent) /*-{
		var style = document.createElement('style');
		style.type = 'text/css';
		style.innerHTML = '.' + className +' { '+ classContent +' }';
		$doc.getElementsByTagName('head')[0].appendChild(style);
	}-*/;

	public void insert(IsWidget widget, int columnIndex) 
	{
		FlowPanel column = null;
		try
		{
			column = (FlowPanel) getWidget(columnIndex);
			column.clear();
			column.add(widget);
			setStyleProperties(column, columnIndex);
		} catch (IndexOutOfBoundsException e)
		{
			column = add(widget, columnIndex);
			setStyleProperties(column, columnIndex);
		}
	}	
}
