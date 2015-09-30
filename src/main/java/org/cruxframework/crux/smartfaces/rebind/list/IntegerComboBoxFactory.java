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
package org.cruxframework.crux.smartfaces.rebind.list;

import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.smartfaces.client.list.IntegerComboBox;
import org.cruxframework.crux.smartfaces.rebind.Constants;

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(targetWidget = IntegerComboBox.class, id = "integerComboBox", library = Constants.LIBRARY_NAME, 
					description = "Combobox component that uses a data provider to display a list of items or widgets")
@TagChildren({ 
	@TagChild(value = ComboBoxFactory.OptionsProcessor.class, autoProcess = false) 
})
public class IntegerComboBoxFactory extends AbstractComboBoxFactory
{
	protected String getValueType()
    {
	    return "Integer";
    }
}