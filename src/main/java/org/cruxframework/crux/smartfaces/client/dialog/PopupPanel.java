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
package org.cruxframework.crux.smartfaces.client.dialog;

import java.util.ArrayList;
import java.util.List;

import org.cruxframework.crux.core.client.collection.FastList;
import org.cruxframework.crux.core.client.css.animation.Animation;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.dialog.animation.HasDialogAnimation;
import org.cruxframework.crux.smartfaces.client.util.animation.InOutAnimation;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;

/**
 * A panel that can "pop up" over other widgets. It overlays the browser's
 * client area (and any previously-created popups).
 * 
 * <p>
 * A PopupPanel should not generally be added to other panels; rather, it should
 * be shown and hidden using the {@link #show()} and {@link #hide()} methods.
 * </p>
 * 
 * @author Thiago da Rosa de Bustamante
 */
public class PopupPanel extends SimplePanel implements HasDialogAnimation, HasCloseHandlers<PopupPanel>, HasOpenHandlers<PopupPanel>, NativePreviewHandler
{
	public static final String DEFAULT_GLASS_STYLE_NAME = "faces-overlay";
	
	private static List<CloseHandler<PopupPanel>> defaultCloseHandlers = new ArrayList<CloseHandler<PopupPanel>>();
	private static List<OpenHandler<PopupPanel>> defaultOpenHandlers = new ArrayList<OpenHandler<PopupPanel>>();

	private static final String FACES_POPUP_CONTENT = "faces-popup-content";
	/**
	 * Coordinates the times that the glass is called.  
	 */
	private static int numberCalls = 0;
	private static final String STYLE_POPUP = "faces-popup";
	
	private boolean animating;
	private InOutAnimation animation;
	private double animationDuration = -1;
	private boolean animationEnabled;
	private boolean autoHide;
	private boolean autoHideOnHistoryEvents;
	private FastList<Element> autoHidePartners;
	private Element containerElement;
	private Element glass;
	private boolean glassShowing;
	private String glassStyleName = DEFAULT_GLASS_STYLE_NAME;
	private HandlerRegistration historyHandlerRegistration;
	private int left = -1;
	private boolean modal;
	private HandlerRegistration nativePreviewHandlerRegistration;
	private PopupCentralizer popupCentralizer = GWT.create(PopupCentralizer.class);

	private boolean showing;
	private int top = -1;

	/**
	 * Creates an empty popup panel. A child widget must be added to it before
	 * it is shown.
	 */
	public PopupPanel()
	{
		this(false);
	}

	/**
	 * Creates an empty popup panel, specifying its "auto-hide" property.
	 * 
	 * @param autoHide
	 *            <code>true</code> if the popup should be automatically hidden
	 *            when the user clicks outside of it or the history token
	 *            changes.
	 */
	public PopupPanel(boolean autoHide)
	{
		this(autoHide, false);
	}

	/**
	 * Creates an empty popup panel, specifying its "auto-hide" and "modal"
	 * properties.
	 * 
	 * @param autoHide - <code>true</code> if the popup should be automatically hidden
	 *            when the user clicks outside of it or the history token
	 *            changes.
	 * @param modal -  <code>true</code> if keyboard or mouse events that do not
	 *            target the PopupPanel or its children should be ignored
	 */
	public PopupPanel(boolean autoHide, boolean modal)
	{
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
		
		this.autoHide = autoHide;
		this.autoHideOnHistoryEvents = autoHide;
		this.modal = modal;
		if (modal)
		{
			glass = Document.get().createDivElement();
			glass.setClassName(glassStyleName);
			glass.addClassName(FacesBackboneResourcesCommon.INSTANCE.css().facesOverlay());			
		}

		addCloseHandler(new CloseHandler<PopupPanel>()
		{
			@Override
			public void onClose(CloseEvent<PopupPanel> event)
			{
				if (defaultCloseHandlers != null)
				{
					for (CloseHandler<PopupPanel> closeHandler : defaultCloseHandlers)
					{
						closeHandler.onClose(event);
					}
				}
			}
		});

		addOpenHandler(new OpenHandler<PopupPanel>()
		{
			@Override
			public void onOpen(OpenEvent<PopupPanel> event)
			{
				if (defaultOpenHandlers != null)
				{
					for (OpenHandler<PopupPanel> openHandler : defaultOpenHandlers)
					{
						openHandler.onOpen(event);
					}
				}
			}
		});

		containerElement = Document.get().createDivElement().cast();
		super.getContainerElement().appendChild(containerElement);
		setPosition(0, 0);
		setStyleName(getContainerElement(), FACES_POPUP_CONTENT);
	}

	/**
	 * Mouse events that occur within an autoHide partner will not hide a panel
	 * set to autoHide.
	 * 
	 * @param partner
	 *            the auto hide partner to add
	 */
	public void addAutoHidePartner(Element partner)
	{
		assert partner != null : "partner cannot be null";
		if (autoHidePartners == null)
		{
			autoHidePartners = new FastList<Element>();
		}
		autoHidePartners.add(partner);
	}
	
	@Override
	public HandlerRegistration addCloseHandler(CloseHandler<PopupPanel> handler)
	{
		return addHandler(handler, CloseEvent.getType());
	}
		
	@Override
	public HandlerRegistration addOpenHandler(OpenHandler<PopupPanel> handler) 
	{
		return addHandler(handler, OpenEvent.getType());
	}

	/**
	 * Centers the popup in the browser window and shows it. If the popup was
	 * already showing, then it is centered.
	 */
	public void center()
	{
		if (!popupCentralizer.isCentralized())
		{
			if (animating)
			{
				fixPositionToCenter();
				Scheduler.get().scheduleFixedPeriod(new RepeatingCommand()
				{
					@Override
					public boolean execute()
					{
						if (animating)
						{
							return true;
						}
						centralizeMe();
						return false;
					}
				}, 10);
			}
			else
			{
				centralizeMe();
				if (!isShowing())
				{
					show();
				}
			}
		}
	}

	/**
	 * Gets the style name to be used on the glass element. 
	 * 
	 * @return the glass element's style name
	 */
	public String getGlassStyleName()
	{
		return glassStyleName;
	}

	/**
	 * Hides the popup and detaches it from the page. This has no effect if it
	 * is not currently showing.
	 */
	public void hide()
	{
		hide(false);
	}

	/**
	 * Determines if the popup is animating or not
	 * 
	 * @return <code>true</code> if the popup is running a animation
	 */
	public boolean isAnimating() 
	{
		return animating;
	}

	@Override
	public boolean isAnimationEnabled()
	{
		return animationEnabled;
	}

	/**
	 * Returns <code>true</code> if the popup should be automatically hidden
	 * when the user clicks outside of it.
	 * 
	 * @return true if autoHide is enabled, false if disabled
	 */
	public boolean isAutoHideEnabled()
	{
		return autoHide;
	}	

	/**
	 * Returns <code>true</code> if the popup should be automatically hidden
	 * when the history token changes, such as when the user presses the
	 * browser's back button.
	 * 
	 * @return true if enabled, false if disabled
	 */
	public boolean isAutoHideOnHistoryEventsEnabled()
	{
		return autoHideOnHistoryEvents;
	}

	/**
	 * Returns <code>true</code> if the popup opens a modal window
	 * @return true if modal
	 */
	public boolean isModal()
	{
		return modal;
	}

	/**
	 * Determines whether or not this popup is showing.
	 * 
	 * @return <code>true</code> if the popup is showing
	 * @see #show()
	 * @see #hide()
	 */
	public boolean isShowing()
	{
		return showing;
	}	

	/**
	 * Determines whether or not this popup is visible. Note that this just
	 * checks the <code>visibility</code> style attribute, which is set in the
	 * {@link #setVisible(boolean)} method. If you want to know if the popup is
	 * attached to the page, use {@link #isShowing()} instead.
	 * 
	 * @return <code>true</code> if the object is visible
	 * @see #setVisible(boolean)
	 */
	@Override
	public boolean isVisible()
	{
		return !getElement().getStyle().getVisibility().equals(Visibility.HIDDEN.getCssName());
	}

	@Override
	public void onPreviewNativeEvent(NativePreviewEvent event)
	{
		// If the event has been canceled or consumed, ignore it
		if (event.isCanceled() || (event.isConsumed())) 
		{
			// We need to ensure that we cancel the event even if its been consumed so
			// that popups lower on the stack do not auto hide
			if (modal) 
			{
				event.cancel();
			}
			return;
		}

		// Fire the event hook and return if the event is canceled
		//	    onPreviewNativeEvent(event);
		// Cancel the event based on the deprecated onEventPreview() method
		//	    if (event.isFirstHandler()
		//	        && !onEventPreview(Event.as(event.getNativeEvent()))) {
		//	      event.cancel();
		//	    }

		if (event.isCanceled()) 
		{
			return;
		}

		// If the event targets the popup or the partner, consume it
		Event nativeEvent = Event.as(event.getNativeEvent());
		boolean eventTargetsPopupOrPartner = eventTargetsPopup(nativeEvent) || eventTargetsPartner(nativeEvent);
		if (eventTargetsPopupOrPartner) 
		{
			event.consume();
		}

		// Cancel the event if it doesn't target the modal popup. Note that the
		// event can be both canceled and consumed.
		if (modal) 
		{
			event.cancel();
		}

		// Switch on the event type
		int type = nativeEvent.getTypeInt();
		switch (type) 
		{
		case Event.ONMOUSEDOWN:
		case Event.ONTOUCHSTART:
			// Don't eat events if event capture is enabled, as this can
			// interfere with dialog dragging, for example.
			if (DOM.getCaptureElement() != null) 
			{
				event.consume();
				return;
			}

			if (!eventTargetsPopupOrPartner && autoHide) 
			{
				hide(true);
				return;
			}
			break;
		case Event.ONMOUSEUP:
		case Event.ONMOUSEMOVE:
		case Event.ONCLICK:
		case Event.ONDBLCLICK:
		case Event.ONTOUCHEND: 
		{
			// Don't eat events if event capture is enabled, as this can
			// interfere with dialog dragging, for example.
			if (DOM.getCaptureElement() != null) 
			{
				event.consume();
				return;
			}
			break;
		}

		case Event.ONFOCUS: 
		{
			@SuppressWarnings("deprecation")
			Element target = nativeEvent.getTarget();
			if (modal && !eventTargetsPopupOrPartner && (target != null)) {
				blur(target);
				event.cancel();
				return;
			}
			break;
		}
		}
	}

	/**
	 * Remove an autoHide partner.
	 * 
	 * @param partner
	 *            the auto hide partner to remove
	 */
	public void removeAutoHidePartner(Element partner)
	{
		assert partner != null : "partner cannot be null";
		if (autoHidePartners != null)
		{
			autoHidePartners.remove(partner);
		}
	}

	/**
	 * Defines the animation used to animate popup entrances and exits
	 * @param animation
	 */
	public void setAnimation(InOutAnimation animation)
	{
		this.animation = animation;
		setAnimationEnabled(animation != null);
	}
	
	/**
	 * Defines the duration for the animation used to animate popup entrances and exits
	 * @param duration animatin duration in seconds.
	 */
	public void setAnimationDuration(double duration)
	{
		this.animationDuration = duration;
	}

	@Override
	public void setAnimationEnabled(boolean enable)
	{
		animationEnabled = enable;
	}

	/**
	 * Enable or disable the autoHide feature. When enabled, the popup will be
	 * automatically hidden when the user clicks outside of it.
	 * 
	 * @param autoHide
	 *            true to enable autoHide, false to disable
	 */
	public void setAutoHideEnabled(boolean autoHide)
	{
		this.autoHide = autoHide;
	}

	/**
	 * Enable or disable autoHide on history change events. When enabled, the
	 * popup will be automatically hidden when the history token changes, such
	 * as when the user presses the browser's back button. Disabled by default.
	 * 
	 * @param enabled
	 *            true to enable, false to disable
	 */
	public void setAutoHideOnHistoryEventsEnabled(boolean enabled)
	{
		this.autoHideOnHistoryEvents = enabled;
	}

	/**
	 * Sets the style name to be used on the glass element. 
	 * 
	 * @param glassStyleName
	 *            the glass element's style name
	 */
	public void setGlassStyleName(String glassStyleName)
	{
		this.glassStyleName = glassStyleName;
		if (glass != null)
		{
			glass.setClassName(glassStyleName);
			glass.addClassName(FacesBackboneResourcesCommon.INSTANCE.css().facesOverlay());
		}
	}

	/**
	 * Sets the popup's position relative to the browser's client area. The
	 * popup's position may be set before calling {@link #show()}.
	 * 
	 * @param left
	 *            the left position, in pixels
	 * @param top
	 *            the top position, in pixels
	 */
	public void setPosition(int left, int top)
	{
		if (popupCentralizer.isCentralized())
		{
			uncentralizeMe();
		}

		// Account for the difference between absolute position and the
		// body's positioning context.
		Document document = Document.get();
		left -= document.getBodyOffsetLeft();
		top -= document.getBodyOffsetTop();

		this.left = left;
		this.top = top;
		setPopupPositionStyle(left, top);
	}

	@Override
	public void setStyleName(String style)
	{
	    super.setStyleName(style);
	    addStyleName(STYLE_POPUP);
	    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesPopup());
	}

	@Override
	public void setStyleName(String style, boolean add)
	{
		super.setStyleName(style, add);
		if (!add)
		{
		    addStyleName(STYLE_POPUP);
		    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesPopup());
		}
	}

	/**
	 * Sets whether this object is visible. This method just sets the
	 * <code>visibility</code> style attribute. You need to call {@link #show()}
	 * to actually attached/detach the {@link PopupPanel} to the page.
	 * 
	 * @param visible
	 *            <code>true</code> to show the object, <code>false</code> to
	 *            hide it
	 * @see #show()
	 * @see #hide()
	 */
	@Override
	public void setVisible(boolean visible)
	{
		// We use visibility here instead of UIObject's default of display
		// Because the panel is absolutely positioned, this will not create
		// "holes" in displayed contents and it allows normal layout passes
		// to occur so the size of the PopupPanel can be reliably determined.
		getElement().getStyle().setVisibility(visible?Visibility.VISIBLE:Visibility.HIDDEN);

		if (glass != null)
		{
			glass.getStyle().setVisibility(visible?Visibility.VISIBLE:Visibility.HIDDEN);
		}
	}

	/**
	 * Shows the popup and attach it to the page. It must have a child widget
	 * before this method is called.
	 */
	public void show()
	{
		doShow(isAnimationEnabled());
	}	

	/**
	 * Normally, the popup is positioned directly below the relative target,
	 * with its left edge aligned with the left edge of the target. Depending on
	 * the width and height of the popup and the distance from the target to the
	 * bottom and right edges of the window, the popup may be displayed directly
	 * above the target, and/or its right edge may be aligned with the right
	 * edge of the target.
	 * 
	 * @param target
	 *            the target to show the popup below
	 */
	public final void showRelativeTo(final UIObject target)
	{
		setVisible(false);
		doShow(false);
		setPosition(getLeftRelativeObject(target), getTopRelativeObject(target));
		setVisible(true);
		if (isAnimationEnabled())
		{
			runEntranceAnimation(null);
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	protected com.google.gwt.user.client.Element getContainerElement()
	{
		return containerElement.cast();
	}

	/**
	 * Hides the popup and detaches it from the page. This has no effect if it
	 * is not currently showing.
	 * 
	 * @param autoClosed
	 *            the value that will be passed to
	 *            {@link CloseHandler#onClose(CloseEvent)} when the popup is
	 *            closed
	 */
	protected void hide(final boolean autoClosed)
	{
		doHide(true, autoClosed, isAnimationEnabled());
	}

	@Override
	protected void onUnload()
	{
		super.onUnload();

		// Just to be sure, we perform cleanup when the popup is unloaded (i.e.
		// removed from the DOM). This is normally taken care of in hide(), but
		// it
		// can be missed if someone removes the popup directly from the
		// RootPanel.
		if (isShowing())
		{
			setState(false, true, false, null);
		}
	}

	/**
	 * Fires a blur event to the element.
	 * @param elt the element.
	 */
	private native void blur(Element elt) /*-{
	    // Issue 2390: blurring the body causes IE to disappear to the background
	    if (elt.blur && elt != $doc.body) {
	      elt.blur();
	    }
	  }-*/;

	private void centralizeMe()
	{
		popupCentralizer.centralize(this);

		left = -1;
		top = -1;
	}

	private void doHide(boolean fireEvent, final boolean autoClosed, boolean animated)
	{
		if (!isShowing())
		{
			return;
		}
		if (animated && popupCentralizer.isCentralized())
		{
			fixPositionToCenter();
		}
		if (fireEvent)
		{
			setState(false, false, animated, new StateChangeCallback()
			{
				@Override
				public void onStateChange()
				{
					CloseEvent.fire(PopupPanel.this, PopupPanel.this, autoClosed);
				}
			});
		}
		else
		{
			setState(false, false, animated, null);
		}
	}

	private void doShow(final boolean animated)
	{
		if (isShowing())
		{
			return;
		}
		else if (isAttached())
		{
			// The popup is attached directly to another panel, so we need to
			// remove
			// it from its parent before showing it. This is a weird use case,
			// but
			// since PopupPanel is a Widget, its legal.
			this.removeFromParent();
		}
		if (popupCentralizer.isCentralized() && animated)
		{
			setVisible(false);
			setState(true, false, false, new StateChangeCallback()
			{
				@Override
				public void onStateChange()
				{
					OpenEvent.fire(PopupPanel.this, PopupPanel.this);
				}
			});
			fixPositionToCenter();
			setVisible(true);
			runEntranceAnimation(new StateChangeCallback()
			{
				@Override
				public void onStateChange()
				{
					centralizeMe();
				}
			});
		}
		else
		{
			setState(true, false, animated, new StateChangeCallback()
			{
				@Override
				public void onStateChange()
				{
					OpenEvent.fire(PopupPanel.this, PopupPanel.this);
				}
			});
		}
	}

	/**
	 * Does the event target one of the partner elements?
	 * 
	 * @param event
	 *            the native event
	 * @return true if the event targets a partner
	 */
	private boolean eventTargetsPartner(NativeEvent event)
	{
		if (autoHidePartners == null)
		{
			return false;
		}

		EventTarget target = event.getEventTarget();
		if (Element.is(target))
		{
			for (int i=0; i < autoHidePartners.size(); i++)
			{
				Element elem = autoHidePartners.get(i);
				if (elem.isOrHasChild(Element.as(target)))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Does the event target this popup?
	 * 
	 * @param event
	 *            the native event
	 * @return true if the event targets the popup
	 */
	private boolean eventTargetsPopup(NativeEvent event)
	{
		EventTarget target = event.getEventTarget();
		if (Element.is(target))
		{
			return getElement().isOrHasChild(Element.as(target));
		}
		return false;
	}

	private void fixPositionToCenter()
	{
		int left = getPopupLeftToCenter(false);
		int top = getPopupTopToCenter(false);
		setPosition(left, top);
	}

	private InOutAnimation getDialogAnimation()
	{
		if (animation == null)
		{
			animation = InOutAnimation.bounce;
		}
		return animation;
	}

	private int getLeftRelativeObject(final UIObject relativeObject)
	{
		int offsetWidth = getOffsetWidth();
		int relativeElemOffsetWidth = relativeObject.getOffsetWidth();
		int offsetWidthDiff = offsetWidth - relativeElemOffsetWidth;
		int left;

		if (LocaleInfo.getCurrentLocale().isRTL())
		{ // RTL case

			int relativeElemAbsoluteLeft = relativeObject.getAbsoluteLeft();
			left = relativeElemAbsoluteLeft - offsetWidthDiff;
			if (offsetWidthDiff > 0)
			{
				int windowRight = Window.getClientWidth() + Window.getScrollLeft();
				int windowLeft = Window.getScrollLeft();

				int relativeElemLeftValForRightEdge = relativeElemAbsoluteLeft + relativeElemOffsetWidth;
				int distanceToWindowRight = windowRight - relativeElemLeftValForRightEdge;
				int distanceFromWindowLeft = relativeElemLeftValForRightEdge - windowLeft;
				if (distanceFromWindowLeft < offsetWidth && distanceToWindowRight >= offsetWidthDiff)
				{
					left = relativeElemAbsoluteLeft;
				}
			}
		}
		else
		{ // LTR case

			left = relativeObject.getAbsoluteLeft();
			if (offsetWidthDiff > 0)
			{
				int windowRight = Window.getClientWidth() + Window.getScrollLeft();
				int windowLeft = Window.getScrollLeft();
				int distanceToWindowRight = windowRight - left;
				int distanceFromWindowLeft = left - windowLeft;
				if (distanceToWindowRight < offsetWidth && distanceFromWindowLeft >= offsetWidthDiff)
				{
					left -= offsetWidthDiff;
				}
			}
		}
		return left;
	}

	/**
	 * Gets the popup's left position relative to the browser's center area.
	 * @param includeScroll if true include the window scroll 
	 * @return the popup's left position
	 */
	private int getPopupLeftToCenter(boolean includeScroll)
	{
		int windowLeft = includeScroll?Window.getScrollLeft():0;
		int windowWidth = Window.getClientWidth();
		int centerLeft = (windowWidth / 2) + windowLeft;

		int offsetWidth = getOffsetWidth();
		return centerLeft - (offsetWidth / 2);
	}

	/**
	 * Gets the popup's top position relative to the browser's center area.
	 * @param includeScroll if true include the window scroll 
	 * @return the popup's top position
	 */
	private int getPopupTopToCenter(boolean includeScroll)
	{
		int windowTop = includeScroll?Window.getScrollTop():0;
		int windowHeight = Window.getClientHeight();
		int centerTop = (windowHeight / 2) + windowTop;

		int offsetHeight = getOffsetHeight();
		return centerTop - (offsetHeight / 2);
	}

	private int getTopRelativeObject(final UIObject relativeObject)
	{
		int offsetHeight = getOffsetHeight();
		int top = relativeObject.getAbsoluteTop();

		int windowTop = Window.getScrollTop();
		int windowBottom = Window.getScrollTop() + Window.getClientHeight();

		int distanceFromWindowTop = top - windowTop;
		int distanceToWindowBottom = windowBottom - (top + relativeObject.getOffsetHeight());

		if (distanceToWindowBottom < offsetHeight && distanceFromWindowTop >= offsetHeight)
		{
			top -= offsetHeight;
		}
		else
		{
			top += relativeObject.getOffsetHeight();
		}
		return top;
	}

	/**
	 * Show or hide the glass.
	 */
	private void maybeShowGlass()
	{
		BodyElement body = Document.get().getBody();
		if (isShowing())
		{
			if (modal)
			{
				try
				{
					body.appendChild(glass);
					if(numberCalls == 0)
					{
						body.addClassName(FacesBackboneResourcesCommon.INSTANCE.css().facesUnscrollable());
						body.addClassName(FacesBackboneResourcesCommon.INSTANCE.css().facesUnselectable());
					}
					glassShowing = true;	
				}
				finally
				{
					numberCalls++;	
				}
			}
		}
		else if (glassShowing)
		{
			try
			{
				body.removeChild(glass);
				if(numberCalls == 1)
				{
					body.removeClassName(FacesBackboneResourcesCommon.INSTANCE.css().facesUnscrollable());
					body.removeClassName(FacesBackboneResourcesCommon.INSTANCE.css().facesUnselectable());
				}
				glassShowing = false;	
			}
			finally
			{
				numberCalls--;	
			}
			
		}
	}

	private void removePopupFromDOM()
	{
		if (popupCentralizer.isCentralized())
		{
			fixPositionToCenter();
		}
		RootPanel.get().remove(PopupPanel.this);
		setPopupPositionStyle(left, top);		
	}

	private void runEntranceAnimation(final StateChangeCallback callback)
	{
		animating = true;
		getDialogAnimation().animateEntrance(this, new Animation.Callback()
		{
			@Override
			public void onAnimationCompleted()
			{
				animating = false;
				if (callback != null)
				{
					callback.onStateChange();
				}
			}
		}, animationDuration);
	}

	private void setPopupPositionStyle(int left, int top)
	{
		Style style = getElement().getStyle();
		style.setPropertyPx("left", left);
		style.setPropertyPx("top", top);
	}

	private void setState(boolean showing, boolean unloading, boolean animated, final StateChangeCallback callback)
	{
		this.showing = showing;
		updateHandlers();

		maybeShowGlass();
		if (isShowing())
		{
			if (animated)
			{
				animating = true;
				getDialogAnimation().animateEntrance(this, new Animation.Callback()
				{
					@Override
					public void onAnimationCompleted()
					{
						animating = false;
						if (callback != null)
						{
							callback.onStateChange();
						}
					}
				}, animationDuration);
			}
			RootPanel.get().add(this);
			if (!animated && callback != null)
			{
				callback.onStateChange();
			}
		}
		else
		{
			if (!unloading)
			{
				if (animated)
				{
					animating = true;
					getDialogAnimation().animateExit(this, new Animation.Callback(){
						@Override
						public void onAnimationCompleted()
						{
							animating = false;
							removePopupFromDOM();
							if (callback != null)
							{
								callback.onStateChange();
							}
						}

					}, animationDuration);
				}
				else
				{
					removePopupFromDOM();
					if (callback != null)
					{
						callback.onStateChange();
					}
				}
			}
		}
	}

	private void uncentralizeMe()
	{
		popupCentralizer.descentralize(this);
	}

	/**
	 * Register or unregister the handlers used by {@link PopupPanel}.
	 */
	private void updateHandlers()
	{
		// Remove any existing handlers.
		if (nativePreviewHandlerRegistration != null)
		{
			nativePreviewHandlerRegistration.removeHandler();
			nativePreviewHandlerRegistration = null;
		}
		if (historyHandlerRegistration != null)
		{
			historyHandlerRegistration.removeHandler();
			historyHandlerRegistration = null;
		}

		// Create handlers if showing.
		if (isShowing())
		{
			nativePreviewHandlerRegistration = Event.addNativePreviewHandler(this);
			historyHandlerRegistration = History.addValueChangeHandler(new ValueChangeHandler<String>()
			{
				public void onValueChange(ValueChangeEvent<String> event)
				{
					if (autoHideOnHistoryEvents)
					{
						hide();
					}
				}
			});
		}
	}

	/**
	 * Add a default close handler that will be appended to each created object
	 * @param defaultCloseHandler
	 */
	public static void addDefaultCloseHandler(CloseHandler<PopupPanel> defaultCloseHandler) 
	{
		if(defaultCloseHandlers == null)
		{
			defaultCloseHandlers = new ArrayList<CloseHandler<PopupPanel>>();
		}
		defaultCloseHandlers.add(defaultCloseHandler);
	}

	/**
	 * Add a default open handler that will be appended to each created object
	 * @param defaultOpenHandler
	 */
	public static void addDefaultOpenHandler(OpenHandler<PopupPanel> defaultOpenHandler) 
	{
		if(defaultOpenHandlers == null)
		{
			defaultOpenHandlers = new ArrayList<OpenHandler<PopupPanel>>();
		}
		defaultOpenHandlers.add(defaultOpenHandler);
	}

	public static abstract class PopupCentralizer
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
		protected void setCentralized(boolean centralized) 
		{
			this.centralized = centralized;
		}
	}
	
	private static interface StateChangeCallback
	{
		void onStateChange();
	}

}
