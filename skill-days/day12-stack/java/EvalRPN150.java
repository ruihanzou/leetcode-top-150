/**
 * LeetCode 150. Evaluate Reverse Polish Notation
 * 难度: Medium
 *
 * 题目描述：
 * 给你一个字符串数组 tokens，表示一个根据逆波兰表示法表示的算术表达式。
 * 请你计算该表达式，返回一个表示表达式值的整数。
 *
 * 注意：
 * - 有效的算符为 '+'、'-'、'*'、'/'。
 * - 除法截断到零（即向零取整）。
 * - 保证表达式有效且不会出现除零错误。
 * - 逆波兰表达式（后缀表达式）：运算符在操作数之后。
 *
 * 示例 1：tokens = ["2","1","+","3","*"] → 输出 9
 *   解释：((2 + 1) * 3) = 9
 * 示例 2：tokens = ["4","13","5","/","+"] → 输出 6
 *   解释：(4 + (13 / 5)) = 6
 * 示例 3：tokens = ["10","6","9","3","+","-11","*","/","*","17","+","5","+"]
 *   → 输出 22
 *
 * 【拓展练习】
 * 1. LeetCode 224. Basic Calculator —— 中缀表达式计算（含括号）
 * 2. LeetCode 227. Basic Calculator II —— 中缀表达式计算（含乘除优先级）
 * 3. LeetCode 772. Basic Calculator III —— 完整中缀表达式计算
 */

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Set;

class EvalRPN150 {

    /**
     * ==================== 解法一：栈计算 ====================
     *
     * 【核心思路】
     * 遍历 tokens，遇到数字压栈，遇到运算符弹出两个操作数计算后将结果压栈。
     * 最后栈中剩余的唯一元素就是表达式的值。
     *
     * 【思考过程】
     * 1. 逆波兰表达式（后缀表达式）的求值天然适合用栈：
     *    - 数字 → 压栈
     *    - 运算符 → 弹出栈顶两个数，计算，结果压回栈
     * 2. 注意操作数的顺序：先弹出的是右操作数 b，后弹出的是左操作数 a。
     *    即 a op b，而不是 b op a。对减法和除法这很重要。
     * 3. Java 的整除 '/' 天然是向零取整，所以不需要特殊处理。
     *
     * 【举例】tokens = ["4","13","5","/","+"]
     *   "4"  → 压栈: [4]
     *   "13" → 压栈: [4, 13]
     *   "5"  → 压栈: [4, 13, 5]
     *   "/"  → 弹出 b=5, a=13, 13/5=2, 压栈: [4, 2]
     *   "+"  → 弹出 b=2, a=4, 4+2=6, 压栈: [6]
     *   返回 6
     *
     * 【时间复杂度】O(n)，遍历一次 tokens
     * 【空间复杂度】O(n)，栈最多存 n/2 个数字
     */
    public int evalRPNStack(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();
        Set<String> ops = Set.of("+", "-", "*", "/");

        for (String token : tokens) {
            if (ops.contains(token)) {
                int b = stack.pop();
                int a = stack.pop();
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/": stack.push(a / b); break;
                }
            } else {
                stack.push(Integer.parseInt(token));
            }
        }
        return stack.pop();
    }

    /**
     * ==================== 解法二：递归（从后往前） ====================
     *
     * 【核心思路】
     * 逆波兰表达式从右往左看，第一个遇到的运算符的右边就是它的两个子表达式。
     * 利用这个性质，从 tokens 末尾开始递归解析：
     * - 如果当前 token 是运算符，递归解析右操作数和左操作数，再计算。
     * - 如果是数字，直接返回。
     *
     * 【思考过程】
     * 1. 逆波兰表达式可以看作一棵二叉表达式树的后序遍历。
     *    → 从后往前就是"根 → 右 → 左"的顺序。
     * 2. 遇到运算符 → 它是某棵子树的根，接下来递归解析右子树和左子树。
     *    遇到数字 → 叶子节点，直接返回。
     * 3. 用一个实例变量 idx 从末尾递减，逐个消费 token。
     * 4. 递归的优雅之处在于它自然地还原了表达式树的结构。
     *
     * 【举例】tokens = ["2","1","+","3","*"]
     *   idx=4, token="*" (运算符)
     *     递归右: idx=3, token="3" (数字) → 返回 3
     *     递归左: idx=2, token="+" (运算符)
     *       递归右: idx=1, token="1" → 返回 1
     *       递归左: idx=0, token="2" → 返回 2
     *       返回 2+1=3
     *     返回 3*3=9
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(n)，递归栈深度
     */
    private int idx;

    public int evalRPNRecursive(String[] tokens) {
        idx = tokens.length - 1;
        return parse(tokens);
    }

    private int parse(String[] tokens) {
        String token = tokens[idx--];
        if (token.equals("+") || token.equals("-") ||
            token.equals("*") || token.equals("/")) {
            int right = parse(tokens);
            int left = parse(tokens);
            switch (token) {
                case "+": return left + right;
                case "-": return left - right;
                case "*": return left * right;
                case "/": return left / right;
            }
        }
        return Integer.parseInt(token);
    }
}
