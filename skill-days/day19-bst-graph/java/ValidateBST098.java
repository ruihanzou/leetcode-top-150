/**
 * LeetCode 98. Validate Binary Search Tree
 * 难度: Medium
 *
 * 题目描述：
 * 给你一个二叉树的根节点 root，判断其是否是一个有效的二叉搜索树。
 * 有效 BST 定义如下：
 * - 节点的左子树只包含 小于 当前节点的数。
 * - 节点的右子树只包含 大于 当前节点的数。
 * - 所有左子树和右子树自身必须也是二叉搜索树。
 *
 * 示例 1：root = [2,1,3] → 输出 true
 * 示例 2：root = [5,1,4,null,null,3,6] → 输出 false
 *   解释：根节点的值是 5，但右子节点的值是 4，不满足 BST 定义。
 *
 * 【拓展练习】
 * 1. LeetCode 94. Binary Tree Inorder Traversal —— 中序遍历基础
 * 2. LeetCode 501. Find Mode in Binary Search Tree —— BST 中找众数
 * 3. LeetCode 700. Search in a Binary Search Tree —— BST 查找
 */

import java.util.ArrayDeque;
import java.util.Deque;

class ValidateBST098 {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * ==================== 解法一：递归 + 范围约束 ====================
     *
     * 【核心思路】
     * 对每个节点维护一个合法的值范围 (low, high)。
     * 根节点范围是 (-∞, +∞)；
     * 往左子树走时，上界收紧为当前节点值；
     * 往右子树走时，下界收紧为当前节点值。
     * 如果某个节点的值不在其合法范围内，则不是有效 BST。
     *
     * 【思考过程】
     * 1. BST 要求左子树所有节点 < 根，不仅仅是直接左孩子。
     *    例如 [5,1,4,null,null,3,6] 中，3 在 5 的右子树中但 3 < 5，非法。
     *
     * 2. 仅仅检查 node.left.val < node.val 不够，
     *    需要检查左子树中"所有"节点都 < node.val。
     *    → 传递上下界：左子树的上界 = 父节点值，右子树的下界 = 父节点值。
     *
     * 3. 递归时不断收紧范围，任何节点越界即返回 false。
     *
     * 【举例】root = [5,1,4,null,null,3,6]
     *   validate(5, -∞, +∞)  → 5 在范围内 ✓
     *     validate(1, -∞, 5) → 1 在范围内 ✓
     *     validate(4, 5, +∞) → 4 不在 (5, +∞) 内 ✗ → 返回 false
     *
     * 【时间复杂度】O(n)，每个节点访问一次
     * 【空间复杂度】O(h)，递归栈深度为树高
     */
    public boolean isValidBSTRange(TreeNode root) {
        return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean validate(TreeNode node, long low, long high) {
        if (node == null) return true;
        if (node.val <= low || node.val >= high) return false;
        return validate(node.left, low, node.val)
            && validate(node.right, node.val, high);
    }

    /**
     * ==================== 解法二：中序遍历（检查严格递增） ====================
     *
     * 【核心思路】
     * BST 的中序遍历一定是严格递增序列。
     * 因此只要在中序遍历过程中检查当前值是否严格大于前一个值即可。
     *
     * 【思考过程】
     * 1. BST 性质 → 中序遍历有序，这是 BST 最重要的性质之一。
     *
     * 2. 如果中序遍历中出现 当前值 <= 前一个值，说明 BST 不合法。
     *
     * 3. 用迭代版中序遍历（栈模拟），在每次"访问"节点时与前一个值比较。
     *    用 long prev 记录上一个访问的值（初始为 Long.MIN_VALUE）。
     *
     * 4. 注意题目要求"严格"小于/大于，不允许相等。
     *
     * 【举例】root = [2,1,3]
     *   中序遍历序列：1 → 2 → 3
     *   1 > -∞ ✓, 2 > 1 ✓, 3 > 2 ✓ → 返回 true
     *
     *   root = [5,1,4,null,null,3,6]
     *   中序遍历：1 → 5 → 3 ...
     *   1 > -∞ ✓, 5 > 1 ✓, 3 > 5 ✗ → 返回 false
     *
     * 【时间复杂度】O(n)，每个节点访问一次
     * 【空间复杂度】O(h)，栈的深度为树高
     */
    public boolean isValidBSTInorder(TreeNode root) {
        Deque<TreeNode> stack = new ArrayDeque<>();
        long prev = Long.MIN_VALUE;
        TreeNode curr = root;

        while (curr != null || !stack.isEmpty()) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }
            curr = stack.pop();
            if (curr.val <= prev) return false;
            prev = curr.val;
            curr = curr.right;
        }

        return true;
    }
}
