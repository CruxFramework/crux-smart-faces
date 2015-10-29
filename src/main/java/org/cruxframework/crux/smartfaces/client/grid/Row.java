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

import org.cruxframework.crux.smartfaces.client.divtable.DivRow;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Define the row data type.
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 * @param <T> the Data Object type.
 */
public class Row<T>
{
	T dataObject;
	int dataProviderRowIndex;
	DivRow divRow;
	boolean editing;
	int index;
	HandlerRegistration onSelectionHandlerRegistration;
	private final PageableDataGrid<T> grid;
	private T oldDataObject;

	protected Row(PageableDataGrid<T> pageableDataGrid, T dataObject, int index, int dataProviderRowIndex)
	{
		grid = pageableDataGrid;
		this.dataObject = dataObject;
		this.oldDataObject = dataObject;

		this.index = index;
		this.dataProviderRowIndex = dataProviderRowIndex;
	}

	/**
	 * Put the Row in the edition mode.
	 */
	public void edit()
	{
		if(!editing)
		{
			dataObject = grid.getDataProvider().get(dataProviderRowIndex);
			grid.setForEdition(dataProviderRowIndex, dataObject);
			editing = true;
			refresh();
		}
	}

	/**
	 * @return the row index.
	 */
	public int getIndex() 
	{
		return index;
	}

	/**
	 * @return true if the row is in the edition mode and false otherwise.
	 */
	public boolean isEditing() 
	{
		return editing;
	}

	/**
	 * Commit all the changes for a single row.
	 */
	public void makeChanges()
	{
		editing = false;
		refresh();
	}

	/**
	 * Undo the changes for a single row.
	 */
	public void undoChanges()
	{
		dataObject = oldDataObject;
		grid.getDataProvider().set(dataProviderRowIndex, dataObject);
		editing = false;
		refresh();
	}

	/**
	 * Refreshes the all the row columns. 
	 */
	void refresh()
	{
		grid.drawColumns(this);
	}
}