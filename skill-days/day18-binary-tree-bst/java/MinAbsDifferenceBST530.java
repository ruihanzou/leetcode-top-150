/**
 * LeetCode 530. Minimum Absolute Difference in BST
 * 难度: Easy
 *
 * 题目描述：
 * 给你一棵二叉搜索树的根节点 root，返回树中任意两不同节点值之间的最小差值。
 * 差值是一个正数，其数值等于两值之差的绝对值。
 *
 * 示例：
 *     4
 *    / \
 *   2   6
 *  / \
 * 1   3
 * 输出: 1
 * 解释: 最小绝对差为 1（2-1=1 或 3-2=1 或 4-3=1）
 *
 * 注意: 本题与 LeetCode 783. Minimum Distance Between BST Nodes 相同。
 *
 * 【拓展练习】
 * 1. LeetCode 783. Minimum Distance Between BST Nodes —— 同题
 * 2. LeetCode 94. Binary Tree Inorder Traversal —— 中序遍历基础
 * 3. LeetCode 98. Validate Binary Search Tree —— BST 性质验证
 */

class MinAbsDifferenceBST530 {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * ==================== 解法一：中序遍历（递归） ====================
     *
     * 【核心思路】
     * BST 的中序遍历结果是严格递增序列。
     * 最小绝对差一定出现在中序序列的相邻两个元素之间。
     * 因此只需中序遍历时维护前一个节点的值，逐步更新最小差值。
     *
     * 【思考过程】
     * 1. BST 的关键性质：中序遍历 = 升序排列。
     *
     * 2. 对于一个升序序列 [a1, a2, ..., an]，
     *    任意两元素之差 |ai - aj| 的最小值一定出现在相邻元素 ai+1 - ai 中。
     *    证明：若 i < j < k，则 ak - ai = (ak - aj) + (aj - ai) >= aj - ai。
     *
     * 3. 因此只需在中序遍历过程中，比较当前节点与前一个节点的差值，
     *    维护全局最小值即可。用 prev 记录前一个访问的节点值。
     *
     * 【举例】root = [4, 2, 6, 1, 3]
     *   中序遍历: 1 → 2 → 3 → 4 → 6
     *   差值:     2-1=1, 3-2=1, 4-3=1, 6-4=2
     *   最小差值: 1
     *
     * 【时间复杂度】O(n) 中序遍历每个节点一次
     * 【空间复杂度】O(h) 递归栈深度，h 为树的高度
     */
    private int minDiff = Integer.MAX_VALUE;
    private Integer prev = null;

    public int getMinimumDifferenceInorder(TreeNode root) {
        minDiff = Integer.MAX_VALUE;
        prev = null;
        inorder(root);
        return minDiff;
    }

    private void inorder(TreeNode node) {
        if (node == null) return;

        inorder(node.left);

        if (prev != null) {
            minDiff = Math.min(minDiff, node.val - prev);
        }
        prev = node.val;

        inorder(node.right);
    }

    /**
     * ==================== 解法二：Morris 中序遍历 ====================
     *
     * 【核心思路】
     * 使用 Morris 遍历在 O(1) 额外空间下完成中序遍历。
     * Morris 遍历通过临时修改树的结构（利用空闲的右指针建立线索）
     * 来代替递归栈，实现常数空间的中序遍历。
     *
     * 【思考过程】
     * 1. 解法一的递归栈占用 O(h) 空间，能否做到 O(1)？
     *    → Morris 遍历：利用叶节点的空右指针建立回溯线索。
     *
     * 2. Morris 中序遍历的核心逻辑：
     *    对于当前节点 curr：
     *    a) 如果 curr 没有左子树 → 访问 curr，移到 curr.right
     *    b) 如果 curr 有左子树 → 找到左子树的最右节点（中序前驱）pred
     *       - 如果 pred.right 为空 → 建线索 pred.right = curr，移到 curr.left
     *       - 如果 pred.right == curr → 已遍历完左子树，断线索，访问 curr，移到 curr.right
     *
     * 3. "访问节点"时就和解法一一样：计算与 prev 的差值，更新最小值。
     *
     * 【举例】root = [4, 2, 6, 1, 3]
     *   Morris 遍历过程（只标记"访问"时刻）：
     *   访问1: prev=null → 不比较
     *   访问2: prev=1 → diff=1, minDiff=1
     *   访问3: prev=2 → diff=1, minDiff=1
     *   访问4: prev=3 → diff=1, minDiff=1
     *   访问6: prev=4 → diff=2, minDiff=1
     *   结果: 1
     *
     * 【时间复杂度】O(n) 每条边最多被访问两次（建线索+断线索）
     * 【空间复杂度】O(1) 只用常数个变量
     */
    public int getMinimumDifferenceMorris(TreeNode root) {
        int result = Integer.MAX_VALUE;
        Integer prevVal = null;
        TreeNode curr = root;

        while (curr != null) {
            if (curr.left == null) {
                if (prevVal != null) {
                    result = Math.min(result, curr.val - prevVal);
                }
                prevVal = curr.val;
                curr = curr.right;
            } else {
                TreeNode pred = curr.left;
                while (pred.right != null && pred.right != curr) {
                    pred = pred.right;
                }

                if (pred.right == null) {
                    pred.right = curr;
                    curr = curr.left;
                } else {
                    pred.right = null;
                    if (prevVal != null) {
                        result = Math.min(result, curr.val - prevVal);
                    }
                    prevVal = curr.val;
                    curr = curr.right;
                }
            }
        }

        return result;
    }
}
