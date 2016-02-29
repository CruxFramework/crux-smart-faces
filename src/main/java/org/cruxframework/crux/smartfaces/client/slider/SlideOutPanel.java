/*
 * Copyright 2016 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.client.slider;

import org.cruxframework.crux.core.client.css.transition.Transition;
import org.cruxframework.crux.core.client.css.transition.Transition.Callback;
import org.cruxframework.crux.core.client.event.HasSelectHandlers;
import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SlideOutPanel extends Composite implements HasSlideStartHandlers, HasSlideEndHandlers, 
														HasSelectHandlers, HasOpenHandlers<SlideOutPanel>, 
														HasCloseHandlers<SlideOutPanel>
{
	public static final String DEFAULT_STYLE_NAME = "faces-SlideOutPanel";
	private static final String SLIDE_OUT_MAIN_STYLE_NAME = "main";
	private static final String SLIDE_OUT_MENU_STYLE_NAME = "menu";

	protected FlowPanel contentPanel;
	
	protected SimplePanel mainPanel;
	protected MenuOrientation menuOrientation;
	protected SimplePanel menuPanel;
	protected boolean open = false;
	protected int slideTransitionDuration = 250;
	protected boolean sliding = false;
	protected FocusPanel touchPanel;
	/**
	 * Constructor
	 */
	public SlideOutPanel() 
	{
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
		touchPanel = new FocusPanel();
		contentPanel = new FlowPanel();

		touchPanel.add(contentPanel);
		initWidget(touchPanel);
		setStyleName(DEFAULT_STYLE_NAME);

		contentPanel.setStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesSlideOutContentPanel());

		menuPanel = new SimplePanel();
		menuPanel.setStyleName(SLIDE_OUT_MENU_STYLE_NAME);
		menuPanel.addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesSlideOutMenu());

		contentPanel.add(menuPanel);

		mainPanel = new SimplePanel();
		mainPanel.setStyleName(SLIDE_OUT_MAIN_STYLE_NAME);
		mainPanel.addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesSlideOutMain());

		contentPanel.add(mainPanel);
		
		SlideOutPanelEventHandlers eventHandlers = new SlideOutPanelEventHandlers(this);
		touchPanel.addTouchStartHandler(eventHandlers);
		
		setMenuOrientation(MenuOrientation.left);
	}

	@Override
	public HandlerRegistration addCloseHandler(CloseHandler<SlideOutPanel> handler)
	{
		return addHandler(handler, CloseEvent.getType());
	}

	@Override
	public HandlerRegistration addOpenHandler(OpenHandler<SlideOutPanel> handler) 
	{
		return addHandler(handler, OpenEvent.getType());
	}

	@Override
	public HandlerRegistration addSelectHandler(SelectHandler handler)
	{
		return addHandler(handler, SelectEvent.getType());
	}

	@Override
	public HandlerRegistration addSlideEndHandler(SlideEndHandler handler)
	{
		return addHandler(handler, SlideEndEvent.getType());
	}

	@Override
	public HandlerRegistration addSlideStartHandler(SlideStartHandler handler)
	{
		return addHandler(handler, SlideStartEvent.getType());
	}

	/**
	 * Hide the nav widget, sliding back horizontally to it.
	 */
	public void close()
	{
		if (open)
		{
			slide(0, true, false);
		}
	}

	public boolean isOpen()
	{
		return open;
	}
	
	public MenuOrientation getMenuOrientation()
	{
		return menuOrientation;
	}
	
	/**
	 * Gets the duration of the slide animations in milliseconds.
	 * @return animations duration
	 */
	public int getSlideTransitionDuration()
	{
		return slideTransitionDuration;
	}

	/**
	 * Check if the panel is slinding any widget
	 * @return true if sliding
	 */
	public boolean isSliding()
	{
		return sliding;
	}

	/**
	 * Show the hidden widget, sliding horizontally to it.
	 */
	public void open()
	{
		if (!open && hasHiddenWidget())
		{
			int slideBy = menuPanel.getElement().getOffsetWidth();
			if (menuOrientation == MenuOrientation.right)
			{
				slideBy *= -1;
			}
			slide(slideBy, true, true);
		}
	}
	
	public void setMainWidget(IsWidget w) 
	{
		setMainWidget(w.asWidget());
	}
		
	public void setMainWidget(Widget widget)
	{
		mainPanel.add(widget);
	}

	public void setMenuWidget(IsWidget w) 
	{
		setMenuWidget(w.asWidget());
	}

	public void setMenuWidget(Widget widget)
	{
		menuPanel.add(widget);
	}

	public void setMenuOrientation(MenuOrientation menuOrientation)
	{
		this.menuOrientation = menuOrientation;
		if (menuOrientation == MenuOrientation.left)
		{
			menuPanel.getElement().getStyle().setProperty("left", "0px");
			menuPanel.getElement().getStyle().setProperty("right", "");
		}
		else
		{
			menuPanel.getElement().getStyle().setProperty("right", "0px");
			menuPanel.getElement().getStyle().setProperty("left", "");
		}
	}

	/**
	 * Sets the duration of the slide animations in milliseconds.
	 * @param slideTransitionDuration
	 */
	public void setSlideTransitionDuration(int transitionDuration) 
	{
		this.slideTransitionDuration = transitionDuration;
	}

	/**
	 * Verify if the hidden panel has a next widget to show.
	 * @return true if has next widget
	 */
	boolean hasHiddenWidget()
	{
		return menuPanel.getWidget() != null;
	}

	void slide(final int slideBy, boolean fireSlidingStartEvent, final boolean openMenu)
	{
		sliding = true;
		if (fireSlidingStartEvent)
		{
			SlideStartEvent.fire(this);
		}
		Transition.translateX(mainPanel, slideBy, slideTransitionDuration, new Callback()
		{
			@Override
			public void onTransitionCompleted()
			{
				SlideEndEvent.fire(SlideOutPanel.this);
				sliding = false;
				if (open != openMenu)
				{
					open = openMenu;
					if (open)
					{
						OpenEvent.fire(SlideOutPanel.this, SlideOutPanel.this);
					}
					else
					{
						CloseEvent.fire(SlideOutPanel.this, SlideOutPanel.this);
					}
				}
			}
		});
	}

	public static enum MenuOrientation {left, right}
}
