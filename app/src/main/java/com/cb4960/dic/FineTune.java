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

/** Options to fine-tune things related to dictionaries.
 *  
 *  Ported from JGlossator. */
public class FineTune
{
  public enum AppendSource
  {
    No, Yes, Yes_Non_Primary
  }

  /**  Remove word type indicators [example: (v1,n)]. */
  public boolean EdictNoWordIndicators = false;
  
  /** Remove "popular" indicator [example: (P)]. */
  public boolean EdictNoP = false;
  
  /** Fallback if no alpha characters (a-z or A-Z) are detected. */
  public boolean JeNoAlphaFallback = false;
  
  /** Keep examples in the definition. */
  public boolean JjKeepExamplesInDef = false;
  
  /** Remove the '‐' and '･' characters from readings. */
  public boolean JjRemoveSpecialReadingChars = false;
  
  /** Fill in example sentence blanks with expression.
    * Example: ▲無罪を___する。  --->  ▲無罪を確信する。 */
  public boolean JjFillInExampleBlanksWithWord = false;
  
  /** Max examples shown in gloss. */
  public int MaxExamples = 3;
}
