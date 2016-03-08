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
import org.cruxframework.crux.core.rebind.screen.widget.creator.CrawlableViewContainerFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.swappanel.SwapCrawlableViewContainer;
import org.cruxframework.crux.smartfaces.rebind.Constants;

/**
 * @author Bruno M. Rafael bruno.rafael@triggolabs.com
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
@DeclarativeFactory(id="swapCrawlableViewContainer", library=Constants.LIBRARY_NAME, targetWidget=SwapCrawlableViewContainer.class)
@TagChildren({
	@TagChild(SwapAnimatedContainerFactory.ViewProcessor.class)
})
public class SwapCrawlableViewContainerFactory extends WidgetCreator<SwapContainerContext> 
						implements CrawlableViewContainerFactory<SwapContainerContext>,
								   SwapAnimatedContainerFactory<WidgetCreatorContext>
{
	@Override
    public SwapContainerContext instantiateContext()
    {
	    return new SwapContainerContext();
    }
}
