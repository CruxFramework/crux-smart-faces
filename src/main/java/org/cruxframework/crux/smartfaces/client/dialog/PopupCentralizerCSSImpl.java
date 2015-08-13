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

import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.dialog.PopupPanel.PopupCentralizer;

import com.google.gwt.user.client.ui.UIObject;

/**
 * Popup centralizer that uses CSS rules.
 * @author Thiago da Rosa de Bustamante
 */
public class PopupCentralizerCSSImpl extends PopupCentralizer
{
	@Override
	public void centralize(UIObject uiObject) 
	{
		uiObject.removeStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesPopupCenter());
		uiObject.addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesPopupCenter());
		setCentralized(true);
	}

	@Override
	public void descentralize(UIObject uiObject) 
	{
		uiObject.removeStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesPopupCenter());
		setCentralized(false);
	}
}
