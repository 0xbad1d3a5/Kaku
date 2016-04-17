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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

public class WordSet
{
  private HashSet<String> wordSet = new HashSet<String>(); 
  
  
  public WordSet(File wordSetFile)
  {
    try
    {
      if(!wordSetFile.exists())
      {
        return;
      }
      
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(wordSetFile), "UTF8"));
      
      String line = "";
      
      // Skip header line
      reader.readLine();
  
      while ((line = reader.readLine()) != null)
      {
         // Skip comment lines and blank lines
        if(line.startsWith("#") || (line.length() == 0))
        {
          continue;
        }
        
        String[] fields = line.split("\t");
        
        if(fields.length > 0)
        {
          String word = fields[0];
          this.wordSet.add(word);     
        }
      }
      
      reader.close();
    }
    catch(Exception e)
    {
      // Don't care
    }
  }
  
  
  public boolean isWordInSet(String word)
  {
    return this.wordSet.contains(word);
  }
}
