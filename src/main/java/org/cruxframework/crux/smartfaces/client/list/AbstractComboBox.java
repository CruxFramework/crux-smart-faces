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
package org.cruxframework.crux.smartfaces.client.list;

import org.cruxframework.crux.core.client.dataprovider.DataProvider;
import org.cruxframework.crux.core.client.dataprovider.PagedDataProvider;
import org.cruxframework.crux.core.client.dataprovider.pager.Pageable;
import org.cruxframework.crux.core.client.dataprovider.pager.PageablePager;
import org.cruxframework.crux.core.client.event.HasSelectHandlers;
import org.cruxframework.crux.core.client.event.SelectEvent;
import org.cruxframework.crux.core.client.event.SelectHandler;
import org.cruxframework.crux.core.client.factory.WidgetFactory;
import org.cruxframework.crux.smartfaces.client.backbone.common.FacesBackboneResourcesCommon;
import org.cruxframework.crux.smartfaces.client.button.Button;
import org.cruxframework.crux.smartfaces.client.dialog.PopupPanel;
import org.cruxframework.crux.smartfaces.client.panel.SelectableFlowPanel;
import org.cruxframework.crux.smartfaces.client.panel.SelectablePanel;
import org.cruxframework.crux.smartfaces.client.util.animation.InOutAnimation;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAnimation;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 * @param <V>
 * @param <T>
 */
public abstract class AbstractComboBox<V, T> extends Composite implements HasValue<V>, Pageable<T>, 
								HasAllFocusHandlers, HasEnabled, HasSelectHandlers, HasAnimation
{
	public static final String DEFAULT_STYLE_NAME = "faces-ComboBox";
	
	private static final String COMBO_BOX_BUTTON = "button";
	private static final String COMBO_BOX_COMBO_ITEM_LIST = "itemList";
	private static final String COMBO_BOX_POPUP = "popup";
	private static final String COMBO_BOX_TEXT = "text";
	private static final String DEFAULT_WIDTH = "150px";
	protected OptionsRenderer<V, T> optionsRenderer = null;
	private final SelectableFlowPanel bodyPanel = new SelectableFlowPanel();
	private final Button button = new Button();
	private ComboBoxOptionList<V, T> optionsList;
	private PopupPanel popup;
	private int selectedIndex;
	private final TextBox textBox = new TextBox();
	private V value;

	/**
	 * @param optionsRenderer
	 */
	public AbstractComboBox(OptionsRenderer<V, T> optionsRenderer)
	{
		initWidget(bodyPanel);
		FacesBackboneResourcesCommon.INSTANCE.css().ensureInjected();
		this.optionsRenderer = optionsRenderer;
		createVisualComponents(optionsRenderer);
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler)
	{
		return addHandler(handler,BlurEvent.getType());
	}

	@Override
	public HandlerRegistration addFocusHandler(FocusHandler handler)
	{
		return addHandler(handler, FocusEvent.getType());
	}

	@Override
	public HandlerRegistration addSelectHandler(SelectHandler handler)
	{
		return addHandler(handler,SelectEvent.getType());
	}
	
	@Override
    public void firstPage()
    {
		optionsList.firstPage();
    }
	
	@Override
	public int getCurrentPage()
    {
	    return optionsList.getCurrentPage();
    }

	@Override
	public PagedDataProvider<T> getDataProvider()
	{
		return optionsList.getDataProvider();
	}

	@Override
	public int getPageCount()
	{
		return optionsList.getPageCount();
	}

	@Override
	public int getPageSize()
	{
		return optionsList.getPageSize();
	}

	/**
	 * Retrieve the index of the selected item.
	 * @return selected index
	 */
	public int getSelectedIndex()
	{
		return this.selectedIndex;
	}

	/**
	 * Retrieve the text displayed on the combobox's textBox.
	 * @return text displayed
	 */
	public String getText()
	{
		return textBox.getText();
	}
	
	/**
	 * Retrieve the value of the selected item
	 * @return the value
	 */
	public V getValue()
	{
		return value;
	}

	@Override
	public void goToPage(int page)
	{
		optionsList.goToPage(page);
	}

	@Override
    public boolean hasNextPage()
    {
	    return optionsList.hasNextPage();
    }

	@Override
    public boolean hasPreviousPage()
    {
	    return optionsList.hasPreviousPage();
    }

	@Override
	public boolean isDataLoaded()
	{
		return optionsList.isDataLoaded();
	}

	@Override
	public boolean isEnabled()
	{
		return button.isEnabled();
	}
	
	@Override
    public void lastPage()
    {
		optionsList.lastPage();
    }

	@Override
	public void nextPage()
	{
		optionsList.nextPage();
	}

	@Override
	public void previousPage()
	{
		optionsList.previousPage();
	}

	/**
	 * Refresh the combobox data. It will reset the its {@link DataProvider}.
	 */
	public void refresh()
	{
		optionsList.reset();
	}

	@Override
	public void setDataProvider(PagedDataProvider<T> dataProvider, boolean autoLoadData)
	{
		optionsList.setDataProvider(dataProvider, autoLoadData);
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		button.setEnabled(enabled);
		textBox.setEnabled(enabled);
	}
	
	/**
	 * Set the height of the list box, containing the items. If not defined, the height of the 
	 * first page loaded into this combobox will be assumed. 
	 * @param height the height to set
	 */
	public void setListHeight(String height)
	{
		optionsList.setHeight(height);
	}	
	
	@Override
	public void setPager(PageablePager<T> pager)
	{
		optionsList.setPager(pager);
	}

	@Override
	public void setPageSize(int pageSize)
	{
		optionsList.setPageSize(pageSize);
	}

	/**
	 * Set the index of the selected item.
	 * @param index item index
	 */
	public void setSelectedIndex(int index)
	{
		DataProvider<T> dataProvider = getDataProvider();
		dataProvider.read(index, new DataProvider.DataReader<T>()
		{
			@Override
            public void read(T object, int index)
            {
				selectItem(optionsRenderer.getText(object), optionsRenderer.getValue(object), index);
            }
		});
	}

	@Override
	public void setStyleName(String style)
	{
	    super.setStyleName(style);
	    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().flexBoxHorizontalContainer());
	}

	@Override
	public void setStyleName(String style, boolean add)
	{
		super.setStyleName(style, add);
		if (!add)
		{
		    addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().flexBoxHorizontalContainer());
		}
	}

	protected void selectItem(String text, V value, int selectedIndex)
	{
		this.selectedIndex = selectedIndex;
		textBox.setText(text);
		this.value = value;
		if(popup.isShowing())
		{
			popup.hide();
		}
		ValueChangeEvent.fire(this, value);
	}
	
	protected abstract void setValueByObject(T obj);
	
	private void createVisualComponents(OptionsRenderer<V, T> optionsRenderer)
	{
		this.optionsRenderer = optionsRenderer;
		bodyPanel.add(textBox);
		bodyPanel.setWidth(DEFAULT_WIDTH);
		bodyPanel.add(button);
		bodyPanel.addSelectHandler(new SelectHandler()
		{
			@Override
			public void onSelect(SelectEvent event)
			{
				SelectEvent selectEvent = SelectEvent.fire(AbstractComboBox.this);
				if (selectEvent.isCanceled())
				{
					event.setCanceled(true);
				}
				if (selectEvent.isStopped())
				{
					event.stopPropagation();
				}
			}
		});
		
		textBox.setStyleName(COMBO_BOX_TEXT);
		textBox.addStyleName(FacesBackboneResourcesCommon.INSTANCE.css().facesBackboneComboboxText());
		textBox.setReadOnly(true);
		
		textBox.addClickHandler(new ClickHandler(){

			@Override	
			public void onClick(ClickEvent event)
			{
				showList();
			}
		});//TODO trocar pra select handler
		
		optionsList = new ComboBoxOptionList<V, T>(optionsRenderer, this);
		optionsList.setStyleName(COMBO_BOX_COMBO_ITEM_LIST);

		popup = new PopupPanel();
		popup.setStyleName(COMBO_BOX_POPUP);
		popup.setAutoHideEnabled(true);
		popup.add(optionsList);
		
		button.setStyleName(COMBO_BOX_BUTTON);
		button.addSelectHandler(new SelectHandler(){
			@Override
			public void onSelect(SelectEvent event)
			{
				showList();
			}
		});

		addHandler(new SelectComboItemHandler<V>(){
			@Override
			public void onSelectItem(SelectComboItemEvent<V> event)
			{
				selectItem(event.text, event.value, event.index);
			}
		}, SelectComboItemEvent.getType());
		setStyleName(DEFAULT_STYLE_NAME);
	}
	
	private void showList()
	{
		popup.showRelativeTo(textBox);
	}
	
	/**
	 * @author wesley.diniz
	 * 
	 * @param <V>
	 * @param <T>
	 */
	public static interface OptionsRenderer<V, T> extends WidgetFactory<T>
	{
		/**
		 * Retrieve the text label that will appears on the comboBox text field, when the
		 * given valueObject is selected.
		 * @param valueObject
		 * @return
		 */
		String getText(T valueObject);

		/**
		 * Retrieve the value that willbe bound  to the comboBox, when the
		 * given valueObject is selected.
		 * @param valueObject
		 * @return
		 */
		V getValue(T valueObject);
	}
	
	/**
	 * @author wesley.diniz
	 * 
	 * @param <V>
	 * @param <T>
	 */
	private static class ComboBoxOptionList<V, T> extends WidgetList<T>
	{
		private final AbstractComboBox<V, T> comboBoxParent;
		private OptionsRenderer<V, T> renderer;
		
		public ComboBoxOptionList(OptionsRenderer<V, T> optionsRenderer, AbstractComboBox<V, T> comboBoxParent)
		{
			super(optionsRenderer);
			this.renderer = optionsRenderer;
			this.comboBoxParent = comboBoxParent;
		}

		@Override
		protected DataProvider.DataReader<T> getDataReader()
		{
			return new DataProvider.DataReader<T>()
			{

				@Override
				public void read(T value, int index)
				{
					IsWidget widget = renderer.createWidget(value);
					ComboBoxOptionPanel<V> panel = new ComboBoxOptionPanel<V>(comboBoxParent);
					panel.setValue(renderer.getValue(value));
					panel.setText(renderer.getText(value));
					int widgetIndex = getPagePanel().getWidgetCount();
					if (pager != null && !pager.supportsInfiniteScroll())
					{
						int numPreviousPage = getDataProvider().getCurrentPage() - 1;
						widgetIndex += (numPreviousPage*getPageSize());
					}
					panel.setIndex(widgetIndex);
					panel.add(widget);
					
					getPagePanel().add(panel);
				}
			};
		}
	}

	/**
	 * @author wesley.diniz
	 * 
	 * @param <V>
	 */
	private static class ComboBoxOptionPanel<V> extends Composite
	{
		private static final String COMBO_BOX_OPTION_PANEL = "optionPanel";
		private SelectablePanel bodyPanel = new SelectablePanel();
		private int index;
		private String text;
		private V value;

		ComboBoxOptionPanel(final Widget parent)
		{
			initWidget(bodyPanel);
			bodyPanel.setStyleName(COMBO_BOX_OPTION_PANEL);
			bodyPanel.addSelectHandler(new SelectHandler(){
				@Override
				public void onSelect(SelectEvent event)
				{
					SelectComboItemEvent<V> ev = new SelectComboItemEvent<V>();
					ev.value = getValue();
					ev.text = getText();
					ev.index = getIndex();
					parent.fireEvent(ev);
				}
			});
		}

		void add(IsWidget w)
		{
			bodyPanel.add(w);
		}

		int getIndex()
		{
			return index;
		}

		String getText()
		{
			return text;
		}

		V getValue()
		{
			return value;
		}

		void setIndex(int index)
		{
			this.index = index;
		}

		void setText(String text)
		{
			this.text = text;
		}

		void setValue(V value)
		{
			this.value = value;
		}
	}

	/**
	 * @author wesley.diniz
	 * 
	 * @param <V>
	 */
	@SuppressWarnings({"rawtypes","unused"})
	private static class SelectComboItemEvent<V> extends GwtEvent<SelectComboItemHandler>
	{
		private static final Type<SelectComboItemHandler> TYPE = new Type<SelectComboItemHandler>();
		private int index;
		private String text;
		private V value;

		@Override
		public Type<SelectComboItemHandler> getAssociatedType()
		{
			return TYPE;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void dispatch(SelectComboItemHandler handler)
		{
			handler.onSelectItem(this);
		}

		V getValue()
		{
			return value;
		}

		static Type<SelectComboItemHandler> getType()
		{
			return TYPE;
		}
	}
	
	/**
	 * @author wesley.diniz
	 * 
	 * @param <V>
	 */
	private static interface SelectComboItemHandler<V> extends EventHandler
	{
		void onSelectItem(SelectComboItemEvent<V> event);
	}
	
	/**
	 * return true if the animation is enabled.
	 */
	public boolean isAnimationEnabled()
	{
		return popup.isAnimationEnabled();
	}

    /**
    * Enable or disable animations.
    *
    * @param animationEnabled true to enable, false to disable
    */
	public void setAnimationEnabled(boolean animationEnabled)
    {
		popup.setAnimationEnabled(animationEnabled);
    }
	
	/**
	 * @param animation
	 */
	public void setAnimation(InOutAnimation animation)
	{
		popup.setAnimation(animation);
	}
}
