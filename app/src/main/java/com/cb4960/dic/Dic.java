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

import java.util.List;

abstract public class Dic
{
  public enum DicTypeEnum
  {
    JE,
    JJ,
    UNKNOWN
  }

  public enum DicExamplesEnum
  {
    YES,    // Yes, dic has example sentences.
    NO,     // No,  dic does not have example sentences.
    J_ONLY, // Dic does not have translations for example sentences.
    UNKNOWN
  }

  public enum DicSourceTypeEnum
  {
    EPWING,
    WEB,
    DEFAULT
  }
  
  /** The name of the dictionary. */
  public String Name = "";
  
  /** The short name of the dictionary. */
  public String ShortName = "";
  
  /** The name of the dictionary in English. */
  public String NameEng = "";
  
  
  /**  The name of the example dictionary. If not given, Name will be used. */
  protected String ExamplesName = "";
  
  public String getExamplesName()
  {
    String exName = this.Name;
    
    if (this.ExamplesName.length() > 0)
    {
      exName = this.ExamplesName;
    }
    
    return exName; 
  }
  
    
  /** Notes related to the example sentences. */
  public String ExamplesNotes = "";
  
  /** The type of dictionary: J-E or J-J. */
  public DicTypeEnum DicType = DicTypeEnum.JE;
  
  /** The source of dictionary: web, EPWING, default. */
  public DicSourceTypeEnum SourceType = DicSourceTypeEnum.DEFAULT;
  
  /**  Info about the example sentences present in this dictionary. */
  public DicExamplesEnum ExamplesType = DicExamplesEnum.NO;
  
  /** Are the definitions from this dictionary enabled? */
  public boolean Enabled = true;
  
  /** Are the example sentences from this dictionary enabled? */
  public boolean ExamplesEnabled = true;
  
  /** Do the examples come from a separate database?
    * For example, EDICT uses Tatoeba. */
  public boolean SeparateExampleDictionary = false;
  
  
  /**Lookup the given word. If includeExamples is set, also gather example sentences.*/
  public abstract List<Entry> lookup(String word, boolean includeExamples, FineTune fineTune);

}
