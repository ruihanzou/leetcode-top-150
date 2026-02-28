/**
 * LeetCode 3. Longest Substring Without Repeating Characters
 * 难度: Medium
 *
 * 题目描述：
 * 给定一个字符串 s，请你找出其中不含有重复字符的最长子串的长度。
 *
 * 示例 1：
 *   输入：s = "abcabcbb"
 *   输出：3
 *   解释：因为无重复字符的最长子串是 "abc"，所以其长度为 3。
 *
 * 示例 2：
 *   输入：s = "bbbbb"
 *   输出：1
 *   解释：因为无重复字符的最长子串是 "b"，所以其长度为 1。
 *
 * 示例 3：
 *   输入：s = "pwwkew"
 *   输出：3
 *   解释：因为无重复字符的最长子串是 "wke"，所以其长度为 3。
 *   注意答案必须是子串的长度，"pwke" 是一个子序列而非子串。
 *
 * 【拓展练习】
 * 1. LeetCode 159. Longest Substring with At Most Two Distinct Characters —— 最多2种字符的最长子串
 * 2. LeetCode 340. Longest Substring with At Most K Distinct Characters —— 最多K种字符的最长子串
 * 3. LeetCode 992. Subarrays with K Different Integers —— 恰好K种不同整数的子数组个数
 */

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

class LongestSubstringWithoutRepeating003 {

    /**
     * ==================== 解法一：滑动窗口 + 哈希集合 ====================
     *
     * 【核心思路】
     * 维护一个窗口 [left, right]，用哈希集合 charSet 记录窗口内的所有字符。
     * right 不断右移，若新字符已在集合中，则持续移动 left 直到该字符被移除。
     * 每一步都更新最大长度。
     *
     * 【思考过程】
     * 1. 无重复字符的子串天然具有滑动窗口的性质：
     *    如果 s[left..right] 无重复，加入 s[right+1] 后若出现重复，
     *    只需把 left 往右移来消除重复，不会错过更优解。
     *
     * 2. 用集合维护窗口内字符，O(1) 判断是否重复。
     *
     * 3. right 每移动一次，while 循环可能移动 left 若干次，
     *    但 left 总共最多移动 n 次，所以总时间仍是 O(n)。
     *
     * 【举例】s = "abcabcbb"
     *   right=0 'a': set={a}, len=1
     *   right=1 'b': set={a,b}, len=2
     *   right=2 'c': set={a,b,c}, len=3
     *   right=3 'a': 'a'在set中 → 移left: 删'a', left=1, set={b,c}
     *            加'a', set={b,c,a}, len=3
     *   right=4 'b': 'b'在set中 → 移left: 删'b', left=2, set={c,a}
     *            加'b', set={c,a,b}, len=3
     *   ...最终答案 = 3
     *
     * 【时间复杂度】O(n) —— left 和 right 各最多走 n 步
     * 【空间复杂度】O(min(m, n)) —— m 是字符集大小
     */
    public int lengthOfLongestSubstringSet(String s) {
        Set<Character> charSet = new HashSet<>();
        int left = 0;
        int maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            while (charSet.contains(s.charAt(right))) {
                charSet.remove(s.charAt(left));
                left++;
            }
            charSet.add(s.charAt(right));
            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }

    /**
     * ==================== 解法二：滑动窗口 + 哈希表优化跳转 ====================
     *
     * 【核心思路】
     * 用哈希表 charIndex 记录每个字符最近一次出现的位置。
     * 当 right 遇到重复字符时，left 直接跳到 charIndex.get(s[right]) + 1，
     * 跳过中间所有不需要逐个删除的字符，减少 left 的移动次数。
     *
     * 【思考过程】
     * 1. 解法一中 left 需要一个一个右移来把重复字符从集合中删除。
     *    但实际上我们只需要知道"重复字符上次出现的位置"，直接跳过去即可。
     *
     * 2. 用哈希表 charIndex[c] = j 表示字符 c 上一次出现在位置 j。
     *    当 s[right] 在表中且 charIndex[s[right]] >= left 时，
     *    说明窗口内有重复，left 直接跳到 charIndex[s[right]] + 1。
     *
     * 3. 注意 left 只能往右跳不能往左，所以取 max(left, charIndex[...] + 1)。
     *
     * 4. 每次更新 charIndex[s[right]] = right。
     *
     * 【举例】s = "abba"
     *   right=0 'a': charIndex={a:0}, left=0, len=1
     *   right=1 'b': charIndex={a:0,b:1}, left=0, len=2
     *   right=2 'b': 'b'上次在1, left=max(0,1+1)=2, charIndex={a:0,b:2}, len=1
     *   right=3 'a': 'a'上次在0, 但0<left=2, 不跳, left=2, charIndex={a:3,b:2}, len=2
     *   答案 = 2
     *
     * 【时间复杂度】O(n) —— right 扫描一遍，left 只跳不回退
     * 【空间复杂度】O(min(m, n)) —— m 是字符集大小
     */
    public int lengthOfLongestSubstringMap(String s) {
        Map<Character, Integer> charIndex = new HashMap<>();
        int left = 0;
        int maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (charIndex.containsKey(c) && charIndex.get(c) >= left) {
                left = charIndex.get(c) + 1;
            }
            charIndex.put(c, right);
            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }
}
