/**
 * LeetCode 637. Average of Levels in Binary Tree
 * 难度: Easy
 *
 * 题目描述：
 * 给定一个非空二叉树的根节点 root，以数组的形式返回每一层节点的平均值。
 *
 * 示例：
 *     3
 *    / \
 *   9  20
 *     /  \
 *    15   7
 * 输出: [3.0, 14.5, 11.0]
 *
 * 【拓展练习】
 * 1. LeetCode 102. Binary Tree Level Order Traversal —— 基础层序遍历
 * 2. LeetCode 107. Binary Tree Level Order Traversal II —— 自底向上层序遍历
 * 3. LeetCode 515. Find Largest Value in Each Tree Row —— 每层最大值
 */

import java.util.*;

class AverageOfLevels637 {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * ==================== 解法一：BFS 层序遍历 ====================
     *
     * 【核心思路】
     * 使用队列做 BFS，逐层遍历二叉树。每层开始时记录队列长度（该层节点数），
     * 遍历该层所有节点求和后除以节点数，得到该层平均值。
     *
     * 【思考过程】
     * 1. 层序遍历的标准模板：用队列，每次处理一层。
     *    关键在于"如何区分层与层"——每层开始时，队列中恰好存放当前层的所有节点，
     *    此时 queue.size() 就是当前层的节点数。
     *
     * 2. 遍历当前层的每个节点：累加 val、将左右孩子入队。
     *    层遍历结束后，sum / count 就是该层平均值。
     *
     * 3. 注意用 double 累加 sum，避免 int 溢出。
     *
     * 【举例】root = [3, 9, 20, null, null, 15, 7]
     *   第0层: queue=[3]       → sum=3, count=1 → avg=3.0
     *   第1层: queue=[9,20]    → sum=29, count=2 → avg=14.5
     *   第2层: queue=[15,7]    → sum=22, count=2 → avg=11.0
     *   结果: [3.0, 14.5, 11.0]
     *
     * 【时间复杂度】O(n) 每个节点访问一次
     * 【空间复杂度】O(n) 队列最多存放一层的节点，最坏情况为 n/2
     */
    public List<Double> averageOfLevelsBFS(TreeNode root) {
        List<Double> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            double levelSum = 0;

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                levelSum += node.val;
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }

            result.add(levelSum / levelSize);
        }

        return result;
    }

    /**
     * ==================== 解法二：DFS（记录每层的和与节点数） ====================
     *
     * 【核心思路】
     * DFS 遍历二叉树，用两个列表分别记录每层的节点值之和与节点个数。
     * 遍历时通过 depth 参数确定当前节点属于哪一层。
     * 遍历结束后，每层 sum/count 即为平均值。
     *
     * 【思考过程】
     * 1. BFS 天然按层处理，但 DFS 也能做到——只需额外记录"当前深度"。
     *
     * 2. 维护 sums.get(depth) 和 counts.get(depth)：
     *    - 如果 depth == sums.size()，说明首次到达该层，新增一个元素。
     *    - 否则累加到已有的对应位置。
     *
     * 3. 最终 result[i] = sums[i] / counts[i]。
     *
     * 【举例】root = [3, 9, 20, null, null, 15, 7]
     *   dfs(3, depth=0): sums=[3], counts=[1]
     *   dfs(9, depth=1): sums=[3,9], counts=[1,1]
     *   dfs(20, depth=1): sums=[3,29], counts=[1,2]
     *   dfs(15, depth=2): sums=[3,29,15], counts=[1,2,1]
     *   dfs(7, depth=2): sums=[3,29,22], counts=[1,2,2]
     *   结果: [3/1, 29/2, 22/2] = [3.0, 14.5, 11.0]
     *
     * 【时间复杂度】O(n) 每个节点访问一次
     * 【空间复杂度】O(h) 递归栈深度，h 为树的高度
     */
    public List<Double> averageOfLevelsDFS(TreeNode root) {
        List<Double> sums = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        dfs(root, 0, sums, counts);

        List<Double> result = new ArrayList<>();
        for (int i = 0; i < sums.size(); i++) {
            result.add(sums.get(i) / counts.get(i));
        }
        return result;
    }

    private void dfs(TreeNode node, int depth, List<Double> sums, List<Integer> counts) {
        if (node == null) return;

        if (depth == sums.size()) {
            sums.add((double) node.val);
            counts.add(1);
        } else {
            sums.set(depth, sums.get(depth) + node.val);
            counts.set(depth, counts.get(depth) + 1);
        }

        dfs(node.left, depth + 1, sums, counts);
        dfs(node.right, depth + 1, sums, counts);
    }
}
