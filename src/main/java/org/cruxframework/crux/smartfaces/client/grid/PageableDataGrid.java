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
import org.cruxframework.crux.core.client.dataprovider.PagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPageable;
import org.cruxframework.crux.core.client.factory.DataFactory;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.divtable.DivTable;
import org.cruxframework.crux.smartfaces.client.grid.Type.SelectStrategy;
import org.cruxframework.crux.smartfaces.client.label.Label;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author samuel.cardoso
 *
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
//TODO Ver os indices quando a grid esta' paginada
@Experimental
public class PageableDataGrid<T> extends AbstractPageable<T>
{
	/**
	 * Style class.
	 */
	public static final String STYLE_FACES_DATAGRID = "faces-Datagrid";
	private SelectStrategy selectStrategy = SelectStrategy.SINGLE;
	
	private DivTable table = new DivTable();
	private Array<Column<?>> columns = CollectionFactory.createArray();
	private Array<Row> rows = CollectionFactory.createArray();
	
	/**
	 * @param dataProvider the dataprovider.
	 * @param autoLoadData if true, the data must be loaded after the constructor has been invoked.
	 */
	public PageableDataGrid(final PagedDataProvider<T> dataProvider, boolean autoLoadData)
	{
		table.setStyleName(STYLE_FACES_DATAGRID);
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
		initWidget(table);
		setDataProvider(dataProvider, autoLoadData);
		
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
	    table.clear();
    }
	
	@Override
	protected void clearRange(int pageStart)
	{
		while (table.getRowCount() > pageStart)
		{
			table.removeRow(pageStart);
		}
	}
	
	/**
	 * @param column the column to be added.
	 */
	public void addColumn(Column<?> column)
    {
		columns.add(column);
    }
	
	/**
	 * @author samuel.cardoso
	 *
	 * @param <T> the DataObject type.
	 * @param <K> the property type inside of DataObject.
	 */
	public static abstract class CellEditor<T, K>
	{
		private boolean autoRefreshRow = false;

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
			
			grid.getTable().setWidget(rowIndex, columnIndex, (Widget) widget);
			
			widget.addValueChangeHandler(new ValueChangeHandler<K>()
			{
				@Override
                public void onValueChange(ValueChangeEvent<K> event)
                {
					CellEditor.this.setProperty(dataObject, event.getValue());
					if(autoRefreshRow)
					{
						grid.rows.get(rowIndex).refresh();
						
						Scheduler.get().scheduleFixedDelay(new RepeatingCommand()
						{
							@Override
							public boolean execute()
							{
//								grid.rows.get(rowIndex).rollback();
								grid.rows.get(rowIndex).commit();
								return false;
							}
						}, 3000);
					}
                }
			});
		}
	}
	
	public class Row
	{
		private T dataObject;
		private int rowIndex;
		private boolean editing;
		
		public Row(T dataObject, int rowIndex)
		{
			this.dataObject = dataObject;
			this.rowIndex = rowIndex;
		}
		
		public boolean isEditing() 
		{
			return editing;
		}

		void setDataObject(T dataObject) 
		{
			this.dataObject = dataObject;
		}
		
		public int getRowIndex() 
		{
			return rowIndex;
		}
		
		T getDataObject() 
		{
			return dataObject;
		}

		public void rollback()
		{
			getDataProvider().rollback();
			editing = false;
			refresh();
		}
		
		public void commit()
		{
			getDataProvider().commit();
			editing = false;
			refresh();
		}
		
		public void edit()
		{
			editing = true;
			refresh();
		}
		
		public void refresh()
		{
			drawColumns(this);
		}
	}
	
	public class Column<V>
	{
		private int columnIndex = 0;
		private Row row;
		@SuppressWarnings("unused")
		private Label label;
		@SuppressWarnings("unused")
		private String header;
		@SuppressWarnings("unused")
		private Comparator<T> comparator;
		private DataFactory<V, T> dataFactory;
		private CellEditor<T, ?> editableCell;

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
		
		public Column<V> setHeader(String header)
	    {
			this.header = header;
		    return this;
	    }

		public Row getRow() 
		{
			return row;
		}

		public Column<V> setComparator(Comparator<T> comparator)
	    {
		    this.comparator = comparator;
		    return this;
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
			
			getDataProvider().read(row.rowIndex, new DataReader<T>() 
			{
				@Override
				public void read(T dataObject, int index) 
				{
					V value = dataFactory.createData(dataObject);
					
					IsWidget widget = null;
					
					if(value == null)
					{
						return;
					}
					
					if(value instanceof IsWidget)
					{
						widget = (IsWidget) value;
					} 
					else
					{
						widget = new Label(value.toString());
					}
					
					table.setWidget(row.rowIndex, columnIndex, widget);		
				}
			});
		}

		private void renderToEdit() 
		{
			editableCell.render(PageableDataGrid.this, row.rowIndex, columnIndex, row.dataObject);
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
	
	/**
	 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
	 *
	 * @param <T>
	 */
	public class DataGridColumnGroup
	{
		private Array<Column<?>> columns;
		
		public void addColumn(Column<?> widgetColumn)
	    {
			columns.add(widgetColumn);
	    }
	}

	public SelectStrategy getSelectStrategy()
	{
		return selectStrategy;
	}

	protected DivTable getTable() 
	{
		return table;
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
			public void read(T dataObject, int rowIndex) 
			{
				Row row = null;
				if(rows.size() <= rowIndex)
				{
					row = new Row(dataObject, rowIndex);
					rows.insert(rowIndex, row);
				} 
				else
				{
					row = rows.get(rowIndex);
				}
				
				drawColumns(row);
			}
		};
	}
	
	private void drawColumns(Row row)
	{
		if(row.isEditing())
		{
			T clonedDataObject = dataProvider.get(row.rowIndex);
			row.setDataObject(clonedDataObject);
			dataProvider.set(row.rowIndex, clonedDataObject);
		}
				
		for(int columnIndex = 0; columnIndex < columns.size(); columnIndex++)
		{
			PageableDataGrid<T>.Column<?> column = columns.get(columnIndex);
			
			column.setRow(row);
			column.render();
		}
	}
}
	