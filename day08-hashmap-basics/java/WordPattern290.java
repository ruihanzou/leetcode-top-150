/**
 * LeetCode 290. Word Pattern
 * 难度: Easy
 *
 * 题目描述：
 * 给定一种规律 pattern 和一个字符串 s，判断 s 是否遵循相同的规律。
 * 这里的"遵循"指完全匹配：pattern 中每个字母和 s 中每个非空单词之间存在双向映射关系。
 *
 * 示例 1：pattern = "abba", s = "dog cat cat dog" → 输出 true
 * 示例 2：pattern = "abba", s = "dog cat cat fish" → 输出 false
 * 示例 3：pattern = "aaaa", s = "dog cat cat dog" → 输出 false
 *
 * 【拓展练习】
 * 1. LeetCode 205. Isomorphic Strings —— 字符级别的同构映射，思路完全一致
 * 2. LeetCode 291. Word Pattern II —— 本题的进阶版，需要回溯搜索所有可能的映射
 * 3. LeetCode 890. Find and Replace Pattern —— 同构模式匹配，找出所有匹配的单词
 */

import java.util.HashMap;
import java.util.Map;

class WordPattern290 {

    /**
     * ==================== 解法一：双向哈希映射 ====================
     *
     * 【核心思路】
     * 将 s 按空格分割成单词数组，建立 pattern 字符 → 单词 和 单词 → pattern 字符
     * 的双向映射，任何不一致都返回 false。
     *
     * 【思考过程】
     * 1. pattern 的每个字符对应 s 中的一个单词，这是一对一的双射关系。
     *
     * 2. 首先检查 pattern 长度是否等于单词数，不等则直接 false。
     *
     * 3. 同时维护两个映射（char→word, word→char），
     *    确保既不出现"一个字符映射到多个单词"，
     *    也不出现"多个字符映射到同一个单词"。
     *
     * 【举例】pattern = "abba", s = "dog cat cat dog"
     *   i=0: a→dog, dog→a  ✓
     *   i=1: b→cat, cat→b  ✓
     *   i=2: b 已映射 cat，words[2]=cat 一致 ✓
     *   i=3: a 已映射 dog，words[3]=dog 一致 ✓
     *   → true
     *
     * 【时间复杂度】O(n)，n 为 pattern 长度（或单词数）
     * 【空间复杂度】O(n)，两个映射表
     */
    public boolean wordPatternTwoMap(String pattern, String s) {
        String[] words = s.split(" ");
        if (pattern.length() != words.length) return false;

        Map<Character, String> charToWord = new HashMap<>();
        Map<String, Character> wordToChar = new HashMap<>();

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            String w = words[i];

            if (charToWord.containsKey(c) && !charToWord.get(c).equals(w)) return false;
            if (wordToChar.containsKey(w) && wordToChar.get(w) != c) return false;

            charToWord.put(c, w);
            wordToChar.put(w, c);
        }
        return true;
    }

    /**
     * ==================== 解法二：编码转换 ====================
     *
     * 【核心思路】
     * 将 pattern 和 s 都转换为"首次出现位置"的索引序列，
     * 比较两个序列是否完全一致。
     *
     * 【思考过程】
     * 1. 双射关系的本质是"结构相同"。我们可以把字符/单词编码为
     *    "第一次在哪个位置出现"的序列。
     *    例如 pattern = "abba" → [0,1,1,0]
     *         words = ["dog","cat","cat","dog"] → [0,1,1,0]
     *    两个序列相同 → 匹配。
     *
     * 2. 用 HashMap 记录每个字符/单词第一次出现的位置索引，
     *    遍历时直接比较两者的首次出现位置。
     *
     * 3. 与解法一相比，这种方法不需要显式建立双向映射，思路更统一。
     *
     * 【举例】pattern = "abba", s = "dog cat cat fish"
     *   pattern 编码：a→0, b→1, b→1, a→0 → [0,1,1,0]
     *   words  编码：dog→0, cat→1, cat→1, fish→3 → [0,1,1,3]
     *   [0,1,1,0] ≠ [0,1,1,3] → false
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(n)
     */
    public boolean wordPatternEncode(String pattern, String s) {
        String[] words = s.split(" ");
        if (pattern.length() != words.length) return false;

        Map<Character, Integer> charIndex = new HashMap<>();
        Map<String, Integer> wordIndex = new HashMap<>();

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            String w = words[i];

            int ci = charIndex.getOrDefault(c, -1);
            int wi = wordIndex.getOrDefault(w, -1);
            if (ci != wi) return false;

            charIndex.put(c, i);
            wordIndex.put(w, i);
        }
        return true;
    }
}
