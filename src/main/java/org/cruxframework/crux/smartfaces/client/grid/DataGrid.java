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

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.dataprovider.PagedDataProvider;
import org.cruxframework.crux.core.client.factory.DataFactory;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.divtable.DivTable;
import org.cruxframework.crux.smartfaces.client.grid.Type.SelectStrategy;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 * @param <T>
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
public class DataGrid<T> extends PageableDataGrid<T> implements HasEnabled
{
	private static final String STYLE_DISABLED = "--disabled";
	private static final String STYLE_FACES_DATAGRID = "faces-Datagrid";
	private boolean enabled;
	private SelectStrategy selectStrategy = SelectStrategy.SINGLE;

	public DataGrid(PagedDataProvider<T> dataProvider, boolean autoLoadData)
	{
		super(dataProvider, autoLoadData);
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
		setStyleName(STYLE_FACES_DATAGRID);
	}

	public SelectStrategy getSelectStrategy()
	{
		return selectStrategy;
	}

	@Override
	protected DivTable initializePagePanel() 
	{
		DivTable divTable = super.initializePagePanel();
		return divTable;
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * Define a column group in order to set a specific header to each group.
	 * @param dataGridColumnGroup
	 */
	public ColumnGroup newColumGroup(Widget header)
	{
		return new ColumnGroup(header);
	}
	
	/**
	 * Inserts a new column.
	 * @param dataFactory
	 * @return
	 */
	public <V extends IsWidget> DataGrid<T>.Column<V> newColumn(DataFactory<V,T> dataFactory, String key)
	{
		PageableDataGrid<T>.Column<V> column = new Column<V>(dataFactory, key);
		addColumn(column);
		return column;
	}

	private void setEnableColumns(boolean enabled) 
	{
		Array<PageableDataGrid<T>.Column<?>> columns = getColumns();
		int sizeColumns = columns.size();

		Array<PageableDataGrid<T>.Row> rows = getRows();
		int sizeRows = rows.size();

		if(sizeColumns <= 0 || sizeRows <= 0)
		{
			return;
		}

		for(int i=0; i<sizeRows;i++)
		{
			for(int j=0;j<sizeColumns;j++)
			{
				Widget widget = getPagePanel().getWidget(i, j);
				if(widget != null)
				{
					try
					{
						((HasEnabled)widget).setEnabled(enabled);
					} catch(ClassCastException e){}
				}
			}
		}
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
		if(!enabled)
		{
			getContentPanel().addStyleDependentName(STYLE_DISABLED);
		}
		else
		{
			getContentPanel().addStyleDependentName(STYLE_DISABLED);
		}
		setEnableColumns(enabled);
	}

	/**
	 * Define a select strategy.
	 * @param selectStrategy
	 */
	public void setSelectStrategy(final SelectStrategy selectStrategy)
	{
		this.selectStrategy = selectStrategy;
	}
}