package org.cruxframework.crux.smartfaces.client.slider;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SlideStartEvent extends GwtEvent<SlideStartHandler>
{
	private static Type<SlideStartHandler> TYPE = new Type<SlideStartHandler>();

	/**
	 * 
	 */
	protected SlideStartEvent()
	{
	}

	/**
	 * @return
	 */
	public static Type<SlideStartHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(SlideStartHandler handler)
	{
		handler.onSlideStart(this);
	}

	@Override
	public Type<SlideStartHandler> getAssociatedType()
	{
		return TYPE;
	}

	public static <T> void fire(HasSlideEndHandlers source) 
	{
		if (TYPE != null) 
		{
			SlideStartEvent event = new SlideStartEvent();
			source.fireEvent(event);
		}
	}	
}