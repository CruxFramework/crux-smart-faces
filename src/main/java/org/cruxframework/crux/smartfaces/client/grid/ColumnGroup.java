package org.cruxframework.crux.smartfaces.client.grid;

import com.google.gwt.user.client.ui.Widget;

public class ColumnGroup<T>
{
	@SuppressWarnings("unused")
	private ColumnGroup<T> columnGroupParent;
	Widget header;
	int index = Integer.MAX_VALUE;
	
	public ColumnGroup(Widget header)
	{
		this.header = header;
	}
	
	public void addColumn(Column<T, ?> column)
	{
		//find out the smaller index
		if(column.index < index)
		{
			index = column.index;
		}
		column.columnGroup = ColumnGroup.this;
	}

	//TODO: implement it!
	public ColumnGroup<T> newColumGroup(Widget header)
	{
		ColumnGroup<T> columnGroup = new ColumnGroup<T>(header);
		columnGroup.columnGroupParent = this;
		return columnGroup;
	}
}