/**
 * LeetCode 101. Symmetric Tree
 * 难度: Easy
 *
 * 题目描述：
 * 给你一个二叉树的根节点 root，检查它是否轴对称。
 * 即判断这棵二叉树是否是镜像对称的（左右子树互为镜像）。
 *
 * 示例 1：root = [1,2,2,3,4,4,3] → 输出 true
 *   解释：
 *       1
 *      / \
 *     2   2
 *    / \ / \
 *   3  4 4  3
 *   左右子树互为镜像。
 * 示例 2：root = [1,2,2,null,3,null,3] → 输出 false
 *   解释：
 *       1
 *      / \
 *     2   2
 *      \   \
 *       3   3
 *   不对称。
 *
 * 【拓展练习】
 * 1. LeetCode 100. Same Tree —— 判断两棵树是否相同（对称的基础）
 * 2. LeetCode 226. Invert Binary Tree —— 翻转二叉树（对称的构造版）
 * 3. LeetCode 951. Flip Equivalent Binary Trees —— 翻转等价二叉树
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

class SymmetricTree101 {

    /**
     * ==================== 解法一：递归（镜像比较） ====================
     *
     * 【核心思路】
     * 对称的定义：左子树和右子树互为镜像。
     * 两棵树互为镜像 ⇔ 根值相同，且 A 的左子树与 B 的右子树镜像，A 的右子树与 B 的左子树镜像。
     *
     * 【思考过程】
     * 1. "对称"本质上是在问：root.left 和 root.right 是否互为镜像。
     * 2. 两棵树"镜像"可以递归定义：
     *    - 都为 null → 镜像
     *    - 一个 null → 不镜像
     *    - 值不等 → 不镜像
     *    - 值相等 → 左A 的左 与 右B 的右 镜像，且 左A 的右 与 右B 的左 镜像
     * 3. 这和 Same Tree 类似，只是比较方向"交叉"了。
     *
     * 【举例】root = [1,2,2,3,4,4,3]
     *   isSymmetric(1)
     *     → isMirror(2, 2)
     *       值相等 2==2
     *       isMirror(2.left=3, 2.right=3) → 3==3, 叶子 → true
     *       isMirror(2.right=4, 2.left=4) → 4==4, 叶子 → true
     *     → true
     *
     * 【时间复杂度】O(n)，每个节点访问一次
     * 【空间复杂度】O(h)，递归栈深度，最坏 O(n)
     */
    public boolean isSymmetricRecursive(TreeNode root) {
        if (root == null) return true;
        return isMirror(root.left, root.right);
    }

    private boolean isMirror(TreeNode left, TreeNode right) {
        if (left == null && right == null) return true;
        if (left == null || right == null) return false;
        return left.val == right.val
                && isMirror(left.left, right.right)
                && isMirror(left.right, right.left);
    }

    /**
     * ==================== 解法二：迭代（队列成对比较） ====================
     *
     * 【核心思路】
     * 用队列模拟递归过程：每次入队一对需要互为镜像的节点，
     * 出队后检查值是否相等，再将它们的子节点按镜像顺序入队。
     *
     * 【思考过程】
     * 1. 递归的镜像比较可以用队列展开：
     *    - 初始入队 (root.left, root.right)
     *    - 每次取出一对 (a, b)：
     *      都 null → 继续；一个 null → false；值不等 → false
     *      值相等 → 入队 (a.left, b.right) 和 (a.right, b.left)
     * 2. 这本质上就是把递归调用的参数放进了队列。
     * 3. 用栈也可以，只是遍历顺序不同，结果一致。
     *
     * 【举例】root = [1,2,2,null,3,null,3]
     *   queue = [2, 2]
     *   取出 (2,2)：值相等，入队 (null,null) 和 (3,3)... 看起来没问题？
     *   但仔细看镜像规则：入队 (2.left, 2.right) 和 (2.right, 2.left)
     *   → 入队 (null, null) 和 (3, null)
     *   取出 (null, null) → continue
     *   取出 (3, null) → 一个为 null → false
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(n)，队列空间
     */
    public boolean isSymmetricIterative(TreeNode root) {
        if (root == null) return true;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root.left);
        queue.offer(root.right);

        while (!queue.isEmpty()) {
            TreeNode a = queue.poll();
            TreeNode b = queue.poll();

            if (a == null && b == null) continue;
            if (a == null || b == null) return false;
            if (a.val != b.val) return false;

            queue.offer(a.left);
            queue.offer(b.right);
            queue.offer(a.right);
            queue.offer(b.left);
        }

        return true;
    }
}
