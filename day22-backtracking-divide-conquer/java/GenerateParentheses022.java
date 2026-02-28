/**
 * LeetCode 22. Generate Parentheses
 * 难度: Medium
 *
 * 题目描述：
 * 数字 n 代表生成括号的对数，请你设计一个函数，用于能够生成所有可能的并且有效的括号组合。
 * 有效括号组合需满足：左括号必须以正确的顺序闭合。
 *
 * 示例 1：n = 3 → ["((()))","(()())","(())()","()(())","()()()"]
 * 示例 2：n = 1 → ["()"]
 *
 * 【拓展练习】
 * 1. LeetCode 17. Letter Combinations of a Phone Number —— 类似回溯枚举所有组合
 * 2. LeetCode 20. Valid Parentheses —— 用栈判断括号是否合法
 * 3. LeetCode 32. Longest Valid Parentheses —— 最长合法括号子串，DP/栈
 */

import java.util.ArrayList;
import java.util.List;

class GenerateParentheses022 {

    /**
     * ==================== 解法一：回溯 ====================
     *
     * 【核心思路】
     * 维护当前路径中剩余可放置的左括号数 left 和右括号数 right。
     * 每一步可以放左括号（如果 left > 0）或右括号（如果 right > left，
     * 保证任何前缀中左括号数 >= 右括号数）。
     *
     * 【思考过程】
     * 1. 合法括号串长度为 2n，由 n 个 '(' 和 n 个 ')' 组成。
     *    任意前缀中 '(' 的数量 >= ')' 的数量。
     *
     * 2. 用 left 表示还能放几个 '('，right 表示还能放几个 ')'。
     *    初始 left=n, right=n。
     *    - 放 '(' 的条件：left > 0
     *    - 放 ')' 的条件：right > left（已放的左括号比已放的右括号多）
     *
     * 3. 当 left == 0 && right == 0 时，构成一个完整的合法串，加入结果。
     *
     * 【举例】n = 2
     *   start: left=2, right=2, path=""
     *   放'(': left=1, right=2, path="("
     *     放'(': left=0, right=2, path="(("
     *       放')': left=0, right=1, path="(()"
     *         放')': left=0, right=0, path="(())" ✓
     *     放')': left=1, right=1, path="()"
     *       放'(': left=0, right=1, path="()("
     *         放')': left=0, right=0, path="()()" ✓
     *   结果: ["(())", "()()"]
     *
     * 【时间复杂度】O(4^n / √n)，第 n 个卡特兰数
     * 【空间复杂度】O(n) 递归栈深度
     */
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        backtrack(result, new StringBuilder(), n, n);
        return result;
    }

    private void backtrack(List<String> result, StringBuilder path, int left, int right) {
        if (left == 0 && right == 0) {
            result.add(path.toString());
            return;
        }
        if (left > 0) {
            path.append('(');
            backtrack(result, path, left - 1, right);
            path.deleteCharAt(path.length() - 1);
        }
        if (right > left) {
            path.append(')');
            backtrack(result, path, left, right - 1);
            path.deleteCharAt(path.length() - 1);
        }
    }

    /**
     * ==================== 解法二：动态规划 ====================
     *
     * 【核心思路】
     * dp[i] 存储 i 对括号的所有合法组合。
     * 递推关系：对于 i 对括号，可以拆分为 "(" + dp[j] + ")" + dp[i-1-j]，
     * 其中 j 从 0 到 i-1，表示最左边那个 '(' 和它匹配的 ')' 之间包含 j 对括号。
     *
     * 【思考过程】
     * 1. 任何合法括号串，最左边的 '(' 一定有一个唯一匹配的 ')'。
     *    假设这个 ')' 在位置 2j+1（0-indexed），则中间包含 j 对括号，
     *    右边剩余 i-1-j 对括号。
     *
     * 2. 中间的 j 对括号组合 = dp[j]，右边的 i-1-j 对组合 = dp[i-1-j]。
     *    枚举 j 从 0 到 i-1，取所有笛卡尔积即可。
     *
     * 3. 基底 dp[0] = [""]（空串）。
     *
     * 【举例】n = 3
     *   dp[0] = [""]
     *   dp[1] = ["(" + dp[0] + ")" + dp[0]] = ["()"]
     *   dp[2] = ["(" + dp[0] + ")" + dp[1], "(" + dp[1] + ")" + dp[0]]
     *         = ["()()", "(())"]
     *   dp[3] = ["(" + dp[0] + ")" + dp[2], "(" + dp[1] + ")" + dp[1],
     *            "(" + dp[2] + ")" + dp[0]]
     *         = ["()()()", "()(())", "(())()", "(()())", "((()))"]
     *
     * 【时间复杂度】O(4^n / √n)，卡特兰数
     * 【空间复杂度】O(4^n / √n)，存储所有结果
     */
    public List<String> generateParenthesisDP(int n) {
        List<List<String>> dp = new ArrayList<>();
        dp.add(new ArrayList<>());
        dp.get(0).add("");

        for (int i = 1; i <= n; i++) {
            List<String> cur = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                for (String inner : dp.get(j)) {
                    for (String outer : dp.get(i - 1 - j)) {
                        cur.add("(" + inner + ")" + outer);
                    }
                }
            }
            dp.add(cur);
        }

        return dp.get(n);
    }
}
