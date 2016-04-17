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
import java.util.Random;

/** Represents a single example sentence.
 * 
 *  Ported from JGlossator.
 *  */
public class Example
{
  /** Used for indicating that an example is not attached to a particular sub-definition. */
  public final static int NO_SUB_DEF = 9999;

  /** The text of the example sentence. */
  public String Text = "";
  
  /** The priority of this example sentence. Lower number = higher priority. */
  public int Priority = 1;
  
  /** The number of the sub-definition that this sentence belongs to. */
  public int SubDefNumber = 1;
  
  /** The name of the dictionary that this sentence came from. */
  public String DicName = "";
  
  /** Does example have a translation? */
  public Boolean HasTranslation = true;
  

  /** Automatically choose example sentences. */
  public static List<Example> getBestExamples(String expression, List<Example> exampleList, int num)
  {
    ArrayList<ArrayList<Example>> subDefExampleLists = new ArrayList<ArrayList<Example>>();
    ArrayList<Example> chosenExamples = new ArrayList<Example>();
    ArrayList<Example> curList = new ArrayList<Example>();
    int curSubDef = 1;

    // Split examples into separate lists by sub-definition
    for (Example example : exampleList)
    {
      if (example.SubDefNumber == curSubDef)
      {
        curList.add(example);
      }
      else
      {
        if (curList.size() > 0)
        {
          subDefExampleLists.add(curList);
        }

        curList = new ArrayList<Example>();
        curList.add(example);
        curSubDef = example.SubDefNumber;
      }
    }

    if (curList.size() > 0)
    {
      subDefExampleLists.add(curList);
    }

    // Select one example from each sub-def
    for (ArrayList<Example> subDefExampleList : subDefExampleLists)
    {
      Random rand = new Random();
      int randIdx = rand.nextInt(subDefExampleList.size());

      chosenExamples.add(subDefExampleList.get(randIdx));
      exampleList.remove(subDefExampleList.get(randIdx));

      if ((chosenExamples.size() >= num)
        || (exampleList.size() == 0))
      {
        break;
      }
    }

    // If don't have enough example sentences yet, choose at random
    for (int i = 0;
      ((chosenExamples.size() < num)
      && (exampleList.size() > 0));
      i++)
    {
      Random rand = new Random();
      int randIdx = rand.nextInt(exampleList.size());

      chosenExamples.add(exampleList.get(randIdx));
      exampleList.remove(randIdx);
    }

    return chosenExamples;
  }




}