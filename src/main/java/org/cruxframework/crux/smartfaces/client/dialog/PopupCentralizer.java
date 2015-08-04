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
 * This class handle the centralizer implementation throught
 * all browsers compatible with this library.
 * @author samuel.cardoso
 *
 */
public abstract class PopupCentralizer
{
	boolean centralized = false;

	/**
	 * Centralize the popup.
	 * @param uiObject
	 */
	public abstract void centralize(UIObject uiObject);
	
	/**
	 * Descentralize the popup.
	 * @param uiObject
	 */
	public abstract void descentralize(UIObject uiObject);

	/**
	 * @return true if the popup is centralized and
	 * false otherwise.
	 */
	public boolean isCentralized()
	{
		return centralized;
	}
	
	/**
	 * @param centralized
	 */
	public void setCentralized(boolean centralized) 
	{
		this.centralized = centralized;
	}
}
