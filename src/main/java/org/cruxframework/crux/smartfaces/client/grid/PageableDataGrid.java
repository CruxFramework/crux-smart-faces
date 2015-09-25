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

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.dataprovider.DataLoadedEvent;
import org.cruxframework.crux.core.client.dataprovider.DataLoadedHandler;
import org.cruxframework.crux.core.client.dataprovider.DataProvider.DataReader;
import org.cruxframework.crux.core.client.dataprovider.PageRequestedEvent;
import org.cruxframework.crux.core.client.dataprovider.PageRequestedHandler;
import org.cruxframework.crux.core.client.dataprovider.PagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPageable;
import org.cruxframework.crux.core.client.factory.DataFactory;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.divtable.DivTable;
import org.cruxframework.crux.smartfaces.client.grid.Type.SelectStrategy;
import org.cruxframework.crux.smartfaces.client.label.Label;

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
 * @author samuel.cardoso
 *
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
public class PageableDataGrid<T> extends AbstractPageable<T, DivTable>
{
	public static final String STYLE_FACES_DATAGRID = "faces-Datagrid";
	
	protected SimplePanel contentPanel = new SimplePanel();
	private SelectStrategy selectStrategy = SelectStrategy.SINGLE;
	private Array<Column<?>> columns = CollectionFactory.createArray();
	private Array<Row> rows = CollectionFactory.createArray();
	
	/**
	 * @param dataProvider the dataprovider.
	 * @param autoLoadData if true, the data must be loaded after the constructor has been invoked.
	 */
	public PageableDataGrid(final PagedDataProvider<T> dataProvider, boolean autoLoadData)
	{
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
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
						if(PageableDataGrid.this.hasPageable == null || !PageableDataGrid.this.hasPageable.supportsInfiniteScroll())
						{
							rows.clear();
						}
					}
				});
			}
		});
	}

	@Override
	protected void refreshPage(int startRecord) 
	{
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

	/**
	 * @param column the column to be added.
	 */
	public void addColumn(Column<?> column)
	{
		columns.add(column);
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

	@Override
	public void rollback() 
	{
		for(int i=0; i<rows.size();i++)
		{
			rows.get(i).undoChanges();
		}
		
		super.rollback();
	}

	/**
	 * @author samuel.cardoso
	 *
	 * @param <T> the DataObject type.
	 * @param <K> the property type inside of DataObject.
	 */
	public static abstract class CellEditor<T, K>
	{
		private boolean autoRefreshRow = true;

		/**
		 * The default constructor.
		 */
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
		 * @param dataObject the dataObject.
		 * @return the widget to be rendered when in edition mode.
		 */
		public abstract HasValue<K> getWidget(T dataObject);

		/**
		 * @param dataObject the dataObject. 
		 * @param newValue the new value to be inserted in the dataObject. 
		 */
		public abstract void setProperty(T dataObject, K newValue);

		/**
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
			final HasValue<K> widget = CellEditor.this.getWidget(dataObject);
			assert(widget != null): "widget cannot be null";

			drawCell(grid, rowIndex, columnIndex, (Widget) widget);

			widget.addValueChangeHandler(new ValueChangeHandler<K>()
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
		}
	}

	public class Row
	{
		private T dataObject;
		private T oldDataObject;
		private int gridRowIndex;
		private int dataProviderRowIndex;
		private boolean editing;

		public Row(T dataObject, int gridRowIndex, int dataProviderRowIndex)
		{
			this.dataObject = dataObject;
			this.oldDataObject = dataObject;
			
			this.gridRowIndex = gridRowIndex;
			this.dataProviderRowIndex = dataProviderRowIndex;
		}

		public boolean isEditing() 
		{
			return editing;
		}

		public int getRowIndex() 
		{
			return gridRowIndex;
		}

		public void undoChanges()
		{
			dataObject = oldDataObject;
			getDataProvider().set(dataProviderRowIndex, dataObject);
			editing = false;
			refresh();
		}

		public void makeChanges()
		{
			editing = false;
			refresh();
		}

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

		public void refresh()
		{
			drawColumns(this);
		}
	}

	public class Column<V extends IsWidget>
	{
		private int columnIndex = 0;
		private Row row;
		private DataFactory<V, T> dataFactory;
		private CellEditor<T, ?> editableCell;

		@SuppressWarnings("unused")
		private Label label;
		
		@SuppressWarnings("unused")
		private boolean sortable = false;
		
		@SuppressWarnings("unused")
		private String header;
		
		@SuppressWarnings("unused")
		private Comparator<T> comparator;
		
		public Column(DataFactory<V, T> dataFactory)
		{
			assert(dataFactory != null): "dataFactory must not be null";
			this.dataFactory = dataFactory;
			this.columnIndex = columns != null ? columns.size() : 0;
		}

		public void clear()
		{
			row = null;
		}

		public Row getRow() 
		{
			return row;
		}

		public Column<V> setDataFactory(DataFactory<V, T> dataFactory)
		{
			this.dataFactory = dataFactory;
			return this;
		}

		public DataFactory<V, T> getDataFactory()
		{
			return dataFactory;
		}

		public Column<V> setCellEditor(CellEditor<T, ?> editableCell)
		{
			this.editableCell = editableCell;
			return this;
		}

		public void setRow(PageableDataGrid<T>.Row row) 
		{
			this.row = row;
		}

		private void renderToView() 
		{
			V widget = dataFactory.createData(row.dataObject);

			if(widget == null)
			{
				return;
			}

			drawCell(PageableDataGrid.this, row.gridRowIndex, columnIndex, widget);
		}

		private void renderToEdit() 
		{
			editableCell.render(PageableDataGrid.this, row.gridRowIndex, columnIndex, row.dataObject);
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
	}

	public SelectStrategy getSelectStrategy()
	{
		return selectStrategy;
	}

	public void setSelectStrategy(SelectStrategy selectStrategy)
	{
		this.selectStrategy = selectStrategy;
	}

	public Array<Column<?>> getColumns()
	{
		return columns;
	}

	public Row getRow(T boundObject)
	{
		int rowIndex = getDataProvider().indexOf(boundObject);
		return rows.get(rowIndex);
	}

	public Array<Row> getRows()
	{
		return rows;
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
				if(PageableDataGrid.this.hasPageable != null && PageableDataGrid.this.hasPageable.supportsInfiniteScroll())
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
	
	private void drawColumns(Row row)
	{
		for(int columnIndex = 0; columnIndex < columns.size(); columnIndex++)
		{
			PageableDataGrid<T>.Column<?> column = columns.get(columnIndex);
			
			column.setRow(row);
			column.render();
		}
	}
	
	private static <T> void drawCell(PageableDataGrid<T> grid, int rowIndex, int columnIndex, IsWidget widget)
	{
		grid.getPagePanel().setWidget(rowIndex, columnIndex, widget);		
	}
	
	@Override
    protected DivTable initializePagePanel()
    {
		DivTable divTable = new DivTable();
		divTable.setStyleName(STYLE_FACES_DATAGRID);
	    return divTable;
    }

	@Override
    protected Panel getContentPanel()
    {
	    return contentPanel;
    }
}
