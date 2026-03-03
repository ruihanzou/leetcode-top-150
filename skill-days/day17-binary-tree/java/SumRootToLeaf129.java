/**
 * LeetCode 129. Sum Root to Leaf Numbers
 * 难度: Medium
 *
 * 题目描述：
 * 给你一个二叉树的根节点 root，树中每个节点都存放有一个 0 到 9 之间的数字。
 * 每条从根节点到叶节点的路径都代表一个数字：例如从根到叶的路径 1→2→3 表示数字 123。
 * 计算从根到叶子节点生成的所有数字之和。
 *
 * 示例：root = [1,2,3] → 输出 25（数字12 + 数字13 = 25）
 * 示例：root = [4,9,0,5,1] → 输出 1026（495 + 491 + 40 = 1026）
 *
 * 【拓展练习】
 * 1. LeetCode 112. Path Sum —— 判断是否存在特定路径和
 * 2. LeetCode 113. Path Sum II —— 找出所有满足条件的路径
 * 3. LeetCode 257. Binary Tree Paths —— 打印所有根到叶路径
 */

import java.util.LinkedList;
import java.util.Queue;

class SumRootToLeaf129 {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * ==================== 解法一：DFS 递归（传递当前路径数字） ====================
     *
     * 【核心思路】
     * 从根到叶子，维护"当前路径代表的数字"。每往下走一层，
     * 当前数字 = 当前数字 * 10 + 子节点值。到达叶子时累加到总和中。
     *
     * 【思考过程】
     * 1. 路径 1→2→3 代表数字 123。
     *    从上往下看：到节点1时数字是1，到节点2时是 1*10+2=12，到节点3时是 12*10+3=123。
     *    → 递推公式：num = num * 10 + node.val
     *
     * 2. DFS 递归，把当前数字 num 作为参数向下传递。
     *    到达叶子节点时，num 就是这条路径的完整数字，加到结果中。
     *
     * 3. 可以用返回值的方式：叶子返回 num，非叶子返回左右子树返回值之和。
     *
     * 【举例】root = [4,9,0,5,1]
     *       4
     *      / \
     *     9    0
     *    / \
     *   5   1
     *
     *   4 → num=4
     *     9 → num=49
     *       5 → num=495（叶子，返回495）
     *       1 → num=491（叶子，返回491）
     *     返回 986
     *     0 → num=40（叶子，返回40）
     *   返回 986+40=1026
     *
     * 【时间复杂度】O(n) 每个节点访问一次
     * 【空间复杂度】O(h) 递归栈深度，h 为树高
     */
    public int sumNumbersDfs(TreeNode root) {
        return dfs(root, 0);
    }

    private int dfs(TreeNode node, int num) {
        if (node == null) return 0;
        num = num * 10 + node.val;
        if (node.left == null && node.right == null) {
            return num;
        }
        return dfs(node.left, num) + dfs(node.right, num);
    }

    /**
     * ==================== 解法二：BFS 迭代 ====================
     *
     * 【核心思路】
     * 用 BFS 层序遍历，队列中存储 (节点, 从根到该节点形成的数字)。
     * 当遇到叶子节点时，将该数字累加到结果中。
     *
     * 【思考过程】
     * 1. BFS 也可以做路径计算，只需在队列中同时维护"到当前节点的数字"。
     *
     * 2. 入队时计算：子节点对应的数字 = 父节点数字 * 10 + 子节点值。
     *
     * 3. 出队时判断是否叶子：是叶子就累加，不是叶子就继续入队子节点。
     *
     * 【举例】root = [1,2,3]
     *     1
     *    / \
     *   2   3
     *
     *   队列初始：[(1, 1)]
     *   出队(1,1)：非叶子，入队 (2, 12), (3, 13)
     *   出队(2,12)：叶子，total += 12 → total=12
     *   出队(3,13)：叶子，total += 13 → total=25
     *   返回 25
     *
     * 【时间复杂度】O(n) 每个节点访问一次
     * 【空间复杂度】O(n) 队列最多存储一层的节点数
     */
    public int sumNumbersBfs(TreeNode root) {
        if (root == null) return 0;

        int total = 0;
        Queue<TreeNode> nodeQueue = new LinkedList<>();
        Queue<Integer> numQueue = new LinkedList<>();
        nodeQueue.offer(root);
        numQueue.offer(root.val);

        while (!nodeQueue.isEmpty()) {
            TreeNode node = nodeQueue.poll();
            int num = numQueue.poll();

            if (node.left == null && node.right == null) {
                total += num;
                continue;
            }
            if (node.left != null) {
                nodeQueue.offer(node.left);
                numQueue.offer(num * 10 + node.left.val);
            }
            if (node.right != null) {
                nodeQueue.offer(node.right);
                numQueue.offer(num * 10 + node.right.val);
            }
        }

        return total;
    }
}
