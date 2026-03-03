/**
 * LeetCode 173. Binary Search Tree Iterator
 * 难度: Medium
 *
 * 设计一个遍历 BST 的迭代器 BSTIterator：
 * - 构造函数传入根节点 root
 * - next() 返回下一个最小的数
 * - hasNext() 判断是否还有下一个数
 *
 * 要求：
 * - next / hasNext 的均摊时间复杂度为 O(1)
 * - 额外空间复杂度 O(h)，h 为树高
 *
 * 【核心思路】
 * 利用 BST 的中序遍历有序性：
 * - 用栈模拟中序遍历，但不一次性遍历整棵树；
 * - 只维护当前「访问指针」所在位置的左链；
 * - 每次 next()：
 *   1. 弹出栈顶（当前最小值）；
 *   2. 如果有右子树，对右子树再次执行「一路向左压栈」；
 * - hasNext() 只需判断栈是否为空。
 *
 * 这样每个节点最多入栈、出栈一次：总操作 O(n)，摊到每次 next 是 O(1)。
 */

import java.util.ArrayDeque;
import java.util.Deque;

class BSTIterator173 {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    private final Deque<TreeNode> stack = new ArrayDeque<>();

    public BSTIterator173(TreeNode root) {
        pushLeftBranch(root);
    }

    private void pushLeftBranch(TreeNode node) {
        while (node != null) {
            stack.push(node);
            node = node.left;
        }
    }

    public int next() {
        TreeNode node = stack.pop();
        if (node.right != null) {
            pushLeftBranch(node.right);
        }
        return node.val;
    }

    public boolean hasNext() {
        return !stack.isEmpty();
    }
}

