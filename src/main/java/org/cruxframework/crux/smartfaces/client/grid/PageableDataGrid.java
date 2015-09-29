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
package org.cruxframework.crux.smartfaces.client.grid;

import java.util.Comparator;
import java.util.LinkedList;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.dataprovider.DataLoadedEvent;
import org.cruxframework.crux.core.client.dataprovider.DataLoadedHandler;
import org.cruxframework.crux.core.client.dataprovider.DataProvider.DataReader;
import org.cruxframework.crux.core.client.dataprovider.PageRequestedEvent;
import org.cruxframework.crux.core.client.dataprovider.PageRequestedHandler;
import org.cruxframework.crux.core.client.dataprovider.PagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPageable;
import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.core.client.factory.DataFactory;
import org.cruxframework.crux.core.client.factory.WidgetFactory;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.divtable.DivRow;
import org.cruxframework.crux.smartfaces.client.divtable.DivTable;
import org.cruxframework.crux.smartfaces.client.panel.SelectablePanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements a div grid based widget.
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
public class PageableDataGrid<T> extends AbstractPageable<T, DivTable>
{
	/**
	 * Handles how the cell should render in the edition mode.
	 * @author samuel.cardoso
	 *
	 * @param <T> the DataObject type.
	 * @param <K> the property type inside of DataObject.
	 */
	public static abstract class CellEditor<T, K> implements WidgetFactory<T>
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

		/**
		 * Renders the cell.
		 * @param grid
		 * @param rowIndex
		 * @param columnIndex
		 * @param dataObject
		 * @param row 
		 */
		public void render(
			final PageableDataGrid<T> grid, 
			final int rowIndex, 
			final int columnIndex, 
			final T dataObject 
			)
		{
			final IsWidget widget = CellEditor.this.createWidget(dataObject);
			assert(widget != null): "widget cannot be null";

			grid.drawCell(grid, grid.hasHeader ? rowIndex+1 : rowIndex, columnIndex, (Widget) widget, false);

			maybeAddHandlerToUpdateRow(grid, rowIndex, dataObject, widget);
		}

		/**
		 * User has to inform how the data will be saved in the dataObject.
		 * @param dataObject the dataObject. 
		 * @param object the new value to be inserted in the dataObject. 
		 */
		public abstract void setProperty(T dataObject, K object);
	}
	
	/**
	 * Define the column data type.
	 * @author samuel.cardoso
	 *
	 * @param <V>
	 */
	public class Column<V extends IsWidget>
	{
		private ColumnComparator<T> columnComparator;
		private int columnIndex = 0;
		private DataFactory<V, T> dataFactory;
		private CellEditor<T, ?> editableCell;
		private IsWidget headerWidget;
		private Row row;
		private boolean sortable = false;

		public Column(DataFactory<V, T> dataFactory)
		{
			assert(dataFactory != null): "dataFactory must not be null";
			this.dataFactory = dataFactory;
			this.columnIndex = columns != null ? columns.size() : 0;
		}

		/**
		 * Clear the column content.
		 */
		public void clear()
		{
			row = null;
		}

		/**
		 * @return the data factory.
		 */
		public DataFactory<V, T> getDataFactory()
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

		public Row getRow() 
		{
			return row;
		}

		public void render() 
		{
			if(row.isEditing() && editableCell != null)
			{
				renderToEdit();
			}
			else
			{
				renderToView();						
			}
		}

		private void renderToEdit() 
		{
			editableCell.render(PageableDataGrid.this, row.gridRowIndex, columnIndex, row.dataObject);
		}

		private void renderToView() 
		{
			V widget = dataFactory.createData(row.dataObject);

			if(widget == null)
			{
				return;
			}

			drawCell(PageableDataGrid.this, hasHeader ? row.gridRowIndex+1 : row.gridRowIndex, columnIndex, widget, false);
		}

		public Column<V> setCellEditor(CellEditor<T, ?> editableCell)
		{
			this.editableCell = editableCell;
			return this;
		}

		public Column<V> setComparator(Comparator<T> comparator)
		{
			this.columnComparator = new ColumnComparator<T>();
			columnComparator.comparator = comparator;
			columnComparator.multiplier = 1;
			return this;
		}

		public Column<V> setDataFactory(DataFactory<V, T> dataFactory)
		{
			this.dataFactory = dataFactory;
			return this;
		}

		public Column<V> setHeaderWidget(IsWidget headerWidget)
		{
			this.headerWidget = headerWidget;
			return this;
		}

		public void setRow(PageableDataGrid<T>.Row row) 
		{
			this.row = row;
		}

		public Column<V> setSortable(boolean sortable)
		{
			this.sortable = sortable;
			return this;
		}

		public void sort()
		{
			assert(getDataProvider() != null) :"No dataProvider set for this component.";
			getDataProvider().sort(new Comparator<T>()
			{
				@Override
				public int compare(T o1, T o2)
				{
					int compareResult = 0;
					int index = 0;
					//uses all the comparators that were added when uses clicks in the column header.
					while((compareResult == 0) && (index != linkedComparators.size()))
					{
						ColumnComparator<T> columnComparator = linkedComparators.get(index);
						compareResult = columnComparator.comparator.compare(o1, o2)*columnComparator.multiplier;
						index++;
					}
					return compareResult;
				}
			});
		}
	}
	/**
	 * Encapsulate the comparator adding a variable to indicate if the ordering should be
	 * ascending or descending.
	 * @author samuel.cardoso
	 *
	 * @param <T>
	 */
	private static class ColumnComparator<T>
	{
		private Comparator<T> comparator;
		private short multiplier = -1; 
	}
	/**
	 * Define the row data type.
	 * @author samuel.cardoso
	 *
	 */
	public class Row
	{
		private T dataObject;
		private int dataProviderRowIndex;
		private boolean editing;
		private int gridRowIndex;
		private T oldDataObject;

		public Row(T dataObject, int gridRowIndex, int dataProviderRowIndex)
		{
			this.dataObject = dataObject;
			this.oldDataObject = dataObject;

			this.gridRowIndex = gridRowIndex;
			this.dataProviderRowIndex = dataProviderRowIndex;
		}

		/**
		 * Put the Row in the edition mode.
		 */
		public void edit()
		{
			if(!editing)
			{
				dataObject = getDataProvider().get(dataProviderRowIndex);
				getDataProvider().set(dataProviderRowIndex, dataObject);
				editing = true;
				refresh();
			}
		}

		/**
		 * @return the row index.
		 */
		public int getRowIndex() 
		{
			return gridRowIndex;
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
		 * Refreshes the all the row columns. 
		 */
		public void refresh()
		{
			drawColumns(this);
		}

		/**
		 * Undo the changes for a single row.
		 */
		public void undoChanges()
		{
			dataObject = oldDataObject;
			getDataProvider().set(dataProviderRowIndex, dataObject);
			editing = false;
			refresh();
		}
	}

	private Array<Column<?>> columns = CollectionFactory.createArray();

	private SimplePanel contentPanel = new SimplePanel();

	private boolean hasHeader;

	private LinkedList<ColumnComparator<T>> linkedComparators = new LinkedList<ColumnComparator<T>>();
	
	private Array<Row> rows = CollectionFactory.createArray();

	/**
	 * @param dataProvider the dataprovider.
	 * @param autoLoadData if true, the data must be loaded after the constructor has been invoked.
	 */
	public PageableDataGrid(final PagedDataProvider<T> dataProvider, boolean autoLoadData)
	{
		initWidget(contentPanel);
		setDataProvider(dataProvider, autoLoadData);
		setAllowRefreshAfterDataChange(false);

		dataProvider.addDataLoadedHandler(new DataLoadedHandler() 
		{
			@Override
			public void onLoaded(DataLoadedEvent event) 
			{
				Scheduler.get().scheduleDeferred(new ScheduledCommand() 
				{
					@Override
					public void execute() 
					{
						PageableDataGrid.super.refreshPage(dataProvider.getCurrentPageStartRecord());		
					}
				});

				dataProvider.addPageRequestedHandler(new PageRequestedHandler() 
				{
					@Override
					public void onPageRequested(PageRequestedEvent event) 
					{
						if(PageableDataGrid.this.pager == null || !PageableDataGrid.this.pager.supportsInfiniteScroll())
						{
							rows.clear();
						}
					}
				});
			}
		});
	}

	/**
	 * @param column the column to be added.
	 */
	public void addColumn(Column<?> column)
	{
		columns.add(column);
	}

	@Override
	protected void clear()
	{
		for (int index = 0; index < getColumns().size(); index++)
		{
			PageableDataGrid<T>.Column<?> dataGridColumn = getColumns().get(index);
			dataGridColumn.clear();
		}
		getPagePanel().clear();
	}

	@Override
	protected void clearRange(int pageStart)
	{
		while (getPagePanel().getRowCount() > pageStart)
		{
			getPagePanel().removeRow(pageStart);
		}
	}

	@Override
	public void commit() 
	{
		for(int i=0; i<rows.size();i++)
		{
			rows.get(i).makeChanges();
		}

		super.commit();
	}

	private void drawCell(PageableDataGrid<T> grid, int rowIndex, int columnIndex, IsWidget widget, boolean isHeader)
	{
		DivRow row = grid.getPagePanel().setWidget(rowIndex, columnIndex, widget);

		//handle the header
		if(isHeader)
		{
			row.removeStyleName("even");
			row.addStyleName("header");
		}
	}
	
	private void drawColumns(Row row)
	{
		//detect if at least one column has a header
		hasHeader = false;
		for(int columnIndex = 0; columnIndex < columns.size(); columnIndex++)
		{
			PageableDataGrid<T>.Column<?> column = columns.get(columnIndex);

			if(column.getHeaderWidget() != null)
			{
				hasHeader = true;
			}
		}

		//iterate over the columns to render the cells (and the header)
		for(int columnIndex = 0; columnIndex < columns.size(); columnIndex++)
		{
			final PageableDataGrid<T>.Column<?> column = columns.get(columnIndex);
			column.setRow(row);
			//header
			maybeCreateHeader(row, columnIndex, column);
			//body
			column.render();
		}
	}

	/**
	 * @return all the table columns.
	 */
	public Array<Column<?>> getColumns()
	{
		return columns;
	}

	@Override
	protected Panel getContentPanel()
	{
		return contentPanel;
	}

	@Override
	protected DataReader<T> getDataReader() 
	{
		return new DataReader<T>() 
		{
			@Override
			public void read(T dataObject, int dataProviderRowIndex) 
			{
				int gridRowIndex = 0;
				if(PageableDataGrid.this.pager != null && PageableDataGrid.this.pager.supportsInfiniteScroll())
				{
					gridRowIndex = dataProviderRowIndex;					
				}
				else
				{
					gridRowIndex = dataProviderRowIndex % getDataProvider().getPageSize();
				}

				Row row = null;
				if(rows.size() <= gridRowIndex)
				{
					row = new Row(dataObject, gridRowIndex, dataProviderRowIndex);
					rows.insert(gridRowIndex, row);
				}
				else
				{
					row = rows.get(gridRowIndex);
				}

				drawColumns(row);
			}
		};
	}

	/**
	 * @param boundObject
	 * @return a row given its bound object.
	 */
	public Row getRow(T boundObject)
	{
		int rowIndex = getDataProvider().indexOf(boundObject);
		return rows.get(rowIndex);
	}

	/**
	 * @return all the table rows. 
	 */
	public Array<Row> getRows()
	{
		return rows;
	}

	@Override
	protected DivTable initializePagePanel()
	{
		DivTable divTable = new DivTable();
		return divTable;
	}

	private boolean isFirstRow(Row row)
	{
		return row.gridRowIndex == 0;
	}

	private void maybeCreateHeader(Row row, int columnIndex, final PageableDataGrid<T>.Column<?> column)
	{
		if(isFirstRow(row) && hasHeader)
		{
			SelectablePanel headerWrapper = new SelectablePanel();
			headerWrapper.add(column.getHeaderWidget());
			if(column.sortable)
			{
				headerWrapper.addSelectHandler(new SelectHandler()
				{
					@Override
					public void onSelect(SelectEvent event)
					{
						linkedComparators.remove(column.columnComparator);
						column.columnComparator.multiplier *= -1;
						linkedComparators.addFirst(column.columnComparator);
						column.sort();
					}
				});
			}

			drawCell(PageableDataGrid.this, 0, columnIndex, headerWrapper, true);
		}
	}

	/**
	 * Rolls back any transaction started in the edition mode.
	 */
	@Override
	public void rollback() 
	{
		for(int i=0; i<rows.size();i++)
		{
			rows.get(i).undoChanges();
		}

		super.rollback();
	}
}
