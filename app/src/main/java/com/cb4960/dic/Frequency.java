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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/** Used to get frequency for words.
 *  
 *  Ported from JGlossator.
 *  */
public class Frequency
{
  private SQLiteDatabase sqliteConnection = null;
   

  
  /** Is the database loaded? */
  public boolean isDatabaseLoaded()
  {
    return (sqliteConnection != null);
  }
  
  
  /** Load the frequency database. */
  public boolean openDatabase(String dbFile)
  {
    boolean status = false;
    
    try
    {
      sqliteConnection = SQLiteDatabase.openDatabase(dbFile, null, 
          SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
      status = true;
    }
    catch (SQLiteException e)
    {
      status = false;
    } 
    
    return status;
  }
  
  
  /** Close the frequency database. */
//  private void closeDatabase()
//  {
//    sqliteConnection.close();
//  }
  
  
   /** Get the frequency of the provided word. */
  public int getFrequency(String word)
  {
    int freq = -1;

    if (sqliteConnection == null)
    {
      return -1;
    }
    
    String query = "SELECT freq FROM Dict WHERE expression='" + word + "'";
    Cursor cursor =  sqliteConnection.rawQuery(query, null);
    
    if (cursor.moveToNext())
    {
      String freqStr = cursor.getString(cursor.getColumnIndex("freq"));

      try 
      {
        freq = Integer.parseInt(freqStr);
      } 
      catch (NumberFormatException e) 
      {
        // Don't care
      }
    }
    
    cursor.close();
    
    return freq;
 }
  
    
  
}
