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
package org.cruxframework.crux.smartfaces.client.dialog;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.UIObject;

/**
 * A Internet Explorer 9 implementation for PopupCentralizar.
 * @author samuel.cardoso
 *
 */
public class PopupCentralizerIE9Impl extends PopupCentralizer
{
	@Override
	public void centralize(final UIObject uiObject) 
	{
		Scheduler.get().scheduleDeferred(new ScheduledCommand() 
		{
			@Override
			public void execute() 
			{
				int clientWidth = Window.getClientWidth();
				int clientHeight = Window.getClientHeight();

				Element elem = uiObject.getElement();

				int offsetLeft = (clientWidth - elem.getOffsetWidth()) / 2;
				int offsetTop = (clientHeight - elem.getOffsetHeight()) / 2;

				setPopupPosition(elem, offsetLeft, offsetTop);
			}
		});
	}

	private void setPopupPosition(Element elem, int left, int top) 
	{
		elem.getStyle().setPropertyPx("left", left);
		elem.getStyle().setPropertyPx("top", top);
	}

	@Override
	public void descentralize(UIObject uiObject) 
	{
		//Do nothing.
	}
}