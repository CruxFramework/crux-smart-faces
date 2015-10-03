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
package org.cruxframework.crux.smartfaces.client.grid;

/**
 * @author Samuel Almeida Cardoso (samuel@cruxframework.org)
 *
 * - EXPERIMENTAL - 
 * THIS CLASS IS NOT READY TO BE USED IN PRODUCTION. IT CAN CHANGE FOR NEXT RELEASES
 */
public interface Type 
{
	public static enum RowSelectStrategy
	{
		unselectable,
		single,
		//singleRadioButton,
		//singleCheckBox,
		multiple	
		//multipleCheckBox,
		//multipleCheckBoxSelectAll
		;
		
		public boolean isSingle()
		{
			return 
				this.equals(single);
				/*
				||
				this.equals(singleRadioButton)
				||
				this.equals(singleCheckBox);
				*/
		}
		
		public boolean isMultiple()
		{
			return 
				this.equals(multiple);
				/*
				||
				this.equals(multipleCheckBox)
				||
				this.equals(multipleCheckBoxSelectAll);
				*/
		}
	}
}
