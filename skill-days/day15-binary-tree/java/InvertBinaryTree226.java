/**
 * LeetCode 226. Invert Binary Tree
 * 难度: Easy
 *
 * 题目描述：
 * 给你一棵二叉树的根节点 root，翻转这棵二叉树，并返回其根节点。
 * 翻转即将每个节点的左右子树互换。
 *
 * 示例 1：root = [4,2,7,1,3,6,9] → 输出 [4,7,2,9,6,3,1]
 *   解释：
 *       4               4
 *      / \             / \
 *     2   7    →     7   2
 *    / \ / \        / \ / \
 *   1  3 6  9      9  6 3  1
 * 示例 2：root = [2,1,3] → 输出 [2,3,1]
 * 示例 3：root = [] → 输出 []
 *
 * 【拓展练习】
 * 1. LeetCode 101. Symmetric Tree —— 判断二叉树是否对称（翻转的逆问题）
 * 2. LeetCode 114. Flatten Binary Tree to Linked List —— 将二叉树展开为链表
 * 3. LeetCode 156. Binary Tree Upside Down —— 上下翻转二叉树
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

class InvertBinaryTree226 {

    /**
     * ==================== 解法一：DFS 递归 ====================
     *
     * 【核心思路】
     * 对每个节点，先递归翻转其左子树和右子树，再交换左右子节点。
     * （也可以先交换再递归，效果相同。）
     *
     * 【思考过程】
     * 1. "翻转整棵树"可以分解为：翻转左子树 + 翻转右子树 + 交换左右。
     * 2. base case：空节点无需操作，直接返回 null。
     * 3. 这是后序遍历的典型应用：先处理子问题，再合并结果。
     *    （前序也行：先交换，再分别递归新的左右子树。）
     *
     * 【举例】root = [4,2,7,1,3,6,9]
     *   invertTree(4)：
     *     left = invertTree(2) → 翻转后 [2,3,1]
     *     right = invertTree(7) → 翻转后 [7,9,6]
     *     交换：root.left = [7,9,6], root.right = [2,3,1]
     *     返回 [4,7,2,9,6,3,1]
     *
     * 【时间复杂度】O(n)，每个节点访问一次
     * 【空间复杂度】O(h)，递归栈深度，最坏 O(n)
     */
    public TreeNode invertTreeDFS(TreeNode root) {
        if (root == null) return null;

        TreeNode left = invertTreeDFS(root.left);
        TreeNode right = invertTreeDFS(root.right);

        root.left = right;
        root.right = left;

        return root;
    }

    /**
     * ==================== 解法二：BFS 迭代 ====================
     *
     * 【核心思路】
     * 用队列做层序遍历，每遍历到一个节点就交换其左右子节点。
     *
     * 【思考过程】
     * 1. 不用递归也能翻转：对每个节点做一次左右交换即可。
     * 2. 遍历顺序无所谓（BFS 或 DFS 都行），关键是每个节点都被访问到。
     * 3. BFS 用队列，逐层处理；对每个出队节点交换左右子节点后，
     *    将非空子节点入队继续处理。
     *
     * 【举例】root = [4,2,7,1,3,6,9]
     *   queue = [4]
     *   取出 4：交换左右 → 4 的左=7，右=2，入队 7,2
     *   取出 7：交换左右 → 7 的左=9，右=6，入队 9,6
     *   取出 2：交换左右 → 2 的左=3，右=1，入队 3,1
     *   取出 9,6,3,1：均为叶子，无需操作
     *   结果 [4,7,2,9,6,3,1]
     *
     * 【时间复杂度】O(n)，每个节点入队出队各一次
     * 【空间复杂度】O(n)，队列空间
     */
    public TreeNode invertTreeBFS(TreeNode root) {
        if (root == null) return null;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();

            TreeNode temp = node.left;
            node.left = node.right;
            node.right = temp;

            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }

        return root;
    }
}
