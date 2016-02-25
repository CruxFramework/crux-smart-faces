package org.cruxframework.crux.smartfaces.client.slider;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SlideEndEvent extends GwtEvent<SlideEndHandler>
{
	private static Type<SlideEndHandler> TYPE = new Type<SlideEndHandler>();

	/**
	 * 
	 */
	protected SlideEndEvent()
	{
	}

	/**
	 * @return
	 */
	public static Type<SlideEndHandler> getType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(SlideEndHandler handler)
	{
		handler.onSlideEnd(this);
	}

	@Override
	public Type<SlideEndHandler> getAssociatedType()
	{
		return TYPE;
	}

	public static <T> void fire(HasSlideEndHandlers source) 
	{
		if (TYPE != null) 
		{
			SlideEndEvent event = new SlideEndEvent();
			source.fireEvent(event);
		}
	}	
}