/**
 * LeetCode 103. Binary Tree Zigzag Level Order Traversal
 * 难度: Medium
 *
 * 题目描述：
 * 给定二叉树的根节点 root，返回其节点值的锯齿形层序遍历。
 * （即先从左往右，再从右往左进行下一层遍历，以此类推，层与层之间交替进行。）
 *
 * 示例：
 *     3
 *    / \
 *   9  20
 *     /  \
 *    15   7
 * 输出: [[3], [20, 9], [15, 7]]
 * 解释: 第0层从左到右: [3]
 *       第1层从右到左: [20, 9]
 *       第2层从左到右: [15, 7]
 *
 * 【拓展练习】
 * 1. LeetCode 102. Binary Tree Level Order Traversal —— 基础层序遍历
 * 2. LeetCode 107. Binary Tree Level Order Traversal II —— 自底向上层序遍历
 * 3. LeetCode 199. Binary Tree Right Side View —— 每层最右节点
 */

import java.util.*;

class ZigzagLevelOrder103 {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * ==================== 解法一：BFS + 奇偶层翻转 ====================
     *
     * 【核心思路】
     * 和标准层序遍历一样用队列 BFS 逐层处理，
     * 唯一的区别是：奇数层（1, 3, 5...）的结果列表需要翻转。
     *
     * 【思考过程】
     * 1. 锯齿形遍历的本质是：偶数层（0, 2, 4...）从左到右，
     *    奇数层（1, 3, 5...）从右到左。
     *
     * 2. BFS 自然地从左到右遍历每层节点（因为入队顺序是先左后右）。
     *    对于需要反向的层，最简单的做法是正常遍历后翻转该层的结果列表。
     *
     * 3. 翻转操作 O(k)（k 为该层节点数），所有层翻转总计 O(n)，
     *    不影响总体时间复杂度。
     *
     * 【举例】root = [3, 9, 20, null, null, 15, 7]
     *   第0层(偶): queue=[3]     → level=[3]      (不翻转) → [3]
     *   第1层(奇): queue=[9,20]  → level=[9,20]   (翻转)   → [20,9]
     *   第2层(偶): queue=[15,7]  → level=[15,7]   (不翻转) → [15,7]
     *   结果: [[3], [20,9], [15,7]]
     *
     * 【时间复杂度】O(n) 每个节点访问一次，翻转总计 O(n)
     * 【空间复杂度】O(n) 队列最多存放一层的节点
     */
    public List<List<Integer>> zigzagLevelOrderReverse(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }

            if (!leftToRight) {
                Collections.reverse(level);
            }

            result.add(level);
            leftToRight = !leftToRight;
        }

        return result;
    }

    /**
     * ==================== 解法二：BFS + 双端队列插入 ====================
     *
     * 【核心思路】
     * 在 BFS 逐层遍历时，用双端队列收集当前层节点值：
     * - 偶数层：从右端 addLast（正常从左到右顺序）
     * - 奇数层：从左端 addFirst（实现从右到左顺序）
     * 这样避免了事后翻转。
     *
     * 【思考过程】
     * 1. 解法一在奇数层需要翻转，能否在遍历时直接得到正确顺序？
     *
     * 2. 思路：遍历节点的顺序不变（始终从左到右出队），
     *    但收集节点值时改变插入方向。
     *    - 从左到右层：addLast → 顺序 [a, b, c]
     *    - 从右到左层：addFirst → 顺序 [c, b, a]
     *
     * 3. 注意：子节点入队顺序始终是先左后右，不改变。
     *    只改变"值插入 level 的方向"。
     *
     * 【举例】root = [3, 9, 20, null, null, 15, 7]
     *   第0层(→): 出队3, addLast → level=[3]
     *   第1层(←): 出队9, addFirst → [9];
     *             出队20, addFirst → [20, 9]
     *   第2层(→): 出队15, addLast → [15];
     *             出队7, addLast → [15, 7]
     *   结果: [[3], [20,9], [15,7]]
     *
     * 【时间复杂度】O(n) 每个节点访问一次
     * 【空间复杂度】O(n) 队列最多存放一层的节点
     */
    public List<List<Integer>> zigzagLevelOrderDeque(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            LinkedList<Integer> level = new LinkedList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();

                if (leftToRight) {
                    level.addLast(node.val);
                } else {
                    level.addFirst(node.val);
                }

                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }

            result.add(level);
            leftToRight = !leftToRight;
        }

        return result;
    }
}
