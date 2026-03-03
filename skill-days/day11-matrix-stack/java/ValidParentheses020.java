/**
 * LeetCode 20. Valid Parentheses
 * 难度: Easy
 *
 * 题目描述：
 * 给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串 s，判断字符串是否有效。
 * 有效字符串需满足：
 * 1. 左括号必须用相同类型的右括号闭合。
 * 2. 左括号必须以正确的顺序闭合。
 * 3. 每个右括号都有一个对应的相同类型的左括号。
 *
 * 示例 1：s = "()" → 输出 true
 * 示例 2：s = "()[]{}" → 输出 true
 * 示例 3：s = "(]" → 输出 false
 * 示例 4：s = "([])" → 输出 true
 *
 * 【拓展练习】
 * 1. LeetCode 22. Generate Parentheses —— 生成所有有效括号组合（回溯）
 * 2. LeetCode 32. Longest Valid Parentheses —— 找最长有效括号子串（动态规划/栈）
 * 3. LeetCode 1249. Minimum Remove to Make Valid Parentheses —— 删除最少括号使其有效
 */

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.HashMap;

class ValidParentheses020 {

    /**
     * ==================== 解法一：栈匹配 ====================
     *
     * 【核心思路】
     * 遍历字符串，遇到左括号就压栈，遇到右括号就弹栈检查是否匹配。
     * 最后栈为空说明所有括号都正确匹配。
     *
     * 【思考过程】
     * 1. 括号匹配的核心特征：最近的左括号要和当前右括号匹配。
     *    → 这正是"后进先出"——栈的天然应用场景。
     *
     * 2. 遇到左括号 '(', '[', '{' → 压栈，等待未来的右括号来匹配。
     *
     * 3. 遇到右括号 ')', ']', '}' → 弹栈，看栈顶是否是对应的左括号。
     *    如果栈为空（没有左括号可匹配）或类型不对 → 无效。
     *
     * 4. 遍历结束后，栈应为空（所有左括号都被匹配掉了）。
     *
     * 【举例】s = "([{}])"
     *   '(' → 栈: ['(']
     *   '[' → 栈: ['(', '[']
     *   '{' → 栈: ['(', '[', '{']
     *   '}' → 弹栈 '{' 匹配 ✓，栈: ['(', '[']
     *   ']' → 弹栈 '[' 匹配 ✓，栈: ['(']
     *   ')' → 弹栈 '(' 匹配 ✓，栈: []
     *   栈为空 → 返回 true
     *
     * 【时间复杂度】O(n)，遍历一次字符串
     * 【空间复杂度】O(n)，最坏情况全是左括号
     */
    public boolean isValidStack(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char ch : s.toCharArray()) {
            if (ch == '(' || ch == '[' || ch == '{') {
                stack.push(ch);
            } else {
                if (stack.isEmpty()) return false;
                char top = stack.pop();
                if ((ch == ')' && top != '(') ||
                    (ch == ']' && top != '[') ||
                    (ch == '}' && top != '{')) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    /**
     * ==================== 解法二：映射表 + 栈 ====================
     *
     * 【核心思路】
     * 用 HashMap 将右括号映射到对应的左括号，使匹配逻辑更简洁。
     * 遇到右括号时，直接查表获取期望的左括号，与栈顶比较。
     *
     * 【思考过程】
     * 1. 解法一中对三种括号分别写 if 判断，代码略冗长。
     *    → 用映射表 {')':'(', ']':'[', '}':'{'} 统一处理。
     *
     * 2. 遇到字符 ch：
     *    - 如果 ch 在映射表中（是右括号）→ 弹栈比较。
     *    - 否则（是左括号）→ 压栈。
     *
     * 3. 弹栈时如果栈为空，直接返回 false。
     *
     * 【举例】s = "{[]}"
     *   mapping: ')' → '(', ']' → '[', '}' → '{'
     *   '{' → 不在mapping中，压栈: ['{']
     *   '[' → 不在mapping中，压栈: ['{', '[']
     *   ']' → mapping.get(']')='[', 弹栈得'[', 匹配 ✓
     *   '}' → mapping.get('}')='{', 弹栈得'{', 匹配 ✓
     *   栈为空 → true
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(n)
     */
    public boolean isValidMapping(String s) {
        Map<Character, Character> mapping = new HashMap<>();
        mapping.put(')', '(');
        mapping.put(']', '[');
        mapping.put('}', '{');

        Deque<Character> stack = new ArrayDeque<>();
        for (char ch : s.toCharArray()) {
            if (mapping.containsKey(ch)) {
                if (stack.isEmpty()) return false;
                char top = stack.pop();
                if (top != mapping.get(ch)) return false;
            } else {
                stack.push(ch);
            }
        }
        return stack.isEmpty();
    }
}
