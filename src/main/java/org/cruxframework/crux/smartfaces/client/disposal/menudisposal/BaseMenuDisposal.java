/*
 * Copyright 2011 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.client.disposal.menudisposal;


import org.cruxframework.crux.core.client.css.animation.Animation.Callback;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.SingleCrawlableViewContainer;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.dialog.animation.DialogAnimation;
import org.cruxframework.crux.smartfaces.client.dialog.animation.HasDialogAnimation;
import org.cruxframework.crux.smartfaces.client.menu.Menu;
import org.cruxframework.crux.smartfaces.client.menu.MenuItem;
import org.cruxframework.crux.smartfaces.client.panel.FooterPanel;
import org.cruxframework.crux.smartfaces.client.panel.HeaderPanel;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a base class to create components that defines a page layout (example: TopMenuDisposalLayout, SideMenuDisposalLayout)
 * @author wesley.diniz
 *
 */
public abstract class BaseMenuDisposal extends SingleCrawlableViewContainer implements HasDialogAnimation
{
	private static final String BASE_MENU_DISPOSAL_MENU = "menu";
	private static final String CONTENT_MENU_STYLE = "contentPanel";
	private static final String FOOTER_PANEL_STYLE = "footerPanel";
	private static final String HEADER_PANEL_STYLE = "headerPanel";
	private static final String MENU_PANEL_STYLE = "menuPanel";
	private static final String SMALL_HEADER_PANEL = "smallHeaderPanel";

	protected DialogAnimation animation;
	protected Panel bodyPanel;
	protected FooterPanel footerPanel;
	protected HeaderPanel headerPanel;
	protected Menu menu;
	protected Panel menuPanel;
	protected Panel viewContentPanel; 
	
	private BaseMenuHandler handler = GWT.create(BaseMenuHandler.class);

	private boolean animationEnabled;

	protected BaseMenuDisposal()
	{
		super(new FlowPanel(), true);
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
		setHistoryControlEnabled(true);
		createChildWidgets();
		buildLayout();
	}

	public void addFooterContent(Widget footer)
	{
		this.footerPanel.add(footer);
	}

	public void addHeaderContent(Widget header)
	{
		headerPanel.add(header);
	}

	public void addLargeFooterContent(Widget footer)
	{
		if (Screen.getCurrentDevice().getSize().equals(Size.large))
		{
			addFooterContent(footer);
		}
	}

	public void addLargeHeaderContent(Widget header)
	{
		if (Screen.getCurrentDevice().getSize().equals(Size.large))
		{
			addHeaderContent(header);
		}
	}
	
	public void addSmallFooterContent(Widget footer)
	{
		if (Screen.getCurrentDevice().getSize().equals(Size.small))
		{
			addFooterContent(footer);
		}
	}

	public void addSmallHeaderContent(Widget header)
	{
		if (Screen.getCurrentDevice().getSize().equals(Size.small))
		{
			addHeaderContent(header);
		}
	}

	public Menu getMenu()
	{
		return this.menu;
	}

	public View getView()
	{
		return getActiveView();
	}

	@Override
	public void setAnimation(DialogAnimation animation)
	{
		this.animation = animation;
		setAnimationEnabled(animation != null);
	}

	@Override
	public void setAnimationEnabled(boolean enable)
	{
		animationEnabled = enable;
	}
	
	@Override
	public boolean isAnimationEnabled()
	{
	    return animationEnabled && animation != null;
	}

	public void setMenu(final Menu menu)
	{
		this.menu = menu;
		menuPanel.add(menu);
		menu.addStyleName(BaseMenuDisposal.BASE_MENU_DISPOSAL_MENU);
		handler.setMenu(this, menu);
	}

	@Override
	protected boolean activate(View view, Panel containerPanel, Object parameter)
	{
		boolean activated = super.activate(view, containerPanel, parameter);
		if (activated)
		{
			Window.scrollTo(0, 0);
		}
		return activated;
	}

	/** 
	 * Must be overridden to create all the internal widgets
	 */
	protected abstract void buildLayout();

	protected void createChildWidgets()
    {
	    bodyPanel = getMainWidget();
		viewContentPanel = new FlowPanel();
		headerPanel = new HeaderPanel();
		menuPanel = new FlowPanel();
		footerPanel = new FooterPanel();
		footerPanel.setStyleName(getFooterStyleName());
		menuPanel.setStyleName(getMenuPanelStyleName());
		viewContentPanel.setStyleName(getContentStyleName());
		setAnimation(DialogAnimation.bounceLeft);
		setStyleName(getDefaultStyleName());		
    }

	@Override
	protected Panel getContainerPanel(View view)
	{
		return viewContentPanel;
	}

	/**
	 * Must be overridden to specify content's style name
	 * @return styteName
	 */
	protected String getContentStyleName()
	{
		return CONTENT_MENU_STYLE;
	}
	
	/**
	 * Must be overridden to specify default stylename
	 * @return styteName
	 */
	protected abstract String getDefaultStyleName();
	/**
	 * Must be overridden to specify footer's stylename
	 * @return styteName
	 */
	protected String getFooterStyleName()
	{
		return FOOTER_PANEL_STYLE;
	}

	/**
	 * Must be overridden to specify header's style name
	 * @return styteName
	 */
	protected String getHeaderStyleName()
	{
		return HEADER_PANEL_STYLE;		
	}

	/**
	 * Must be overridden to specify menu's style name
	 * @return styteName
	 */
	protected String getMenuPanelStyleName()
	{
		return MENU_PANEL_STYLE;
	}

	protected String getSmallHeaderStyleName()
	{
		return SMALL_HEADER_PANEL;		
	}

	@Override
	protected void handleViewTitle(String title, Panel containerPanel, String viewId)
	{
		Window.setTitle(title);
	}

	protected void showSmallMenu()
	{
		handler.showSmallMenu(this);
	}

	@Override
	protected void showView(String viewName, String viewId, Object parameter)
	{
		if (getView() != null)
		{
			if (getView().removeFromContainer())
			{
				super.showView(viewName, viewId, parameter);
			}
		}
		else
		{
			super.showView(viewName, viewId, parameter);
		}
	}

	static interface BaseMenuHandler
	{
		void setMenu(BaseMenuDisposal disposal, Menu menu);
		void showSmallMenu(BaseMenuDisposal disposal);
	}

	static class LargeBaseMenuHandler implements BaseMenuHandler
	{
		@Override
		public void setMenu(final BaseMenuDisposal disposal, Menu menu)
		{
			menu.addSelectionHandler(new SelectionHandler<MenuItem>(){

				@Override
				public void onSelection(SelectionEvent<MenuItem> event)
				{
					String viewName = event.getSelectedItem().getValue();
					if(!StringUtils.isEmpty(viewName))
					{
						disposal.showView(viewName);
					}
				}
			});
		}

		@Override
		public void showSmallMenu(BaseMenuDisposal disposal) {}
	}

	static class SmallBaseMenuHandler implements BaseMenuHandler
	{
		private boolean menuVisible = false;
		
		@Override
		public void setMenu(final BaseMenuDisposal disposal, final Menu menu)
		{
			disposal.menuPanel.setVisible(menuVisible);
			menu.addSelectionHandler(new SelectionHandler<MenuItem>(){

				@Override
				public void onSelection(SelectionEvent<MenuItem> event)
				{
					String viewName = event.getSelectedItem().getValue();
					if(!StringUtils.isEmpty(viewName))
					{
						disposal.showView(viewName);
					}

					if(!event.getSelectedItem().hasChildren())
					{
						showSmallMenu(disposal);
					}
				}
			});
		}

		@Override
		public void showSmallMenu(final BaseMenuDisposal disposal)
		{
			if (disposal.menu != null)
			{
				BodyElement body = Document.get().getBody();
				if (menuVisible)
				{
					body.removeClassName(FacesBackboneResourcesCommon.INSTANCE.css().facesUnscrollable());
					if (disposal.isAnimationEnabled())
					{
						disposal.animation.animateExit(disposal.menuPanel, new Callback()
						{
							@Override
							public void onAnimationCompleted()
							{
								disposal.menuPanel.setVisible(false);
							}
						});
					}
					else
					{
						disposal.menuPanel.setVisible(false);
					}
				} 
				else
				{
					body.addClassName(FacesBackboneResourcesCommon.INSTANCE.css().facesUnscrollable());
					disposal.menuPanel.setVisible(true);
					if (disposal.isAnimationEnabled())
					{
						disposal.animation.animateEntrance(disposal.menuPanel, null);
					}
				}
				menuVisible = !menuVisible;
			}
		}
	}
}
