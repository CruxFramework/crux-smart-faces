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
package org.cruxframework.crux.smartfaces.rebind.grid;

import java.util.Comparator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.cruxframework.crux.core.client.dto.DataObject;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Device;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Input;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.Event;
import org.cruxframework.crux.core.rebind.screen.EventFactory;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.ControllerAccessHandler;
import org.cruxframework.crux.core.rebind.screen.widget.EvtProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.PropertyBindInfo;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator.WidgetConsumer;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.AbstractPageableFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasAnimationFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasDataProviderDataBindingProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.HasEnabledFactory;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.AnyWidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.ChoiceChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.smartfaces.client.grid.CellEditor;
import org.cruxframework.crux.smartfaces.client.grid.Column;
import org.cruxframework.crux.smartfaces.client.grid.ColumnGroup;
import org.cruxframework.crux.smartfaces.client.grid.DataGrid;
import org.cruxframework.crux.smartfaces.client.grid.GridDataFactory;
import org.cruxframework.crux.smartfaces.client.grid.GridWidgetFactory;
import org.cruxframework.crux.smartfaces.client.grid.Type.RowSelectStrategy;
import org.cruxframework.crux.smartfaces.client.label.Label;
import org.cruxframework.crux.smartfaces.client.util.animation.InOutAnimation;
import org.cruxframework.crux.smartfaces.rebind.Constants;
import org.cruxframework.crux.smartfaces.rebind.animation.HasInOutAnimationFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.dev.util.collect.HashSet;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="dataGrid", library=Constants.LIBRARY_NAME, targetWidget=DataGrid.class, 
description="A data grid that use a DataProvider to provide data and a gridDataFactory "
	+ "to bound the data to a widget. This data can be paged by a Pager.")
@TagChildren({
	@TagChild(value=DataGridFactory.DetailProcessor.class), 
	@TagChild(value=DataGridFactory.DataGridChildrenProcessor.class)
})
@TagAttributes({
	@TagAttribute(value="detailPopupHeader", type=String.class, supportsI18N=true, supportsResources=true, supportsDataBinding=true),
	@TagAttribute(value="dialogAnimation", processor=DataGridFactory.DialogAnimationProcessor.class, 
	  type=HasInOutAnimationFactory.InOutAnimations.class, widgetType=InOutAnimation.class,
	  description="The animation to be aplied when the dialog is opened or closed."),
	@TagAttribute(value="rowAnimation", processor=DataGridFactory.RowAnimationProcessor.class, 
	  type=HasInOutAnimationFactory.InOutAnimations.class, widgetType=InOutAnimation.class,
	  description="The animation to be aplied when the row is opened or closed for edit mode."),
	@TagAttribute(value="rowSelectStrategy",  type=RowSelectStrategy.class, 
	  description="The strategy used to select rows for this grid."),
})
public class DataGridFactory extends AbstractPageableFactory<WidgetCreatorContext> implements HasEnabledFactory<WidgetCreatorContext>, 
																					HasAnimationFactory<WidgetCreatorContext>
{
	@Override
	public WidgetCreatorContext instantiateContext()
	{
		return new WidgetCreatorContext();
	}
	
	@Override
	public void instantiateWidget(SourcePrinter out, WidgetCreatorContext context) throws CruxGeneratorException
	{
		JClassType dataObject = getDataObject(context);
		String dataObjectName = dataObject.getParameterizedQualifiedSourceName();
		String className = getWidgetClassName() + "<" + dataObjectName + ">";

		RowSelectStrategy selectionStrategy = RowSelectStrategy.valueOf(context.readChildProperty("rowSelectStrategy", RowSelectStrategy.row.toString()));

		out.println("final " + className + " " + context.getWidget() + " = new " + className + "(" +
			RowSelectStrategy.class.getCanonicalName() + "." + selectionStrategy + ");");

		processChildren(out, context, context.getWidgetElement(), null);
	}
	
	protected String createColumn(SourcePrinter out, WidgetCreatorContext context, JClassType dataObject, JSONObject columnElement)
	{
		String columnVar = createColumnByWidget(out, context, columnElement, dataObject);
		if (columnVar == null)
		{
			columnVar = createColumnByProperty(out, context, columnElement, dataObject); 
		}

		createColumnHeader(out, context, columnElement, columnVar);
		createColumnComparator(out, context, columnElement, columnVar, dataObject);
		createColumnEditor(out, context, columnElement, columnVar, dataObject);
		return columnVar;
	}

	protected String createColumnByProperty(SourcePrinter out, WidgetCreatorContext context, JSONObject columnElement, JClassType dataObject)
	{
		String columnVar = createVariableName("column");
		String key = columnElement.optString("key");
		boolean detail = isDetailColumn(columnElement);
		String tooltip = columnElement.optString("tooltip");
		String dataObjectName = dataObject.getParameterizedQualifiedSourceName();
		String widgetClassName = Label.class.getCanonicalName();
		String bindingContextVariable = createVariableName("context");
		Set<String> converterDeclarations = new HashSet<String>();

		HasDataProviderDataBindingProcessor dataBindingProcessor = createDataBindingProcessor(context, dataObject, bindingContextVariable);
		String dataObjectVariable = dataBindingProcessor.getCollectionDataObjectVariable();
		String dataObjectAlias = getDataObjectAlias(dataObject);

		String propertyAttr = columnElement.optString("property");
		if (StringUtils.isEmpty(propertyAttr))
		{
			throw new CruxGeneratorException("There is no expression bound to this column. "
				+ "Use property attribute or a widget tag on your column. Grid ["+context.getWidgetId()+"]. View ["+getView().getId()+"].");
		}

		String resultVariable = createVariableName("result");
		StringBuilder valueExpression = new StringBuilder(); 

		JType valueType = getDataBindingReadExpression(resultVariable, dataObjectAlias, bindingContextVariable, 
			propertyAttr, converterDeclarations, Label.class.getCanonicalName(), "text", 
			dataBindingProcessor, valueExpression);	

		String factoryClassName = GridDataFactory.class.getCanonicalName()+"<" + dataObjectName + ">";
		String columnClassName = Column.class.getCanonicalName()+"<" + dataObjectName + ", "  + widgetClassName + ">";
		out.println(columnClassName + " " + columnVar + " = " + context.getWidget()+".newColumn(new " + factoryClassName + "(){");

		generateBindingContextDeclaration(out, bindingContextVariable, getViewVariable());

		boolean hasTooltip = createGetTooltipMethod(out, dataBindingProcessor, converterDeclarations, tooltip, dataObjectName, dataObjectAlias);

		out.println("public " + widgetClassName + " createData("+dataObjectName+" "+dataObjectVariable+", final int rowIndex){");

		for (String converterDecl : converterDeclarations)
		{
			out.println(converterDecl);
		}
		
		out.println(valueType.getParameterizedQualifiedSourceName() + " " + resultVariable + ";");
		out.println(valueExpression + ";");

		boolean isStringType = valueType.getQualifiedSourceName().equals(String.class.getCanonicalName());

		if (hasTooltip)
		{
			String widgetVar = createVariableName("widget");
			out.println(widgetClassName + " " + widgetVar + " = new " + widgetClassName + "(" + (!isStringType?"\"\"+" +resultVariable: resultVariable) + ");");
			out.println(widgetVar + ".setTitle(getTooltip("+dataObjectVariable+"));");
			out.println("return " + widgetVar + ";");
		}
		else
		{
			out.println("return new " + widgetClassName + "(" + (!isStringType?"\"\"+" +resultVariable: resultVariable) + ");");
		}

		out.println("}");
		out.println("}, " + EscapeUtils.quote(key) + ", " + detail + ");");

		return columnVar;
	}

	protected String createColumnByWidget(SourcePrinter out, WidgetCreatorContext context, JSONObject columnElement, JClassType dataObject)
	{
		String widgetClassName = IsWidget.class.getCanonicalName();
		String dataObjectName = dataObject.getParameterizedQualifiedSourceName();
		String widgetFactoryClassName = GridDataFactory.class.getCanonicalName()+"<"+dataObjectName+">";

		JSONArray children = ensureChildren(columnElement, true, context.getWidgetId());
		if (children != null)
		{
			for (int i = 0; i < children.length(); i++)
			{
				JSONObject child = children.optJSONObject(i);
				String childName = getChildName(child);

				if (childName != null && childName.startsWith("widget"))
				{
					String columnVar = createVariableName("column");
					String columnClassName = Column.class.getCanonicalName()+"<" + dataObjectName + ", "  + widgetClassName + ">";
					out.println(columnClassName + " " + columnVar + " = " + context.getWidget()+".newColumn(");
					String key = columnElement.optString("key");
					boolean detail = isDetailColumn(columnElement);

					if (childName.equals("widget"))
					{
						out.println("new " + widgetFactoryClassName + "(){");
						String bindingContextVariable = createVariableName("_context");
						generateBindingContextDeclaration(out, bindingContextVariable, getViewVariable());
						generateWidgetCreationForCellByTemplate(out, context, child, 
							dataObject, bindingContextVariable, columnElement,
							widgetClassName);
						out.println("}");
					}
					else if (childName.equals("widgetFactory"))
					{
						out.println("new " + widgetFactoryClassName + "(){");
						generateWidgetCreationForCellOnController(out, context, child, dataObject, columnElement,
							widgetClassName);
						out.println("}");
					}
					else
					{
						throw new CruxGeneratorException("Invalid tag name ["+childName+"]");
					}
					out.println(", " + EscapeUtils.quote(key) + ", " + detail + ");");

					return columnVar;
				}
			}
		}

		return null;
	}

	protected void createColumnComparator(SourcePrinter out, WidgetCreatorContext context, JSONObject columnElement, String columnVar, JClassType dataObject)
	{
		String sortable = columnElement.optString("sortable");
		if (!StringUtils.isEmpty(sortable) && Boolean.parseBoolean(sortable))
		{
			String propertyAttr = columnElement.optString("property");
			if (!StringUtils.isEmpty(propertyAttr))
			{
				String dataObjectName = dataObject.getParameterizedQualifiedSourceName();

				String bindingContextVariable = createVariableName("context");
				HasDataProviderDataBindingProcessor dataBindingProcessor = createDataBindingProcessor(context, dataObject, bindingContextVariable);
				Set<String> converterDeclarations = new HashSet<String>();
				String resultVariable = createVariableName("result");
				String dataObjectAlias = getDataObjectAlias(dataObject);

				StringBuilder valueExpression = new StringBuilder();
				JType bindingType = getDataBindingReadExpression(resultVariable, dataObjectAlias, bindingContextVariable, 
					propertyAttr, converterDeclarations, Label.class.getCanonicalName(), "text", 
					dataBindingProcessor, valueExpression);	

				if (bindingType != null)
				{
					String typeName = bindingType.getParameterizedQualifiedSourceName();

					JClassType comparableType = getContext().getGeneratorContext().getTypeOracle().findType(Comparable.class.getCanonicalName());
					JPrimitiveType primitive = bindingType.isPrimitive();

					if (primitive != null || bindingType.isClassOrInterface().isAssignableTo(comparableType))
					{
						out.println(columnVar + ".setComparator(new "+Comparator.class.getCanonicalName()+"<"+dataObjectName+">(){");

						generateBindingContextDeclaration(out, bindingContextVariable, getViewVariable());

						for (String converterDecl : converterDeclarations)
						{
							out.println(converterDecl);
						}
						String dataObjectVariable = dataBindingProcessor.getCollectionDataObjectVariable();

						out.println("public " + typeName + " getValue(" + dataObjectName + " " + dataObjectVariable +") {");
						out.println(typeName + " " + resultVariable + ";");
						out.println(valueExpression + ";");
						out.println("return " + resultVariable + ";");
						out.println("}");

						out.println("public int compare(" + dataObjectName + " o1, " + dataObjectName + " o2) {");
						out.println(typeName + " v1 = getValue(o1);");
						out.println(typeName + " v2 = getValue(o2);");
						if (primitive != null)
						{
							if (primitive == JPrimitiveType.BOOLEAN)
							{
								out.println("return (v1 == v2 ? 0 : (v1? 1 : -1));");
							}
							else
							{
								out.println("return (v1 == v2 ? 0 : (v1 < v2 ? -1 : 1));");
							}
						}
						else
						{
							out.println("if (v1==v2 || (v1==null && v2==null)) return 0;");
							out.println("if (v1==null) return -1;");
							out.println("if (v2==null) return 1;");
							out.println("return v1.compareTo(v2);");
							out.println("}");
							out.println("});");
						}
					}
				}
			}
			out.println(columnVar + ".setSortable(" + sortable + ");");
		}
	}
	
	@Override
	protected Set<String> generateWidgetCreationForCellByTemplate(SourcePrinter out, WidgetCreatorContext context, JSONObject child, 
		JClassType dataObject, String bindingContextVariable, HasDataProviderDataBindingProcessor bindingProcessor)
	{
		child = ensureFirstChild(child, false, context.getWidgetId());

		out.println("public "+IsWidget.class.getCanonicalName()+" createData("+dataObject.getParameterizedQualifiedSourceName()
				    +" "+bindingProcessor.getCollectionObjectReference()+", final int rowIndex){");
	    String childWidget = createChildWidget(out, child, WidgetConsumer.EMPTY_WIDGET_CONSUMER, bindingProcessor, context);
	    out.println("return "+childWidget+";");
	    out.println("}");
	    
	    return bindingProcessor.getConverterDeclarations();
	}

	protected void createColumnEditor(SourcePrinter out, WidgetCreatorContext context, JSONObject columnElement, String columnVar,
		JClassType dataObject)
	{
		JSONObject editorTag = getChildTag(columnElement, "editor", context.getWidgetId());
		if (editorTag != null)
		{
			JSONObject child = ensureFirstChild(editorTag, true, context.getWidgetId());
			if (child != null)
			{
				Set<String> converterDeclarations = new HashSet<String>();

				String bindingContextVariable = createVariableName("context");
				HasDataProviderDataBindingProcessor dataBindingProcessor = createDataBindingProcessor(context, dataObject, bindingContextVariable);

				String propertyValue = editorTag.optString("property");
				if (StringUtils.isEmpty(propertyValue))
				{
					propertyValue = columnElement.optString("property");
				}
				if (StringUtils.isEmpty(propertyValue))
				{
					throw new CruxGeneratorException("Column Editor needs the property attribute defined. Grid [" + 
						context.getWidgetId() + "]. View [" + getView().getId() + "]");
				}

				PropertyBindInfo binding = getObjectDataBinding(propertyValue, null, null, true, dataBindingProcessor);

				if (binding != null)
				{
					String dataObjectVariable = dataBindingProcessor.getCollectionDataObjectVariable();
					String newValueVariable = createVariableName("newValue");
					String expression = binding.getDataObjectWriteExpression(dataObjectVariable, newValueVariable);
					String converterDeclaration = binding.getConverterDeclaration();
					if (converterDeclaration != null)
					{
						converterDeclarations.add(converterDeclaration);
					}
					String typeName = binding.getType().getParameterizedQualifiedSourceName();

					boolean autoRefreshRow = editorTag.optBoolean("autoRefreshRow", false);
					String dataObjectType = dataObject.getParameterizedQualifiedSourceName();
					out.println(columnVar + ".setCellEditor(new " + CellEditor.class.getCanonicalName() + 
						"<" + dataObjectType+", " + typeName + ">(" + autoRefreshRow + ") {");

					generateBindingContextDeclaration(out, bindingContextVariable, getViewVariable());

					out.println("public void setProperty(" + dataObjectType + " " + dataObjectVariable + ", " + typeName + " " + newValueVariable + "){");
					out.println(expression);
					out.println("}");

					String childName = getChildName(child);

					if (childName.equals("widget"))
					{

						Set<String> converters = generateWidgetCreationForCellByTemplate(out, context, child, dataObject, bindingContextVariable, dataBindingProcessor);
						converterDeclarations.addAll(converters);

					}
					else if (childName.equals("widgetFactory"))
					{
						generateWidgetCreationForCellOnController(out, context, child, dataObject);
					}
					else
					{
						throw new CruxGeneratorException("Invalid child tag for editor on widget ["+context.getWidgetId()+"], "
							+ "View ["+getView().getId()+"]. Please revalidade your XML file.");
					}

					for (String converterDecl : converterDeclarations)
					{
						out.println(converterDecl);
					}
					out.println("});");
				}
			}
		}
	}

	protected String createColumnGroup(SourcePrinter out, WidgetCreatorContext context, JClassType dataObject, JSONObject columnGroupElement)
	{
		String key = columnGroupElement.optString("key");
		String columnGroupVar = createVariableName("columnGroup");
		String dataObjectName = dataObject.getParameterizedQualifiedSourceName();
		String columnGroupClassName = ColumnGroup.class.getCanonicalName()+"<" + dataObjectName + ">";
		out.println(columnGroupClassName + " " + columnGroupVar + " = " + context.getWidget()+".newColumnGroup(" + EscapeUtils.quote(key) + ");");

		createColumnHeader(out, context, columnGroupElement, columnGroupVar);
		processChildren(out, context, columnGroupElement, columnGroupVar);

		return columnGroupVar;
	}

	protected void createColumnHeader(SourcePrinter out, WidgetCreatorContext context, JSONObject columnElement, String columnVar)
	{
		JSONObject headerTag = getChildTag(columnElement, "header", context.getWidgetId());
		if (headerTag != null)
		{
			JSONObject headerChild = ensureFirstChild(headerTag, false, context.getWidgetId());
			String childName = getChildName(headerChild);
			if (childName.equals("text"))
			{
				String header = getDeclaredMessage(headerChild.optString("text"));
				out.println(columnVar + ".setHeaderWidget(new " + Label.class.getCanonicalName() + "(" + header + "));");
			}
			else if (childName.equals("widget"))
			{
				JSONObject childWidget = ensureFirstChild(headerChild, false, context.getWidgetId());
				String child = createChildWidget(out, childWidget, WidgetConsumer.EMPTY_WIDGET_CONSUMER, context.getDataBindingProcessor(), context);
				out.println(columnVar + ".setHeaderWidget(" + child + ");");
			}
			else
			{
				throw new CruxGeneratorException("Invalid tag. Validate your view file.");
			}
		}
		else
		{
			String header = getDeclaredMessage(columnElement.optString("header"));
			if (!StringUtils.isEmpty(header))
			{
				out.println(columnVar + ".setHeaderWidget(new " + Label.class.getCanonicalName() + "(" + header + "));");
			}
		}
	}


	protected boolean createGetTooltipMethod(SourcePrinter out, HasDataProviderDataBindingProcessor dataBindingProcessor, 
		Set<String> converterDeclarations, String tooltip, String dataObjectName, String dataObjectAlias)
	{
		if (StringUtils.isEmpty(tooltip))
		{
			return false;
		}

		String dataObjectVariable = dataBindingProcessor.getCollectionDataObjectVariable();
		out.println("String getTooltip("+dataObjectName+" "+dataObjectVariable+"){");
		String resultVariable = createVariableName("result");
		StringBuilder tooltipExpression = new StringBuilder(); 

		String bindingContextVariable = dataBindingProcessor.getBindingContextVariable();
		JType tooltipType = getDataBindingReadExpression(resultVariable, dataObjectAlias, bindingContextVariable, 
			tooltip, converterDeclarations, Label.class.getCanonicalName(), "title", 
			dataBindingProcessor, tooltipExpression);	

		out.println(tooltipType.getParameterizedQualifiedSourceName() + " " + resultVariable + ";");
		out.println(tooltipExpression + ";");
		out.println("return " + resultVariable + ";");
		out.println("}");

		return true;
	}

	/**
	 * Generate the createWidget method and return the set of converter declarations used by the generated method
	 * @param out
	 * @param context
	 * @param child
	 * @param dataObject
	 * @param bindingContextVariable
	 * @return
	 */
	protected void generateWidgetCreationForCellByTemplate(SourcePrinter out, WidgetCreatorContext context, JSONObject child, 
		JClassType dataObject, String bindingContextVariable, JSONObject columnElement, String widgetClassName)
	{
		HasDataProviderDataBindingProcessor bindingProcessor = createDataBindingProcessor(context, dataObject, bindingContextVariable);
		child = ensureFirstChild(child, false, context.getWidgetId());
		String tooltip = columnElement.optString("tooltip");

		String dataObjectClassName = dataObject.getParameterizedQualifiedSourceName();
		String dataObjectAlias = getDataObjectAlias(dataObject);

		boolean hasTooltip = createGetTooltipMethod(out, bindingProcessor, bindingProcessor.getConverterDeclarations(), tooltip, 
			dataObjectClassName, dataObjectAlias);

		String dataObjectVariable = bindingProcessor.getCollectionObjectReference();
		out.println("public "+widgetClassName+" createData("+dataObjectClassName+" "+dataObjectVariable + 
			", final int rowIndex){");
		String childWidget = createChildWidget(out, child, WidgetConsumer.EMPTY_WIDGET_CONSUMER, bindingProcessor, context);
		if (hasTooltip)
		{
			out.println(childWidget + ".setTitle(getTooltip("+dataObjectVariable+"));");
		}
		out.println("return "+childWidget+";");
		out.println("}");

		for (String converterDeclaration : bindingProcessor.getConverterDeclarations())
		{
			out.println(converterDeclaration);
		}
	}

	protected void generateWidgetCreationForCellOnController(SourcePrinter out, WidgetCreatorContext context, JSONObject child,
		JClassType dataObject, JSONObject columnElement, String widgetClassName)
	{
		try
		{
			String tooltip = columnElement.optString("tooltip");
			String onCreateWidget = child.getString("onCreateWidget");
			Event event = EventFactory.getEvent("onCreateWidget", onCreateWidget);
			if (event != null)
			{
				String controllerClass = getControllerAccessorHandler().getControllerImplClassName(event.getController(), getDevice());
				out.println("private "+controllerClass+" controller = " + getControllerAccessorHandler().getControllerExpression(
					event.getController(), getDevice())+";");
			}
			ControllerAccessHandler controllerAccessHandler = new ControllerAccessHandler()
			{
				@Override
				public String getControllerExpression(String controller, Device device)
				{
					return "this.controller";
				}

				@Override
				public String getControllerImplClassName(String controller, Device device)
				{
					return getControllerAccessorHandler().getControllerImplClassName(controller, device);
				}
			};

			String dataObjectClassName = dataObject.getParameterizedQualifiedSourceName();
			String dataObjectAlias = getDataObjectAlias(dataObject);

			String bindingContextVariable = createVariableName("_context");
			generateBindingContextDeclaration(out, bindingContextVariable, getViewVariable());
			HasDataProviderDataBindingProcessor dataBindingProcessor = createDataBindingProcessor(context, dataObject, bindingContextVariable);
			boolean hasTooltip = createGetTooltipMethod(out, dataBindingProcessor, dataBindingProcessor.getConverterDeclarations(), 
				tooltip, dataObjectClassName, dataObjectAlias);

			out.println("public "+widgetClassName+" createData("+dataObjectClassName+" value, final int rowIndex){");

			String childWidget = createVariableName("widget");
			out.print(widgetClassName + " " + childWidget + " = ");
			EvtProcessor.printEvtCall(out, onCreateWidget, "onCreateWidget", dataObjectClassName, "value", 
				getContext(), getView(), controllerAccessHandler, getDevice(), false);

			out.println(";");
			if (hasTooltip)
			{
				String dataObjectVariable = dataBindingProcessor.getCollectionObjectReference();
				out.println(childWidget + ".setTitle(getTooltip("+dataObjectVariable+"));");
			}

			out.print("return "+childWidget+";");
			out.println("}");

			for (String converterDeclaration : dataBindingProcessor.getConverterDeclarations())
			{
				out.println(converterDeclaration);
			}

		}
		catch (JSONException e)
		{
			throw new CruxGeneratorException("Missing required attribute [onCreateWidget], on widgetFactoryOnController "
				+ "tag on widget declaration. WidgetID ["+context.getWidgetId()+"]. View ["+getView().getId()+"]");
		}    }


	protected JSONObject getChildTag(JSONObject parent, String tagName, String widgetId)
	{
		JSONArray children = ensureChildren(parent, true, widgetId);
		if (children != null)
		{
			for (int i = 0; i < children.length(); i++)
			{
				JSONObject child = children.optJSONObject(i);
				String childName = getChildName(child);

				if (childName != null && childName.equals(tagName))
				{
					return child;
				}
			}
		}
		return null;
	}

	protected String getDataObjectAlias(JClassType dataObject)
	{
		DataObject dataObjectAnnotation = dataObject.getAnnotation(DataObject.class);

		if (dataObjectAnnotation == null)
		{
			throw new CruxGeneratorException("Invalid dataObject: "+dataObject.getQualifiedSourceName());
		}
		String dataObjectAlias = dataObjectAnnotation.value();
		return dataObjectAlias;
	}

	protected boolean isDetailColumn(JSONObject columnElement)
    {
		String detailOnSize = columnElement.optString("detailOnSize");
		String detailOnInput = columnElement.optString("detailOnInput");
		boolean defaultValue = false;
		
		Device device = getDevice();
		if (device != null)
		{
			if (!StringUtils.isEmpty(detailOnSize))
			{
				if (device.getSize().toString().equals(detailOnSize))
				{
					defaultValue = true;
				}
			}
			if (!StringUtils.isEmpty(detailOnInput))
			{
				if (device.getInput().toString().equals(detailOnInput))
				{
					if (StringUtils.isEmpty(detailOnSize))
					{
						defaultValue = true;
					}
				}
				else
				{
					defaultValue = false;
				}
			}
		}
		
	    return columnElement.optBoolean("detail", defaultValue);
    }

	protected void proccessDetailHeader(SourcePrinter out, WidgetCreatorContext context, JSONObject detailElement)
	{
		JSONObject headerTag = getChildTag(detailElement, "header", context.getWidgetId());
		if (headerTag != null)
		{
			out.println(context.getWidget() + ".setDetailColumnHeaderWidgetFactory(new "+ GridWidgetFactory.class.getCanonicalName() + "(){");
			out.println("public " + IsWidget.class.getCanonicalName() + " createWidget(){");

			JSONObject headerChild = ensureFirstChild(headerTag, false, context.getWidgetId());
			String childName = getChildName(headerChild);
			if (childName.equals("text"))
			{
				String header = getDeclaredMessage(headerChild.optString("text"));
				out.println("return new " + Label.class.getCanonicalName() + "(" + header + ");");
			}
			else if (childName.equals("widget"))
			{
				JSONObject childWidget = ensureFirstChild(headerChild, false, context.getWidgetId());
				String child = createChildWidget(out, childWidget, WidgetConsumer.EMPTY_WIDGET_CONSUMER, context.getDataBindingProcessor(), context);
				out.println("return " + child + ";");
			}
			else
			{
				throw new CruxGeneratorException("Invalid tag. Validate your view file.");
			}
			out.println("}");
			out.println("});");
		}
		else
		{
			String header = getDeclaredMessage(detailElement.optString("header"));
			if (!StringUtils.isEmpty(header))
			{
				out.println(context.getWidget() + ".setDetailColumnHeaderWidgetFactory(new "+ GridWidgetFactory.class.getCanonicalName() + "(){");
				out.println("public " + IsWidget.class.getCanonicalName() + " createWidget(){");

				out.println("return new " + Label.class.getCanonicalName() + "(" + header + ");");

				out.println("}");
				out.println("});");
			}
		}

	}

	protected void proccessDetailTrigger(SourcePrinter out, WidgetCreatorContext context, JSONObject detailElement)
	{
		JSONObject triggerTag = getChildTag(detailElement, "trigger", context.getWidgetId());
		if (triggerTag != null)
		{
			out.println(context.getWidget() + ".setDetailTriggerWidgetFactory(new "+ GridWidgetFactory.class.getCanonicalName() + "(){");

			JSONObject triggerChild = ensureFirstChild(triggerTag, false, context.getWidgetId());
			String childName = getChildName(triggerChild);
			if (childName.equals("widgetFactory"))
			{
				try
				{
					String onCreateWidget = triggerChild.getString("onCreateWidget");
					Event event = EventFactory.getEvent("onCreateWidget", onCreateWidget);
					if (event != null)
					{
						String controllerClass = getControllerAccessorHandler().getControllerImplClassName(event.getController(), getDevice());
						out.println("private "+controllerClass+" controller = " + getControllerAccessorHandler().getControllerExpression(
							event.getController(), getDevice())+";");
					}
					ControllerAccessHandler controllerAccessHandler = new ControllerAccessHandler()
					{
						@Override
						public String getControllerExpression(String controller, Device device)
						{
							return "this.controller";
						}

						@Override
						public String getControllerImplClassName(String controller, Device device)
						{
							return getControllerAccessorHandler().getControllerImplClassName(controller, device);
						}
					};

					out.println("public " + IsWidget.class.getCanonicalName() + " createWidget(){");
					
					out.print("return ");
					EvtProcessor.printEvtCall(out, onCreateWidget, "onCreateWidget", null, null, 
						getContext(), getView(), controllerAccessHandler, getDevice(), true);
					
					out.println("}");
				}
				catch (JSONException e)
				{
					throw new CruxGeneratorException("Missing required attribute [onCreateWidget], on widgetFactoryOnController "
						+ "tag on widget declaration. WidgetID ["+context.getWidgetId()+"]. View ["+getView().getId()+"]");
				}
			}
			else if (childName.equals("widget"))
			{
				out.println("public " + IsWidget.class.getCanonicalName() + " createWidget(){");

				JSONObject childWidget = ensureFirstChild(triggerChild, false, context.getWidgetId());
				String child = createChildWidget(out, childWidget, WidgetConsumer.EMPTY_WIDGET_CONSUMER, context.getDataBindingProcessor(), context);
				out.println("return " + child + ";");
				
				out.println("}");
			}
			else
			{
				throw new CruxGeneratorException("Invalid tag. Validate your view file.");
			}
			out.println("});");
		}	    
	}

	protected void processChildren(SourcePrinter out, WidgetCreatorContext context, JSONObject parent, String groupVar)
	{
		JClassType dataObject = getDataObject(context);
		JSONArray children = ensureChildren(parent, false, context.getWidgetId());
		if (children != null)
		{
			for (int i = 0; i < children.length(); i++)
			{
				JSONObject child = children.optJSONObject(i);
				String childName = getChildName(child);

				if (childName.equals("column"))
				{
					if (targetsDevice(child))
					{
						String column = createColumn(out, context, dataObject, child);
						if (!StringUtils.isEmpty(groupVar))
						{
							out.println(groupVar + ".addColumn(" + column + ");");
						}
					}
				}
				else if (childName.equals("columnGroup"))
				{
					if (targetsDevice(child))
					{
						String columnGroup = createColumnGroup(out, context, dataObject, child);
						if (!StringUtils.isEmpty(groupVar))
						{
							out.println(groupVar + ".addColumnGroup(" + columnGroup + ");");
						}
					}
				}
				else if (childName.equals("detail"))
				{
					if (targetsDevice(child))
					{
						processDetail(out, context, child);
					}
				}
			}
		}
	}

	protected void processDetail(SourcePrinter out, WidgetCreatorContext context, JSONObject detailElement)
	{
		proccessDetailHeader(out, context, detailElement);
		proccessDetailTrigger(out, context, detailElement);
	}

	public static class ChildWidgetHeaderCreator extends AnyWidgetChildProcessor<WidgetCreatorContext>{}

	@TagConstraints(tagName="columnGroup", applyDeviceFilters=true)
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="key", required=true, supportsI18N=false, supportsResources=false, 
			supportsDataBinding=false,  
			description="The column key, used to identify the column on this grid."),
			@TagAttributeDeclaration(value="header", supportsI18N=true, supportsResources=true, 
			description="The column header.")
	})
	@TagChildren({
		@TagChild(HeaderCreator.class),
		@TagChild(value=DataGridChildrenProcessor.class)
	})
	public static class ColumnGroupProcessor extends WidgetChildProcessor<WidgetCreatorContext>{}

	@TagConstraints(tagName="column", applyDeviceFilters=true)
	@TagChildren({
		@TagChild(HeaderCreator.class),
		@TagChild(OptionalWidgetChildCreator.class),
		@TagChild(EditorCreator.class)
	})
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="key", required=true, supportsI18N=false, supportsResources=false, 
			supportsDataBinding=false,  
			description="The column key, used to identify the column on this grid."),
			@TagAttributeDeclaration(value="property", supportsI18N=false, supportsResources=false, supportsDataBinding=true,  
			description="The expression used to bind the column value from the dataProvider object"),
			@TagAttributeDeclaration(value="tooltip", supportsI18N=true, supportsResources=true, 
			description="The expression used to bind the column hint from the dataProvider object"),
			@TagAttributeDeclaration(value="header", supportsI18N=true, supportsResources=true, 
			description="The column header."),
			@TagAttributeDeclaration(value="sortable", type=Boolean.class, 
			description="If true makes this column sortable"),
			@TagAttributeDeclaration(value="detail", type=Boolean.class, defaultValue="false",  
			description="If true, this column will only be displayed inside a details popup"),
			@TagAttributeDeclaration(value="detailOnInput", type=Input.class,
			description="If informed, this column will be displayed inside a details popup for the devices with the given input type."),
			@TagAttributeDeclaration(value="detailOnSize", type=Size.class,  
			description="If informed, this column will be displayed inside a details popup for the devices with the given size.")
	})
	public static class ColumnProcessor extends WidgetChildProcessor<WidgetCreatorContext>{}

	@TagConstraints(minOccurs="1", maxOccurs="unbounded")
	@TagChildren({
		@TagChild(value=ColumnProcessor.class, autoProcess=false), 
		@TagChild(value=ColumnGroupProcessor.class, autoProcess=false) 
	})
	public static class DataGridChildrenProcessor extends ChoiceChildProcessor<WidgetCreatorContext>{}

	@TagConstraints(tagName="detail", minOccurs="0", applyDeviceFilters=true)
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="header", supportsI18N=true, supportsResources=true, 
			description="The column header.")
	})
	@TagChildren({
		@TagChild(value=HeaderCreator.class, autoProcess=false), 
		@TagChild(value=DetailTriggerProcessor.class, autoProcess=false) 
	})
	public static class DetailProcessor extends WidgetChildProcessor<WidgetCreatorContext>{}

	@TagConstraints(tagName="trigger", minOccurs="0")
	@TagChildren({
		@TagChild(WidgetChildCreator.class)
	})
	public static class DetailTriggerProcessor extends WidgetChildProcessor<WidgetCreatorContext>{}

	public static class DialogAnimationProcessor extends AttributeProcessor<WidgetCreatorContext>
    {
		public DialogAnimationProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
	        out.println(context.getWidget()+".setDialogAnimation("+InOutAnimation.class.getCanonicalName()+"."+attributeValue+");");
        }
    }

	@TagConstraints(tagName="editor", minOccurs="0", maxOccurs="1")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="autoRefreshRow", type=Boolean.class, defaultValue="false"), 
		@TagAttributeDeclaration(value="property", supportsI18N=false, supportsResources=false, supportsDataBinding=true)
	})
	@TagChildren({
		@TagChild(value=WidgetChildCreator.class, autoProcess=false)
	})
	public static class EditorCreator extends WidgetChildProcessor<WidgetCreatorContext>{}

	@TagChildren({
		@TagChild(TextHeaderCreator.class),
		@TagChild(WidgetHeaderCreator.class)
	})
	public static class HeaderChildrenCreator extends ChoiceChildProcessor<WidgetCreatorContext>{}

	@TagConstraints(tagName="header", minOccurs="0", maxOccurs="1")
	@TagChildren({
		@TagChild(HeaderChildrenCreator.class),
	})
	public static class HeaderCreator extends WidgetChildProcessor<WidgetCreatorContext>{}

	@TagConstraints(minOccurs="0", maxOccurs="1")
	@TagChildren({
		@TagChild(WidgetFactoryChildCreator.class),
		@TagChild(WidgetFactoryControllerChildCreator.class)
	})
	public static class OptionalWidgetChildCreator extends ChoiceChildProcessor<WidgetCreatorContext>{}

	public static class RowAnimationProcessor extends AttributeProcessor<WidgetCreatorContext>
    {
		public RowAnimationProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, WidgetCreatorContext context, String attributeValue)
        {
	        out.println(context.getWidget()+".setRowAnimation("+InOutAnimation.class.getCanonicalName()+"."+attributeValue+");");
        }
    }

	@TagConstraints(tagName="text")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="text", required=true, supportsI18N=true, supportsResources=true, 
			supportsDataBinding=false,  
			description="The column header.")
	})
	public static class TextHeaderCreator extends WidgetChildProcessor<WidgetCreatorContext>{}

	@TagConstraints(tagName="widget")
	@TagChildren({
		@TagChild(ChildWidgetHeaderCreator.class)
	})
	public static class WidgetHeaderCreator extends WidgetChildProcessor<WidgetCreatorContext>{}
}