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

package org.cruxframework.crux.smartfaces.client.grid;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Defines a group of column type.
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 * @param <T> the Data Object type
 */
public class ColumnGroup<T>
{
	ColumnGroup<T> columnGroupParent;
	IsWidget header;
	int index = Integer.MAX_VALUE;
	String key;
	String width;

	protected ColumnGroup(String key)
	{
		this.key = key;
	}

	public ColumnGroup<T> addColumn(Column<T, ?> column)
	{
		//find out the smaller index
		if(column.index < index)
		{
			index = column.index;
		}
		column.columnGroup = ColumnGroup.this;
		return this;
	}

	public IsWidget getHeaderWidget()
	{
		return header;
	}

	public ColumnGroup<T> setHeaderWidget(IsWidget header)
	{
		this.header = header;
		return this;
	}
	
	public ColumnGroup<T> setWidth(String width)
	{
		this.width = width;
		return this;
	}
}