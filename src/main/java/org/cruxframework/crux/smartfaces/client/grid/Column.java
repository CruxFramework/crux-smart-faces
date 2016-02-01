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

import java.util.ArrayList;
import java.util.Comparator;

import org.cruxframework.crux.smartfaces.client.grid.Type.RowSelectStrategy;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Define the column data type.
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 * @param <T> the Data Object type.
 * @param <W> the cell Widget.
 */
public class Column<T, W extends IsWidget>
{
	ColumnComparator<T> columnComparator;
	ColumnGroup<T> columnGroup;
	boolean detail = false;
	IsWidget headerWidget;
	int index = 0;
	String key;
	Row<T> row;
	RowSelectStrategy rowSelectStrategy = null;
	boolean sortable = true;
	boolean sorted;
	private GridDataFactory<T> dataFactory;
	private CellEditor<T, ?> editableCell;
	private final PageableDataGrid<T> grid;
	private ArrayList<String> keys = new ArrayList<String>();

	protected Column(PageableDataGrid<T> pageableDataGrid, GridDataFactory<T> dataFactory, String key, boolean detail)
	{
		this.grid = pageableDataGrid;
		this.detail = detail;
		assert(!keys.contains(key)): "key must be unique.";
		this.key = key;
		assert(dataFactory != null): "dataFactory must not be null";
		this.dataFactory = dataFactory;
		this.index = grid.getColumns() != null ? grid.getColumns().size() : 0;
	}

	public Column<T, W> add()
	{
		grid.addColumn(this);
		return this;
	}

	/**
	 * @return the data factory.
	 */
	public GridDataFactory<T> getDataFactory()
	{
		return dataFactory;
	}

	/**
	 * @return the widget 
	 */
	public IsWidget getHeaderWidget() 
	{
		return headerWidget;
	}

	/**
	 * @return true if the column show be rendered as a detail widget
	 * and false otherwise.
	 */
	public boolean isDetail()
	{
		return detail;
	}

	public Column<T, W> setCellEditor(CellEditor<T, ?> editableCell)
	{
		this.editableCell = editableCell;
		return this;
	}

	public Column<T, W> setComparator(Comparator<T> comparator)
	{
		this.columnComparator = new ColumnComparator<T>();
		columnComparator.comparator = comparator;
		columnComparator.multiplier = 1;
		return this;
	}

	/**
	 * @param detail true if the column should be rendered
	 * as a detail column and false otherwise.
	 */
	public void setDetail(boolean detail)
	{
		grid.columns.remove(this);
		grid.detailColumns.add(this);
		this.detail = detail;
	}

	public Column<T, W> setHeaderWidget(IsWidget headerWidget)
	{
		this.headerWidget = headerWidget;
		return this;
	}

	public Column<T, W> setSortable(boolean sortable)
	{
		this.sortable = sortable;
		return this;
	}

	public void sort()
	{
		assert(grid.getDataProvider() != null) :"No dataProvider set for this component.";
		sorted = true;
		grid.getDataProvider().sort(new Comparator<T>()
		{
			@Override
			public int compare(T o1, T o2)
			{
				int compareResult = 0;
				int index = 0;
				//uses all the comparators that were added when uses clicks in the column header.
				while((compareResult == 0) && (index != grid.linkedComparators.size()))
				{
					ColumnComparator<T> columnComparator = grid.linkedComparators.get(index);
					compareResult = columnComparator.comparator.compare(o1, o2)*columnComparator.multiplier;
					index++;
				}
				return compareResult;
			}
		});
	}

	/**
	 * Clear the column content.
	 */
	void clear()
	{
		row = null;
	}

	IsWidget render(boolean detailColumn) 
	{
		if(row.editing && editableCell != null)
		{
			return renderToEdit(detailColumn);
		}
		else
		{
			return renderToView(detailColumn);
		}
	}

	private IsWidget renderToEdit(boolean detailColumn)
	{
		return editableCell.render(grid, row, index, detailColumn);
	}

	private IsWidget renderToView(boolean detailColumn) 
	{
		IsWidget widget = dataFactory.createData(row.dataObject, row.index);

		if(widget != null && !detailColumn)
		{
			grid.drawCell(row, index, widget);
		}

		return widget;
	}
	
	/**
	 * Encapsulate the comparator adding a variable to indicate if the ordering should be
	 * ascending or descending.
	 * @author samuel.cardoso
	 *
	 * @param <T>
	 */
	static class ColumnComparator<T>
	{
		Comparator<T> comparator;
		short multiplier = -1; 
	}
}