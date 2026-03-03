/**
 * LeetCode 108. Convert Sorted Array to Binary Search Tree
 * 难度: Easy
 *
 * 题目描述：
 * 给你一个整数数组 nums，其中元素已经按升序排列，请你将其转换为一棵高度平衡的二叉搜索树。
 * 高度平衡二叉树是指一棵树中每个节点的左右两个子树的高度差的绝对值不超过 1。
 *
 * 示例 1：nums = [-10,-3,0,5,9] → [0,-3,9,-10,null,5]（取中间元素0为根）
 * 示例 2：nums = [1,3] → [3,1] 或 [1,null,3]
 *
 * 【拓展练习】
 * 1. LeetCode 109. Convert Sorted List to Binary Search Tree —— 有序链表转BST，中序模拟
 * 2. LeetCode 1382. Balance a Binary Search Tree —— 将BST重新平衡
 * 3. LeetCode 105. Construct Binary Tree from Preorder and Inorder Traversal —— 从遍历序列建树
 */

class SortedArrayToBST108 {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * ==================== 解法一：递归（取中间元素为根） ====================
     *
     * 【核心思路】
     * 有序数组天然对应 BST 的中序遍历。取中间元素作为根节点，
     * 左半部分递归构建左子树，右半部分递归构建右子树，即可得到高度平衡的 BST。
     *
     * 【思考过程】
     * 1. BST 的中序遍历就是有序序列。反过来，给定有序序列，
     *    如何构建一棵"尽可能平衡"的 BST？
     *    → 让根节点把序列尽可能等分为两半。
     *
     * 2. 取 mid = (left + right) / 2 作为根，
     *    nums[left..mid-1] 构建左子树，nums[mid+1..right] 构建右子树。
     *    左右子树的大小差最多为 1，所以高度差也最多为 1。
     *
     * 3. 递归基：left > right 时返回 null（空子树）。
     *
     * 【举例】nums = [-10, -3, 0, 5, 9]
     *   mid=2, root=0
     *   左子树：[-10, -3], mid=0, root=-10
     *     右子树：[-3], root=-3
     *   右子树：[5, 9], mid=3, root=5
     *     右子树：[9], root=9
     *   结果：     0
     *           /     \
     *        -10       5
     *          \        \
     *          -3        9
     *
     * 【时间复杂度】O(n)，每个元素恰好访问一次
     * 【空间复杂度】O(log n) 递归栈深度
     */
    public TreeNode sortedArrayToBST(int[] nums) {
        return build(nums, 0, nums.length - 1);
    }

    private TreeNode build(int[] nums, int left, int right) {
        if (left > right) return null;
        int mid = left + (right - left) / 2;
        TreeNode node = new TreeNode(nums[mid]);
        node.left = build(nums, left, mid - 1);
        node.right = build(nums, mid + 1, right);
        return node;
    }

    /**
     * ==================== 解法二：选取右中间节点 ====================
     *
     * 【核心思路】
     * 当数组长度为偶数时，中间位置有两个候选：左中间 mid = (left+right)/2
     * 和右中间 mid = (left+right+1)/2。两者都能生成合法的高度平衡 BST，
     * 但树的结构不同。本解法选择右中间节点。
     *
     * 【思考过程】
     * 1. 解法一中 mid = (left+right)/2 总是选偏左的中间节点。
     *    当区间长度为偶数时，左子树比右子树多一个节点。
     *
     * 2. 如果改为 mid = (left+right+1)/2（向上取整），
     *    则选偏右的中间节点，右子树可能多一个节点。
     *
     * 3. 两种选法都满足"高度平衡"的要求，但得到的树不同。
     *    这说明答案不唯一，题目也允许返回任意一个合法的 BST。
     *
     * 【举例】nums = [1, 3]
     *   左中间（解法一）: mid=0, root=1, 右子树=[3]
     *     1
     *      \
     *       3
     *   右中间（本解法）: mid=1, root=3, 左子树=[1]
     *     3
     *    /
     *   1
     *   两种都是合法的高度平衡BST。
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(log n) 递归栈深度
     */
    public TreeNode sortedArrayToBSTRight(int[] nums) {
        return buildRight(nums, 0, nums.length - 1);
    }

    private TreeNode buildRight(int[] nums, int left, int right) {
        if (left > right) return null;
        int mid = left + (right - left + 1) / 2;
        TreeNode node = new TreeNode(nums[mid]);
        node.left = buildRight(nums, left, mid - 1);
        node.right = buildRight(nums, mid + 1, right);
        return node;
    }
}
