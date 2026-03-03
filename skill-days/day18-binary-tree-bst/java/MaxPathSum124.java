/**
 * LeetCode 124. Binary Tree Maximum Path Sum
 * 难度: Hard
 *
 * 题目描述：
 * 二叉树中的「路径」被定义为一条节点序列，序列中每对相邻节点之间都存在一条边。
 * 同一个节点在一条路径中最多出现一次。该路径至少包含一个节点，且不一定经过根节点。
 * 路径和是路径中各节点值的总和。
 * 给你一个二叉树的根节点 root，返回其最大路径和。
 *
 * 示例 1：root = [1,2,3] → 输出 6（路径 2→1→3）
 * 示例 2：root = [-10,9,20,null,null,15,7] → 输出 42（路径 15→20→7）
 *
 * 【拓展练习】
 * 1. LeetCode 112. Path Sum —— 判断是否存在根到叶子的路径和等于目标值
 * 2. LeetCode 543. Diameter of Binary Tree —— 求二叉树直径（最长路径的边数）
 * 3. LeetCode 687. Longest Univalue Path —— 最长同值路径
 */

class MaxPathSum124 {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * ==================== 解法一：DFS后序遍历 + 全局变量 ====================
     *
     * 【核心思路】
     * 对每个节点 node，计算"经过 node 的最大路径和"（左链 + node + 右链），
     * 用全局变量记录所有节点中的最大值。同时，向父节点返回"以 node 为端点的
     * 最大单链和"（node + max(左链, 右链)），因为路径不能分叉。
     *
     * 【思考过程】
     * 1. 路径可以从任意节点出发到任意节点，可能经过也可能不经过根。
     *    关键观察：任何路径都有一个"最高点"（拐弯处），路径在这个节点处
     *    从左子树上来、经过该节点、再到右子树下去（或只走一边）。
     *
     * 2. 对于每个节点 node，经过它的最大路径和 = leftGain + node.val + rightGain，
     *    其中 leftGain = max(0, 左子树能贡献的最大单链和)，
     *    rightGain = max(0, 右子树能贡献的最大单链和)。
     *    取 max(0, ...) 是因为如果子树贡献为负，不如不走。
     *
     * 3. 但向父节点汇报时，只能选一条链（左或右），不能两边都选，
     *    否则父节点那里路径就分叉了。所以返回 node.val + max(leftGain, rightGain)。
     *
     * 4. 遍历所有节点，取"经过该节点的路径和"的最大值，就是答案。
     *
     * 【举例】root = [-10, 9, 20, null, null, 15, 7]
     *           -10
     *           / \
     *          9   20
     *             / \
     *            15   7
     *
     *   节点15: leftGain=0, rightGain=0, 经过路径和=15, 返回15
     *   节点7:  leftGain=0, rightGain=0, 经过路径和=7, 返回7
     *   节点20: leftGain=15, rightGain=7, 经过路径和=15+20+7=42, 返回20+15=35
     *   节点9:  leftGain=0, rightGain=0, 经过路径和=9, 返回9
     *   节点-10: leftGain=9, rightGain=35, 经过路径和=9+(-10)+35=34, 返回-10+35=25
     *   全局最大值 = max(15, 7, 42, 9, 34) = 42
     *
     * 【时间复杂度】O(n)，每个节点访问一次
     * 【空间复杂度】O(h)，递归栈深度，h 为树高
     */
    private int maxSum;

    public int maxPathSum(TreeNode root) {
        maxSum = Integer.MIN_VALUE;
        dfs(root);
        return maxSum;
    }

    private int dfs(TreeNode node) {
        if (node == null) return 0;

        int leftGain = Math.max(0, dfs(node.left));
        int rightGain = Math.max(0, dfs(node.right));

        maxSum = Math.max(maxSum, leftGain + node.val + rightGain);

        return node.val + Math.max(leftGain, rightGain);
    }

    /**
     * ==================== 解法二：DFS + 数组传递全局最大值 ====================
     *
     * 【核心思路】
     * 与解法一逻辑完全相同，但不使用类的实例变量来保存全局最大值，
     * 而是通过 int[] 数组参数传递（Java 中基本类型按值传递，
     * 需要用数组或包装对象来模拟引用传递）。
     *
     * 【思考过程】
     * 1. 解法一用实例变量 maxSum，但在某些场景（如多线程、函数式风格）中，
     *    我们希望避免使用共享可变状态。
     *
     * 2. Java 不支持基本类型的引用传递，但数组是对象，传递的是引用。
     *    用 int[1] 来模拟一个可变的整数引用。
     *
     * 3. 核心逻辑不变：后序遍历，每个节点计算经过它的最大路径和并更新全局最大值，
     *    向父节点返回最大单链和。
     *
     * 【时间复杂度】O(n)，每个节点访问一次
     * 【空间复杂度】O(h)，递归栈深度
     */
    public int maxPathSumArray(TreeNode root) {
        int[] result = { Integer.MIN_VALUE };
        dfsWithArray(root, result);
        return result[0];
    }

    private int dfsWithArray(TreeNode node, int[] result) {
        if (node == null) return 0;

        int leftGain = Math.max(0, dfsWithArray(node.left, result));
        int rightGain = Math.max(0, dfsWithArray(node.right, result));

        result[0] = Math.max(result[0], leftGain + node.val + rightGain);

        return node.val + Math.max(leftGain, rightGain);
    }
}
