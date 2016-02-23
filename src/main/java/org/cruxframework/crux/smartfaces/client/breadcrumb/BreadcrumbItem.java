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
package org.cruxframework.crux.smartfaces.client.breadcrumb;

import org.cruxframework.crux.core.client.css.animation.Animation;
import org.cruxframework.crux.core.client.event.HasSelectHandlers;
import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.core.client.permission.Permissions;
import org.cruxframework.crux.smartfaces.client.label.Label;
import org.cruxframework.crux.smartfaces.client.panel.SelectablePanel;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;

/**
 * An item for a {@link Breadcrumb} widget
 * @author Thiago da Rosa de Bustamante
 */
public class BreadcrumbItem extends UIObject implements HasSelectHandlers, HasEnabled, HasText
{
	private Breadcrumb breadcrumb;
	private boolean enabled = true;
	private HandlerManager handlerManager;
	private SelectablePanel itemPanel;
	private String name;
	private HandlerRegistration showViewSelectHandler;
	private String text;
	private String viewId;
	private String viewName;

	/**
	 * Constructor
	 */
	public BreadcrumbItem()
	{
		itemPanel = new SelectablePanel();
		itemPanel.addSelectHandler(new SelectHandler()
		{
			@Override
			public void onSelect(SelectEvent event)
			{
				if(!isEnabled() || !breadcrumb.isEnabled())
				{
					event.setCanceled(true);
					event.stopPropagation();
					return;
				}
				
				if (isActive())
				{
					if (breadcrumb.isCollapsible())
					{
						breadcrumb.setCollapsed(!breadcrumb.isCollapsed());
					}
				}
				else if (breadcrumb.isActivateItemsOnSelectionEnabled())
				{
					int index = breadcrumb.indexOf(BreadcrumbItem.this);
					breadcrumb.setActiveIndex(index, true, true);
				}
				
				SelectEvent.fire(BreadcrumbItem.this);
			}
		});		
		
		setElement(Document.get().createElement("li"));
		setStyleName(Breadcrumb.STYLE_BREADCRUMB_ITEM);
		getElement().appendChild(itemPanel.getElement());
	}

	/**
	 * Constructor
	 * @param name the item name.
	 */
	public BreadcrumbItem(String name)
	{
		this();
		setName(name);
	}
	
	/**
	 * Constructor
	 * @param name the item name.
	 * @param itemWidget an widget to be added inside this item.
	 */
	public BreadcrumbItem(String name, IsWidget itemWidget)
	{
		this(name);
		setWidget(itemWidget);
	}
	
	/**
	 * Constructor
	 * @param name the item name.
	 * @param text an text to be added inside this item.
	 */
	public BreadcrumbItem(String name, String text)
	{
		this(name);
		setText(text);
	}

	@Override
	public HandlerRegistration addSelectHandler(SelectHandler handler) 
	{
		return addHandler(handler, SelectEvent.getType());
	}

	/**
	 * Verify if the current user has permission to select this item
	 * 
	 * @param role user role
	 */
	public void checkEditPermission(String role)
	{
		if (!Permissions.hasRole(role))
		{
			Permissions.markAsUnauthorizedForEdition(this);
		}
	}
	
	/**
	 * Verify if the current user has permission to see this item.
	 * 
	 * @param user role
	 */
	public void checkViewPermission(String role)
	{
		if (!Permissions.hasRole(role))
		{
			Permissions.markAsUnauthorizedForViewing(this);
		}
	}

	@Override
	public void fireEvent(GwtEvent<?> event)
	{
		if (handlerManager != null)
		{
			handlerManager.fireEvent(event);
		}
	}
	
	/**
	 * Retrieve the name property of this item. It can be used to find the items inside the {@link Breadcrumb}.
	 * It should be unique to avoid conflicts.
	 * @return item name.
	 */
	public String getName()
	{
		return name;
	}

	@Override
	public String getText() 
	{
		return text;
	}
	
	/**
	 * Retrieve the view identifier of this item. If this item has a viewName and viewId, when selected, 
	 * it tries to open this view on the {@link Breadcrumb}'s viewContainer.
	 * @return view identifier.
	 */
	public String getViewId()
	{
		return viewId;
	}	
	
	/**
	 * Retrieve the view name of this item. If this item has a viewName and viewId, when selected, 
	 * it tries to open this view on the {@link Breadcrumb}'s viewContainer.
	 * @return view name.
	 */
	public String getViewName()
	{
		return viewName;
	}
	
	/**
	 * Check if the current item is active on its breadcrumb
	 * @return
	 */
	public boolean isActive()
    {
        return BreadcrumbItem.this == breadcrumb.getActiveItem();
    }	

	@Override
	public boolean isEnabled() 
	{
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) 
	{
		this.enabled = enabled;
	}
	
	/**
	 * Inform a label text to be added inside this item.
	 * @param itemtext label text.
	 * @return the Item reference.
	 */
	public BreadcrumbItem setLabel(String text)
	{
		setText(text);
		return this;
	}
	
	/**
	 * Set the name property of this item. It can be used to find the items inside the {@link Breadcrumb}.
	 * It should be unique to avoid conflicts.
	 * @param name item name.
	 * @return the Item reference.
	 */
	public BreadcrumbItem setName(String name)
	{
		this.name = name;
		return this;
	}
	
	@Override
	public void setText(String text) 
	{
		this.text = text;
		setWidget(new Label(text));
	}
	
	/**
	 * Add a view reference to this item. When selected, 
	 * it tries to open this view on the {@link Breadcrumb}'s viewContainer.
	 * @param viewName view name.
	 */
	public BreadcrumbItem setView(String viewName)
	{
		return setView(viewName, viewName);
	}

	/**
	 * Add a view reference to this item. When selected, 
	 * it tries to open this view on the {@link Breadcrumb}'s viewContainer.
	 * @param viewName view name.
	 * @param viewId view identifier.
	 */
	public BreadcrumbItem setView(String viewName, String viewId)
	{
		this.viewName = viewName;
		this.viewId = viewId;
		
		if (showViewSelectHandler != null)
		{
			showViewSelectHandler.removeHandler();
		}
		
		if (viewName != null && viewId != null)
		{
			showViewSelectHandler = addSelectHandler(new SelectHandler() 
			{
				@Override
				public void onSelect(SelectEvent event) 
				{
					if (breadcrumb.getViewContainer() != null)
					{
						breadcrumb.getViewContainer().showView(BreadcrumbItem.this.viewName, BreadcrumbItem.this.viewId);
					}
				}
			});
		}
		
		return this;
	}

	/**
	 * Inform an widget to be added inside this item.
	 * @param itemWidget the widget to be added.
	 * @return the Item reference.
	 */
	public BreadcrumbItem setWidget(IsWidget itemWidget) 
	{
		itemPanel.setWidget(itemWidget);
		return this;
	}

	protected void collapse()
    {
		collapse(true);
    }
	
	protected void collapse(boolean allowAnimations)
    {
		if (allowAnimations && breadcrumb.isAnimationEnabled())
		{
			breadcrumb.getCollapseAnimation().animateExit(getElement(), new Animation.Callback()
			{
				@Override
                public void onAnimationCompleted()
                {
					setVisible(false);
                }
			}, breadcrumb.getAnimationDuration());
		}
		else
		{
			setVisible(false);
		}
    }
	
	protected void setBreadcrumb(Breadcrumb breadcrumb, int onPosition)
	{
		if (this.breadcrumb != breadcrumb)
		{
			if (breadcrumb == null)
			{
				this.breadcrumb.orphan(this);
			}
			else
			{
				if (this.breadcrumb != null)
				{
					this.breadcrumb.orphan(this);
				}
				breadcrumb.adopt(this, onPosition);
			}
			this.breadcrumb = breadcrumb;
		}
	}
	
	protected void uncollapse()
    {
		uncollapse(true);
    }
	protected void uncollapse(boolean allowAnimations)
    {
		setVisible(true);
		if (allowAnimations && breadcrumb.isAnimationEnabled())
		{
			breadcrumb.getCollapseAnimation().animateEntrance(getElement(), null, breadcrumb.getAnimationDuration());
		}
    }
	
	<H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type)
	{
		return ensureHandlers().addHandler(type, handler);
	}

	/**
	 * Ensures the existence of the handler manager.
	 * 
	 * @return the handler manager
	 * */
	HandlerManager ensureHandlers()
	{
		return handlerManager == null ? handlerManager = new HandlerManager(this) : handlerManager;
	}

	SelectablePanel getItemPanel()
	{
		return itemPanel;
	}
}