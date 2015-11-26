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
package org.cruxframework.crux.smartfaces.client.pager;

import org.cruxframework.crux.core.client.dataprovider.pager.PageEvent;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.panel.NavPanel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

/**
 * A pager which knows the total number of pages.  
 * @author Thiago da Rosa de Bustamante
 */
public class PredictivePager<T> extends NavigationButtonsPager<T>
{
	public static final String DEFAULT_STYLE_NAME = "faces-PredictivePager";
	
	private ListBox listBox;
	private int pageCount;
	private NavPanel panel;
	
	/**
	 * Constructor
	 */
	public PredictivePager()
	{
		this.listBox = createListBox();	
		
		this.panel = new NavPanel();
		this.panel.add(createFirstPageButton());
		this.panel.add(createPreviousButton());
		this.panel.add(listBox);
		this.panel.add(createNextButton());
		this.panel.add(createLastPageButton());	
		
		initWidget(this.panel);		
		
		setStyleName(DEFAULT_PAGER_STYLE_NAME);
		this.panel.addStyleName(DEFAULT_STYLE_NAME);
	}

	@Override
	public void setStyleName(String style)
	{
	    super.setStyleName(style);
	    super.addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesPredictivePager());
	}
	
	
	@Override
	public void setStyleName(String style, boolean add)
	{
		super.setStyleName(style, add);
		if (!add)
		{
		    super.addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesPredictivePager());
		}
	}
	
	
	@Override
	protected void hideLoading()
	{
		listBox.setEnabled(true);		
	}

	@Override
	protected void onUpdate()
	{
		if(this.pageCount != getPageCount())
		{
			this.pageCount = getPageCount();
			this.listBox.clear();
			
			for (int i = 1; i <= getPageCount(); i++)
			{
				String page = "" + i;
				listBox.addItem(page, page);
			}
		}
		
		if(this.listBox.getItemCount() > 0)
		{
			listBox.setEnabled(true);
			listBox.setSelectedIndex(getCurrentPage() - 1);
		} else
		{
			listBox.setEnabled(false);
		}
	}
	
	@Override
	protected void setInteractionEnabled(boolean enabled) 
	{
		super.setInteractionEnabled(enabled);
		listBox.setEnabled(enabled);
	}
	
	@Override
	protected void showLoading()
	{
		listBox.setEnabled(false);
	}
	
	/**
	 * Creates a list box with page numbers
	 * @return
	 */
	private ListBox createListBox()
	{
		final ListBox list = new ListBox();
		list.setEnabled(false);
		list.addChangeHandler
		(
			new ChangeHandler()
			{
				public void onChange(ChangeEvent event)
				{
					if(!transactionRunning)
					{
						PageEvent pageEvent = PageEvent.fire(PredictivePager.this, getCurrentPage() + 1);
						if(!pageEvent.isCanceled())
						{
							int selected = list.getSelectedIndex();
							String page = list.getValue(selected);
							goToPage(Integer.valueOf(page));
						}
						else
						{
							list.setSelectedIndex(getCurrentPage() - 1);
						}
					}
				}				
			}
		);
		
		return list;
	}
}