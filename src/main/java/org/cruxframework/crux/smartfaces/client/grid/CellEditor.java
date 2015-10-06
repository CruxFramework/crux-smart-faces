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

import org.cruxframework.crux.core.client.factory.WidgetFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Handles how the cell should render in the edition mode.
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 * @param <T> the Data Object type.
 * @param <K> the property type inside of DataObject.
 */
public abstract class CellEditor<T, K> implements WidgetFactory<T>
{
	private boolean autoRefreshRow = true;

	public CellEditor()
	{
	}

	/**
	 * @param autoRefreshRow if true, the row will be refreshed when changed.
	 */
	public CellEditor(boolean autoRefreshRow)
	{
		this.autoRefreshRow = autoRefreshRow;
	}

	/**
	 * Renders the cell.
	 * @param grid
	 * @param rowIndex
	 * @param columnIndex
	 * @param dataProviderRowIndex 
	 * @param dataObject
	 * @param row 
	 */
	public void render(
		PageableDataGrid<T> grid, 
		int rowIndex, 
		int columnIndex, 
		int dataProviderRowIndex, 
		T dataObject 
		)
	{
		final IsWidget widget = CellEditor.this.createWidget(dataObject);
		assert(widget != null): "widget cannot be null";

		grid.drawCell(grid, rowIndex, columnIndex, dataProviderRowIndex, (Widget) widget);

		maybeAddHandlerToUpdateRow(grid, rowIndex, dataObject, widget);
	}

	/**
	 * User has to inform how the data will be saved in the dataObject.
	 * @param dataObject the dataObject. 
	 * @param object the new value to be inserted in the dataObject. 
	 */
	public abstract void setProperty(T dataObject, K object);

	@SuppressWarnings({ "unchecked" })
	private void maybeAddHandlerToUpdateRow(final PageableDataGrid<T> grid, final int rowIndex, final T dataObject,
		final IsWidget widget)
	{
		try
		{
			((HasValue<K>) widget).addValueChangeHandler(new ValueChangeHandler<K>()
			{
				@Override
				public void onValueChange(ValueChangeEvent<K> event)
				{
					CellEditor.this.setProperty(dataObject, event.getValue());
					if(autoRefreshRow)
					{
						grid.rows.get(rowIndex).makeChanges();
					}
				}
			});
		} catch (ClassCastException e){}
	}
}