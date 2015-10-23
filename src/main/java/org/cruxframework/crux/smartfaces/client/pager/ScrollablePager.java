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
	
	public ScrollablePager()
    {
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
		scrollable = new ScrollPanel();
		
		initWidget(scrollable);
		setStyleName(DEFAULT_STYLE_NAME);
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
				if (oldScrollPos >= lastScrollPos)
				{
					return;
				}

				int maxScrollTop = scrollable.getWidget().getOffsetHeight() - scrollable.getOffsetHeight();
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
								nextPage();
							}
						}
					}
				}
			}
		});
    }

	@Override
    public void initializeContentPanel(final Panel contentPanel)
    {
		setContentPanelHeight(contentPanel);
		contentPanel.clear();
		contentPanel.add(this);
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
	    // Do nothing
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
