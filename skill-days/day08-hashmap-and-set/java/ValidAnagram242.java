/**
 * LeetCode 242. Valid Anagram
 * 难度: Easy
 *
 * 题目描述：
 * 给定两个字符串 s 和 t，判断 t 是否是 s 的字母异位词。
 * 字母异位词是指由相同的字母重新排列组成的字符串，每个字母的使用次数必须相同。
 *
 * 示例 1：s = "anagram", t = "nagaram" → 输出 true
 * 示例 2：s = "rat", t = "car" → 输出 false
 *
 * 进阶：如果输入字符串包含 unicode 字符怎么办？
 *
 * 【拓展练习】
 * 1. LeetCode 49. Group Anagrams —— 将字母异位词分组，需要设计合适的分组键
 * 2. LeetCode 438. Find All Anagrams in a String —— 滑动窗口查找所有异位词子串
 * 3. LeetCode 567. Permutation in String —— 判断一个字符串的排列是否是另一个的子串
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class ValidAnagram242 {

    /**
     * ==================== 解法一：排序比较 ====================
     *
     * 【核心思路】
     * 将两个字符串排序后直接比较，若相等则互为字母异位词。
     *
     * 【思考过程】
     * 1. 字母异位词由完全相同的字母组成，只是排列顺序不同。
     *
     * 2. 排序会消除顺序差异，如果排序后的字符串相同，
     *    说明它们包含完全相同的字符且每个字符出现次数相同。
     *
     * 3. 这是最直观的解法，代码简洁，但时间复杂度受排序限制。
     *
     * 【举例】s = "anagram", t = "nagaram"
     *   排序后：s → "aaagmnr", t → "aaagmnr"
     *   相等 → true
     *
     * 【时间复杂度】O(n log n)，排序主导
     * 【空间复杂度】O(n)，排序用到的额外空间
     */
    public boolean isAnagramSort(String s, String t) {
        if (s.length() != t.length()) return false;
        char[] sa = s.toCharArray();
        char[] ta = t.toCharArray();
        Arrays.sort(sa);
        Arrays.sort(ta);
        return Arrays.equals(sa, ta);
    }

    /**
     * ==================== 解法二：哈希计数 ====================
     *
     * 【核心思路】
     * 用 HashMap 统计 s 中每个字符的频次，然后遍历 t 逐个减去。
     * 最后检查所有频次是否归零。
     *
     * 【思考过程】
     * 1. 字母异位词等价于"两个字符串中每种字符的出现次数完全相同"。
     *
     * 2. 用 HashMap 记录字符频次差异：s 中出现的 +1，t 中出现的 -1。
     *    如果最终所有计数为 0，说明完全匹配。
     *
     * 3. 适用于 unicode 字符的情况（进阶问题），因为 HashMap 可以存储任意字符。
     *
     * 【举例】s = "rat", t = "car"
     *   统计 s：{r:1, a:1, t:1}
     *   消耗 t：c → {r:1, a:1, t:1, c:-1}  出现负数 → false
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)，最多 26 个字母（或 O(n) 对于 unicode）
     */
    public boolean isAnagramHash(String s, String t) {
        if (s.length() != t.length()) return false;

        Map<Character, Integer> count = new HashMap<>();
        for (char c : s.toCharArray()) {
            count.merge(c, 1, Integer::sum);
        }
        for (char c : t.toCharArray()) {
            int val = count.merge(c, -1, Integer::sum);
            if (val < 0) return false;
        }
        return true;
    }

    /**
     * ==================== 解法三：数组计数 ====================
     *
     * 【核心思路】
     * 用长度 26 的 int 数组统计字符频次差异，
     * s 中出现的字符 +1，t 中出现的字符 -1，最终检查数组全为 0。
     *
     * 【思考过程】
     * 1. 题目限定小写英文字母，字符范围固定为 26 个，
     *    数组比 HashMap 在常数因子上更优。
     *
     * 2. 用 +1 和 -1 的抵消策略：遍历 s 时 count[c-'a']++，
     *    遍历 t 时 count[c-'a']--，最终全为 0 则是异位词。
     *
     * 3. 可以优化为单次遍历：同时遍历 s 和 t，一边加一边减。
     *
     * 【举例】s = "anagram", t = "nagaram"
     *   遍历 s 后：a:3, g:1, m:1, n:1, r:1
     *   遍历 t 抵消：a:3-3=0, g:1-1=0, m:1-1=0, n:1-1=0, r:1-1=0
     *   全为 0 → true
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)，固定 26 长度数组
     */
    public boolean isAnagramArray(String s, String t) {
        if (s.length() != t.length()) return false;

        int[] count = new int[26];
        for (int i = 0; i < s.length(); i++) {
            count[s.charAt(i) - 'a']++;
            count[t.charAt(i) - 'a']--;
        }
        for (int c : count) {
            if (c != 0) return false;
        }
        return true;
    }
}
