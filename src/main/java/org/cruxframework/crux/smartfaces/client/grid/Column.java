package org.cruxframework.crux.smartfaces.client.grid;

import java.util.ArrayList;
import java.util.Comparator;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Define the row data type.
 * @author samuel.cardoso
 *
 */
/**
 * Define the column data type.
 * @author samuel.cardoso
 *
 * @param <V>
 */
public class Column<T, V extends IsWidget>
{
	/**
	 * 
	 */
	private final PageableDataGrid<T> grid;
	
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

	public class ColumnGroup
	{
		@SuppressWarnings("unused")
		private ColumnGroup columnGroupParent;
		Widget header;
		int index = Integer.MAX_VALUE;
		
		protected ColumnGroup(Widget header)
		{
			this.header = header;
		}
		
		public void addColumn(Column<T, V> column)
		{
			//find out the smaller index
			if(column.index < index)
			{
				index = column.index;
			}
			column.columnGroup = ColumnGroup.this;
		}

		//TODO: implement it!
		public ColumnGroup newColumGroup(Widget header)
		{
			ColumnGroup columnGroup = new ColumnGroup(header);
			columnGroup.columnGroupParent = this;
			return columnGroup;
		}
	}
	
	private ColumnComparator<T> columnComparator;
	private ColumnGroup columnGroup;
	private GridDataFactory<V, T> dataFactory;
	private CellEditor<T, ?> editableCell;
	private IsWidget headerWidget;
	private int index = 0;
	private String key;
	private ArrayList<String> keys = new ArrayList<String>();
	private Row<T> row;
	private boolean sortable = false;

	public Column(PageableDataGrid<T> pageableDataGrid, GridDataFactory<V, T> dataFactory, String key)
	{
		this.grid = pageableDataGrid;
		assert(!keys.contains(key)): "key must be unique.";
		this.key = key;
		assert(dataFactory != null): "dataFactory must not be null";
		this.dataFactory = dataFactory;
		this.index = grid.columns != null ? grid.columns.size() : 0;
	}

	public Column<T, V> add()
	{
		//////grid.addColumn(this);
		return this;
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
	public GridDataFactory<V, T> getDataFactory()
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

	public Row<T> getRow() 
	{
		return row;
	}

	private void render() 
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
		editableCell.render(grid, row.index, index, row.dataProviderRowIndex, row.dataObject);
	}

	private void renderToView() 
	{
		V widget = dataFactory.createData(row.dataObject, row);

		if(widget == null)
		{
			return;
		}

		grid.drawCell(grid, row.index, index, row.dataProviderRowIndex, widget);
	}

	public Column<T, V> setCellEditor(CellEditor<T, ?> editableCell)
	{
		this.editableCell = editableCell;
		return this;
	}
	
	public Column<T, V> setComparator(Comparator<T> comparator)
	{
		this.columnComparator = new ColumnComparator<T>();
		columnComparator.comparator = comparator;
		columnComparator.multiplier = 1;
		return this;
	}

	public Column<T, V> setDataFactory(GridDataFactory<V, T> dataFactory)
	{
		this.dataFactory = dataFactory;
		return this;
	}

	public Column<T, V> setHeaderWidget(IsWidget headerWidget)
	{
		this.headerWidget = headerWidget;
		return this;
	}

	public Column<T, V> setSortable(boolean sortable)
	{
		this.sortable = sortable;
		return this;
	}

	public void sort()
	{
		assert(grid.getDataProvider() != null) :"No dataProvider set for this component.";
		grid.refreshRowCache();
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
				//////ColumnComparator<T> columnComparator = grid.linkedComparators.get(index);
				//////compareResult = columnComparator.comparator.compare(o1, o2)*columnComparator.multiplier;
				//////index++;
				}
				return compareResult;
			}
		});
	}
}