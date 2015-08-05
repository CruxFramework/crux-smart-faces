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

import com.google.gwt.user.client.ui.UIObject;

/**
 * A common implementation for PopupCentralizar.
 * @author samuel.cardoso
 *
 */
public class PopupCentralizerImpl extends PopupCentralizer
{
	private static final String DEFAULT_CENTER_STYLE_NAME = "faces-popup--center";

	@Override
	public void centralize(UIObject uiObject) 
	{
		uiObject.removeStyleName(DEFAULT_CENTER_STYLE_NAME);
		uiObject.addStyleName(DEFAULT_CENTER_STYLE_NAME);
		centralized = true;
	}

	@Override
	public void descentralize(UIObject uiObject) 
	{
		uiObject.removeStyleName(DEFAULT_CENTER_STYLE_NAME);
		centralized = false;
	}
}
