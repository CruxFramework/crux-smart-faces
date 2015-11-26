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
package org.cruxframework.crux.smartfaces.rebind.animation;

import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.smartfaces.client.util.animation.AttentionAnimation;

/**
 * A helper class to help on HasAttentionAnimation widgets creation, based on crux pages metadata.
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 */
@TagAttributes({
	@TagAttribute(value="animation", processor=HasAttentionAnimationFactory.AnimationProcessor.class, 
				  type=HasAttentionAnimationFactory.AttentionAnimations.class, widgetType=AttentionAnimation.class,  
				  description="The animation to be aplied when the dialog is opened or closed.")
})
public interface HasAttentionAnimationFactory<C extends WidgetCreatorContext> extends HasAnimationFactory<C>
{
	public static enum AttentionAnimations{bounce, flash, pulse, rubberBand, shake, swing, tada, wobble}
	
	public static class AnimationProcessor extends AttributeProcessor<WidgetCreatorContext>
    {
		public AnimationProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
	        out.println(context.getWidget()+".setAnimation("+AttentionAnimation.class.getCanonicalName()+"."+attributeValue+");");
        }
    }
}
