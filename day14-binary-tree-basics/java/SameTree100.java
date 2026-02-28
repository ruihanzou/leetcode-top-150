/**
 * LeetCode 100. Same Tree
 * 难度: Easy
 *
 * 题目描述：
 * 给你两棵二叉树的根节点 p 和 q，编写一个函数来检验这两棵树是否相同。
 * 如果两棵树在结构上相同，并且节点具有相同的值，则认为它们是相同的。
 *
 * 示例 1：p = [1,2,3], q = [1,2,3] → 输出 true
 *   解释：
 *     1       1
 *    / \     / \
 *   2   3   2   3
 *   两棵树结构和值完全相同。
 * 示例 2：p = [1,2], q = [1,null,2] → 输出 false
 *   解释：
 *     1       1
 *    /         \
 *   2           2
 *   结构不同。
 * 示例 3：p = [1,2,1], q = [1,1,2] → 输出 false
 *
 * 【拓展练习】
 * 1. LeetCode 101. Symmetric Tree —— 判断一棵树是否镜像对称
 * 2. LeetCode 572. Subtree of Another Tree —— 判断一棵树是否是另一棵树的子树
 * 3. LeetCode 951. Flip Equivalent Binary Trees —— 翻转等价二叉树
 *
 * TreeNode 定义：
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val; this.left = left; this.right = right;
 *     }
 * }
 */

import java.util.LinkedList;
import java.util.Queue;

class SameTree100 {

    /**
     * ==================== 解法一：DFS 递归比较 ====================
     *
     * 【核心思路】
     * 同时递归遍历两棵树，逐节点比较：
     * - 如果两个节点都为 null，相同
     * - 如果只有一个为 null，不同
     * - 如果值不等，不同
     * - 否则递归比较左子树和右子树
     *
     * 【思考过程】
     * 1. "两棵树相同"可以递归定义：根节点值相同，且左子树相同，且右子树相同。
     * 2. base case：两个 null → 相同；一个 null 一个非 null → 不同。
     * 3. 三行代码就能搞定，非常优雅。
     *
     * 【举例】p = [1,2,3], q = [1,2,3]
     *   isSame(1,1) → 值相等
     *     isSame(2,2) → 值相等
     *       isSame(null,null) → true
     *       isSame(null,null) → true
     *     → true
     *     isSame(3,3) → 值相等
     *       isSame(null,null) → true
     *       isSame(null,null) → true
     *     → true
     *   → true
     *
     * 【时间复杂度】O(n)，n 为较小树的节点数
     * 【空间复杂度】O(h)，递归栈深度，最坏 O(n)
     */
    public boolean isSameTreeDFS(TreeNode p, TreeNode q) {
        if (p == null && q == null) return true;
        if (p == null || q == null) return false;
        return p.val == q.val
                && isSameTreeDFS(p.left, q.left)
                && isSameTreeDFS(p.right, q.right);
    }

    /**
     * ==================== 解法二：BFS 迭代（队列） ====================
     *
     * 【核心思路】
     * 用队列同时对两棵树做层序遍历，每次从队列取出一对节点进行比较。
     * 如果任何一对节点不匹配，立即返回 false。
     *
     * 【思考过程】
     * 1. 递归本质上使用了系统栈，我们可以用显式队列（或栈）来模拟。
     * 2. 每次入队的是"一对"节点 (p_node, q_node)，出队后检查：
     *    - 都为 null → 跳过（这对没问题）
     *    - 一个 null → 返回 false
     *    - 值不等 → 返回 false
     *    - 值相等 → 把 (p.left, q.left) 和 (p.right, q.right) 入队
     * 3. 队列清空后说明所有节点对都匹配，返回 true。
     *
     * 【举例】p = [1,2], q = [1,null,2]
     *   queue = [(1,1)]
     *   取出 (1,1)：值相等，入队 (2,null) 和 (3,3)...
     *   取出 (2,null)：一个为 null → 返回 false
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(n)，队列空间
     */
    public boolean isSameTreeBFS(TreeNode p, TreeNode q) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(p);
        queue.offer(q);

        while (!queue.isEmpty()) {
            TreeNode node1 = queue.poll();
            TreeNode node2 = queue.poll();

            if (node1 == null && node2 == null) continue;
            if (node1 == null || node2 == null) return false;
            if (node1.val != node2.val) return false;

            queue.offer(node1.left);
            queue.offer(node2.left);
            queue.offer(node1.right);
            queue.offer(node2.right);
        }

        return true;
    }
}
