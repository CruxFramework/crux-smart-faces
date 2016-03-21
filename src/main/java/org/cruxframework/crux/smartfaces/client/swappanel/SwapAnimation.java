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

package org.cruxframework.crux.smartfaces.client.swappanel;

import org.cruxframework.crux.core.client.css.animation.Animation;
import org.cruxframework.crux.core.client.css.animation.Animation.Callback;
import org.cruxframework.crux.core.client.css.animation.StandardAnimation;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author bruno.rafael
 *
 */
public abstract class SwapAnimation
{
	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation bounce = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration) 
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceIn);
		}

		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOut);
		};
	};
	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation bounceForward = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceInRight);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutLeft);
		}
	};
	
	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation bounceDownUp = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceInUp);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutDown);
		}
	};
	
	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation bounceDownward = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceInDown);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutDown);
		}
	};
	
	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation bounceBackward = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceInLeft);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutRight);
		}
	};
	
	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation bounceLeft = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceInLeft);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutLeft);
		}
	};
	
	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation bounceRight = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceInRight);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutRight);
		}
	};
	
	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation bounceUpDown = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceInDown);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutUp);
		}
	};


	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation bounceUpward = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceInUp);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.bounceOutUp);
		}
	};
	
	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fade = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeIn);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOut);
		}
	};
	
	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeAndSlideLeft = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration) 
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.slideInLeft);
		}

		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOut);
		};
	};
	
	
	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeAndSlideRight = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration) 
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.slideInRight);
		}

		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOut);
		};
	};
	
	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation fadeBackward = new SwapAnimation()
	{
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInRightBig);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutLeftBig);
		}
	};
	
	
	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeDownUp = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInDown);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutUp);
		}
	};
	
	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeDownUpBig = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInDownBig);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutUpBig);
		}
	};
	
	
	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation fadeDownward = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInDownBig);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutDownBig);
		}
	};


	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation fadeForward = new SwapAnimation()
	{
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInLeftBig);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutRightBig);
		}
	};


	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeLeft = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInLeft);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutLeft);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeLeftBig = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInLeftBig);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutLeftBig);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeRight = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInRight);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutRight);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeRightBig = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInRightBig);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutRightBig);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeUpDown = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInUp);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutDown);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation fadeUpDownBig = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInUpBig);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutDownBig);
		}
	};
	

	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation fadeUpward = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeInUpBig);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.fadeOutUpBig);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation flipX = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.flipInX);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.flipOutX);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation flipY = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.flipInY);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.flipOutY);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation lightSpeed = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.lightSpeedIn);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.lightSpeedOut);
		}
	};
	

	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation roll = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rollIn);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rollOut);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation rotate = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateIn);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOut);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation rotateDownLeft = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateInDownLeft);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutUpLeft);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation rotateDownRight = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateInDownRight);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutUpRight);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation rotateUpLeft = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateInUpLeft);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutDownLeft);
		}
	};
	

	/**
	 * This is an inOrder animation.
	 */
	public static SwapAnimation rotateUpRight = new SwapAnimation()
	{
		
		@Override
		public void animate(Widget in, Widget out, SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
		{
			animateInOrder(in, out, handler, callback, duration);
		}
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateInUpRight);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.rotateOutDownRight);
		}
	};
	

	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation slideForward = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.slideInRight);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.slideOutLeft);
		}
	};

	/**
	 * This is an Parallel animation.
	 */
	public static SwapAnimation slideBackward = new SwapAnimation()
	{
		
		@Override
		protected StandardAnimation getEntranceAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.slideInLeft);
		}
		
		@Override
		protected StandardAnimation getExitAnimation()
		{
			return new StandardAnimation(StandardAnimation.Type.slideOutRight);
		}
	};
	
	
	public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback)
	{
		animate(in, out, handler, callback, -1);
	}
	
	public void animate(Widget in, Widget out, final SwapAnimationHandler handler, SwapAnimationCallback callback, double duration)
	{
		animateParallel(in, out, handler, callback, duration);
	}
	
	protected void animateInOrder(final Widget in, final Widget out, 
		final SwapAnimationHandler handler, final SwapAnimationCallback callback, 
		final double duration)
    {
		getExitAnimation().animate(out, new Callback()
		{
			@Override
            public void onAnimationCompleted()
            {
				if (handler != null)
				{
					handler.setOutElementInitialState(out);
					handler.setInElementInitialState(in);
				}
				getEntranceAnimation().animate(in, new Callback()
				{

					@Override
                    public void onAnimationCompleted()
                    {
						handler.setInElementFinalState(in);
						handler.setOutElementFinalState(out);
	                    callback.onAnimationCompleted();
                    }
				}, duration);
            }
		}, duration);
    }
	
	protected void animateParallel(final Widget in, final Widget out, 
								   final SwapAnimationHandler handler, final SwapAnimationCallback callback, 
								   final double duration)
    {
	    getExitAnimation().animate(out, null, duration);
		if (handler != null)
		{
			handler.setInElementInitialState(in);
		}
		getEntranceAnimation().animate(in, new Callback()
		{

			@Override
            public void onAnimationCompleted()
            {
				handler.setInElementFinalState(in);
				handler.setOutElementFinalState(out);
	            callback.onAnimationCompleted();
            }
		}, duration);
    }
	
	protected abstract Animation<?> getEntranceAnimation();
	
	protected abstract Animation<?> getExitAnimation();
	
	public static interface SwapAnimationCallback
	{
		void onAnimationCompleted();
	}

	public static interface SwapAnimationHandler
	{
		void setInElementFinalState(Widget in);
		void setInElementInitialState(Widget in);
		void setOutElementFinalState(Widget out);
		void setOutElementInitialState(Widget out);
	}
}
