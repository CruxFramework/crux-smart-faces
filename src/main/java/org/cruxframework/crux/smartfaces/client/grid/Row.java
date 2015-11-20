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

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * Define the row data type.
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 * @param <T> the Data Object type.
 */
public class Row<T>
{
	CheckBox checkbox;
	T dataObject;
	int dataProviderRowIndex;
	boolean editing;
	int index;
	HandlerRegistration onSelectionHandlerRegistration;
	RadioButton radioButton;
	private DivRow divRow;
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
			editing = true;
			refresh();
		}
	}

	public DivRow getDivRow()
	{
		return divRow;
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
		oldDataObject = dataObject;
		concludeOperation();
	}

	public void setDivRow(DivRow divRow)
	{
		this.divRow = divRow;
		this.divRow.addAttachHandler(new Handler()
		{
			@Override
			public void onAttachOrDetach(AttachEvent event)
			{
				if(!event.isAttached() && onSelectionHandlerRegistration != null)
				{
					onSelectionHandlerRegistration.removeHandler();
					onSelectionHandlerRegistration = null;
				}
			}
		});
	}

	/**
	 * Undo the changes for a single row.
	 */
	public void undoChanges()
	{
		dataObject = oldDataObject;
		concludeOperation();
	}

	/**
	 * Refreshes the all the row columns. 
	 */
	void refresh()
	{
		grid.drawColumnsAndDetails(this);
	}

	private void concludeOperation()
	{
		grid.setForEdition(dataProviderRowIndex, dataObject);
		editing = false;
		refresh();
	}
}