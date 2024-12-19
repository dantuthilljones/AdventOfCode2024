package me.detj.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Trie {

    private final TrieNode root;

    public List<String> getPrefixingWords(String prefix) {
        List<String> prefixingWords = new ArrayList<>();

        TrieNode current = root;
        for(char c : prefix.toCharArray()) {
            current = current.getChildren().get(c);
            if (current == null) {
                return prefixingWords;
            }
            if(current.isEndOfWord) {
                prefixingWords.add(current.word);
            }
        }
        return prefixingWords;
    }

    @Data
    @AllArgsConstructor
    private static class TrieNode {
        @NonNull
        final Map<Character, TrieNode> children;
        String word;
        boolean isEndOfWord;
    }

    public static Trie build(List<String> strings) {
        TrieNode root = new TrieNode(new HashMap<>(), null,false);

        for (String string : strings) {
            TrieNode current = root;
            for (char c : string.toCharArray()) {
                current = current.getChildren().computeIfAbsent(c, k -> new TrieNode(new HashMap<>(), null,false));
            }
            current.setWord(string);
            current.setEndOfWord(true);
        }

        return new Trie(root);
    }
}
