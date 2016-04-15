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
import org.cruxframework.crux.core.client.event.TouchEventsHandler;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SlideOutPanel extends Composite implements HasSlideStartHandlers, HasSlideEndHandlers, 
														HasSelectHandlers, HasOpenHandlers<SlideOutPanel>, 
														HasCloseHandlers<SlideOutPanel>,
														TouchEventsHandler
{
	public static final String DEFAULT_STYLE_NAME = "faces-SlideOutPanel";
	private static final int DEFAULT_SLIDE_SENSITIVITY = 5;
	private static final String SLIDE_OUT_MAIN_STYLE_NAME = "main";
	private static final String SLIDE_OUT_MENU_STYLE_NAME = "menu";

	protected boolean autoHideMenu = false;
	protected FlowPanel contentPanel;
	protected SimplePanel mainPanel;
	protected MenuOrientation menuOrientation;
	protected SimplePanel menuPanel;
	protected boolean open = false;
	protected boolean preventDefaultTouchEvents = false;
	protected boolean slideEnabled;
	protected int slideSensitivity = DEFAULT_SLIDE_SENSITIVITY;
	protected int slideTransitionDuration = 250;
	protected boolean sliding = false;
	
	protected boolean stopPropagationTouchEvents = false;
	protected SimplePanel touchPanel;
	private HandlerRegistration autoHideSelectHandler;
	private SlideOutPanelEventHandlers eventHandlers;
	private boolean hasSelectHandlers = false;
	
	/**
	 * Constructor
	 */
	public SlideOutPanel() 
	{
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
		touchPanel = new SimplePanel();
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
		
		setSlideEnabled(true);
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
		if (eventHandlers == null)
		{
			eventHandlers = GWT.create(SlideOutPanelEventHandlers.class);
			eventHandlers.handleEvents(this);
		}
		this.hasSelectHandlers = true;
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

	/**
	 * Retrieve the menu orientation
	 * @return
	 */
	public MenuOrientation getMenuOrientation()
	{
		return menuOrientation;
	}

	public int getSlideSensitivity()
	{
		return slideSensitivity;
	}

	/**
	 * Gets the duration of the slide animations in milliseconds.
	 * @return animations duration
	 */
	public int getSlideTransitionDuration()
	{
		return slideTransitionDuration;
	}

	public boolean isAutoHideMenu()
	{
		return autoHideMenu;
	}

	/**
	 * Verify if the slide movement is horizontally orientated
	 * @return
	 */
	public boolean isHorizontalOrientation()
    {
	    return menuOrientation == MenuOrientation.right || menuOrientation == MenuOrientation.left;
    }
	
	/**
	 * Check if the panel is open
	 * @return
	 */
	public boolean isOpen()
	{
		return open;
	}
	
	/**
	 * If enabled, slide will be available for touch devices
	 * @return
	 */
	public boolean isSlideEnabled()
	{
		return slideEnabled;
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
			int slideBy;
			switch (menuOrientation)
            {
				case left:
					slideBy = menuPanel.getElement().getOffsetWidth();
				break;
				case right:
					slideBy = -menuPanel.getElement().getOffsetWidth();
				break;
				case top:
					slideBy = menuPanel.getElement().getOffsetHeight();
				break;
				case bottom:
					slideBy = -menuPanel.getElement().getOffsetHeight();
				break;
				default:
					slideBy = 0;
				break;
			}
			slide(slideBy, true, true);
		}
	}
	
	public void setAutoHideMenu(boolean autoHideMenu)
	{
		this.autoHideMenu = autoHideMenu;
		if (autoHideMenu)
		{
			autoHideSelectHandler = addSelectHandler(new SelectHandler()
			{
				@Override
				public void onSelect(SelectEvent event)
				{
					if (isOpen())
					{
						close();
					}
				}
			});
		}
		else
		{
			if (autoHideSelectHandler != null)
			{
				autoHideSelectHandler.removeHandler();
				autoHideSelectHandler = null;
			}
		}
	}

	/**
	 * Sets the widget to be displayed into the main area
	 * @param w
	 */
	public void setMainWidget(IsWidget w) 
	{
		setMainWidget(w.asWidget());
	}

	/**
	 * Sets the widget to be displayed into the main area
	 * @param widget
	 */
	public void setMainWidget(Widget widget)
	{
		mainPanel.add(widget);
	}

	/**
	 * Define the menu orientation
	 * @param menuOrientation
	 */
	public void setMenuOrientation(MenuOrientation menuOrientation)
	{
		this.menuOrientation = menuOrientation;
		switch (menuOrientation)
        {
			case left:
				menuPanel.getElement().getStyle().setProperty("left", "0px");
				menuPanel.getElement().getStyle().setProperty("right", "");
				menuPanel.getElement().getStyle().setProperty("top", "");
				menuPanel.getElement().getStyle().setProperty("bottom", "");
			break;
			case right:
				menuPanel.getElement().getStyle().setProperty("left", "");
				menuPanel.getElement().getStyle().setProperty("right", "0px");
				menuPanel.getElement().getStyle().setProperty("top", "");
				menuPanel.getElement().getStyle().setProperty("bottom", "");
			break;
			case top:
				menuPanel.getElement().getStyle().setProperty("left", "");
				menuPanel.getElement().getStyle().setProperty("right", "");
				menuPanel.getElement().getStyle().setProperty("top", "0px");
				menuPanel.getElement().getStyle().setProperty("bottom", "");
			break;
			case bottom:
				menuPanel.getElement().getStyle().setProperty("left", "");
				menuPanel.getElement().getStyle().setProperty("right", "");
				menuPanel.getElement().getStyle().setProperty("top", "");
				menuPanel.getElement().getStyle().setProperty("bottom", "0px");
			break;
		}
	}

	/**
	 * Sets the widget to be displayed into the menu area
	 * @param w
	 */
	public void setMenuWidget(IsWidget w) 
	{
		setMenuWidget(w.asWidget());
	}

	/**
	 * Sets the widget to be displayed into the menu area
	 * @param widget
	 */
	public void setMenuWidget(Widget widget)
	{
		menuPanel.add(widget);
	}

	/**
	 * Defines the menu area's width
	 * @param width
	 */
	public void setMenuWidth(String width)
	{
		menuPanel.setWidth(width);
	}

	public void setPreventDefaultTouchEvents(boolean preventDefaultTouchEvents)
	{
		this.preventDefaultTouchEvents = preventDefaultTouchEvents;
	}
	
	public void setSlideEnabled(boolean enabled)
    {
		if (eventHandlers != null && !enabled && !hasSelectHandlers)
		{
			eventHandlers.releaseEvents();
			eventHandlers = null;
		}
		if (eventHandlers == null && enabled)
		{
			eventHandlers = GWT.create(SlideOutPanelEventHandlers.class);
			eventHandlers.handleEvents(this);
		}
		this.slideEnabled = enabled;
    }

	public void setSlideSensitivity(int slideSensitivity)
	{
		this.slideSensitivity = slideSensitivity;
	}

	/**
	 * Sets the duration of the slide animations in milliseconds.
	 * @param slideTransitionDuration
	 */
	public void setSlideTransitionDuration(int transitionDuration) 
	{
		this.slideTransitionDuration = transitionDuration;
	}

	public void setStopPropagationTouchEvents(boolean stopPropagationTouchEvents)
	{
		this.stopPropagationTouchEvents = stopPropagationTouchEvents;
	}

	protected HandlerRegistration addClickHandler(ClickHandler handler)
	{
		return addDomHandler(handler, ClickEvent.getType());
	}

	protected HandlerRegistration addTouchEndHandler(TouchEndHandler handler)
	{
		return addDomHandler(handler, TouchEndEvent.getType());
	}
	
	protected HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler)
	{
		return addDomHandler(handler, TouchMoveEvent.getType());
	}

	protected HandlerRegistration addTouchStartHandler(TouchStartHandler handler)
	{
		return addDomHandler(handler, TouchStartEvent.getType());
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
		if (isHorizontalOrientation())
		{
			Transition.translateX(mainPanel, slideBy, slideTransitionDuration, new Callback()
			{
				@Override
				public void onTransitionCompleted()
				{
					SlideEndEvent.fire(SlideOutPanel.this);
					sliding = false;
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
			});
		}
		else
		{
			Transition.translateY(mainPanel, slideBy, slideTransitionDuration, new Callback()
			{
				@Override
				public void onTransitionCompleted()
				{
					SlideEndEvent.fire(SlideOutPanel.this);
					sliding = false;
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
			});
			
		}
	}

	public static enum MenuOrientation {bottom, left, right, top}

	static class SlideOutPanelEventHandlers
	{
		protected SlideOutPanel slideOutPanel;

		public void releaseEvents()
        {
			this.slideOutPanel = null;
        }

		protected boolean eventTargetsMenu(NativeEvent event)
		{
			EventTarget target = event.getEventTarget();
			if (Element.is(target))
			{
				return slideOutPanel.menuPanel.getElement().isOrHasChild(Element.as(target));
			}
			return false;
		}
		
		protected void handleEvents(SlideOutPanel slideOutPanel)
		{
			this.slideOutPanel = slideOutPanel;
		}
	}

}
