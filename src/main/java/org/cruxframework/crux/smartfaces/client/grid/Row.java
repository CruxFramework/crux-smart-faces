package org.cruxframework.crux.smartfaces.client.grid;

import org.cruxframework.crux.smartfaces.client.divtable.DivRow;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Define the row data type.
 * @author samuel.cardoso
 *
 */
public class Row<T>
{
	/**
	 * 
	 */
	private final PageableDataGrid<T> grid;
	T dataObject;
	int dataProviderRowIndex;
	boolean editing;
	int index;
	private T oldDataObject;
	boolean selected;
	DivRow divRow;
	HandlerRegistration onSelectionHandlerRegistration;

	public Row(PageableDataGrid<T> pageableDataGrid, T dataObject, int index, int dataProviderRowIndex)
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
	 * Refreshes the all the row columns. 
	 */
	void refresh()
	{
		grid.drawColumns(this);
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
}