package org.cruxframework.crux.smartfaces.client.slider;

import java.util.Date;

import org.cruxframework.crux.core.client.css.transition.Transition;
import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.smartfaces.client.slider.SlideOutPanel.MenuOrientation;
import org.cruxframework.crux.smartfaces.client.slider.SlideOutPanel.SlideOutPanelEventHandlers;

import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
class SlideOutPanelTouchEventHandlers extends SlideOutPanelEventHandlers 
							implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
{
	private static final int SWIPE_THRESHOLD = 50;
	private static final long SWIPE_TIME_THRESHOLD = 250;
	private static final int TAP_EVENT_THRESHOLD = 5;

	private int currentTouchPosition;
	private boolean didMove;
	private int menuPanelHeight;
	private int menuPanelWidth;
	private int startTouchPosition;
	private long startTouchTime;
	private HandlerRegistration touchEndHandler;
	private HandlerRegistration touchMoveHandler;
	private HandlerRegistration touchStartHandler;

	@Override
	public void onTouchEnd(TouchEndEvent event)
	{
		if (slideOutPanel.sliding)
		{
			return;
		}
		if (currentTouchPosition != startTouchPosition)
		{
			final int slideBy = getSlideBy();
			slideOutPanel.slide(slideBy, false, slideBy != 0);
		}
		else
		{
			SlideEndEvent.fire(slideOutPanel);
		}
		if (!didMove)
		{
			SelectEvent.fire(slideOutPanel);
		}
		if(touchMoveHandler != null)
		{
			touchMoveHandler.removeHandler();
		}
		if(touchEndHandler != null)
		{
			touchEndHandler.removeHandler();
		}
	}
	
	@Override
	public void onTouchMove(TouchMoveEvent event)
	{
		if (slideOutPanel.sliding)
		{
			return;
		}
		if (slideOutPanel.isHorizontalOrientation())
		{
			handleHorizontalTouchMove(event);
		}
		else
		{
			handleVerticalTouchMove(event);
		}
	}
	
	@Override
	public void onTouchStart(TouchStartEvent event)
	{
		didMove = false;
		SlideStartEvent.fire(slideOutPanel);
		startTouchPosition = slideOutPanel.isHorizontalOrientation()?event.getTouches().get(0).getClientX():event.getTouches().get(0).getClientY();
		currentTouchPosition = startTouchPosition;
		startTouchTime = new Date().getTime();
		touchMoveHandler = slideOutPanel.touchPanel.addTouchMoveHandler(this);
		touchEndHandler = slideOutPanel.touchPanel.addTouchEndHandler(this);
		menuPanelWidth = slideOutPanel.menuPanel.getElement().getOffsetWidth();
		menuPanelHeight = slideOutPanel.menuPanel.getElement().getOffsetHeight();
	}

	@Override
	public void releaseEvents()
	{
	    if (touchStartHandler != null)
	    {
	    	touchStartHandler.removeHandler();
	    	touchStartHandler = null;
	    }
	    super.releaseEvents();
	}

	@Override
	protected void handleEvents(SlideOutPanel slideOutPanel)
	{
	    super.handleEvents(slideOutPanel);
		touchStartHandler = slideOutPanel.touchPanel.addTouchStartHandler(this);
	}

	private int getHorizontalSlideBy()
    {
	    int slideBy;
		int distX = currentTouchPosition - startTouchPosition;
		int width = slideOutPanel.menuPanel.getElement().getOffsetWidth();

		if (isSwapEvent(distX))
		{
			slideBy = distX > 0?width:-width;
		}
		else
		{
			if (Math.abs(distX) > width / 2)
			{
				slideBy = (distX > 0) ? width : width * -1;
			}
			else if (slideOutPanel.open)
			{
				slideBy = menuPanelWidth;
			}
			else
			{
				slideBy = 0;
			}
		}

		if ((slideBy > 0 && slideOutPanel.menuOrientation == MenuOrientation.right) 
			|| (slideBy < 0 && slideOutPanel.menuOrientation == MenuOrientation.left))
		{
			slideBy = 0;
		}

		return slideBy;
    }

	/**
	 * return the final width used to slide the panels.
	 * @param hasPreviousPanel
	 * @param hasNextPanel
	 * @return negative width means "go to next", positive "go to previous" and zero "keep on current"
	 */
	private int getSlideBy()
	{
		if (slideOutPanel.isHorizontalOrientation())
		{
			return getHorizontalSlideBy();
		}
		return getVerticalSlideBy();
	}
	
	private int getVerticalSlideBy()
    {
	    int slideBy;
		int distY = currentTouchPosition - startTouchPosition;
		int height = slideOutPanel.menuPanel.getElement().getOffsetHeight();

		if (isSwapEvent(distY))
		{
			slideBy = distY > 0?height:-height;
		}
		else
		{
			if (Math.abs(distY) > height / 2)
			{
				slideBy = (distY > 0) ? height : height * -1;
			}
			else if (slideOutPanel.open)
			{
				slideBy = menuPanelHeight;
			}
			else
			{
				slideBy = 0;
			}
		}

		if ((slideBy > 0 && slideOutPanel.menuOrientation == MenuOrientation.bottom) 
			|| (slideBy < 0 && slideOutPanel.menuOrientation == MenuOrientation.top))
		{
			slideBy = 0;
		}

		return slideBy;
    }

	private void handleHorizontalTouchMove(TouchMoveEvent event)
    {
	    int clientX = event.getTouches().get(0).getClientX();
		int diff = clientX - startTouchPosition;
		int absDiff = Math.abs(diff);
		if (absDiff < menuPanelWidth)
		{
			int delta = diff;
			if (slideOutPanel.open)
			{
				delta += slideOutPanel.menuOrientation == MenuOrientation.left?menuPanelWidth:-menuPanelWidth;
			}
			// check boundaries
			if ((slideOutPanel.menuOrientation == MenuOrientation.right && delta < 0 && delta > -menuPanelWidth) || 
				(slideOutPanel.menuOrientation == MenuOrientation.left && delta > 0 && delta < menuPanelWidth))
			{
				currentTouchPosition = clientX;
				Transition.translateX(slideOutPanel.mainPanel, delta, null);
			}
		}
		if (!didMove && (absDiff > TAP_EVENT_THRESHOLD))
		{
			didMove = true;
		}
    }	
	
	private void handleVerticalTouchMove(TouchMoveEvent event)
    {
	    int clientY = event.getTouches().get(0).getClientY();
		int diff = clientY - startTouchPosition;
		int absDiff = Math.abs(diff);
		if (absDiff < menuPanelHeight)
		{
			int delta = diff;
			if (slideOutPanel.open)
			{
				delta += slideOutPanel.menuOrientation == MenuOrientation.top?menuPanelHeight:-menuPanelHeight;
			}
			// check boundaries
			if ((slideOutPanel.menuOrientation == MenuOrientation.bottom && delta < 0 && delta > -menuPanelHeight) || 
				(slideOutPanel.menuOrientation == MenuOrientation.top && delta > 0 && delta < menuPanelHeight))
			{
				currentTouchPosition = clientY;
				Transition.translateY(slideOutPanel.mainPanel, delta, null);
			}
		}
		if (!didMove && (absDiff > TAP_EVENT_THRESHOLD))
		{
			didMove = true;
		}
    }

	private boolean isSwapEvent(int distX)
	{
		long endTime = new Date().getTime();
		long diffTime = endTime - this.startTouchTime;

		if (diffTime <= SWIPE_TIME_THRESHOLD)
		{
			if (Math.abs(distX) >= SWIPE_THRESHOLD)
			{
				return true;
			}
		}
		return false;
	}
}
