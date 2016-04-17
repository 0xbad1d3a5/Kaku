/*******************************************************************************
 * Copyright 2013-2015 Christopher Brochtrup
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.cb4960.dic;

import java.util.ArrayList;
import java.util.List;

/** Represents a dictionary entry. */
public class Entry
{
  /** Expression/word. */
  public String Expression = "";
  
  /** Reading of expression. */
  public String Reading = "";
  
  /** Definition. */
  public String Definition = "";
  
  /** Used for Ken5 and Chujiten when user check the "De-prioritize definitions 
    * that don't contain English" fine-tune option. */
  public String SecondaryDefinition;
  
  /** List of example sentences. */
  public List<Example> ExampleList = new ArrayList<Example>();
  
  /** Deinflection rule.
    * Example: (< passive < past) */
  public String DeinflectionRule = "";
  
  /** The original inflected form of the word.
    * Example: 歩いて  */
  public String Inflected = "";
  
  /** Reference to the dictionary that the entry came form. */
  public Dic SourceDic = null;
  
  
  /** Does this entry have the same reading and expression as other entry? */
  public boolean haveSameReadingAndExpression(Entry otherEntry)
  {
    boolean same = false;

    String thisExpression = UtilsFormatting.removeSpecialCharsFromExpression(this.Expression);
    String otherExpression = UtilsFormatting.removeSpecialCharsFromExpression(otherEntry.Expression);

    if(thisExpression == otherExpression)
    {
      String thisReading = UtilsFormatting.removeSpecialCharsFromReading(this.Reading);
      String otherReading = UtilsFormatting.removeSpecialCharsFromReading(otherEntry.Reading);

      if (thisReading == otherReading)
      {
        same = true;
      }
    }

    return same;
  }

}
