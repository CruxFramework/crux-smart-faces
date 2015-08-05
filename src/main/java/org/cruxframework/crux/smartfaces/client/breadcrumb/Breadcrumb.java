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

import org.cruxframework.crux.core.client.collection.Array;
import org.cruxframework.crux.core.client.collection.CollectionFactory;
import org.cruxframework.crux.core.client.screen.views.ViewActivateEvent;
import org.cruxframework.crux.core.client.screen.views.ViewActivateHandler;
import org.cruxframework.crux.core.client.screen.views.ViewContainer;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.shared.Experimental;
import org.cruxframework.crux.smartfaces.client.image.Image;
import org.cruxframework.crux.smartfaces.client.panel.BasePanel;
import org.cruxframework.crux.smartfaces.client.panel.SelectablePanel;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;

/**
 * A Breadcrumb component. Help to guide the user navigation. 
 * @author Thiago da Rosa de Bustamante
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
@Experimental
public class Breadcrumb extends Composite implements HasEnabled
{
	public static final String DEFAULT_STYLE_NAME = "faces-Breadcrumb";
	public static final String STYLE_BREADCRUMB_ITEM = "faces-Breadcrumb-Item";
	public static final String STYLE_BREADCRUMB_SEPARATOR = "faces-Breadcrumb-Separator";
	private static final String STYLE_BREADCRUMB_DISABLED_SUFFIX = "-Disabled";
	private static final String STYLE_BREADCRUMB_ITEM_ACTIVE_SUFFIX = "-Active";

	private HandlerRegistration activateHandler;
	private boolean activateItemsOnSelectionEnabled = true;
	private int activeIndex = -1;
	private Array<BreadcrumbItem> children = CollectionFactory.createArray();
	private Image dividerImage;
	private String dividerText;
	private boolean enabled = true;
	private BreadcrumbPanel mainPanel;
	private boolean removeInactiveItems;
	private boolean singleActivationModeEnabled = false;
	private boolean updateOnViewChangeEnabled = true;
	private ViewContainer viewContainer;
	
	/**
	 * Constructor
	 */
	public Breadcrumb() 
	{
		mainPanel = new BreadcrumbPanel(this);
		initWidget(mainPanel);
		setStyleName(DEFAULT_STYLE_NAME);
	}

	/**
	 * Add a new {@link BreadcrumbItem} to this component. It is appended as the last item on
	 * this Breadcrumb.
	 * @param item Item to add.
	 * @return the Breadcrumb item added.
	 */
	public BreadcrumbItem add(BreadcrumbItem item)
	{
		return add(item, -1);
	}
	
	/**
	 * Add a new {@link BreadcrumbItem} to this component. It is inserted on
	 * the position informed through beforeIndex parameter.
	 * @param item Item to add.
	 * @param beforeIndex the item position. A negative or an out of range value makes the 
	 * Breadcrumb to append the item as its last child. 
	 * @return the Breadcrumb item added.
	 */
	public BreadcrumbItem add(BreadcrumbItem item, int beforeIndex)
	{
		item.setBreadcrumb(this, beforeIndex);
		mainPanel.add(item, beforeIndex);
		
		if (activeIndex >= 0 && activeIndex >= beforeIndex)
		{
			activeIndex++;
		}
		return item;
	}
	
	/**
	 * Create an add a new {@link BreadcrumbItem} to this component. It is appended as the last item on
	 * this Breadcrumb.
	 * @param name the item name
	 * @param label the item label
	 * @return the Breadcrumb item added.
	 */
	public BreadcrumbItem add(String name, String label)
	{
		return add(name, label, -1);
	}
	
	/**
	 * Create an add a new {@link BreadcrumbItem} to this component. It is inserted on
	 * the position informed through beforeIndex parameter.
	 * @param name the item name
	 * @param label the item label
	 * @param beforeIndex the item position. A negative or an out of range value makes the 
	 * @return the Breadcrumb item added.
	 */
	public BreadcrumbItem add(String name, String label, int beforeIndex)
	{
		BreadcrumbItem item = new BreadcrumbItem(name, label);
		return add(item, beforeIndex);
	}
	
	/**
	 * Retrieve the index of the current active item on this Breadcrumb.
	 * @return active item.
	 */
	public int getActiveIndex()
	{
		return activeIndex;
	}
	
	/**
	 * Retrieve the index of the current active item on this Breadcrumb.
	 * @return active item.
	 */
	public BreadcrumbItem getActiveItem()
	{
		if (activeIndex != -1)
		{
			return getItem(activeIndex);
		}
		return null;
	}

	/**
	 * Retrieve the image used as divider between items on this Breadcrumb.
	 * @return divider image
	 */
	public Image getDividerImage() 
	{
		return dividerImage;
	}
	
	/**
	 * Retrieve the text used as divider between items on this Breadcrumb. If a divider 
	 * image is also provided, this text is used as image title.
	 * @return divider text.
	 */
	public String getDividerText() 
	{
		return dividerText;
	}
	
	/**
	 * Retrieve the item on the given position.
	 * @param index item index
	 * @return the {@link BreadcrumbItem}
	 */
	public BreadcrumbItem getItem(int index)
	{
		return children.get(index);
	}

	/**
	 * Retrieve the item with the given name.
	 * @param name item name
	 * @return the {@link BreadcrumbItem}
	 */
	public BreadcrumbItem getItem(String name)
	{
		int index = indexOf(name);
		if (index >= 0)
		{
			return children.get(index);
		}
		return null;
	}
	
	/**
	 * Retrieve the associated ViewContainer. If there is a ViewContainer associated, 
	 * the {@link BreadcrumbItem}s can automatically navigate to views on this container
	 * when selected. 
	 * @return Associated ViewContainer.
	 */
	public ViewContainer getViewContainer()
	{
		return viewContainer;
	}

	/**
	 * Inform if this Breadcrumb has any divider configured.
	 * @return true if it has a divider configured.
	 */
	public boolean hasDivider()
	{
		return dividerText != null || dividerImage != null;
	}

	/**
	 * Find the index of the given item on this Breadcrumb, or -1 if it is not present.
	 * @param item item to find.
	 * @return item index or -1;
	 */
	public int indexOf(BreadcrumbItem item)
	{
		return children.indexOf(item);
	}

	/**
	 * Find the index of the item with the given name on this Breadcrumb, or -1 if it is not present.
	 * @param name the name of the item to find.
	 * @return item index or -1;
	 */
	public int indexOf(String name)
	{
		for (int i=0; i < children.size(); i++)
		{
			if (StringUtils.unsafeEquals(name, children.get(i).getName()))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Retrieve the activateItemsOnSelectionEnabled property value. If this is enabled, the Breadcrumb will
	 * set the active index for an item when it is selected by the user.
	 * @return true if enabled.
	 */
	public boolean isActivateItemsOnSelectionEnabled() 
	{
		return activateItemsOnSelectionEnabled;
	}
	
	
	@Override
	public boolean isEnabled() 
	{
		return enabled;
	}
	
	/**
	 * Retrieve the removeInactiveItems property value. When this property is true, 
	 * the Breadcrumb automatically remove the items that are not active anymore
	 * @return true if enabled.
	 */
	public boolean isRemoveInactiveItemsEnabled()
	{
		return removeInactiveItems;
	}
	
	/**
	 * Retrieve the singleActivationModeEnabled property value. If this is enabled, the Breadcrumb will
	 * keep only one item activated at a time. If false, all previous items are also activated.
	 * @return true if enabled.
	 */
	public boolean isSingleActivationModeEnabled() 
	{
		return singleActivationModeEnabled;
	}
	
	/**
	 * Retrieve the updateOnViewChangeEnabled property value. If this is enabled, the Breadcrumb will
	 * set the active index for an item when it is bound to a view that is activated on the Breadcrumb's 
	 * viewContainer.
	 * @return true if enabled.
	 */
	public boolean isUpdateOnViewChangeEnabled()
	{
		return updateOnViewChangeEnabled;
	}
	
	/**
	 * Remove the given item from this Breadcrumb.
	 * @param item item to be removed.
	 * @return the Breadcrumb reference.
	 */
	public Breadcrumb remove(BreadcrumbItem item) 
	{
		return remove(item, indexOf(item));
	}
	
	/**
	 * Remove the item positioned on the given index from this Breadcrumb.
	 * @param index item index.
	 * @return the Breadcrumb reference.
	 */
	public Breadcrumb remove(int index)
	{
		BreadcrumbItem item = getItem(index);
		remove(item);
		return this;
	}
	
	/**
	 * Remove all items from this Breadcrumb, starting from the given position
	 * @param index the start position to remove
	 * @return the Breadcrumb reference.
	 */
	public Breadcrumb removeFrom(int index)
	{
		int from = index + 1;
		while (size() > from)
		{
			remove(from);
		}
		return this;
	}
	
	/**
	 * Set the activateItemsOnSelectionEnabled property value. If this is enabled, the Breadcrumb will
	 * set the active index for an item when it is selected by the user.
	 * @param activateItemsOnSelection true to enable.
	 * @return the Breadcrumb reference.
	 */
	public Breadcrumb setActivateItemsOnSelectionEnabled(boolean activateItemsOnSelection) 
	{
		this.activateItemsOnSelectionEnabled = activateItemsOnSelection;
		return this;
	}

	/**
	 * Set the index of the current active item on this Breadcrumb.
	 * @return active item.
	 * @return the Breadcrumb reference.
	 */
	public Breadcrumb setActiveIndex(int index)
	{
		int s = size();

		if (index < 0 || index >= s || activeIndex == index)
		{
			return this;
		}
				
		if (singleActivationModeEnabled)
		{
			children.get(index).addStyleDependentName(STYLE_BREADCRUMB_ITEM_ACTIVE_SUFFIX);
		}
		else
		{
			for (int i = 0; i <= index; i++)
			{
				children.get(i).addStyleDependentName(STYLE_BREADCRUMB_ITEM_ACTIVE_SUFFIX);
			}
		}
		if (removeInactiveItems && (index < size() -1))
		{
			removeFrom(index+1);
		}
		else if (singleActivationModeEnabled && activeIndex >= 0)
		{
			children.get(activeIndex).removeStyleDependentName(STYLE_BREADCRUMB_ITEM_ACTIVE_SUFFIX);
		}
		else
		{
			for (int i = index+1; i < s; i++)
			{
				children.get(i).removeStyleDependentName(STYLE_BREADCRUMB_ITEM_ACTIVE_SUFFIX);
			}
		}
		this.activeIndex = index;
		
		return this;
	}

	/**
	 * Set the image to be used as divider between items on this Breadcrumb.
	 * @param divider divider image
	 * @return the Breadcrumb reference.
	 */
	public Breadcrumb setDividerImage(Image divider)
	{
		dividerImage = divider;
		return this;
	}

	/**
	 * Set a text to be used as divider between items on this Breadcrumb. If a divider 
	 * image is also provided, use this text as image title.
	 * @param divider divider text.
	 * @return the Breadcrumb reference.
	 */
	public Breadcrumb setDividerText(String divider)
	{
		dividerText = divider;
		return this;
	}

	@Override
	public void setEnabled(boolean enabled) 
	{
		this.enabled = enabled;
		if (enabled)
		{
			removeStyleDependentName(STYLE_BREADCRUMB_DISABLED_SUFFIX);
		}
		else
		{
			addStyleDependentName(STYLE_BREADCRUMB_DISABLED_SUFFIX);
		}
	}
	
	/**
	 * Set the removeInactiveItems property value. When this property is true, 
	 * the Breadcrumb automatically remove the items that are not active anymore
	 * @param removeInactiveItems true to remove inactive items
	 * @return the Breadcrumb reference.
	 */
	public Breadcrumb setRemoveInactiveItemsEnabled(boolean removeInactiveItems)
	{
		this.removeInactiveItems = removeInactiveItems;
		return this;
	}

	/**
	 * Set the singleActivationModeEnabled property value. If this is enabled, the Breadcrumb will
	 * keep only one item activated at a time. If false, all previous items are also activated.
	 * @param singleActivationModeEnabled true to enable.
	 * @return the Breadcrumb reference.
	 */
	public Breadcrumb setSingleActivationModeEnabled(boolean singleActivationModeEnabled) 
	{
		this.singleActivationModeEnabled = singleActivationModeEnabled;
		return this;
	}

	/**
	 * Set the updateOnViewChangeEnabled property value. If this is enabled, the Breadcrumb will
	 * set the active index for an item when it is bound to a view that is activated on the Breadcrumb's 
	 * viewContainer.
	 * @param updateOnViewChangeEnabled true to enable.
	 * @return the Breadcrumb reference.
	 */
	public void setUpdateOnViewChangeEnabled(boolean updateOnViewChangeEnabled)
	{
		this.updateOnViewChangeEnabled = updateOnViewChangeEnabled;
	}
	
	/**
	 * Inform the associated ViewContainer. If there is a ViewContainer associated, 
	 * the {@link BreadcrumbItem}s can automatically navigate to views on this container
	 * when selected. 
	 * @param viewContainer the container.
	 * @return the Breadcrumb reference.
	 */
	public Breadcrumb setViewContainer(ViewContainer viewContainer)
	{
		if (this.viewContainer != viewContainer)
		{
			if (this.activateHandler != null)
			{
				activateHandler.removeHandler();
				activateHandler = null;
			}
			if (viewContainer != null)
			{
				activateHandler = viewContainer.addViewActivateHandler(new ViewActivateHandler()
				{
					@Override
					public void onActivate(ViewActivateEvent event)
					{
						if (updateOnViewChangeEnabled)
						{
							String viewId = event.getSenderId();
							setActiveIndex(indexOfItemByView(viewId));
						}
					}
				});
			}
			
			this.viewContainer = viewContainer;
		}
		return this;
	}

	/**
	 * Retrieve the number of items inside this Breadcrumb.
	 * @return number of children.
	 */
	public int size()
	{
		return children.size();
	}
	
	/**
	 * Called by {@link BreadcrumbItem} when it is associated to a Breadcrumb. It adopt the item as
	 * a new child.
	 * @param item the item to be adopted.
	 * @param onPosition the position where the item are being inserted.
	 */
	protected void adopt(BreadcrumbItem item, int onPosition)
	{
		children.add(item);
		mainPanel.adopt(item);
	}
	
	/**
	 * Create a new divider element. 
	 * @return the element created
	 */
	protected Element createDivider() 
	{
		Element el = null;
		if (dividerImage != null) 
		{
			el = dividerImage.getElement().cloneNode(true).cast();
			if (!StringUtils.isEmpty(dividerText))
			{
				el.setTitle(dividerText);
			}
		}
		else if (!StringUtils.isEmpty(dividerText))
		{
			el = Document.get().createSpanElement();
			el.setInnerText(dividerText);
		}
		
		return el;
	}
	
	/**
	 * Find the index of the item with the given viewId on this Breadcrumb, or -1 if it is not present.
	 * @param viewID the viewID of the item to find.
	 * @return item index or -1;
	 */
	protected int indexOfItemByView(String viewId)
	{
		for (int i=0; i < children.size(); i++)
		{
			if (StringUtils.unsafeEquals(viewId, children.get(i).getViewId()))
			{
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Called by {@link BreadcrumbItem} when it is removed from the Breadcrumb. It removes the item
	 * from the internal children list.
	 * @param item the item to be removed.
	 */
	protected void orphan(BreadcrumbItem item)
	{
		children.remove(item);
	    mainPanel.orphan(item);
	}
	
	protected Breadcrumb remove(BreadcrumbItem item, int index) 
	{
		item.setBreadcrumb(null, -1);
		mainPanel.remove(item);
		if (activeIndex > index && !singleActivationModeEnabled)
		{
			activeIndex--;
		}
		else if (activeIndex == index)
		{
			activeIndex = -1;
		}
		return this;
	}
	
	/**
	 * Internal class representing the outer panel of a Breadcrumb component.
	 * It generates a structure like:
	 * 
	 * {@code <nav><ol></ol></nav>}. Each breadcrumb item is a rendered on a 
	 * {@code <li></li>} tag.
	 * @author Thiago da Rosa de Bustamante
	 */
	protected static class BreadcrumbPanel extends BasePanel
	{
		private Breadcrumb breadcrumb;
		private Element listElement;

		protected BreadcrumbPanel(Breadcrumb breadcrumb) 
		{
			super("nav");
			this.breadcrumb = breadcrumb;
		    listElement = Document.get().createElement("ol");
			getElement().appendChild(listElement);
		}

		protected void add(BreadcrumbItem item, int beforeIndex)
		{
			int listSize = listElement.getChildCount();
			boolean hasDivider = breadcrumb.hasDivider();
			if (beforeIndex < 0 || beforeIndex >= listSize)
			{
				if (hasDivider && listSize > 0)
				{
					listElement.appendChild(createNewDivider());
				}
				listElement.appendChild(item.getElement());
			}
			else
			{
				Node referenceNode;
				
				if (hasDivider)
				{
					int referenceNodePosition = Math.min(0, beforeIndex*2 -1);
					referenceNode = listElement.getChild(referenceNodePosition);
					Node dividerNode = listElement.insertBefore(referenceNode, createNewDivider());
					if (referenceNodePosition == 0)
					{
						referenceNode = dividerNode; 
					}
				}
				else
				{
					int referenceNodePosition = beforeIndex;
					referenceNode = listElement.getChild(referenceNodePosition);
				}
				listElement.insertBefore(referenceNode, item.getElement());
			}
		}

		protected void adopt(BreadcrumbItem item)
		{
			SelectablePanel itemPanel = item.getItemPanel();
			if (itemPanel != null)
			{
			    getChildren().add(itemPanel);
				adopt(itemPanel);
			}
		}

		protected Node createNewDivider() 
		{
			Element dividerElement = Document.get().createElement("li");
			dividerElement.setClassName(STYLE_BREADCRUMB_SEPARATOR);
			dividerElement.appendChild(breadcrumb.createDivider());
			return dividerElement;
		}

		protected void orphan(BreadcrumbItem item)
		{
			SelectablePanel itemPanel = item.getItemPanel();
			if (itemPanel != null)
			{
			    getChildren().remove(itemPanel);
				orphan(itemPanel);
			}
		}
		
		protected void remove(BreadcrumbItem item) 
		{
			if (breadcrumb.hasDivider())
			{
				Element divider = item.getElement().getPreviousSiblingElement();
				if (divider != null)
				{
					divider.removeFromParent();
				}
			}
			item.getElement().removeFromParent();
		}
	}	
}
