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
package org.cruxframework.crux.smartfaces.client.pager;

import org.cruxframework.crux.core.client.dataprovider.DataProvider;
import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPager;
import org.cruxframework.crux.core.client.dataprovider.pager.Pager;
import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.label.Label;
import org.cruxframework.crux.smartfaces.client.panel.NavPanel;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

/**
 * A {@link DataProvider} {@link Pager} that use a Bullets panel to navigate between pages.
 * @author Thiago da Rosa de Bustamante
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
public class BulletsPager<T> extends AbstractPager<T> 
{
	public static final String DEFAULT_STYLE_NAME = "faces-BulletsPager";
	public static final String STYLE_BULLET = "bullet";

	private static final String ACTIVE_STYLE_SUFFIX = "-active";

	protected AutoTransiteTimer<T> autoTransiteTimer;
	protected boolean autoTransitionEnabled = true;
	protected int autoTransitionInterval = 10000;
	protected int currentBullet = -1;
	protected NavPanel mainPanel = new NavPanel();
	protected int pageCount;

	/**
	 * Cosntructor
	 */
	public BulletsPager() 
	{
		autoTransiteTimer = new AutoTransiteTimer<T>(this);

		initWidget(mainPanel);
		
		addAttachHandler(new Handler() 
		{
			@Override
			public void onAttachOrDetach(AttachEvent event) 
			{
				if (event.isAttached())
				{
					autoTransiteTimer.reschedule();
				}
				else
				{
					autoTransiteTimer.cancel();
				}
			}
		});
		
		setStyleName(DEFAULT_PAGER_STYLE_NAME);
		addStyleName(DEFAULT_STYLE_NAME);
	}
	
	@Override
	public void setStyleName(String style)
	{
	    super.setStyleName(style);
	    super.addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesBulletsPager());
	}
	
	
	@Override
	public void setStyleName(String style, boolean add)
	{
		super.setStyleName(style, add);
		if (!add)
		{
		    super.addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesBulletsPager());
		}
	}
	
	/**
	 * Retrieve the autoTransitionInterval property value. This value defines the time 
	 * between each automatic transition of pages. It is only used when autoTransitionEnabled
	 * property is set.
	 * @return time in milliseconds.
	 */
	public int getAutoTransitionInterval()
	{
		return autoTransitionInterval;
	}

	/**
	 * Retrieve the autoTransitionEnabled property value. If this is enabled, this pager will
	 * change the active page automatically, going to the next page. Use the property
	 * autoTransitionInterval to define the time between transactions.
	 * @return true if enabled
	 */
	public boolean isAutoTransitionEnabled() 
	{
		return autoTransitionEnabled;
	}
	
	/**
	 * Set the autoTransitionEnabled property value. If this is enabled, this pager will
	 * change the active page automatically, going to the next page. Use the property
	 * autoTransitionInterval to define the time between transactions.
	 * @param autoTransitionEnabled true to enable
	 */
	public void setAutoTransitionEnabled(boolean autoTransitionEnabled) 
	{
		this.autoTransitionEnabled = autoTransitionEnabled;
	}

	/**
	 * Set the autoTransitionInterval property value. This value defines the time 
	 * between each automatic transition of pages. It is only used when autoTransitionEnabled
	 * property is set.
	 * @param autoTransitionInterval time in milliseconds.
	 */
	public void setAutoTransitionInterval(int autoTransitionInterval)
	{
		this.autoTransitionInterval = autoTransitionInterval;
		autoTransiteTimer.reschedule();
	}

	protected Widget createBullet(final int index) 
	{
		Label bullet = new Label();
		bullet.setStyleName(STYLE_BULLET);
		bullet.addSelectHandler(new SelectHandler() 
		{
			@Override
			public void onSelect(SelectEvent event) 
			{
				if (!transactionRunning)
				{
					goToPage(index);
				}
			}
		});
		return bullet;
	}

	@Override
	protected void hideLoading() 
	{
		autoTransiteTimer.reschedule();
	}

	@Override
	protected void onUpdate() 
	{
		if(this.pageCount != getPageCount())
		{
			this.pageCount = getPageCount();
			this.mainPanel.clear();
			currentBullet = getCurrentPage() - 1;
			
			for (int i = 1; i <= pageCount; i++)
			{
				mainPanel.add(createBullet(i));
			}
			if (currentBullet >= 0 && this.pageCount > 0)
			{
				mainPanel.getWidget(currentBullet).addStyleDependentName(ACTIVE_STYLE_SUFFIX);
			}
		}
		else if(pageCount > 0)
		{
			if (currentBullet != getCurrentPage() - 1)
			{
				int previousBullet = currentBullet;
				if (previousBullet >= 0)
				{
					mainPanel.getWidget(previousBullet).removeStyleDependentName(ACTIVE_STYLE_SUFFIX);
				}
				currentBullet = getCurrentPage() - 1;
				if (currentBullet >= 0)
				{
					mainPanel.getWidget(currentBullet).addStyleDependentName(ACTIVE_STYLE_SUFFIX);
				}
			}
		}
		else
		{
			currentBullet = -1;
		}
	}
	
	@Override
	protected void showLoading() 
	{
		autoTransiteTimer.cancel();
	}
	
	/**
	 * The timer that schedule the automatic page transitions.
	 * @author Thiago da Rosa de Bustamante
	 *
	 * @param <T>
	 */
	protected static class AutoTransiteTimer<T> extends Timer
	{
		private BulletsPager<T> pager;

		public AutoTransiteTimer(BulletsPager<T> pager)
		{
			this.pager = pager;
		}

		public void reschedule()
		{
			this.cancel();
			if (pager.isAttached())
			{
				this.scheduleRepeating(pager.autoTransitionInterval);
			}
		}

		@Override
		public void run()
		{
			if (pager.autoTransitionEnabled && pager.isInteractionEnabled())
			{
				if (pager.hasNextPage())
				{
					pager.nextPage();
				}
				else
				{
					pager.firstPage();
				}
			}
		}
	}
}
