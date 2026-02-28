/**
 * LeetCode 224. Basic Calculator
 * 难度: Hard
 *
 * 题目描述：
 * 给你一个字符串表达式 s，请你实现一个基本计算器来计算并返回它的值。
 * 表达式中可能包含 '+'、'-'、'('、')' 和非负整数以及空格。
 * 注意：不允许使用任何将字符串作为数学表达式计算的内置函数。
 *
 * 示例 1：s = "1 + 1" → 输出 2
 * 示例 2：s = " 2-1 + 2 " → 输出 3
 * 示例 3：s = "(1+(4+5+2)-3)+(6+8)" → 输出 23
 *
 * 【拓展练习】
 * 1. LeetCode 227. Basic Calculator II —— 增加乘除运算（无括号也需处理优先级）
 * 2. LeetCode 772. Basic Calculator III —— 同时包含加减乘除和括号
 * 3. LeetCode 394. Decode String —— 栈处理嵌套括号的另一经典题
 */

import java.util.Deque;
import java.util.ArrayDeque;

class BasicCalculator224 {

    /**
     * ==================== 解法一：栈处理括号 ====================
     *
     * 【核心思路】
     * 维护当前结果 result 和当前符号 sign。
     * 遇到数字：累加到 result 中（乘以 sign）。
     * 遇到 '('：将当前 result 和 sign 压栈，重置 result 和 sign。
     * 遇到 ')'：弹栈恢复外层的 result 和 sign，合并内层结果。
     *
     * 【思考过程】
     * 1. 只有加减法，没有优先级问题，核心难点是括号的嵌套。
     * 2. 括号的作用：开启一个"子计算环境"。进入括号时保存外层状态，
     *    退出括号时恢复外层状态并合并结果。
     *    → 这正是栈的用武之地。
     *
     * 3. 用 result 记录当前层的累计结果，sign 记录下一个数的正负。
     *    - 数字 → result += sign * num
     *    - '+' → sign = 1
     *    - '-' → sign = -1
     *    - '(' → 把 (result, sign) 压栈，然后 result=0, sign=1（进入新层）
     *    - ')' → 弹出 (prev_sign, prev_result)，
     *            result = prev_result + prev_sign * result（合并回外层）
     *
     * 4. 多位数需要连续读取所有数字字符拼成完整数字。
     *
     * 【举例】s = "(1+(4+5+2)-3)+(6+8)"
     *   '(' → 压栈[0, 1], result=0, sign=1
     *   '1' → result=1
     *   '+' → sign=1
     *   '(' → 压栈[1, 1], result=0, sign=1
     *   '4' → result=4, '+' → sign=1
     *   '5' → result=9, '+' → sign=1
     *   '2' → result=11
     *   ')' → 弹出 sign=1, prev=1, result=1+1*11=12
     *   '-' → sign=-1
     *   '3' → result=12+(-1)*3=9
     *   ')' → 弹出 sign=1, prev=0, result=0+1*9=9
     *   '+' → sign=1
     *   '(' → 压栈[9, 1], result=0, sign=1
     *   '6' → result=6, '+' → sign=1
     *   '8' → result=14
     *   ')' → 弹出 sign=1, prev=9, result=9+1*14=23
     *   返回 23
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(n)，栈深度取决于括号嵌套层数
     */
    public int calculateStack(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        int result = 0;
        int sign = 1;
        int i = 0;

        while (i < s.length()) {
            char ch = s.charAt(i);
            if (Character.isDigit(ch)) {
                int num = 0;
                while (i < s.length() && Character.isDigit(s.charAt(i))) {
                    num = num * 10 + (s.charAt(i) - '0');
                    i++;
                }
                result += sign * num;
                continue;
            } else if (ch == '+') {
                sign = 1;
            } else if (ch == '-') {
                sign = -1;
            } else if (ch == '(') {
                stack.push(result);
                stack.push(sign);
                result = 0;
                sign = 1;
            } else if (ch == ')') {
                int prevSign = stack.pop();
                int prevResult = stack.pop();
                result = prevResult + prevSign * result;
            }
            i++;
        }
        return result;
    }

    /**
     * ==================== 解法二：递归下降解析 ====================
     *
     * 【核心思路】
     * 将表达式看作文法：
     *   expr → term (('+' | '-') term)*
     *   term → number | '(' expr ')'
     * 用递归下降法解析，遇到 '(' 递归处理子表达式，遇到 ')' 返回。
     *
     * 【思考过程】
     * 1. 递归下降是编译原理中解析表达式的经典方法。
     *    将表达式定义为文法规则，每个规则对应一个递归函数。
     *
     * 2. 对于本题（只有加减和括号）：
     *    - expr：一个或多个 term 用 +/- 连接
     *    - term：一个数字，或者一对括号包裹的 expr
     *
     * 3. 用实例变量 idx 跟踪当前解析位置。
     *    - parseExpr()：解析整个表达式
     *    - parseTerm()：解析单个项（数字或括号子表达式）
     *
     * 4. 这种方法的优势：
     *    - 天然处理括号嵌套（递归）
     *    - 容易扩展到更复杂的文法（如加入乘除、优先级）
     *    - 代码结构清晰，每个函数职责明确
     *
     * 【举例】s = "1+(2-3)"
     *   parseExpr():
     *     parseTerm() → 读取 '1'，返回 1
     *     读取 '+'，sign=1
     *     parseTerm() → 遇到 '('
     *       递归 parseExpr():
     *         parseTerm() → '2'，返回 2
     *         读取 '-'，sign=-1
     *         parseTerm() → '3'，返回 3
     *         result = 2 + (-1)*3 = -1
     *       读取 ')'
     *       返回 -1
     *     result = 1 + 1*(-1) = 0
     *   返回 0
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(n)，递归栈深度
     */
    private int pos;

    public int calculateRecursive(String s) {
        pos = 0;
        return parseExpr(s);
    }

    private void skipSpaces(String s) {
        while (pos < s.length() && s.charAt(pos) == ' ') {
            pos++;
        }
    }

    private int parseNumber(String s) {
        int num = 0;
        while (pos < s.length() && Character.isDigit(s.charAt(pos))) {
            num = num * 10 + (s.charAt(pos) - '0');
            pos++;
        }
        return num;
    }

    private int parseTerm(String s) {
        skipSpaces(s);
        if (pos < s.length() && s.charAt(pos) == '(') {
            pos++;
            int val = parseExpr(s);
            pos++;
            return val;
        }
        return parseNumber(s);
    }

    private int parseExpr(String s) {
        int result = parseTerm(s);
        while (pos < s.length()) {
            skipSpaces(s);
            if (pos >= s.length() || (s.charAt(pos) != '+' && s.charAt(pos) != '-')) {
                break;
            }
            char op = s.charAt(pos);
            pos++;
            int term = parseTerm(s);
            if (op == '+') {
                result += term;
            } else {
                result -= term;
            }
        }
        return result;
    }
}
