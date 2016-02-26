package org.cruxframework.crux.smartfaces.client.slider;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.cruxframework.crux.core.client.css.transition.Transition;
import org.cruxframework.crux.core.client.css.transition.Transition.Callback;
import org.cruxframework.crux.core.client.event.HasSelectHandlers;
import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.core.client.event.swap.HasSwapHandlers;
import org.cruxframework.crux.core.client.event.swap.SwapEvent;
import org.cruxframework.crux.core.client.event.swap.SwapHandler;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Input;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;

import com.google.gwt.dom.client.PartialSupport;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel that swaps its contents using slide animations.
 * @author Thiago da Rosa de Bustamante
 */
@PartialSupport
public class TouchSlider extends Composite implements HasSwapHandlers, HasSlideStartHandlers, 
											HasSlideEndHandlers, HasSelectHandlers, 
											HasWidgets.ForIsWidget, IndexedPanel.ForIsWidget
{
	public static final String DEFAULT_STYLE_NAME = "faces-TouchSlider";
	private static Boolean supported = null;
	private static final String TOUCH_SLIDER_ITEM_STYLE_NAME = "item";
	
	protected boolean circularShowing = false;
	protected FlowPanel contentPanel;
	protected int currentWidget = -1;
	protected int slideTransitionDuration = 500;
	protected boolean sliding = false;
	protected FocusPanel touchPanel;
	
	/**
	 * Constructor
	 */
	public TouchSlider() 
	{
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
		touchPanel = new FocusPanel();
		contentPanel = new FlowPanel();

		touchPanel.add(contentPanel);
		initWidget(touchPanel);
		setStyleName(DEFAULT_STYLE_NAME);

		contentPanel.setStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesTouchSliderContentPanel());

		final TouchSliderEventHandlers eventHandlers = new TouchSliderEventHandlers(this);
		touchPanel.addTouchStartHandler(eventHandlers);
		
		addAttachHandler(new Handler()
		{
			private HandlerRegistration orientationHandlerRegistration;

			@Override
			public void onAttachOrDetach(AttachEvent event)
			{
				if (event.isAttached())
				{
					orientationHandlerRegistration = Screen.addOrientationChangeHandler(eventHandlers);
				}
				else if (orientationHandlerRegistration != null)
				{
					orientationHandlerRegistration.removeHandler();
					orientationHandlerRegistration = null;
				}
			}
		});
	}

	@Override
	public void add(IsWidget w) 
	{
		add(w.asWidget());
	}

	@Override
	public void add(Widget widget)
	{
		SimplePanel itemWrapper = new SimplePanel();

		itemWrapper.add(widget);
		wrapItem(itemWrapper);
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

	@Override
	public HandlerRegistration addSwapHandler(SwapHandler handler)
	{
		return addHandler(handler, SwapEvent.getType());
	}

	@Override
	public void clear()
	{
		contentPanel.clear();
	}

	/**
	 * Retrieve the current widget being shown on this slider.
	 * @return current widget
	 */
	public int getCurrentWidget()
	{
		return currentWidget;
	}
	
	/**
	 * Gets the duration of the slide animations in milliseconds.
	 * @return animations duration
	 */
	public int getSlideTransitionDuration()
	{
		return slideTransitionDuration;
	}
	
	@Override
	public Widget getWidget(int index)
	{
		return contentPanel.getWidget(index);
	}

	@Override
	public int getWidgetCount()
	{
		return contentPanel.getWidgetCount();
	}

	@Override
	public int getWidgetIndex(IsWidget child) 
	{
		return contentPanel.getWidgetIndex(child.asWidget().getParent());
	}

	@Override
	public int getWidgetIndex(Widget child)
	{
		return contentPanel.getWidgetIndex(child.getParent());
	}

	/**
	 * Retrieve the circularShowing property value. If this property is true, the slider will start
	 * again on first item when the end of widgets collection is reached.
	 * @return true if enabled.
	 */
	public boolean isCircularShowing()
    {
    	return circularShowing;
    }

	/**
	 * Check if the panel is slinding any widget
	 * @return true if sliding
	 */
	public boolean isSliding()
	{
		return sliding;
	}

	@Override
	public Iterator<Widget> iterator() 
	{
	    return new Iterator<Widget>()
		{
	    	private int index = -1;
	    	
			@Override
            public boolean hasNext()
            {
			      return index < (getWidgetCount() - 1);
            }

			@Override
            public Widget next()
			{
				if (index >= getWidgetCount()) 
				{
					throw new NoSuchElementException();
				}
				return getWidget(++index);
            }

			@Override
            public void remove()
            {
				if ((index < 0) || (index >= getWidgetCount())) 
				{
					throw new IllegalStateException();
				}
				TouchSlider.this.remove(index--);
            }
		};
    }

	/**
	 * Show the next widget, sliding horizontally to it.
	 */
	public void next()
	{
		if (currentWidget < 0)
		{
			showFirstWidget();
		}
		else if (hasNextWidget())
		{
			slide(-contentPanel.getElement().getOffsetWidth(), true);
		}
	}
	
	/**
	 * Show the previous widget, sliding back horizontally to it.
	 */
	public void previous()
	{
		if (currentWidget < 0)
		{
			showFirstWidget();
		}
		else if (hasPreviousWidget())
		{
			slide(contentPanel.getElement().getOffsetWidth(), true);
		}
	}
	
	
	@Override
	public boolean remove(int index)
	{
		return contentPanel.remove(index);
	}
	
	@Override
	public boolean remove(IsWidget w) 
	{
		return contentPanel.remove(w);
	}
	
	@Override
	public boolean remove(Widget w) 
	{
		return contentPanel.remove(w);
	}

	/**
	 * set the circularShowing property value. If this property is true, the slider will start
	 * again on first item when the end of widgets collection is reached.
	 * @param circularShowing true to enable.
	 */
	public void setCircularShowing(boolean circularShowing)
    {
   		this.circularShowing = circularShowing;
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
	 * Show the first widget into this slider
	 */
	public void showFirstWidget()
	{
		if (getWidgetCount() > 0)
		{
			showWidget(0);
		}
	}

	/**
	 * Set the current widget to the widget at the given index on widgets collection.
	 * @param index widget position
	 */
	public void showWidget(int index)
	{
		setCurrentWidget(index);
	}

	/**
	 * Adopt the given panel as a new item of this slider.
	 * @param itemPanel panel to adopt.
	 */
	public void wrapItem(SimplePanel itemPanel)
	{
		itemPanel.setStyleName(TOUCH_SLIDER_ITEM_STYLE_NAME);
		itemPanel.addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesTouchSliderItem());
		itemPanel.setVisible(false);
		
		contentPanel.add(itemPanel);
	}

	Widget getCurrentPanel()
	{
		return contentPanel.getWidget(currentWidget);
	}

	Widget getNextPanel()
	{
		int widgetCount = getWidgetCount();
		if (widgetCount > 0)
		{
			int index = currentWidget+1; 
			if (index >= widgetCount)
			{
				if (isCircularShowingEnabled())
				{
					index = 0;
				}
				else
				{
					return null;
				}
			}
			return contentPanel.getWidget(index);
		}
		return null;
	}

	Widget getPreviousPanel()
	{
		int widgetCount = getWidgetCount();
		if (widgetCount > 0)
		{
			int index = currentWidget-1;
			if (index < 0)
			{
				if (isCircularShowingEnabled())
				{
					index = widgetCount-1;
				}
				else
				{
					return null;
				}
			}
			return contentPanel.getWidget(index);
		}
		return null;
	}

	/**
	 * Verify if the current panel has a next widget to show.
	 * @return true if has next widget
	 */
	boolean hasNextWidget()
	{
		return isCircularShowingEnabled() || (currentWidget < getWidgetCount()-1);
	}

	/**
	 * Verify if the current panel has a previous widget to show.
	 * @return true if has previous widget
	 */
	boolean hasPreviousWidget()
	{
		return isCircularShowingEnabled() || (currentWidget > 0);
	}

	void slide(final int slideBy, boolean fireSlidingStartEvent)
	{
		sliding = true;
		if (fireSlidingStartEvent)
		{
			SlideStartEvent.fire(this);
		}
		Transition.translateX(getCurrentPanel(), slideBy, slideTransitionDuration, new Callback()
		{
			@Override
			public void onTransitionCompleted()
			{
				int nextIndex = getNextIndexAfterSlide(slideBy);
				sliding = false;
				setCurrentWidget(nextIndex);
				SlideEndEvent.fire(TouchSlider.this);
			}
		});
		if (hasPreviousWidget())
		{
			Widget previousPanel = getPreviousPanel();
			Transition.translateX(previousPanel, slideBy-previousPanel.getOffsetWidth(), slideTransitionDuration, null);
		}
		if (hasNextWidget())
		{
			Widget nextPanel = getNextPanel();
			Transition.translateX(nextPanel, slideBy+nextPanel.getOffsetWidth(), slideTransitionDuration, null);
		}
	}

	private void configureCurrentPanel() 
	{
		Widget currentPanel = getCurrentPanel();
		Transition.resetTransition(currentPanel);
		currentPanel.setVisible(true);
	}

	private void configureHiddenPanel(Widget panel, boolean forward) 
	{
		panel.setVisible(true);
		int width = panel.getOffsetWidth();
		Transition.translateX(panel, forward?width:-width, null);
	}

	private void configureNextPanel() 
	{
		configureHiddenPanel(getNextPanel(), true);
	}

	private void configurePanels()
	{
		if (currentWidget >=0 && currentWidget < getWidgetCount())
		{
			configureCurrentPanel();
			if (hasPreviousWidget())
			{
				configurePreviousPanel();
			}
			if (hasNextWidget())
			{
				configureNextPanel();
			}
		}
	}

	private void configurePreviousPanel() 
	{
		configureHiddenPanel(getPreviousPanel(), false);
	}

	private int getNextIndexAfterSlide(final int slideBy)
    {
        int index = currentWidget + (slideBy==0?0:slideBy<0?1:-1);
        if (isCircularShowingEnabled())
        {
        	int widgetCount = getWidgetCount();
			if (index >= widgetCount)
        	{
        		index = 0;
        	}
        	else if (index < 0)
        	{
        		index = widgetCount -1;
        	}
        }
		return index;
    }

	private boolean isCircularShowingEnabled()
	{
		return circularShowing && getWidgetCount() > 2;
	}

	/**
	 * Sets the widget that will be visible on this panel. 
	 * @param index
	 */
	private void setCurrentWidget(final int index) 
	{
		assert(index >=0 && index < getWidgetCount()):"Invalid index";
		if (currentWidget != index)
		{
			this.currentWidget = index;
			configurePanels();
			SwapEvent.fire(this);
		}
	}
	
	public static TouchSlider createIfSupported()
	{
		if (isSupported())
		{
			return new TouchSlider();
		}
		return null;
	}
	
	public static boolean isSupported()
	{
		if (supported == null)
		{
			supported = supportDetection();
		}
		return (supported);
	}

	private static Boolean supportDetection()
    {
	    return Screen.getCurrentDevice().getInput().equals(Input.touch);
    }
	
	
}
