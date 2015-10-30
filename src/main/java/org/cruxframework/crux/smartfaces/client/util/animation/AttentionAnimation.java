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
package org.cruxframework.crux.smartfaces.client.util.animation;

import org.cruxframework.crux.core.client.css.animation.StandardAnimation;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public abstract class AttentionAnimation extends InOutAnimation
{
	protected StandardAnimation getExitAnimation()
	{
		return new StandardAnimation(StandardAnimation.Type.fadeOut);
	}

	public static InOutAnimation bounce = new AttentionAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.bounce);
        }
	};

	public static InOutAnimation flash = new AttentionAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.flash);
        }
	};

	public static InOutAnimation pulse = new AttentionAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.pulse);
        }
	};

	public static InOutAnimation rubberBand = new AttentionAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rubberBand);
        }
	};

	public static InOutAnimation shake = new AttentionAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.shake);
        }
	};

	public static InOutAnimation swing = new AttentionAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.swing);
        }
	};

	public static InOutAnimation tada = new AttentionAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.tada);
        }
	};

	public static InOutAnimation wobble = new AttentionAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.wobble);
        }
	};
}
