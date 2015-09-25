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

import org.cruxframework.crux.core.client.dataprovider.pager.AbstractPager;
import org.cruxframework.crux.core.client.dataprovider.pager.HasPageable;
import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.button.Button;
import org.cruxframework.crux.smartfaces.client.swappanel.SwapAnimation;
import org.cruxframework.crux.smartfaces.client.swappanel.SwapPanel;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A Pager that allow animations to swap the pages. It uses an SwapPanel to animate
 * the transitions.
 * @author Thiago da Rosa de Bustamante
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
public class SwapPager<T> extends AbstractPager<T> implements HasPageable<T>, HasAnimation
{
	public static final String DEFAULT_STYLE_NAME = "faces-SwapPager";

	private static final String STYLE_SWAP_PAGER_BACK_PANEL = "faces-SwapPager-BackPanel";
	private static final String STYLE_SWAP_PAGER_NEXT_PANEL = "faces-SwapPager-NextPanel";
	private static final String STYLE_SWAP_PAGER_PAGER_LOADING = "faces-SwapPager--pagerLoading";

	private SwapAnimation animationBackward = SwapAnimation.fade;
	private SwapAnimation animationForward = SwapAnimation.fade;
	private Button backButton;
	private SimplePanel backPanel;
	private boolean circularPaging = true;
	private DivElement loadingElement;
	private FlowPanel mainPanel;
	private Button nextButton;
	private SimplePanel nextPanel;
	private SwapPanel swapPanel;
	
	/**
	 * Constructor
	 */
	public SwapPager() 
	{
		mainPanel = new FlowPanel();
		swapPanel = new SwapPanel();
		
		backPanel = new SimplePanel();
		backPanel.setStyleName(STYLE_SWAP_PAGER_BACK_PANEL);
		backButton = new Button();
		backButton.addSelectHandler(new SelectHandler() 
		{
			@Override
			public void onSelect(SelectEvent event) 
			{
				if (!swapPanel.isAnimating())
				{
					if (hasPreviousPage())
					{
						previousPage();
					}
					else if (circularPaging)
					{
						lastPage();
					}
				}
			}
		});
		backPanel.add(backButton);
		
		nextPanel = new SimplePanel();
		nextPanel.setStyleName(STYLE_SWAP_PAGER_NEXT_PANEL);
		nextButton = new Button();
		nextButton.addSelectHandler(new SelectHandler() 
		{
			@Override
			public void onSelect(SelectEvent event) 
			{
				if (!swapPanel.isAnimating())
				{
					if (hasNextPage())
					{
						nextPage();
					}
					else if (circularPaging)
					{
						firstPage();
					}
				}
			}
		});
		nextPanel.add(nextButton);
		
		mainPanel.add(backPanel);
		mainPanel.add(swapPanel);
		mainPanel.add(nextPanel);
		
		initWidget(mainPanel);
		setStyleName(DEFAULT_STYLE_NAME);
	}
	
	/**
	 * Retrieve the animation that will be used to animate swaps to backward.
	 * @return animation
	 */
	public SwapAnimation getAnimationBackward() 
	{
		return animationBackward;
	}
	
	/**
	 * Retrieve the animation that will be used to animate swaps to forward.
	 * @return animation
	 */
	public SwapAnimation getAnimationForward() 
	{
		return animationForward;
	}
	
	@Override
	public void initializeContentPanel(Panel contentPanel) 
	{
		contentPanel.clear();
		contentPanel.add(this);
	}
	
	/**
	 * Return true if this pager is performing some animation
	 * @return true if animating
	 */
	public boolean isAnimating()
	{
		return swapPanel.isAnimating();
	}
	
	@Override
	public boolean isAnimationEnabled() 
	{
		return swapPanel.isAnimationEnabled();
	}

	/**
	 * Retrieve the circularPaging property value. If this property is true, the pager will start 
	 * at the first position after the last page is reached during paginations.
	 * @return true if is enabled
	 */
	public boolean isCircularPaging() 
	{
		return circularPaging;
	}

	/**
	 * Retrieve the navigationButtonsVisible property value. If this is true, the next and back buttons
	 * are visible.
	 * @return true if visible.
	 */
	public boolean isNavigationButtonsVisible()
	{
		return nextPanel.isVisible();
	}

	/**
	 * Set the animation that will be used to animate swaps to backward.
	 * @param animationBackward the animation
	 */
	public void setAnimationBackward(SwapAnimation animationBackward) 
	{
		this.animationBackward = animationBackward;
	}

	@Override
	public void setAnimationEnabled(boolean enable) 
	{
		swapPanel.setAnimationEnabled(enable);
	}

	/**
	 * Set the animation that will be used to animate swaps to forward.
	 * @param animationForward the animation
	 */
	public void setAnimationForward(SwapAnimation animationForward) 
	{
		this.animationForward = animationForward;
	}

	/**
	 * Set the circularPaging property value. If this property is true, the pager will start 
	 * at the first position after the last page is reached during paginations.
	 * @param circularPaging true to enable
	 */
	public void setCircularPaging(boolean circularPaging) 
	{
		this.circularPaging = circularPaging;
	}

	/**
	 * Set the navigationButtonsVisible property value. If this is true, the next and back buttons
	 * are visible.
	 * @param visible true to show buttons.
	 */
	public void setNavigationButtonsVisible(boolean visible)
	{
		nextPanel.setVisible(visible);
		backPanel.setVisible(visible);
	}

	@Override
	public boolean supportsInfiniteScroll() 
	{
		return false;
	}

	@Override
	public void updatePagePanel(IsWidget pagePanel, boolean forward) 
	{
		if (swapPanel.getCurrentWidget() != null && !swapPanel.isAnimating())
		{
			swapPanel.transitTo(pagePanel.asWidget(), forward?animationForward:animationBackward);
		}
		else
		{
			swapPanel.setCurrentWidget(pagePanel.asWidget());
		}
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
	protected void onUpdate() 
	{
		// Do nothing
	}

	@Override
	public void setEnabled(boolean enabled) 
	{
		super.setEnabled(enabled);
		nextButton.setEnabled(enabled);
		backButton.setEnabled(enabled);
	}

	@Override
    protected void showLoading()
    {
		if (loadingElement == null)
		{
			loadingElement = Document.get().createDivElement();
			loadingElement.setClassName(STYLE_SWAP_PAGER_PAGER_LOADING);
			Document.get().getBody().appendChild(loadingElement);
		}
    }
}
