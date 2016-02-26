package org.cruxframework.crux.smartfaces.client.slider;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface HasSlideStartHandlers extends HasHandlers 
{
	HandlerRegistration addSlideStartHandler(SlideStartHandler handler);
}
