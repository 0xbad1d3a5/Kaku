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

/** Represents a Kanji.
 *  
 *  Ported from JGlossator.
 *  */
public class InfoKanji
{
  public static int NO_FREQ = 999999;

  // Notes: 
  // All integers are set to -1 if unknown
  // Examples in comments based on entry for 頑:
  //   頑|B181 G8 S13 F1247 N5122 V6624 H1040 DK712 L61 IN1848 E1119 P1-4-9 I9a4.6 Ywan2|ガン かたく|||stubborn, foolish, firmly
  // Examples for special readings based on entry for 阜:
  //   阜|B170 G9 S8 F1532 N4977 V6423 H2628 L2928 P2-6-2 I2k6.3 Yfu4|フ フウ|おか|ぎふのふ|hill, mound, left village radical (no. 170)
  // References:     
  //   http://www.csse.monash.edu.au/~jwb/kanjidic.html
  //   http://ftp.monash.edu.au/pub/nihongo/kanjidic2_ov.html
  
  /// <summary>
  /// The kanji itself:
  /// Example: 頑
  /// </summary>
  public String Kanji = ""; 

  /// <summary>
  /// The readings of the kanji.
  /// Readings in katakana are the "ON" (Chinese origin) readings.
  /// Readings in hiragana are the "KUN" (Japanese origin) readings.
  /// </summary>
  public String Readings = "";

  /// <summary>
  /// The 名乗り (nanori) readings of the kanji. These are special readings only used with proper names.
  /// Example: おか
  /// </summary>
  public String ReadingsNanori = "";
  public String ReadingsNanoriFormatted()
  { 
    return (this.ReadingsNanori.length() == 0) ? "n/a" : this.ReadingsNanori;
  }
  
  /// <summary>
  /// The 部首名 readings of the kanji. 
  /// Example: ぎふのふ
  /// </summary>
  public String ReadingsBushumei = "";
  public String ReadingsBushumeiFormatted()
  { 
    return (this.ReadingsBushumei.length() == 0) ? "n/a" : this.ReadingsBushumei;
  }
  
  /// <summary>
  /// Meanings of the kanji.
  /// Example: stubborn, foolish, firmly
  /// </summary>
  public String Meanings = "";
  public String MeaningsFormatted()
  { 
    return (this.Meanings.length() == 0) ? "n/a" : this.Meanings;
  }
  
  /// <summary>
  /// A frequency-of-use ranking. The 2,500 most-used characters have a ranking; those characters
  /// that lack this field are not ranked. The frequency is a number from 1 to 2,500 that expresses
  /// the relative frequency of occurrence of a character in modern Japanese. This is based on a
  /// survey in newspapers, so it is biassed towards kanji used in newspaper articles. The
  /// discrimination between the less frequently used kanji is not strong. 
  /// Example: F1247
  /// </summary>
  public int Freq = NO_FREQ;
  public String FreqStr()
  { 
    return (this.Freq == NO_FREQ) ? "n/a" : Integer.toString(this.Freq);
  }
  

  /// <summary>
  /// Jouyou Kanji grade level.  1 through 6 indicate the grade in which the kanji is taught in 
  /// Japanese schools. 8 indicates it is one of the remaining Jouyou Kanji to be learned in junior 
  /// high school, and 9 indicates it is a Jinmeiyou (for use in names) kanji.
  /// Example: G8
  /// </summary>
  public int Grade = -1; // G8. Jouyou Kanji grade level.  1 through 6 indicate the grade in which
  public String GradeStr() 
  { 
    return (this.Grade == -1) ? "n/a" : Integer.toString(this.Grade);
  }
  
  /// <summary>
  /// Kanji number in "New Japanese-English Character Dictionary" by Jack Halpern. 
  /// Example: H1040
  /// </summary>
  public int Halpern = -1;
  public String HalpernStr()
  { 
    return (this.Halpern == -1) ? "n/a" : Integer.toString(this.Halpern);
  }

  /// <summary>
  /// Kanji number in "Remembering The Kanji" by James Heisig. 
  /// Example: L61
  /// </summary>
  public int Heisig = -1;
  public String HeisigStr()
  { 
    return (this.Heisig == -1) ? "n/a" : Integer.toString(this.Heisig);
  }

  /// <summary>
  /// "Kanji number in "A Guide To Remembering Japanese Characters" by Kenneth G. Henshall.
  /// Example: E1119
  /// </summary>
  public int Henshall = -1;
  public String HenshallStr()
  {
    return (this.Henshall == -1) ? "n/a" : Integer.toString(this.Henshall);
  }

  /// <summary>
  /// Kanji number in "Kanji Learners Dictionary" by Jack Halpern.
  /// Example: DK712
  /// </summary>
  public int KanjiLearnersDictionary = -1;
  public String KanjiLearnersDictionaryStr()
  { 
    return (this.KanjiLearnersDictionary == -1) ? "n/a" : Integer.toString(this.KanjiLearnersDictionary);
  }

  /// <summary>
  /// Kanji number in "Modern Reader's Japanese-English Character Dictionary" by Andrew N. Nelson.
  /// Example: N5122
  /// </summary>
  public int Nelson = -1;
  public String NelsonStr()
  { 
    return (this.Nelson == -1) ? "n/a" : Integer.toString(this.Nelson);
  }

  /// <summary>
  /// Kanji number in "The New Nelson Japanese-English Character Dictionary" by John H. Haig. 
  /// Example: V6624
  /// </summary>
  public int NewNelson = -1;
  public String NewNelsonStr()
  { 
    return (this.NewNelson == -1) ? "n/a" : Integer.toString(this.NewNelson);
  }

  /// <summary>
  /// The PinYin (Chinese) pronunciation(s) of the kanji.
  /// Example: Ywan2
  /// </summary>
  public String PinYin = "";
  public String PinYinFormatted()
  { 
    return (this.PinYin.length() == 0) ? "n/a" : this.PinYin;
  }
  
  /// <summary>
  /// Primative elements that make up the kanji (heisig+community).
  /// Example: beginning,two,human legs,head,page,one,ceiling,drop,shellfish,clam,oyster,eye,animal legs,eight
  /// </summary>
  public String Primatives = "";

  /// <summary>
  /// Indexes into radical.dat. The first index is the main radical.
  /// Example: B181 (this line number in radicals.dat that contains the main radical)
  /// </summary>
  public List<Integer> Radicals = new ArrayList<Integer>();

  /// <summary>
  /// The "SKIP" coding of the kanji, as used in Halpern.
  /// Example: P1-4-9
  /// </summary>
  public String SkipPattern = "";
  public String SkipPatternFormatted()
  { 
    return (this.SkipPattern.length() == 0) ? "n/a" : this.SkipPattern;
  }
  
  /// <summary>
  /// Stroke count.
  /// Example: S13
  /// </summary>
  public int Strokes = -1;
  public String StrokesStr()
  { 
    return (this.Strokes == -1) ? "n/a" : Integer.toString(this.Strokes);
  }

  /// <summary>
  // Kanji number in "Japanese Kanji & Kana: A Complete Guide to the Japanese Writing System" by Wolfgang Hadamitzky and Mark Spahn.
  /// Example: IN1848
  /// </summary>
  public String TuttleKanjiAndKana = "";
  public String TuttleKanjiAndKanaFormatted()
  { 
    return (this.TuttleKanjiAndKana.length() == 0) ? "n/a" : this.TuttleKanjiAndKana;
  }

  /// <summary>
  /// Kanji number in "The Kanji Dictionary" by Mark Spahn and  Wolfgang Hadamitzky.
  /// Example: I9a4.6
  /// </summary>
  public String TuttleKanjiDic = "";
  public String TuttleKanjiDicFormatted()
  { 
    return (this.TuttleKanjiDic.length() == 0) ? "n/a" : this.TuttleKanjiDic;
  }
  
  /// <summary>
  /// The unicode/ISO 10646 code of the kanji.
  /// Example: 0x9811
  /// </summary>
  public int Unicode = -1;
  public String UnicodeStr()
  { 
    return (this.Unicode == -1) ? "n/a" : Integer.toHexString(Unicode);
  }
}
