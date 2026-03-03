/**
 * LeetCode 17. Letter Combinations of a Phone Number
 * 难度: Medium
 *
 * 题目描述：
 * 给定一个仅包含数字 2-9 的字符串，返回所有它能表示的字母组合。答案可以按任意顺序返回。
 * 数字到字母的映射与电话按键相同：
 * 2→abc, 3→def, 4→ghi, 5→jkl, 6→mno, 7→pqrs, 8→tuv, 9→wxyz
 *
 * 示例：
 * 输入: digits = "23"
 * 输出: ["ad","ae","af","bd","be","bf","cd","ce","cf"]
 *
 * 【拓展练习】
 * 1. LeetCode 22. Generate Parentheses —— 类似的回溯生成所有合法组合
 * 2. LeetCode 39. Combination Sum —— 回溯选数，组合类问题
 * 3. LeetCode 401. Binary Watch —— 数字映射 + 枚举组合
 */

import java.util.*;

class LetterCombinations017 {

    private static final String[] MAPPING = {
        "", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"
    };

    /**
     * ==================== 解法一：回溯 ====================
     *
     * 【核心思路】
     * 对 digits 中的每个数字，从其对应的字母集合中选择一个字母，
     * 递归处理下一个数字，直到所有数字都处理完毕，将当前路径加入结果。
     *
     * 【思考过程】
     * 1. 这是一个典型的"逐位选择"问题：第一个数字选一个字母，
     *    第二个数字选一个字母……每一步的选择独立于前一步选了什么。
     *
     * 2. 用回溯框架：
     *    - 状态：当前处理到第几个数字（index），以及已选的字母路径（path）。
     *    - 选择列表：当前数字对应的所有字母。
     *    - 终止条件：index == digits.length()，路径长度等于数字个数。
     *
     * 3. 因为每一步的选择之间没有冲突（不像排列需要去重），
     *    所以不需要 visited 数组，直接遍历当前数字的字母即可。
     *
     * 【举例】digits = "23"
     *   数字映射: 2→"abc", 3→"def"
     *
     *   index=0, 数字'2', 遍历"abc":
     *     选'a' → index=1, 数字'3', 遍历"def":
     *       选'd' → index=2, 路径="ad", 加入结果
     *       选'e' → index=2, 路径="ae", 加入结果
     *       选'f' → index=2, 路径="af", 加入结果
     *     选'b' → index=1, 数字'3', 遍历"def":
     *       选'd' → "bd", 选'e' → "be", 选'f' → "bf"
     *     选'c' → 同理 → "cd", "ce", "cf"
     *
     *   结果: ["ad","ae","af","bd","be","bf","cd","ce","cf"]
     *
     * 【时间复杂度】O(3^m * 4^n)  m 是映射到3个字母的数字个数，n 是映射到4个字母的数字个数
     * 【空间复杂度】O(m + n) 递归栈深度等于 digits 长度
     */
    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits == null || digits.isEmpty()) return result;
        backtrack(digits, 0, new StringBuilder(), result);
        return result;
    }

    private void backtrack(String digits, int index, StringBuilder path, List<String> result) {
        if (index == digits.length()) {
            result.add(path.toString());
            return;
        }
        String letters = MAPPING[digits.charAt(index) - '0'];
        for (char c : letters.toCharArray()) {
            path.append(c);
            backtrack(digits, index + 1, path, result);
            path.deleteCharAt(path.length() - 1);
        }
    }

    /**
     * ==================== 解法二：BFS / 迭代（队列逐层扩展） ====================
     *
     * 【核心思路】
     * 把组合过程看作"逐层扩展"：初始队列里放空串，
     * 每处理一个数字，就把队列中现有的所有字符串取出来，
     * 分别拼上该数字对应的每个字母，再放回队列。
     * 处理完所有数字后，队列中的就是所有组合。
     *
     * 【思考过程】
     * 1. 回溯是 DFS 思路，换成 BFS 也很自然：
     *    - 第0层：[""]
     *    - 处理数字'2'(abc) → 第1层：["a","b","c"]
     *    - 处理数字'3'(def) → 第2层：["ad","ae","af","bd","be","bf","cd","ce","cf"]
     *
     * 2. 每一层的大小 = 上一层大小 × 当前数字的字母数。
     *    用队列（或列表）存储当前层的所有字符串，逐层替换即可。
     *
     * 3. 这种方法不需要递归，代码更简洁，但需要同时存储所有中间字符串，
     *    空间开销比回溯大。
     *
     * 【举例】digits = "23"
     *   初始: result = [""]
     *   处理'2'(abc):
     *     取出"", 拼上 a/b/c → ["a","b","c"]
     *   处理'3'(def):
     *     取出"a", 拼上 d/e/f → "ad","ae","af"
     *     取出"b", 拼上 d/e/f → "bd","be","bf"
     *     取出"c", 拼上 d/e/f → "cd","ce","cf"
     *   结果: ["ad","ae","af","bd","be","bf","cd","ce","cf"]
     *
     * 【时间复杂度】O(3^m * 4^n)
     * 【空间复杂度】O(3^m * 4^n) 需要存储所有中间结果
     */
    public List<String> letterCombinationsBFS(String digits) {
        List<String> result = new ArrayList<>();
        if (digits == null || digits.isEmpty()) return result;

        result.add("");
        for (char digit : digits.toCharArray()) {
            String letters = MAPPING[digit - '0'];
            List<String> next = new ArrayList<>();
            for (String existing : result) {
                for (char c : letters.toCharArray()) {
                    next.add(existing + c);
                }
            }
            result = next;
        }

        return result;
    }
}
