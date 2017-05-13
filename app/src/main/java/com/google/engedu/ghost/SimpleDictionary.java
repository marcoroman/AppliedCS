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

package com.google.engedu.ghost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words, evens, odds;
    private Random gen = new Random();

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        evens = new ArrayList<>();
        odds = new ArrayList<>();

        String line = null;

        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH) {
                words.add(line.trim());

                if(word.length() % 2 == 0){
                    evens.add(line.trim());
                }else{
                    odds.add(line.trim());
                }
            }
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        String word = null;

        if(prefix.isEmpty()){
            return words.get(gen.nextInt(words.size() - 1));
        }else{
            word = binarySearch(words, prefix);
        }

        return word;
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        String selected = null;

        if(prefix.isEmpty()){
            return words.get(gen.nextInt(words.size() - 1));
        }else{
            if(GhostActivity.getTurn()) {
                selected = binarySearch(odds, prefix);
            }else {
                selected = binarySearch(evens, prefix);
            }
        }

        return selected;
    }

    public String binarySearch(ArrayList<String> w, String prefix){
        int low = 0; int high = w.size() - 1;
        String word = null;

        while(high >= low){
            int middle = (high + low) / 2;

            if(w.get(middle).startsWith(prefix)){
                word = w.get(middle);
                break;
            }else if(w.get(middle).compareTo(prefix) < 0){
                low = middle + 1;
            }else{
                high = middle - 1;
            }
        }

        return word;
    }
}