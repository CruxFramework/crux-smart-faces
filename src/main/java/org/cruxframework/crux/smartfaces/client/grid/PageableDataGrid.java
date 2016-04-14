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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.dataprovider.DataProvider.DataReader;
import org.cruxframework.crux.core.client.dataprovider.DataProvider.SelectionMode;
import org.cruxframework.crux.core.client.dataprovider.DataProviderRecord;
import org.cruxframework.crux.core.client.dataprovider.DataSelectionEvent;
import org.cruxframework.crux.core.client.dataprovider.DataSelectionHandler;
import org.cruxframework.crux.core.client.dataprovider.LazyProvider;
import org.cruxframework.crux.core.client.dataprovider.PagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPageable;
import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.smartfaces.client.WidgetMsgFactory;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.dialog.DialogBox;
import org.cruxframework.crux.smartfaces.client.divtable.DivRow;
import org.cruxframework.crux.smartfaces.client.divtable.DivTable;
import org.cruxframework.crux.smartfaces.client.divtable.DivTable.DivTableHandler;
import org.cruxframework.crux.smartfaces.client.grid.Column.ColumnComparator;
import org.cruxframework.crux.smartfaces.client.grid.Type.RowSelectStrategy;
import org.cruxframework.crux.smartfaces.client.image.Image;
import org.cruxframework.crux.smartfaces.client.label.Label;
import org.cruxframework.crux.smartfaces.client.panel.SelectableFlowPanel;
import org.cruxframework.crux.smartfaces.client.panel.SelectablePanel;
import org.cruxframework.crux.smartfaces.client.util.animation.InOutAnimation;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * Implements a pageable div grid based widget.
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 */
public abstract class PageableDataGrid<T> extends AbstractPageable<T, DivTable> implements HasEnabled, HasAnimation
{
	private static Array<String> columnClasses = CollectionFactory.createArray();
	private static int nextTableId = 0;
	private static final String SYTLE_DATAGRID_ARROW = "arrow";
	private static final String SYTLE_DATAGRID_ARROW_DOWN = "arrowDown";
	private static final String SYTLE_DATAGRID_ARROW_UP = "arrowUp";
	private static final String SYTLE_DATAGRID_ARROW_UP_DOWN = "arrowUpDown";
	private static final String SYTLE_DATAGRID_COLUMNGROUP = "columnGroup";
	private static final String SYTLE_DATAGRID_COLUMNGROUP_HEADER = "columnGroupHeader";
	private static final String SYTLE_DATAGRID_DETAILS = "details";
	private static final String SYTLE_DATAGRID_DETAILS_ROW = "detailsRow";
	private static final String SYTLE_DATAGRID_HEADER_WRAPPER = "headerWrapper";
	private static final String SYTLE_DATAGRID_SELECTED = "-selected";
	private static final String SYTLE_FACES_DATAGRID_HEADER = "header";
	private static final String SYTLE_FACES_DATAGRID_HEADER_ROW = "headerRow";
	protected final String tableId;
	Array<Column<T, ?>> columns = CollectionFactory.createArray();
	Array<Column<T, ?>> detailColumns = CollectionFactory.createArray();
	LinkedList<ColumnComparator<T>> linkedComparators = new LinkedList<ColumnComparator<T>>(); 
	Array<Row<T>> rows = CollectionFactory.createArray();
	private boolean animated = true;
	private Array<ColumnGroup<T>> columnGroups = CollectionFactory.createArray();
	private FlowPanel contentPanel = new FlowPanel();
	private HandlerRegistration dataSelectionHandler;
	private String defaultDetailPopupHeader = WidgetMsgFactory.getMessages().more();
	private GridWidgetFactory detailColumnHeaderWidgetFactory = null;
	private String detailPopupHeader = WidgetMsgFactory.getMessages().details();
	private GridWidgetFactory detailTriggerWidgetFactory = null;
	private InOutAnimation dialogAnimation = InOutAnimation.bounce;
	private boolean drawn = false;
	private DivTable headerSection;
	private HandlerRegistration pageRequestedHandler;
	private InOutAnimation rowAnimation = InOutAnimation.flipX;
	private double rowAnimationDuration = -1;
	private RowSelectStrategy rowSelectStrategy;
	private GridTableHandler tableHandler;

	/**
	 */
	public PageableDataGrid(RowSelectStrategy rowSelectStrategy)
	{
		tableId = Integer.toString(nextTableId++);
		tableHandler = new GridTableHandler(this);
		
		headerSection = new DivTable(tableHandler);
		handleRowSelectStrategy(rowSelectStrategy);
		initWidget(contentPanel);
		getContentPanel().add(headerSection);
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
	
	/**
	 * Force the grid to be rendered, even if it has no data and no dataprovider events happened.
	 * It will render its headers at least.
	 */
	public void draw()
	{
		if (!drawn)
		{
			drawHeaders();
			drawn = true;
			refresh();
		}
	}

	private void drawHeaders()
    {
	    int columnIndex = -1;
	    Column<T, ?> column = null;
	    //iterate over the columns to render the body (and the header)
	    for(columnIndex = 0; columnIndex < columns.size(); columnIndex++)
	    {
	    	column = columns.get(columnIndex);
	    	createHeader(column);
	    }			
	    if (columnIndex >= 0)
	    {
	    	headerSection.setWidget(0, columnIndex, getDetailColumnHeaderWidgetFactory().createWidget(), column.width);
	    }
    }

	/**
	 * @return the column given its key and 'null' case none were found.
	 */
	public Column<T, ?> getColumn(String key)
	{
		if(getColumns() == null || StringUtils.isEmpty(key))
		{
			return null;
		}

		for (int index = 0; index < getColumns().size(); index++)
		{
			Column<T, ?> column = getColumns().get(index);
			if(column.key.equals(key))
			{
				return column; 
			}
		}
		return null;
	}

	/**
	 * @return the column given its key and 'null' case none were found.
	 */
	public ColumnGroup<T> getColumnGroup(String key)
	{
		if(getColumnGroups() == null)
		{
			return null;
		}

		for (int index = 0; index < getColumnGroups().size(); index++)
		{
			ColumnGroup<T> columnGroup = getColumnGroups().get(index);
			if(columnGroup.key.equals(key))
			{
				return columnGroup; 
			}
		}
		return null;
	}

	/**
	 * @return all column keys
	 */
	public Array<String> getColumnGroupKeys()
	{
		Array<String> keys = CollectionFactory.createArray(columnGroups.size());

		for (int i = 0; i < columnGroups.size(); i++)
		{
			keys.add(columnGroups.get(i).key);
		}

		return keys;
	}

	/**
	 * @return all column keys
	 */
	public Array<String> getColumnKeys()
	{
		Array<String> keys = CollectionFactory.createArray(columns.size());

		for (int i = 0; i < columns.size(); i++)
		{
			keys.add(columns.get(i).key);
		}

		return keys;
	}

	public InOutAnimation getDialogAnimation()
	{
		return dialogAnimation;
	}

	public InOutAnimation getRowAnimation()
	{
		return rowAnimation;
	}

	/**
	 * @return the row selection strategy.
	 */
	public RowSelectStrategy getRowSelectStrategy()
	{
		return rowSelectStrategy;
	}

	@Override
	public boolean isAnimationEnabled()
	{
		return animated ;
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

	public void setAnimationDuration(double duration)
	{
		this.rowAnimationDuration = duration;
	}

	@Override
	public void setAnimationEnabled(boolean animated)
	{
		this.animated = animated;
	}

	@Override
	public void setDataProvider(PagedDataProvider<T> dataProvider, boolean autoLoadData)
	{
		super.setDataProvider(dataProvider, autoLoadData);

		if(dataProvider.getSelectionMode().equals(SelectionMode.single) && rowSelectStrategy.equals(RowSelectStrategy.checkBox)
			||
			dataProvider.getSelectionMode().equals(SelectionMode.multiple) && rowSelectStrategy.equals(RowSelectStrategy.radioButton))
		{
			throw new IllegalStateException("You cannot select this data provider selection ["+
				dataProvider.getSelectionMode().toString()+"] mode with current row strategy ["+rowSelectStrategy.toString()+"].");
		}
	}

	public void setDetailColumnHeaderWidgetFactory(GridWidgetFactory detailColumnHeaderWidgetFactory)
	{
		this.detailColumnHeaderWidgetFactory = detailColumnHeaderWidgetFactory;
	}

	public void setDetailPopupHeader(String msgDetailPopupHeader)
	{
		this.detailPopupHeader = msgDetailPopupHeader;
	}

	public void setDetailTriggerWidgetFactory(GridWidgetFactory detailTriggerWidgetFactory)
	{
		this.detailTriggerWidgetFactory = detailTriggerWidgetFactory;
	}

	public void setDialogAnimation(InOutAnimation dialogAnimation)
	{
		this.dialogAnimation = dialogAnimation;
	}

	public void setRowAnimation(InOutAnimation rowAnimation)
	{
		this.rowAnimation = rowAnimation;
	}

	/**
	 * @param rowSelectStrategy
	 */
	public void setRowSelectStrategy(RowSelectStrategy rowSelectStrategy)
	{
		this.rowSelectStrategy = rowSelectStrategy;
	}
	
	/**
	 * @param column the column to be added.
	 */
	protected void addColumn(Column<T,?> column)
	{
		addColumn(null, column);
	}
	
	/**
	 * Replaces one column by another.
	 * @param key of the column to be replaced.
	 * @param column the new column.
	 */
	protected void addColumn(String key, Column<T,?> column)
	{
		if(column.isDetail())
		{
			insertColumn(detailColumns, column, getColumn(key));
		}
		else
		{
			insertColumn(columns, column, getColumn(key));
		}
	}
	
	protected void addColumnGroup(ColumnGroup<T> columnGroup)
	{
		columnGroups.add(columnGroup);
	}

	@Override
	protected void addDataProviderHandler()
	{
		super.addDataProviderHandler();
		dataSelectionHandler = getDataProvider().addDataSelectionHandler(new DataSelectionHandler<T>()
		{
			@Override
			public void onDataSelection(DataSelectionEvent<T> event)
			{
				if(getDataProvider().getSelectionMode().equals(SelectionMode.unselectable))
				{
					return;
				}

				Array<DataProviderRecord<T>> changedRecords = event.getChangedRecords();
				if(changedRecords != null)
				{
					for(int i=0; i<changedRecords.size(); i++)
					{
						DataProviderRecord<T> dataProviderRecord = changedRecords.get(i);
						Row<T> row = getCurrentPageRow(dataProviderRecord.getRecordObject());
						
						//Row can be null is it's state is changed to unselected but it not
						//present in the DOM. This happens when we remove a line.
						if(row != null)
						{
							setRowSelectionState(row, dataProviderRecord.isSelected());
						}
					}
				}
			}
		});
	}

	@Override
	protected void clear()
	{
		//clear rows
		if(rows != null)
		{
			//remove row selecion handlers
			int size = rows.size();
			for(int i=0; i<size;i++)
			{
				Row<T> row = rows.get(i);
				row.getDivRow().removeFromParent();
			}
			//remove rows
			rows.clear();
		}
		
		//clear columns
		for (int index = 0; index < getColumns().size(); index++)
		{
			Column<T, ?> dataGridColumn = getColumns().get(index);
			dataGridColumn.clear();
		}
		
		//clear panel
		if(getPagePanel() != null)
		{
			getPagePanel().clear();
		}
	}
	
	@Override
	protected void clearRange(int pageStart)
	{
		while (getPagePanel().getRowCount() > pageStart)
		{
			getPagePanel().removeRow(pageStart);
		}
	}

	protected Array<ColumnGroup<T>> getColumnGroups()
	{
		return columnGroups;
	}

	/**
	 * @return all the table columns.
	 */
	protected Array<Column<T, ?>> getColumns()
	{
		return columns;
	}

	@Override
	protected FlowPanel getContentPanel()
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
				int currentRowIndex = 0;
				currentRowIndex = getCurrentRowIndex(dataProviderRowIndex);

				Row<T> row = null;
				if(rows.size() <= currentRowIndex)
				{
					row = new Row<T>(PageableDataGrid.this, dataObject, currentRowIndex, dataProviderRowIndex);
					rows.insert(currentRowIndex, row);
				}
				else
				{
					row = rows.get(currentRowIndex);
				}

				drawColumnsAndDetails(row);
			}
		};
	}

	@Override
	protected DivTable initializePagePanel()
	{
		DivTable divTable = new DivTable(tableHandler);
		return divTable;
	}

	@Override
	protected void removeDataProviderHandler()
	{
		super.removeDataProviderHandler();

		if (dataSelectionHandler != null)
		{
			dataSelectionHandler.removeHandler();
			dataSelectionHandler = null;
		}
		if (pageRequestedHandler != null)
		{
			pageRequestedHandler.removeHandler();
			pageRequestedHandler = null;
		}
	}

	@Override
	protected void render(boolean refresh, boolean clearRange,
	    org.cruxframework.crux.core.client.dataprovider.pager.AbstractPageable.RenderCallback callback)
	{
	    super.render(refresh, clearRange, callback);
	    drawn = true;
	}

	@Override
	protected void setForEdition(int index, T object)
	{
		super.setForEdition(index, object);
	}

	void drawCell(final Row<T> row, int columnIndex, IsWidget widget, String width)
	{
		final DivRow divRow = getPagePanel().setWidget(row.index, columnIndex, widget, width);

		//for each row...
		if(columnIndex == 0)
		{
			//Adding a pointer to row.
			if(row.getDivRow() == null)
			{
				row.setDivRow(divRow);
			}

			if(!row.editing)
			{
				handleSelectionStrategy(row.dataProviderRowIndex, row);
			}
		}
	}

	void drawColumnsAndDetails(Row<T> row)
	{
		int columnIndex;
		columnIndex = drawColumns(row);
		drawDetails(row, columnIndex);
	}

	/**
	 * @return all the table rows. 
	 */
	Array<Row<T>> getCurrentPageRows()
	{
		return rows;
	}

	private void createHeader(final Column<T, ?> column)
	{
		SelectableFlowPanel headerWrapper = new SelectableFlowPanel();

		headerWrapper.setStyleName(SYTLE_DATAGRID_HEADER_WRAPPER);
		headerWrapper.addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesBackboneDataGridHeaderWrapper());
		
		//Adding the header widget
		if(column.headerWidget != null)
		{
			headerWrapper.add(column.headerWidget);
		}

		//Handle sort events
		handleSortEvents(column, headerWrapper);

		//Insert the element and set up the row
		handleHeaderInsertion(column, headerWrapper);
	}

	private int drawColumns(Row<T> row)
	{
		int columnIndex;
		//iterate over the columns to render the body (and the header)
		for(columnIndex = 0; columnIndex < columns.size(); columnIndex++)
		{
			Column<T, ?> column = columns.get(columnIndex);
			column.row = row;

			//header
			if(row.index == 0 && !drawn)
			{
				createHeader(column);
			}

			//body
			column.render(false);

			//animation
			if(row.editing && isAnimationEnabled())
			{
				getRowAnimation().animateEntrance(row.getDivRow(), null, rowAnimationDuration);
			}
		}
		return columnIndex;
	}

	private void drawDetails(final Row<T> row, int columnIndex)
	{
		boolean firstDetail = true;
		int detailColumnIndex;
		for(detailColumnIndex = 0; detailColumnIndex < detailColumns.size(); detailColumnIndex++)
		{
			if(firstDetail)
			{
				firstDetail = false;

				SelectablePanel selectablePanel = new SelectablePanel();
				//handle action button
				selectablePanel.add(getDetailTriggerWidgetFactory().createWidget());
				selectablePanel.addSelectHandler(new SelectHandler()
				{
					@Override
					public void onSelect(SelectEvent event)
					{
						DialogBox dialogBox = new DialogBox();
						if(animated)
						{
							dialogBox.setAnimation(dialogAnimation);
						}
						dialogBox.setDialogTitle(detailPopupHeader);
						final FlowPanel wrapperDetails = new FlowPanel();
						wrapperDetails.setStyleName(SYTLE_DATAGRID_DETAILS);
						dialogBox.add(wrapperDetails);

						int detailColumnIndex;
						for(detailColumnIndex = 0; detailColumnIndex < detailColumns.size(); detailColumnIndex++)
						{
							Column<T, ?> column = detailColumns.get(detailColumnIndex);
							column.row = row;

							FlowPanel wrapperDetailsRow = new FlowPanel();
							wrapperDetailsRow.setStyleName(SYTLE_DATAGRID_DETAILS_ROW);
							wrapperDetailsRow.add(column.render(true));
							//handle header widget
							if(column.headerWidget != null)
							{
								wrapperDetails.add(column.headerWidget);	
							}
							wrapperDetails.add(wrapperDetailsRow);
						}

						dialogBox.show();
						dialogBox.center();
					}
				});
				Column<T, ?> column = columns.get(columnIndex);

				//first line 
				if(row.index == 0 && !drawn)
				{
					//handle header
					headerSection.setWidget(0, columnIndex, getDetailColumnHeaderWidgetFactory().createWidget(), column.width);
				}

				//draw button
				drawCell(row, columnIndex, selectablePanel, column.width);
			}
		}
	}

	//This should not be exposed as it only returns rows for the current page
	//and is used for internal purposes.
	private Row<T> getCurrentPageRow(T boundObject)
	{
		int rowIndex = getDataProvider().indexOf(boundObject);
		if(rowIndex < 0)
		{
			throw new IllegalStateException("This object should be bounded to the dataprovider. "
				+ "Maybe you forgot to implement hashCode and equals to your dataObject?");
		}
		
		int currentRowIndex = getCurrentRowIndex(rowIndex);
		
		//This can happen when we change the page and select a new record. 
		//The event of unselecting the previous record will try to access 
		//a position that is not present anymore in the rows array.
		if(rowIndex > rows.size())
		{
			return null;
		}
		
		return rows.get(currentRowIndex);
	}

	private int getCurrentRowIndex(int dataProviderRowIndex)
	{
		int index;
		if(PageableDataGrid.this.pager != null && PageableDataGrid.this.pager.supportsInfiniteScroll())
		{
			index = dataProviderRowIndex;					
		}
		else
		{
			index = dataProviderRowIndex % getDataProvider().getPageSize();
		}
		return index;
	}

	private GridWidgetFactory getDetailColumnHeaderWidgetFactory()
	{
		if (detailColumnHeaderWidgetFactory == null)
		{
			detailColumnHeaderWidgetFactory = new GridWidgetFactory()
			{
				@Override
				public IsWidget createWidget()
				{
					return new Label(detailPopupHeader);
				}
			};
		}
		return detailColumnHeaderWidgetFactory;
	}

	private GridWidgetFactory getDetailTriggerWidgetFactory()
	{
		if (detailTriggerWidgetFactory == null)
		{
			detailTriggerWidgetFactory = new GridWidgetFactory()
			{
				@Override
				public IsWidget createWidget()
				{
					return new Label(defaultDetailPopupHeader);
				}
			};
		}
		return detailTriggerWidgetFactory;
	}


	private String getStyleProperties(String type, int index, int classIndex)
	{
		String typeClassName = tableId + "_" + type+"_" + classIndex;
		if(columnClasses.indexOf(typeClassName) < 0)
		{
			StyleInjector.inject("."+typeClassName+"{"+("order: " + String.valueOf(index))+"}");
			columnClasses.add(typeClassName);
		}
		return type + " " + typeClassName;
	}

	private void handleHeaderInsertion(final Column<T, ?> column, SelectableFlowPanel headerWrapper)
	{
		if(column.index == 0)
		{
			headerSection.clear();
		}

		if(column.columnGroup != null)
		{
			DivTable columnGroupTable = null;
			try
			{
				columnGroupTable = (DivTable)headerSection.getWidget(0, column.columnGroup.index);
			} catch (IndexOutOfBoundsException e){};

			if(columnGroupTable == null)
			{
				columnGroupTable = new DivTable(tableHandler);
				headerSection.addStyleName(SYTLE_FACES_DATAGRID_HEADER);
				columnGroupTable.setWidget(0, 0, column.columnGroup.header, column.columnGroup.width);			
				column.columnGroup.header.asWidget().getParent().setStyleName(SYTLE_DATAGRID_COLUMNGROUP_HEADER);
				columnGroupTable.addStyleName(SYTLE_FACES_DATAGRID_HEADER_ROW);

				headerSection.setWidget(0, column.columnGroup.index, columnGroupTable, column.columnGroup.width);
				columnGroupTable.getParent().setStyleName(
					getStyleProperties(SYTLE_DATAGRID_COLUMNGROUP, column.columnGroup.index, column.index-column.columnGroup.index));
			}

			columnGroupTable.setWidget(1, column.index-column.columnGroup.index, headerWrapper, 
				getStyleProperties(DivRow.STYLES_FACES_GRID_COLUMN, column.index, column.index), column.width);
		}
		else
		{
			DivRow divRow = headerSection.setWidget(0, column.index, headerWrapper, column.width);
			if(!divRow.getStyleName().contains(SYTLE_FACES_DATAGRID_HEADER_ROW))
			{
				divRow.addStyleName(SYTLE_FACES_DATAGRID_HEADER_ROW);
			}
		}
	}

	private void handleRowSelectStrategy(RowSelectStrategy rowSelectStrategy)
	{
		this.rowSelectStrategy = rowSelectStrategy;

		if(rowSelectStrategy.equals(RowSelectStrategy.checkBox))
		{
			CheckBox checkAll = new CheckBox();
			checkAll.addValueChangeHandler(new ValueChangeHandler<Boolean>()
			{
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event)
				{
					if (getDataProvider().getSelectionMode().equals(SelectionMode.multiple))
					{
						getDataProvider().selectAll(event.getValue());
						Array<Row<T>> currentPageRows = PageableDataGrid.this.getCurrentPageRows();
						if(currentPageRows != null)
						{
							for(int i=0; i<currentPageRows.size(); i++)
							{
								Row<T> row = currentPageRows.get(i);
								setRowSelectionState(row, event.getValue());
							}
						}
					}
				}
			});

			columns.add(new Column<T, CheckBox>(this, new GridDataFactory<T>()
			{
				@Override
				public CheckBox createData(T value, final int rowIndex)
				{
					final Row<T> row = rows.get(rowIndex);
					final CheckBox checkBox = new CheckBox();
					row.checkbox = checkBox;
					boolean selected = getDataProvider().isSelected(row.dataProviderRowIndex);
					checkBox.setValue(selected);
					checkBox.addClickHandler(new ClickHandler()
					{
						@Override
						public void onClick(ClickEvent event)
						{
							getDataProvider().select(row.dataProviderRowIndex, checkBox.getValue());
						}
					});
					return checkBox;
				}
			}, "checkbox", false)
				.setHeaderWidget(checkAll));
		}
		else if(rowSelectStrategy.equals(RowSelectStrategy.radioButton))
		{
			columns.add(new Column<T, RadioButton>(this, new GridDataFactory<T>()
			{
				@Override
				public RadioButton createData(T value, final int rowIndex)
				{
					final Row<T> row = rows.get(rowIndex);
					final RadioButton radioButton = new RadioButton(tableId);
					row.radioButton = radioButton;
					boolean selected = getDataProvider().isSelected(rows.get(rowIndex).dataProviderRowIndex);
					radioButton.setValue(selected);
					radioButton.addClickHandler(new ClickHandler()
					{
						@Override
						public void onClick(ClickEvent event)
						{
							getDataProvider().select(row.dataProviderRowIndex, radioButton.getValue());
						}
					});
					return radioButton;
				}
			}, "radiobutton", false)
				);
		}
	}

	private void handleSelectionStrategy(final int dataProviderRowIndex, final Row<T> row)
	{
		if(rowSelectStrategy.equals(RowSelectStrategy.row))
		{
			row.getDivRow().addSelectHandler(new SelectHandler()
			{
				@Override
				public void onSelect(SelectEvent event)
				{
					boolean selected = row.getDivRow().getStyleName().contains(SYTLE_DATAGRID_SELECTED);
					getDataProvider().select(dataProviderRowIndex, !selected);
				}
			});
		}
		setRowSelectionState(row, getDataProvider().isSelected(dataProviderRowIndex));
	}

	private void handleSortEvents(final Column<T, ?> column, SelectableFlowPanel headerWrapper)
	{
		if(column.isSortable() && (!getDataProvider().isDirty() || getDataProvider() instanceof LazyProvider))
		{
			//create arrow button
			final Image arrow = new Image();

			//create buttons
			headerWrapper.add(arrow);

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

			if(!column.sorted)
			{
				arrow.setStyleName(SYTLE_DATAGRID_ARROW);
				arrow.addStyleName(SYTLE_DATAGRID_ARROW_UP_DOWN);
			}
			else
			{
				//set up visibility
				if(column.columnComparator != null)
				{
					if(column.columnComparator.multiplier > 0)
					{
						arrow.setStyleName(SYTLE_DATAGRID_ARROW);
						arrow.addStyleName(SYTLE_DATAGRID_ARROW_DOWN);
					}
					else
					{
						arrow.setStyleName(SYTLE_DATAGRID_ARROW);
						arrow.addStyleName(SYTLE_DATAGRID_ARROW_UP);
					}
				}
			}
		}
	}

	private void insertColumn(Array<Column<T, ?>> columnList, Column<T, ?> column, Column<T, ?> oldColumn)
	{
		if(oldColumn != null)
		{
			int index = columnList.indexOf(oldColumn);
			columnList.remove(oldColumn);
			columnList.insert(index, column);
		}
		else
		{
			columnList.add(column);
		}
	}

	private void setRowSelectionState(Row<T> row, boolean selected)
	{
		if(selected)
		{
			row.getDivRow().addStyleDependentName(SYTLE_DATAGRID_SELECTED);
		}
		else
		{
			row.getDivRow().removeStyleDependentName(SYTLE_DATAGRID_SELECTED);
		}

		if(row.checkbox != null)
		{
			row.checkbox.setValue(selected);
		}

		if(row.radioButton != null)
		{
			row.radioButton.setValue(selected);
		}
	}

	private static class GridTableHandler implements DivTableHandler
	{
		private static final String STYLES_FACES_GRID = "grid";
		private Set<String> columnsCreated = new HashSet<String>();
		private Element style;
		private String tableId;
		
		private GridTableHandler(PageableDataGrid<?> grid)
        {
			this.tableId = grid.tableId;
			grid.addAttachHandler(new Handler()
			{
				@Override
				public void onAttachOrDetach(AttachEvent event)
				{
					if (!event.isAttached() && style != null)
					{
						style.removeFromParent();
						style = null;
					}
				}
			});
        }
		
		@Override
		public String getColumnClassName(int columnIndex)
		{
		    String columnName = STYLES_FACES_GRID + "_" + tableId + "_" + DivRow.STYLES_FACES_GRID_COLUMN + "_" + columnIndex;
		    return columnName;
		}
		
		@Override
		public boolean initClassNameForColumn(int columnIndex)
		{
			String columnName = getColumnClassName(columnIndex);
			if (!columnsCreated.contains(columnName))
			{
				columnsCreated.add(columnName);
				return true;
			}
			return false;
		}

		@Override
        public void injectStyle(String contents)
        {
			if (style == null)
			{
				String elementId = "__css__"+tableId;
				style = Document.get().getElementById(elementId);
				if (style == null)
				{
					style = Document.get().createStyleElement();
					style.setPropertyString("language", "text/css");
					getHead().appendChild(style);
				}
			}	            
			style.setInnerText(style.getInnerText()+contents);
        }		

	    private Element getHead() 
	    {
	          return Document.get().getElementsByTagName("head").getItem(0);
	    }
	}
}
