/**
 * LeetCode 71. Simplify Path
 * 难度: Medium
 *
 * 题目描述：
 * 给你一个字符串 path，表示指向某一文件或目录的 Unix 风格绝对路径（以 '/' 开头），
 * 请你将其转化为更加简洁的规范路径。
 *
 * 规则：
 * - 多个连续斜杠 '//' 视为一个 '/'
 * - '.' 表示当前目录（忽略）
 * - '..' 表示上一级目录（弹出上一级）
 * - 任何其他格式的句点（如 '...' 或 'a..b'）视为文件/目录名
 * - 规范路径不以 '/' 结尾（根目录除外）
 *
 * 示例 1：path = "/home/" → 输出 "/home"
 * 示例 2：path = "/home//foo/" → 输出 "/home/foo"
 * 示例 3：path = "/home/user/Documents/../Pictures" → 输出 "/home/user/Pictures"
 * 示例 4：path = "/../" → 输出 "/"
 * 示例 5：path = "/.../a/../b/c/../d/./" → 输出 "/.../b/d"
 *
 * 【拓展练习】
 * 1. LeetCode 1166. Design File System —— 设计文件系统（Trie/HashMap）
 * 2. LeetCode 609. Find Duplicate File in System —— 在文件系统中查找重复文件
 * 3. LeetCode 388. Longest Absolute File Path —— 文件路径中最长的绝对路径
 */

import java.util.Deque;
import java.util.ArrayDeque;

class SimplifyPath071 {

    /**
     * ==================== 解法一：栈模拟 ====================
     *
     * 【核心思路】
     * 按 '/' 分割路径得到各组件，用栈模拟目录层级：
     * - 空字符串或 '.'：跳过（多余的斜杠或当前目录）
     * - '..'：弹栈（返回上一级，若栈空则忽略）
     * - 其他：压栈（进入子目录）
     * 最后将栈中元素用 '/' 拼接即为规范路径。
     *
     * 【思考过程】
     * 1. Unix 路径的核心操作就是"进入子目录"和"返回上级"。
     *    → 栈天然适合模拟这种嵌套层级结构。
     *
     * 2. 用 split("/") 把路径打碎成组件列表。
     *    例如 "/a//b/../c" → ["", "a", "", "b", "..", "c"]
     *    空字符串来自连续的 '/' 或首尾的 '/'，直接跳过即可。
     *
     * 3. 对每个组件：
     *    - "" 或 "."：无意义，跳过
     *    - ".."：回上级 → 弹栈（如果栈非空）
     *    - 其他：进入目录 → 压栈
     *
     * 4. 最后从栈底到栈顶用 "/" 拼接，前面加 "/"。
     *
     * 【举例】path = "/home/user/Documents/../Pictures"
     *   分割 → ["", "home", "user", "Documents", "..", "Pictures"]
     *   "" → 跳过
     *   "home" → 压栈: ["home"]
     *   "user" → 压栈: ["home", "user"]
     *   "Documents" → 压栈: ["home", "user", "Documents"]
     *   ".." → 弹栈: ["home", "user"]
     *   "Pictures" → 压栈: ["home", "user", "Pictures"]
     *   拼接 → "/home/user/Pictures"
     *
     * 【时间复杂度】O(n)，n 为路径长度
     * 【空间复杂度】O(n)
     */
    public String simplifyPathStack(String path) {
        Deque<String> stack = new ArrayDeque<>();
        String[] parts = path.split("/");

        for (String part : parts) {
            if (part.equals("..")) {
                if (!stack.isEmpty()) {
                    stack.pollLast();
                }
            } else if (!part.isEmpty() && !part.equals(".")) {
                stack.offerLast(part);
            }
        }

        StringBuilder sb = new StringBuilder();
        for (String dir : stack) {
            sb.append('/').append(dir);
        }
        return sb.length() == 0 ? "/" : sb.toString();
    }

    /**
     * ==================== 解法二：Deque 双端队列 ====================
     *
     * 【核心思路】
     * 与栈解法思路相同，但显式使用 Deque 的双端特性。
     * 使用 pollLast 弹出（模拟栈），使用迭代器从头到尾遍历构建结果。
     * 本质上和解法一完全相同，展示 Deque 作为通用数据结构的灵活性。
     *
     * 【思考过程】
     * 1. Java 中 Stack 类是遗留类（继承自 Vector），官方推荐用 Deque 代替。
     * 2. Deque 既可以当栈用（push/pop 操作头部），也可以当队列用。
     * 3. 这里我们用 ArrayDeque，从尾部操作（offerLast/pollLast）模拟栈，
     *    遍历时从头到尾，这样输出顺序就是正确的目录层级。
     *
     * 【举例】path = "/../a/b/../c/"
     *   分割 → ["", "..", "a", "b", "..", "c", ""]
     *   "" → 跳过
     *   ".." → deque 空，跳过
     *   "a" → 加入: deque["a"]
     *   "b" → 加入: deque["a", "b"]
     *   ".." → 弹出: deque["a"]
     *   "c" → 加入: deque["a", "c"]
     *   "" → 跳过
     *   拼接 → "/a/c"
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(n)
     */
    public String simplifyPathDeque(String path) {
        Deque<String> deque = new ArrayDeque<>();
        String[] parts = path.split("/");

        for (String part : parts) {
            if (part.equals("..")) {
                if (!deque.isEmpty()) {
                    deque.pollLast();
                }
            } else if (!part.isEmpty() && !part.equals(".")) {
                deque.offerLast(part);
            }
        }

        return "/" + String.join("/", deque);
    }
}
