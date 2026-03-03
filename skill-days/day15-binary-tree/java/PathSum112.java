/**
 * LeetCode 112. Path Sum
 * 难度: Easy
 *
 * 题目描述：
 * 给你二叉树的根节点 root 和一个表示目标和的整数 targetSum。
 * 判断该树中是否存在根节点到叶子节点的路径，这条路径上所有节点值相加等于目标和 targetSum。
 * 叶子节点是指没有子节点的节点。
 *
 * 示例：root = [5,4,8,11,null,13,4,7,2,null,null,null,1], targetSum = 22
 *       路径 5→4→11→2 的和为 22 → 输出 true
 *
 * 【拓展练习】
 * 1. LeetCode 113. Path Sum II —— 找出所有满足条件的路径（回溯）
 * 2. LeetCode 437. Path Sum III —— 路径不必从根开始（前缀和）
 * 3. LeetCode 124. Binary Tree Maximum Path Sum —— 最大路径和（任意路径）
 */

import java.util.LinkedList;
import java.util.Queue;

class PathSum112 {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * ==================== 解法一：DFS 递归 ====================
     *
     * 【核心思路】
     * 从根节点开始，每到一个节点就将 targetSum 减去当前节点值。
     * 到达叶子节点时，检查剩余的 targetSum 是否恰好为 0。
     *
     * 【思考过程】
     * 1. "根到叶子的路径和" → 从根递归到叶子，逐步减去节点值。
     *
     * 2. 递归函数定义：hasPathSum(node, remaining)
     *    - base case：node 为空 → 返回 false（空树没有叶子）
     *    - 叶子节点（无左无右子）：remaining == node.val 时返回 true
     *    - 非叶子：递归检查左右子树，remaining 减去 node.val
     *
     * 3. 为什么空节点返回 false 而不是检查 remaining==0？
     *    因为题目要求路径必须到"叶子节点"，不是到空节点。
     *    例如 root=[1,2], targetSum=1，路径 1→(null) 不算，必须是 1→2。
     *
     * 【举例】root = [5,4,8,11,null,13,4,7,2,null,null,null,1], targetSum=22
     *   5 → remaining=22-5=17
     *     4 → remaining=17-4=13
     *       11 → remaining=13-11=2
     *         7 → remaining=2-7=-5, 叶子, ✗
     *         2 → remaining=2-2=0, 叶子, ✓ → 返回 true
     *
     * 【时间复杂度】O(n) 最坏遍历所有节点
     * 【空间复杂度】O(h) 递归栈深度，h 为树高
     */
    public boolean hasPathSumDfs(TreeNode root, int targetSum) {
        if (root == null) return false;
        if (root.left == null && root.right == null) {
            return root.val == targetSum;
        }
        int remaining = targetSum - root.val;
        return hasPathSumDfs(root.left, remaining) || hasPathSumDfs(root.right, remaining);
    }

    /**
     * ==================== 解法二：BFS 迭代 ====================
     *
     * 【核心思路】
     * 用 BFS 层序遍历，队列中存储 (节点, 从根到该节点的路径和)。
     * 当遇到叶子节点且路径和等于 targetSum 时返回 true。
     *
     * 【思考过程】
     * 1. DFS 用递归实现很自然，但也可以用 BFS 迭代。
     *    BFS 的好处是如果存在较浅的满足条件的路径，可以更快找到。
     *
     * 2. 用两个队列分别存节点和对应的路径和，或者包装成一个对象。
     *    这里用两个队列保持简洁。
     *
     * 3. 出队时判断：
     *    - 如果是叶子节点且 currentSum == targetSum → true
     *    - 否则把子节点和更新后的 currentSum 入队
     *
     * 【举例】root = [5,4,8,11,null,13,4,7,2,null,null,null,1], targetSum=22
     *   队列初始：[(5, 5)]
     *   出队(5,5)：入队 (4,9), (8,13)
     *   出队(4,9)：入队 (11,20)
     *   出队(8,13)：入队 (13,26), (4,17)
     *   出队(11,20)：入队 (7,27), (2,22)
     *   出队(2,22)：叶子，22==22 ✓ → true
     *
     * 【时间复杂度】O(n) 最坏遍历所有节点
     * 【空间复杂度】O(n) 队列最多存储一层的节点数
     */
    public boolean hasPathSumBfs(TreeNode root, int targetSum) {
        if (root == null) return false;

        Queue<TreeNode> nodeQueue = new LinkedList<>();
        Queue<Integer> sumQueue = new LinkedList<>();
        nodeQueue.offer(root);
        sumQueue.offer(root.val);

        while (!nodeQueue.isEmpty()) {
            TreeNode node = nodeQueue.poll();
            int currentSum = sumQueue.poll();

            if (node.left == null && node.right == null && currentSum == targetSum) {
                return true;
            }
            if (node.left != null) {
                nodeQueue.offer(node.left);
                sumQueue.offer(currentSum + node.left.val);
            }
            if (node.right != null) {
                nodeQueue.offer(node.right);
                sumQueue.offer(currentSum + node.right.val);
            }
        }

        return false;
    }
}
