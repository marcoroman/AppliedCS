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

import java.util.HashMap;
import java.util.Random;


public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String s) {
        TrieNode node = this;
        Character c;

        for(int i = 0; i < s.length(); ++i){
            c = s.charAt(i);

            if(node.children.containsKey(c.toString())){
                node = node.children.get(c.toString());
            }else{
                node.children.put(c.toString(), new TrieNode());
                node = node.children.get(c.toString());
            }
        }

        node.isWord = true;
    }

    public boolean isWord(String s) {
        TrieNode node = this;
        Character c;

        for(int i = 0; i < s.length(); ++i){
            c = s.charAt(i);

            if(node.children.containsKey(c.toString())){
                node = node.children.get(c.toString());
            }else{
                return false;
            }
        }

        if(node.isWord){
            return true;
        }else{
            return false;
        }
    }

    public String getAnyWordStartingWith(String prefix) {
        TrieNode node = this;
        String word = "";
        Character c;

        Random gen = new Random();

        for(int i = 0; i < prefix.length(); ++i){
            c = prefix.charAt(i);

            if(node.children.containsKey(c.toString())){
                node = node.children.get(c.toString());
                word += c;
            }else{
                return null;
            }
        }

        while(!node.isWord){
            int select = gen.nextInt(node.children.size());
            word += node.children.keySet().toArray()[select];
            node = node.children.get(node.children.keySet().toArray()[select].toString());
        }

        return word;
    }

    public String getGoodWordStartingWith(String prefix) {
        TrieNode node = this;
        String word = "";
        Character c;

        Random gen = new Random();

        if(prefix.isEmpty()) {
            return (String) node.children.keySet().toArray()[gen.nextInt(node.children.size())];
        }

        for(int i = 0; i < prefix.length(); ++i){
            c = prefix.charAt(i);

            if(node.children.containsKey(c.toString())){
                node = node.children.get(c.toString());
                word += c;
            }else{
                return null;
            }
        }

        int i = 0;

        while(i < node.children.size()){
            word += node.children.keySet().toArray()[i];

            if(!node.children.get(node.children.keySet().toArray()[i]).isWord){
                return word;
            }

            word = word.substring(0, word.length() - 1);
            ++i;
        }

        word += node.children.keySet().toArray()[gen.nextInt(node.children.size())];

        return word;
    }
}