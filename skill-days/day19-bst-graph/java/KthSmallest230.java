/**
 * LeetCode 230. Kth Smallest Element in a BST
 * 难度: Medium
 *
 * 题目描述：
 * 给定一个二叉搜索树的根节点 root，和一个整数 k，请你设计一个算法查找其中第 k 小的元素（从 1 开始计数）。
 *
 * 示例 1：root = [3,1,4,null,2], k = 1 → 输出 1
 * 示例 2：root = [5,3,6,2,4,null,null,1], k = 3 → 输出 3
 *
 * 【拓展练习】
 * 1. LeetCode 94. Binary Tree Inorder Traversal —— 中序遍历基础练习
 * 2. LeetCode 671. Second Minimum Node In a Binary Tree —— 特殊二叉树找第二小
 * 3. LeetCode 378. Kth Smallest Element in a Sorted Matrix —— 矩阵中第k小元素
 */

import java.util.ArrayDeque;
import java.util.Deque;

class KthSmallest230 {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * ==================== 解法一：中序遍历递归 ====================
     *
     * 【核心思路】
     * BST 的中序遍历（左→根→右）恰好产生升序序列。
     * 因此第 k 小的元素就是中序遍历的第 k 个节点值。
     *
     * 【思考过程】
     * 1. BST 的核心性质：左子树所有节点 < 根 < 右子树所有节点。
     *    所以中序遍历自然产生有序序列。
     *
     * 2. 最朴素的做法：做完整中序遍历得到有序数组，返回第 k-1 个元素。
     *    但其实不需要遍历整棵树，只要数到第 k 个就可以停下来。
     *
     * 3. 用成员变量 count 和 result 记录状态，
     *    当 count == k 时记录答案并返回。
     *
     * 【举例】root = [3,1,4,null,2], k = 1
     *   中序遍历顺序：1 → 2 → 3 → 4
     *   第 1 个就是 1，返回 1
     *
     * 【时间复杂度】O(n)，最坏遍历全部节点（k=n 时）
     * 【空间复杂度】O(h)，递归栈深度为树的高度
     */
    private int count;
    private int result;

    public int kthSmallestRecursive(TreeNode root, int k) {
        count = 0;
        result = 0;
        inorder(root, k);
        return result;
    }

    private void inorder(TreeNode node, int k) {
        if (node == null || count >= k) return;
        inorder(node.left, k);
        count++;
        if (count == k) {
            result = node.val;
            return;
        }
        inorder(node.right, k);
    }

    /**
     * ==================== 解法二：中序遍历迭代（栈模拟） ====================
     *
     * 【核心思路】
     * 用显式栈模拟中序遍历过程，每弹出一个节点就将 k 减 1，
     * 当 k 减到 0 时，当前弹出的节点就是第 k 小元素。
     *
     * 【思考过程】
     * 1. 递归本质是系统帮我们维护栈，迭代版本就是自己管理栈。
     *
     * 2. 中序遍历的迭代模板：
     *    - 不断将左子节点压栈（一路向左走到底）
     *    - 弹出栈顶（最小的未访问节点）
     *    - 转向右子树继续
     *
     * 3. 每次弹出栈顶就是访问一个节点，此时 k--。
     *    当 k == 0 时，栈顶就是答案。
     *
     * 4. 迭代的好处：不需要遍历完整棵树，找到第 k 个就停，
     *    时间复杂度为 O(H + k)，H 是树高。
     *
     * 【举例】root = [5,3,6,2,4,null,null,1], k = 3
     *   栈操作过程：
     *   push 5, push 3, push 2, push 1  （一路向左）
     *   pop 1 → k=2
     *   pop 2 → k=1
     *   pop 3 → k=0 → 返回 3
     *
     * 【时间复杂度】O(H + k)，H 为树高，先走到最左需要 H 步，再弹出 k 个
     * 【空间复杂度】O(H)，栈中最多存储树高个节点
     */
    public int kthSmallestIterative(TreeNode root, int k) {
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode curr = root;

        while (curr != null || !stack.isEmpty()) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }
            curr = stack.pop();
            k--;
            if (k == 0) return curr.val;
            curr = curr.right;
        }

        return -1;
    }
}
