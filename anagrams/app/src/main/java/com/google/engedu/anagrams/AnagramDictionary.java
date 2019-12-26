/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import android.util.Log;
import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private int wordLength;

    private ArrayList<String> wordList;
    private HashSet<String> wordSet;
    private HashMap<String, ArrayList<String>> lettersToWord;
    private SparseArray<ArrayList<String>> sizeToWords;

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        wordList = new ArrayList<>();
        wordSet = new HashSet<>();
        lettersToWord = new HashMap<>();
        sizeToWords = new SparseArray<>();
        wordLength = DEFAULT_WORD_LENGTH;

        while ((line = in.readLine()) != null) {
            String word = line.trim();
            wordList.add(word);
            wordSet.add(word);

            String sortedWord = sortLetters(word);

            if (lettersToWord.containsKey(sortedWord)) {
                lettersToWord.get(sortedWord).add(word);
            } else {
                ArrayList<String> anagrams = new ArrayList<>();
                anagrams.add(word);
                lettersToWord.put(sortedWord, anagrams);
            }

//            if(sizeToWords.containsKey(word.length())){
//                sizeToWords.get(word.length()).add(word);
//            }else {
//                ArrayList<String> words = new ArrayList<>();
//                words.add(word);
//                sizeToWords.put(word.length(), words);
//            }

            if(sizeToWords.get(word.length()) != null){
                sizeToWords.get(word.length()).add(word);
            }else {
                ArrayList<String> words = new ArrayList<>();
                words.add(word);
                sizeToWords.append(word.length(), words);
            }
        }
    }

    public boolean isGoodWord(String word, String base) {

        if(wordSet.contains(word) && !word.contains(base)){
            return true;
        }

        return false;
    }

    /*public List<String> getAnagrams(String targetWord) {

        String sorted = sortLetters(targetWord);
        ArrayList<String> result = lettersToWord.get(sorted);
        return result;
    }*/

    private String sortLetters(String word) {
        char[] sorted = word.toCharArray();
        Arrays.sort(sorted);
        return String.valueOf(sorted);
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();

        for (int i = 0; i < 26; i++) {
            String newWord = word + (char) ('a' + i);
            String sortedNewWord = sortLetters(newWord);

            if(lettersToWord.containsKey(sortedNewWord) && !lettersToWord.get(sortedNewWord).isEmpty()){
                ArrayList<String> anagrams = lettersToWord.get(sortedNewWord);

                for (String anagram : anagrams){
                    if(isGoodWord(anagram, word)){
                        Log.e("ANAGRAMS", anagram);
                        result.add(anagram);
                    }
                }
            }
        }

        return result;
    }

    public String pickGoodStarterWord() {

        ArrayList<String> words = sizeToWords.get(wordLength);
        int startPoint;

        while(true){
            startPoint = random.nextInt(words.size());
            int numberOfAnagrams = getAnagramsWithOneMoreLetter(words.get(startPoint)).size();

            if(numberOfAnagrams >= MIN_NUM_ANAGRAMS){
                break;
            }
        }

        if(wordLength < MAX_WORD_LENGTH){
            wordLength++;
        }

        return words.get(startPoint);
    }
}
