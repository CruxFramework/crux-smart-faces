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

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPager;
import org.cruxframework.crux.core.client.dataprovider.pager.PageEvent;
import org.cruxframework.crux.core.client.dataprovider.pager.Pageable;
import org.cruxframework.crux.core.client.dataprovider.pager.PageablePager;
import org.cruxframework.crux.core.client.dataprovider.pager.Pager;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * A {@link Pager} that change pages from a {@link Pageable} when user scrolls down the pager.
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class ScrollablePager<T> extends AbstractPager<T> implements PageablePager<T>
{
	public static final String DEFAULT_STYLE_NAME = "faces-ScollablePager";

	private int lastRequestedPage = 0;
	private int lastScrollPos = 0;
	private DivElement loadingElement;
	private ScrollPanel scrollable;

	private int startVisiblePage = 0;
	private Array<ScrollingData> backingScrollingData = CollectionFactory.createArray();
	private Array<ScrollingData> forwardScrollingData = CollectionFactory.createArray();

	private IsWidget pagePanel;
	private int scrollableHeight;
	private int pagePanelHeight;

	private int visibleWindowMinScrollTop;
	private int visibleWindowMaxScrollTop;
	
	public ScrollablePager()
    {
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
		scrollable = new ScrollPanel();
		
		initWidget(scrollable);
		
		setStyleName(DEFAULT_PAGER_STYLE_NAME);
		addStyleName(DEFAULT_STYLE_NAME);
		
		// Do not let the scrollable take tab focus.
		scrollable.getElement().setTabIndex(-1);

		// Handle scroll events.
		scrollable.addScrollHandler(new ScrollHandler()
		{
			public void onScroll(ScrollEvent event)
			{
				// If scrolling up, ignore the event.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = scrollable.getVerticalScrollPosition();
				int maxScrollTop = pagePanelHeight - scrollableHeight;
				if (oldScrollPos >= lastScrollPos)
				{
					maybeUpdateBackwardVisibleWindow();
					return;
				}
				maybeUpdateForwardVisibleWindow();
				if (lastScrollPos >= maxScrollTop)
				{
					if (hasNextPage() && isEnabled())
					{
						int nextRequestedPage = getCurrentPage() + 1;
						if (lastRequestedPage != nextRequestedPage)
						{
							lastRequestedPage = nextRequestedPage;
							PageEvent pageEvent = PageEvent.fire(ScrollablePager.this, nextRequestedPage);
							if(!pageEvent.isCanceled())
							{
								nextPage(maxScrollTop);
							}
						}
					}
				}
			}
		});
    }
	
	private void nextPage(int scrollTop)
	{
		int currentPage = getDataProvider().getCurrentPage();
		if (currentPage > 1)
		{
			updateForwardVisibleWindow(null, scrollTop);
		}
		
	    nextPage();
	}
	
	private void maybeUpdateBackwardVisibleWindow()
    {
		if (visibleWindowMinScrollTop > lastScrollPos)
		{
			int size = backingScrollingData.size();
			if (size > 0)
			{
				ScrollingData scrollingData = backingScrollingData.get(backingScrollingData.size() - 1);
				if (scrollingData != null)
				{
					updateBackwardVisibleWindow(scrollingData);
					backingScrollingData.remove(size - 1);
				}
			}
		}
    }
	
	private void maybeUpdateForwardVisibleWindow()
    {
		if (visibleWindowMaxScrollTop < lastScrollPos)
		{
			int size = forwardScrollingData.size();
			if (size > 0)
			{
				ScrollingData scrollingData = forwardScrollingData.get(forwardScrollingData.size() - 1);
				if (scrollingData != null)
				{
					updateForwardVisibleWindow(scrollingData, scrollingData.scrollTop);
					forwardScrollingData.remove(size - 1);
				}
			}
		}
    }

	
	private void updateBackwardVisibleWindow(ScrollingData scrollingData)
    {
	    restorePage(scrollingData);
	    int pageSize = getDataProvider().getPageSize();
		int startRecord = startVisiblePage + (2*pageSize) -1;
		startVisiblePage--;
		ScrollingData pageScrollingData = hidePage(startRecord, lastScrollPos);
		forwardScrollingData.add(pageScrollingData);
		visibleWindowMinScrollTop -= scrollingData.size;
		visibleWindowMaxScrollTop -= pageScrollingData.size;
    }

	private void updateForwardVisibleWindow(ScrollingData scrollingData, int scrollTop)
    {
		if (scrollingData != null)
		{
		    restorePage(scrollingData);
		}
		int startRecord = startVisiblePage;
		this.startVisiblePage++;
	    
	    ScrollingData pageScrollingData = hidePage(startRecord, scrollTop);
		backingScrollingData.add(pageScrollingData);
	    visibleWindowMinScrollTop = scrollingData != null?visibleWindowMinScrollTop+scrollingData.size:scrollTop;
	    visibleWindowMaxScrollTop = visibleWindowMinScrollTop + pageScrollingData.size;
    }

	private void restorePage(ScrollingData scrollingData)
    {
	    int pageSize = scrollingData.children.size();
		for (int i = 0; i < pageSize; i++)
	    {
		    pagePanel.asWidget().getElement().insertBefore(scrollingData.children.get(i), scrollingData.replacement);
	    }
		scrollingData.replacement.removeFromParent();
    }

	private ScrollingData hidePage(int startRecord, int scrollTop)
    {
	    int pageSize = getDataProvider().getPageSize();
	    
	    ScrollingData scrollingData = new ScrollingData();
	    scrollingData.size = 0;
	    scrollingData.scrollTop = scrollTop;
	    for (int i = 0; i < pageSize; i++)
	    {
	    	Element child = pagePanel.asWidget().getElement().getChild(startRecord).cast();
	    	scrollingData.children.add(child);
	    	scrollingData.size += child.getOffsetHeight();
	    	child.removeFromParent();
	    }
	    scrollingData.replacement = Document.get().createDivElement();
	    scrollingData.replacement.setAttribute("style", "height: "+scrollingData.size+"px");
	    Element element = pagePanel.asWidget().getElement();
	    if (element.getChildCount() > startRecord)
	    {
	    	Node refChild = element.getChild(startRecord);
	    	pagePanel.asWidget().getElement().insertBefore(scrollingData.replacement, refChild);
	    }
	    else
	    {
	    	pagePanel.asWidget().getElement().appendChild(scrollingData.replacement);
	    }
	    scrollable.setVerticalScrollPosition(lastScrollPos);
	    return scrollingData;
    }

	static class ScrollingData
	{
		int scrollTop;
		int size;
		Array<Element> children = CollectionFactory.createArray();
		Element replacement;
	}
	

	@Override
    public void initializeContentPanel(final Panel contentPanel)
    {
		setContentPanelHeight(contentPanel);
		contentPanel.clear();
		contentPanel.add(this);
    }

	@Override
	public void setPageable(Pageable<T> pageable)
	{
		if (pageable != null)
		{
			pageable.setPager(this);
		}
	}
	
	@Override
	public void setStyleName(String style)
	{
	    super.setStyleName(style);
	    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesScrollablePager());
	}
	
	@Override
	public void setStyleName(String style, boolean add)
	{
		super.setStyleName(style, add);
		if (!add)
		{
		    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesScrollablePager());
		}
	}
	
	@Override
	public boolean supportsInfiniteScroll()
	{
	    return true;
	}
	
	@Override
    public void updatePagePanel(IsWidget pagePanel, boolean forward)
    {
		this.pagePanel = pagePanel;
		scrollable.setWidget(pagePanel);
    }

	@Override
    protected void hideLoading()
    {
		if (loadingElement != null)
		{
			loadingElement.removeFromParent();
			loadingElement = null;
		}
    }
	
	@Override
	protected void onTransactionStarted(int startRecord)
	{
		super.onTransactionStarted(startRecord);
		int pageSize = getDataProvider().getPageSize();
		int index = startRecord + 1;
		lastRequestedPage = (index / pageSize) + (index%pageSize==0?0:1);	
	}

	@Override
    protected void onUpdate()
    {
		scrollableHeight = scrollable.getOffsetHeight();
		if (pagePanel != null)
		{
			pagePanelHeight = pagePanel.asWidget().getOffsetHeight();
		}
		else
		{
			pagePanelHeight = 0;
		}
    }
	

	@Override
	protected void setInteractionEnabled(boolean enabled) 
	{
		super.setInteractionEnabled(enabled);
		scrollable.setTouchScrollingDisabled(!enabled);
	}

	@Override
    protected void showLoading()
    {
		if (loadingElement == null)
		{
			loadingElement = Document.get().createDivElement();
			loadingElement.setClassName(getLoaderStyleName());
			Document.get().getBody().appendChild(loadingElement);
		}
    }

	private void defineContentPageHeightToPageHeight(final Panel contentPanel)
    {
	    addAttachHandler(new Handler()
	    {
	    	@Override
	    	public void onAttachOrDetach(AttachEvent event)
	    	{
	    		if (event.isAttached())
	    		{
	    			Scheduler.get().scheduleDeferred(new ScheduledCommand() 
	    			{
	    				@Override
	    				public void execute() 
	    				{
	    					int clientHeight = contentPanel.getElement().getClientHeight();
	    					if (clientHeight > 0)
	    					{
	    						contentPanel.setHeight(clientHeight + "px");
	    					}
	    				}
	    			});
	    		}
	    	}
	    });
    }

	private void setContentPanelHeight(final Panel contentPanel)
    {
	    String height = contentPanel.getElement().getPropertyString("height");
		if (StringUtils.isEmpty(height))
		{
			defineContentPageHeightToPageHeight(contentPanel);
		}
    }	
}
