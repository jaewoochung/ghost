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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.List;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
                words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        // If prefix is empty return a randomly selected word from the words ArrayList
        // Otherwise perform a binary search over the words Arraylist until you find a word
        // that starts with the given prefix and return it
        // If no such words exists, return null
        Random random = new Random();
        if (prefix == null || prefix.isEmpty()) {
            int randomNum = random.nextInt(words.size());
            return words.get(randomNum);
        }

        // Perform binary search over the words ArrayList until you find word with prefix
        // words.txt is sorted keep that in mind
        int idx = Collections.binarySearch(words, prefix, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1.startsWith(o2)) {
                    return 0;
                }
                for (int i = 0; i < Math.min(o1.length(), o2.length()); i++) {
                    if (o1.charAt(i) < o2.charAt(i)) {
                        return -1;
                    }
                    if (o1.charAt(i) > o2.charAt(i)) {
                        return 1;
                    }
                }
                return -1;
            }
        });

        return idx < 0 ? null : words.get(idx);

    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        // binary search to determine the whole range of words that start with given prefix
        // divide words to even and odd lengths
        // randomly select appropriate set
        Random random = new Random();
        if (prefix == null || prefix.isEmpty()) {
            int randomNum = random.nextInt(words.size());
            return words.get(randomNum);
        }

        int lowerBoundary = lowerBoundary(prefix);
        List<String> evenLeftString = new ArrayList<>();
        String loseWord = null;
        while (lowerBoundary < words.size() && words.get(lowerBoundary).startsWith(prefix)) {
            if ((words.get(lowerBoundary).length() - prefix.length()) % 2 == 0) {
                evenLeftString.add(words.get(lowerBoundary));
            }
            if (loseWord == null) {
                loseWord = words.get(lowerBoundary);
            }
            lowerBoundary++;
        }
        Random random2 = new Random();
        return evenLeftString.size() > 0 ? evenLeftString.get(random2.nextInt(evenLeftString.size())) : loseWord;

    }

    public int lowerBoundary(String prefix) {
        int left = 0, right = words.size();
        while (left < right) {
            int middle = (left + right) >> 1;
            String word = words.get(middle);
            int cmp = word.compareTo(prefix);
            if (cmp < 0) {
                left = middle + 1;
            }
            else {
                right = middle;
            }
        }
        return left;
    }
}
