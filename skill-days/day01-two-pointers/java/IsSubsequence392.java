/**
 * LeetCode 392. Is Subsequence
 * 难度: Easy
 *
 * 题目描述：
 * 给定字符串 s 和 t，判断 s 是否为 t 的子序列。
 * 字符串的一个子序列是原始字符串删除一些（也可以不删除）字符而不改变剩余字符
 * 相对位置形成的新字符串。（例如，"ace" 是 "abcde" 的一个子序列，而 "aec" 不是）
 *
 * 示例 1：s = "abc", t = "ahbgdc" → 输出 true
 * 示例 2：s = "axc", t = "ahbgdc" → 输出 false
 *
 * 进阶：如果有大量输入的 s（记作 s1, s2, ..., sk，其中 k >= 10亿），
 * 你需要依次检查它们是否为 t 的子序列。此时你会怎样改变代码？
 *
 * 【拓展练习】
 * 1. LeetCode 792. Number of Matching Subsequences —— 多个字符串同时判断子序列
 * 2. LeetCode 1143. Longest Common Subsequence —— 最长公共子序列（DP）
 * 3. LeetCode 524. Longest Word in Dictionary through Deleting —— 通过删除字母匹配最长单词
 */

import java.util.*;

class IsSubsequence392 {

    /**
     * ==================== 解法一：双指针 ====================
     *
     * 【核心思路】
     * 用两个指针分别遍历 s 和 t。当 s[i] == t[j] 时，i 和 j 都前进；
     * 否则只有 j 前进。最终如果 i 走完了 s，说明 s 是 t 的子序列。
     *
     * 【思考过程】
     * 1. 子序列要求字符按原顺序出现在 t 中，但不必连续。
     * 2. 贪心策略：对于 s 中的每个字符，在 t 中尽早找到匹配位置。
     *    因为越早匹配，留给后续字符的空间越大。
     * 3. 所以只需要一次顺序扫描 t，同时推进 s 的指针。
     *
     * 【举例】s = "abc", t = "ahbgdc"
     *   i=0,j=0: s[0]='a'==t[0]='a' ✓ → i=1,j=1
     *   i=1,j=1: s[1]='b'!=t[1]='h' → j=2
     *   i=1,j=2: s[1]='b'==t[2]='b' ✓ → i=2,j=3
     *   i=2,j=3: s[2]='c'!=t[3]='g' → j=4
     *   i=2,j=4: s[2]='c'!=t[4]='d' → j=5
     *   i=2,j=5: s[2]='c'==t[5]='c' ✓ → i=3
     *   i=3 == s.length → 返回 true
     *
     * 【时间复杂度】O(n)，n 为 t 的长度，最多遍历一次 t
     * 【空间复杂度】O(1)，仅用两个指针
     */
    public boolean isSubsequenceTwoPointer(String s, String t) {
        int i = 0, j = 0;
        while (i < s.length() && j < t.length()) {
            if (s.charAt(i) == t.charAt(j)) {
                i++;
            }
            j++;
        }
        return i == s.length();
    }

    /**
     * ==================== 解法二：递归 ====================
     *
     * 【核心思路】
     * 递归地判断：如果 s 为空则为子序列；否则在 t 中找到 s[0] 的位置，
     * 然后递归判断 s[1:] 是否为 t[pos+1:] 的子序列。
     *
     * 【思考过程】
     * 1. 子序列问题具有天然的递归结构：
     *    - 如果 s 为空，任何字符串都包含空子序列 → true
     *    - 如果 t 为空但 s 不为空 → false
     *    - 如果首字符相同，问题缩小为 s[1:] 和 t[1:] 的子问题
     *    - 如果首字符不同，问题缩小为 s 和 t[1:] 的子问题
     * 2. 这种方式代码直观，但有 O(n) 的递归栈开销。
     *
     * 【举例】s = "abc", t = "ahbgdc"
     *   isSubseq("abc","ahbgdc") → 'a'=='a' → isSubseq("bc","hbgdc")
     *   isSubseq("bc","hbgdc") → 'b'!='h' → isSubseq("bc","bgdc")
     *   isSubseq("bc","bgdc") → 'b'=='b' → isSubseq("c","gdc")
     *   isSubseq("c","gdc") → 'c'!='g' → isSubseq("c","dc")
     *   isSubseq("c","dc") → 'c'!='d' → isSubseq("c","c")
     *   isSubseq("c","c") → 'c'=='c' → isSubseq("","") → true
     *
     * 【时间复杂度】O(n)，n 为 t 的长度
     * 【空间复杂度】O(n)，递归栈深度最大为 t 的长度
     */
    public boolean isSubsequenceRecursive(String s, String t) {
        return helper(s, t, 0, 0);
    }

    private boolean helper(String s, String t, int i, int j) {
        if (i == s.length()) return true;
        if (j == t.length()) return false;
        if (s.charAt(i) == t.charAt(j)) {
            return helper(s, t, i + 1, j + 1);
        }
        return helper(s, t, i, j + 1);
    }

    /**
     * ==================== 解法三：二分查找（适用于大量查询的 follow-up） ====================
     *
     * 【核心思路】
     * 预处理 t：为每个字符记录其所有出现位置的索引列表。
     * 对于 s 中的每个字符，使用二分查找在对应索引列表中找到
     * 第一个大于当前位置的索引。
     *
     * 【思考过程】
     * 1. 如果有大量 s 需要判断（follow-up 场景），每次都遍历 t 太慢。
     * 2. 可以对 t 做一次预处理，记录每个字符出现的所有位置。
     * 3. 对 s 中的每个字符，在预处理的索引列表中二分查找"下一个可用位置"。
     * 4. 预处理 O(n) 一次，之后每个查询 O(m·log n)，m 是 s 的长度。
     *
     * 【举例】t = "ahbgdc"
     *   预处理索引：a→[0], h→[1], b→[2], g→[3], d→[4], c→[5]
     *   查询 s = "abc"：
     *     'a': 在 [0] 中找 > -1 的最小值 → 0, prev=0
     *     'b': 在 [2] 中找 > 0 的最小值 → 2, prev=2
     *     'c': 在 [5] 中找 > 2 的最小值 → 5, prev=5
     *     全部找到 → true
     *
     * 【时间复杂度】O(n + m·log n)，n 为 t 长度，m 为 s 长度
     * 【空间复杂度】O(n)，存储 t 中每个字符的索引列表
     */
    public boolean isSubsequenceBinarySearch(String s, String t) {
        Map<Character, List<Integer>> charIndices = new HashMap<>();
        for (int i = 0; i < t.length(); i++) {
            charIndices.computeIfAbsent(t.charAt(i), k -> new ArrayList<>()).add(i);
        }

        int prev = -1;
        for (char c : s.toCharArray()) {
            List<Integer> indices = charIndices.get(c);
            if (indices == null) return false;

            int pos = Collections.binarySearch(indices, prev + 1);
            if (pos < 0) pos = -pos - 1;
            if (pos >= indices.size()) return false;

            prev = indices.get(pos);
        }
        return true;
    }
}

/**
 * ==================== 进阶：大量 s 查询时的优化 ====================
 *
 * 【使用场景】
 * t 固定，需要依次判断 s1, s2, ..., sk（k >= 10^9）是否为 t 的子序列。
 *
 * 【核心思路】
 * 1. 构造函数中对 t 做一次 O(n) 预处理，建立「字符 → 出现位置列表」的索引
 * 2. 每个 isSubsequence(s) 查询只需 O(m·log n)，m 为 s 的长度
 *
 * 【与单次双指针的对比】
 * - 双指针：每次查询 O(n)，k 次共 O(k·n) —— t 每次都要完整遍历
 * - 预处理+二分：预处理 O(n) 一次，k 次查询共 O(k·m·log n)
 *
 * 【用法示例】
 *   SubsequenceChecker checker = new SubsequenceChecker("ahbgdc");
 *   checker.isSubsequence("abc");   // true
 *   checker.isSubsequence("axc");   // false
 */
class SubsequenceChecker {
    private final Map<Character, List<Integer>> charIndices;

    public SubsequenceChecker(String t) {
        charIndices = new HashMap<>();
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            charIndices.computeIfAbsent(c, k -> new ArrayList<>()).add(i);
        }
    }

    public boolean isSubsequence(String s) {
        int prev = -1;
        for (char c : s.toCharArray()) {
            List<Integer> indices = charIndices.get(c);
            if (indices == null) return false;

            int pos = Collections.binarySearch(indices, prev + 1);
            if (pos < 0) pos = -pos - 1;
            if (pos >= indices.size()) return false;

            prev = indices.get(pos);
        }
        return true;
    }
}
