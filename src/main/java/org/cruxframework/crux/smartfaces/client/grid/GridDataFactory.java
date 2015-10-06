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
package org.cruxframework.crux.smartfaces.client.grid;

/**
 * Define the a new data factory passing the row as a parameter. 
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 * @param <V> the widget type.
 * @param <T> the Data Object type.
 */
public interface GridDataFactory<V, T>
{
	V createData(T value, Row<T> row);
}
