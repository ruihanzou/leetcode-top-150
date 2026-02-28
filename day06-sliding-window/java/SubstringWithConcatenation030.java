/**
 * LeetCode 30. Substring with Concatenation of All Words
 * 难度: Hard
 *
 * 题目描述：
 * 给定一个字符串 s 和一个字符串数组 words。words 中所有字符串长度相同。
 * s 中的串联子串是指一个包含 words 中所有字符串以任意顺序排列连接起来的子串。
 * 返回所有串联子串在 s 中的起始索引。你可以以任意顺序返回答案。
 *
 * 示例 1：
 *   输入：s = "barfoothefoobarman", words = ["foo","bar"]
 *   输出：[0,9]
 *   解释：
 *     从索引 0 开始的子串是 "barfoo"，它是 ["bar","foo"] 的串联。
 *     从索引 9 开始的子串是 "foobar"，它是 ["foo","bar"] 的串联。
 *
 * 示例 2：
 *   输入：s = "wordgoodgoodgoodbestword", words = ["word","good","best","word"]
 *   输出：[8]
 *
 * 示例 3：
 *   输入：s = "barfoofoobarthefoobarman", words = ["bar","foo","the"]
 *   输出：[6,9,12]
 *
 * 【拓展练习】
 * 1. LeetCode 76. Minimum Window Substring —— 滑动窗口 + 字符计数
 * 2. LeetCode 438. Find All Anagrams in a String —— 固定窗口找变位词
 * 3. LeetCode 567. Permutation in String —— 判断是否包含排列
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

class SubstringWithConcatenation030 {

    /**
     * ==================== 解法一：暴力逐位检查 ====================
     *
     * 【核心思路】
     * 枚举 s 中每一个可能的起始位置 i，从 i 开始每次截取 wordLen 长度的子串，
     * 连续截取 wordCount 个，检查这些子串是否恰好构成 words 的一个排列。
     *
     * 【思考过程】
     * 1. 串联子串的总长度 = wordCount * wordLen，
     *    所以起始位置 i 的范围是 [0, s.length() - totalLen]。
     *
     * 2. 对每个 i，用一个临时计数器 seen，逐个截取长度 wordLen 的子串，
     *    如果该子串不在 words 中或者出现次数超过 words 中的次数，就提前终止。
     *
     * 3. 如果成功截取了 wordCount 个子串，说明 i 是一个合法起始位置。
     *
     * 【举例】s = "barfoothefoobarman", words = ["foo","bar"]
     *   wordLen=3, wordCount=2, totalLen=6
     *   wordFreq = {"foo":1, "bar":1}
     *
     *   i=0: 截取 "bar"(✓), "foo"(✓), 共2个 → 合法，加入结果
     *   i=1: 截取 "arf" → 不在words中，跳过
     *   ...
     *   i=9: 截取 "foo"(✓), "bar"(✓), 共2个 → 合法，加入结果
     *   结果 = [0, 9]
     *
     * 【时间复杂度】O(n * m * k) —— n=s.length(), m=wordCount, k=wordLen
     * 【空间复杂度】O(m * k) —— 计数器空间
     */
    public List<Integer> findSubstringBrute(String s, String[] words) {
        List<Integer> result = new ArrayList<>();
        if (s == null || s.isEmpty() || words == null || words.length == 0) {
            return result;
        }

        int wordLen = words[0].length();
        int wordCount = words.length;
        int totalLen = wordLen * wordCount;

        Map<String, Integer> wordFreq = new HashMap<>();
        for (String w : words) {
            wordFreq.merge(w, 1, Integer::sum);
        }

        for (int i = 0; i <= s.length() - totalLen; i++) {
            Map<String, Integer> seen = new HashMap<>();
            int j = 0;
            while (j < wordCount) {
                int start = i + j * wordLen;
                String word = s.substring(start, start + wordLen);
                if (!wordFreq.containsKey(word)) {
                    break;
                }
                seen.merge(word, 1, Integer::sum);
                if (seen.get(word) > wordFreq.get(word)) {
                    break;
                }
                j++;
            }
            if (j == wordCount) {
                result.add(i);
            }
        }

        return result;
    }

    /**
     * ==================== 解法二：滑动窗口（按单词长度分组） ====================
     *
     * 【核心思路】
     * 将所有起始位置按 i % wordLen 分成 wordLen 组。
     * 每组内，以 wordLen 为步长滑动窗口，窗口内维护单词计数。
     * 窗口右端加入一个新单词时更新计数，窗口内单词数超过 wordCount 时左端弹出。
     *
     * 【思考过程】
     * 1. 暴力解法中，相邻起始位置 i 和 i+1 的检查窗口有大量重叠，重复工作太多。
     *
     * 2. 关键观察：如果我们把 s 按 wordLen 为单位分段，那么一个合法的串联子串
     *    一定是从某一段的起点开始，连续取 wordCount 段。
     *
     * 3. 起始偏移量只有 wordLen 种（0, 1, ..., wordLen-1）。
     *    对于每种偏移 offset，我们把 s 切成一段一段的"单词"：
     *    s[offset..offset+wl), s[offset+wl..offset+2*wl), ...
     *
     * 4. 然后在这些"单词"上做滑动窗口：维护一个大小为 wordCount 的窗口，
     *    右端加入新单词，左端必要时弹出多余单词。
     *
     * 5. 这样每个单词只被加入和弹出各一次，每组时间 O(n/wordLen)，
     *    共 wordLen 组，总时间 O(n)。
     *
     * 【举例】s = "barfoothefoobarman", words = ["foo","bar"], wordLen=3
     *   offset=0: 分段 ["bar","foo","the","foo","bar","man"]
     *     right=0 "bar": window={"bar":1}, matched=1
     *     right=1 "foo": window={"bar":1,"foo":1}, matched=2==wordCount
     *             → 起始=0 ✓, 弹left "bar": matched=1
     *     right=2 "the": 不在words中 → 清空, left跳过
     *     right=3 "foo": window={"foo":1}, matched=1
     *     right=4 "bar": window={"foo":1,"bar":1}, matched=2
     *             → 起始=9 ✓, 弹left "foo": matched=1
     *     right=5 "man": 不在words中 → 清空
     *   结果 = [0, 9]
     *
     * 【时间复杂度】O(n * k) —— 外层 wordLen 组，每组 O(n/wordLen) 个单词，
     *                           每个单词截取 O(k)，总计 O(n*k)
     * 【空间复杂度】O(m) —— 窗口计数器最多 m=wordCount 个条目
     */
    public List<Integer> findSubstringSliding(String s, String[] words) {
        List<Integer> result = new ArrayList<>();
        if (s == null || s.isEmpty() || words == null || words.length == 0) {
            return result;
        }

        int wordLen = words[0].length();
        int wordCount = words.length;

        Map<String, Integer> wordFreq = new HashMap<>();
        for (String w : words) {
            wordFreq.merge(w, 1, Integer::sum);
        }

        for (int offset = 0; offset < wordLen; offset++) {
            int left = offset;
            Map<String, Integer> window = new HashMap<>();
            int matched = 0;

            for (int rightStart = offset; rightStart + wordLen <= s.length(); rightStart += wordLen) {
                String word = s.substring(rightStart, rightStart + wordLen);

                if (!wordFreq.containsKey(word)) {
                    window.clear();
                    matched = 0;
                    left = rightStart + wordLen;
                    continue;
                }

                window.merge(word, 1, Integer::sum);
                matched++;

                while (window.get(word) > wordFreq.get(word)) {
                    String leftWord = s.substring(left, left + wordLen);
                    window.merge(leftWord, -1, Integer::sum);
                    matched--;
                    left += wordLen;
                }

                if (matched == wordCount) {
                    result.add(left);
                }
            }
        }

        return result;
    }
}
