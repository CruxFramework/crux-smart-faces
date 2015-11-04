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

import org.cruxframework.crux.core.client.css.animation.Animation;
import org.cruxframework.crux.core.client.css.animation.StandardAnimation;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Thiago da Rosa de Bustamante
 * @author Samuel Almeida Cardoso
 *
 */
public abstract class InOutAnimation
{
	public static InOutAnimation bounce = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.bounceIn);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOut);
		}
	};
	public static InOutAnimation bounceDownUp = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.bounceInUp);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutDown);
		}
	};

	public static InOutAnimation bounceLeft = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.bounceInLeft);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutLeft);
		}
	};
	
	public static InOutAnimation bounceRight = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.bounceInRight);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutRight);
		}
	};
	
	public static InOutAnimation bounceUpDown = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.bounceInDown);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutUp);
		}
	};
	public static InOutAnimation fade = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeIn);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOut);
		}
	};
	
	public static InOutAnimation fadeDownUp = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInDown);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutUp);
		}
	};

	public static InOutAnimation fadeDownUpBig = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInDownBig);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutUpBig);
		}
	};

	public static InOutAnimation fadeLeft = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInLeft);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutLeft);
		}
	};

	public static InOutAnimation fadeLeftBig = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInLeftBig);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutLeftBig);
		}
	};

	public static InOutAnimation fadeRight = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInRight);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutRight);
		}
	};

	public static InOutAnimation fadeRightBig = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInRightBig);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutRightBig);
		}
	};

	public static InOutAnimation fadeUpDown = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInUp);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutDown);
		}
	};

	public static InOutAnimation fadeUpDownBig = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.fadeInUpBig);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutDownBig);
		}
	};

	public static InOutAnimation flipX = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.flipInX);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.flipOutX);
		}
	};

	public static InOutAnimation flipY = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.flipInY);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.flipOutY);
		}
	};
	
	public static InOutAnimation lightSpeed = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.lightSpeedIn);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.lightSpeedOut);
		}
	};

	public static InOutAnimation roll = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rollIn);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rollOut);
		}
	};

	public static InOutAnimation rotate = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rotateIn);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOut);
		}
	};

	public static InOutAnimation rotateDownLeft = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rotateInDownLeft);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutUpLeft);
		}
	};	

	public static InOutAnimation rotateDownRight = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rotateInDownRight);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutUpRight);
		}
	};	

	public static InOutAnimation rotateUpLeft = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rotateInUpLeft);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutDownLeft);
		}
	};	

	public static InOutAnimation rotateUpRight = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.rotateInUpRight);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutDownRight);
		}
	};	

	public static InOutAnimation slideDown = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.slideInDown);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.slideOutUp);
		}
	};	

	public static InOutAnimation slideLeft = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.slideInLeft);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.slideOutLeft);
		}
	};	

	public static InOutAnimation slideRight = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.slideInRight);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.slideOutRight);
		}
	};	

	public static InOutAnimation zoom = new InOutAnimation()
	{
		protected StandardAnimation getEntranceAnimation()
        {
	        return new StandardAnimation(StandardAnimation.Type.zoomIn);
        }

		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.zoomOut);
		}
	};	

	private Animation<?> entrance = getEntranceAnimation();	

	private Animation<?> exit = getExitAnimation();	

	public void animateEntrance(Widget widget, Animation.Callback callback)
	{
		entrance.animate(widget, callback);
	}	

	public void animateExit(Widget widget, Animation.Callback callback)
	{
		exit.animate(widget, callback);
	}	

	protected abstract Animation<?> getEntranceAnimation();
	
	protected abstract Animation<?> getExitAnimation();	
}
