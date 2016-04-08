/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.rebind.swapviewcontainer;

import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.swappanel.SwapViewContainer;
import org.cruxframework.crux.smartfaces.rebind.Constants;

class SwapContainerContext extends WidgetCreatorContext
{
	protected boolean hasActiveView = false;
}

/**
 * @author Bruno M. Rafael bruno.rafael@triggolabs.com
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
@DeclarativeFactory(id="swapViewContainer", library=Constants.LIBRARY_NAME, targetWidget=SwapViewContainer.class)
@TagAttributes({
	@TagAttribute(value="fitToChildrenHeight", type=Boolean.class, description="If true the panel will change its height to fit the "
		+ "height every time a transition to a new widget is performed. If false, it will keep its original height.", defaultValue="true")
})
@TagChildren({
	@TagChild(SwapAnimatedContainerFactory.ViewProcessor.class)
})
public class SwapViewContainerFactory extends WidgetCreator<SwapContainerContext> 
									implements SwapAnimatedContainerFactory<WidgetCreatorContext>
{
	@Override
    public SwapContainerContext instantiateContext()
    {
	    return new SwapContainerContext();
    }
}
