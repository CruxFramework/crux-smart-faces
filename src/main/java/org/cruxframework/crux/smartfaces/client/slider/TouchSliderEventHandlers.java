package org.cruxframework.crux.smartfaces.client.slider;

import java.util.Date;

import org.cruxframework.crux.core.client.css.transition.Transition;
import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.smartfaces.client.slider.Slider.SliderEventHandlers;

import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
class TouchSliderEventHandlers extends SliderEventHandlers implements TouchStartHandler, TouchMoveHandler, TouchEndHandler 
										  
{
	private static final int SWIPE_THRESHOLD = 50;
	private static final long SWIPE_TIME_THRESHOLD = 250;
	private static final int TAP_EVENT_THRESHOLD = 5;

	private int currentTouchPosition;
	private boolean didMove;
	private int startTouchPosition;
	private long startTouchTime;
	private HandlerRegistration touchEndHandler;
	private HandlerRegistration touchMoveHandler;

	@Override
	public void onTouchEnd(TouchEndEvent event)
	{
		if (slider.sliding)
		{
			return;
		}
		if (currentTouchPosition != startTouchPosition)
		{
			final int slideBy = getSlideBy();
			slider.slide(slideBy, false);
		}
		else
		{
			SlideEndEvent.fire(slider);
		}
		if (!didMove)
		{
			SelectEvent.fire(slider);
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
		if (slider.sliding)
		{
			return;
		}
		int clientX = event.getTouches().get(0).getClientX();
		int diff = clientX - startTouchPosition;

		boolean hasNextPanel = slider.hasNextWidget();
		boolean hasPreviousPanel = slider.hasPreviousWidget();

		if ((diff < 0 && hasNextPanel) || (diff > 0 && hasPreviousPanel))
		{
			currentTouchPosition = clientX;
			Transition.translateX(slider.getCurrentPanel(), diff, null);
			if (hasPreviousPanel)
			{
				Widget previousPanel = slider.getPreviousPanel();
				Transition.translateX(previousPanel, diff-previousPanel.getOffsetWidth(), null);
			}
			if (hasNextPanel)
			{
				Widget nextPanel = slider.getNextPanel();
				Transition.translateX(nextPanel, diff+nextPanel.getOffsetWidth(), null);
			}
		}
		if (!didMove && (Math.abs(diff) > TAP_EVENT_THRESHOLD))
		{
			didMove = true;
		}
	}

	@Override
	public void onTouchStart(TouchStartEvent event)
	{
		didMove = false;
		SlideStartEvent.fire(slider);
		startTouchPosition = event.getTouches().get(0).getClientX();
		currentTouchPosition = startTouchPosition;
		startTouchTime = new Date().getTime();
		touchMoveHandler = slider.touchPanel.addTouchMoveHandler(this);
		touchEndHandler = slider.touchPanel.addTouchEndHandler(this);
	}

	@Override
	protected void handleSliderEvents() 
	{
		slider.touchPanel.addTouchStartHandler(this);
		super.handleSliderEvents();
	}
	
	/**
	 * return the final width used to slide the panels.
	 * @param hasPreviousPanel
	 * @param hasNextPanel
	 * @return negative width means "go to next", positive "go to previous" and zero "keep on current"
	 */
	private int getSlideBy()
	{
		int slideBy;
		int distX = currentTouchPosition - startTouchPosition;
		int width = slider.contentPanel.getElement().getOffsetWidth();

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
			else
			{
				slideBy = 0;
			}
		}

		if ((slideBy > 0 && !slider.hasPreviousWidget()) || (slideBy < 0 && !slider.hasNextWidget()))
		{
			slideBy = 0;
		}

		return slideBy;
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
