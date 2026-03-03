/**
 * LeetCode 104. Maximum Depth of Binary Tree
 * 难度: Easy
 *
 * 题目描述：
 * 给定一个二叉树 root，返回其最大深度。
 * 二叉树的最大深度是指从根节点到最远叶子节点的最长路径上的节点数。
 *
 * 示例 1：root = [3,9,20,null,null,15,7] → 输出 3
 *   解释：
 *       3
 *      / \
 *     9  20
 *       /  \
 *      15   7
 *   最大深度为 3。
 * 示例 2：root = [1,null,2] → 输出 2
 *
 * 【拓展练习】
 * 1. LeetCode 111. Minimum Depth of Binary Tree —— 求二叉树的最小深度
 * 2. LeetCode 110. Balanced Binary Tree —— 判断是否为平衡二叉树
 * 3. LeetCode 559. Maximum Depth of N-ary Tree —— N叉树的最大深度
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

class MaxDepth104 {

    /**
     * ==================== 解法一：DFS 递归 ====================
     *
     * 【核心思路】
     * 树的最大深度 = 1 + max(左子树深度, 右子树深度)。
     * 空节点深度为 0，这就是递归的 base case。
     *
     * 【思考过程】
     * 1. 树是递归定义的数据结构，天然适合递归处理。
     * 2. 一个节点的"深度"取决于左右子树谁更深，取较大值再加 1（自身这一层）。
     * 3. 空节点没有高度，返回 0。
     * 4. 这是最经典、最简洁的树递归题。
     *
     * 【举例】root = [3,9,20,null,null,15,7]
     *   maxDepth(3)
     *     = 1 + max(maxDepth(9), maxDepth(20))
     *     = 1 + max(1, 1 + max(maxDepth(15), maxDepth(7)))
     *     = 1 + max(1, 1 + max(1, 1))
     *     = 1 + max(1, 2)
     *     = 3
     *
     * 【时间复杂度】O(n)，每个节点访问一次
     * 【空间复杂度】O(h)，递归栈深度，h 为树高；最坏 O(n)，最好 O(log n)
     */
    public int maxDepthDFS(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(maxDepthDFS(root.left), maxDepthDFS(root.right));
    }

    /**
     * ==================== 解法二：BFS 层序遍历 ====================
     *
     * 【核心思路】
     * 使用队列进行层序遍历（BFS），每遍历完一层，深度计数器加 1。
     * 遍历结束时的计数器值就是最大深度。
     *
     * 【思考过程】
     * 1. 层序遍历天然地一层一层处理节点。
     * 2. 每处理完一层（队列中当前层的所有节点出队并将下一层入队），depth++。
     * 3. 当队列为空时，所有层都处理完了，depth 就是最大深度。
     * 4. 适合不想用递归，或者需要按层处理的场景。
     *
     * 【举例】root = [3,9,20,null,null,15,7]
     *   初始：queue = [3], depth = 0
     *   第1层：处理 3，入队 9,20 → queue = [9,20], depth = 1
     *   第2层：处理 9,20，入队 15,7 → queue = [15,7], depth = 2
     *   第3层：处理 15,7，无子节点 → queue = [], depth = 3
     *   队列空，返回 3
     *
     * 【时间复杂度】O(n)，每个节点入队出队各一次
     * 【空间复杂度】O(n)，队列最多存储一层的节点数，最坏为 n/2
     */
    public int maxDepthBFS(TreeNode root) {
        if (root == null) return 0;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int depth = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            depth++;
        }

        return depth;
    }
}
