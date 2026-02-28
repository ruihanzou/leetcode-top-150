/**
 * LeetCode 106. Construct Binary Tree from Inorder and Postorder Traversal
 * 难度: Medium
 *
 * 题目描述：
 * 给定两个整数数组 inorder 和 postorder，其中 inorder 是二叉树的中序遍历，
 * postorder 是同一棵树的后序遍历，请构造并返回这棵二叉树。
 *
 * 示例：inorder = [9,3,15,20,7], postorder = [9,15,7,20,3] → 输出 [3,9,20,null,null,15,7]
 *
 * 【拓展练习】
 * 1. LeetCode 105. Construct Binary Tree from Preorder and Inorder Traversal —— 类似思路，前序第一个是根
 * 2. LeetCode 889. Construct Binary Tree from Preorder and Postorder Traversal —— 前序+后序构造（不唯一）
 * 3. LeetCode 654. Maximum Binary Tree —— 递归构造思路的变体
 */

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

class ConstructFromInorderPostorder106 {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * ==================== 解法一：递归 + 哈希表 ====================
     *
     * 【核心思路】
     * 后序遍历的最后一个元素是当前子树的根节点。
     * 在中序遍历中找到该根节点的位置，就能划分出左子树和右子树，
     * 然后递归地对左右子树重复这个过程。
     *
     * 【思考过程】
     * 1. 后序遍历顺序：左 → 右 → 根，所以 postorder 的最后一个元素一定是根。
     *    中序遍历顺序：左 → 根 → 右，根把中序数组分成左右两半。
     *
     * 2. 在中序数组中找到根的位置 rootIdx，则：
     *    - 左子树的中序部分：inorder[inStart .. rootIdx-1]，共 leftSize 个节点
     *    - 右子树的中序部分：inorder[rootIdx+1 .. inEnd]
     *
     * 3. 对应到后序数组：
     *    - 左子树的后序部分：postorder[postStart .. postStart+leftSize-1]
     *    - 右子树的后序部分：postorder[postStart+leftSize .. postEnd-1]
     *    - 根：postorder[postEnd]
     *
     * 4. 每次在中序数组中查找根需要 O(n)，用哈希表预处理后降为 O(1)。
     *
     * 【举例】inorder = [9,3,15,20,7], postorder = [9,15,7,20,3]
     *   postorder 最后元素 3 是根 → 在 inorder 中 3 的位置是 index=1
     *   左子树：inorder=[9], postorder=[9] → 节点9
     *   右子树：inorder=[15,20,7], postorder=[15,7,20]
     *     postorder 最后 20 是根 → inorder 中 20 的位置 index=3
     *     左子树：inorder=[15], postorder=[15] → 节点15
     *     右子树：inorder=[7], postorder=[7] → 节点7
     *   结果：    3
     *           / \
     *          9   20
     *             / \
     *            15   7
     *
     * 【时间复杂度】O(n) 每个节点处理一次，哈希表查找 O(1)
     * 【空间复杂度】O(n) 哈希表 + 递归栈 O(h)
     */
    private Map<Integer, Integer> idxMap;
    private int[] post;

    public TreeNode buildTreeRecursive(int[] inorder, int[] postorder) {
        idxMap = new HashMap<>();
        post = postorder;
        for (int i = 0; i < inorder.length; i++) {
            idxMap.put(inorder[i], i);
        }
        return build(0, inorder.length - 1, 0, postorder.length - 1);
    }

    private TreeNode build(int inStart, int inEnd, int postStart, int postEnd) {
        if (inStart > inEnd) return null;

        int rootVal = post[postEnd];
        TreeNode root = new TreeNode(rootVal);

        int rootIdx = idxMap.get(rootVal);
        int leftSize = rootIdx - inStart;

        root.left = build(inStart, rootIdx - 1,
                          postStart, postStart + leftSize - 1);
        root.right = build(rootIdx + 1, inEnd,
                           postStart + leftSize, postEnd - 1);
        return root;
    }

    /**
     * ==================== 解法二：迭代 ====================
     *
     * 【核心思路】
     * 从后序数组的末尾向前遍历（即逆后序：根 → 右 → 左），
     * 同时用中序数组从末尾向前作为"右边界检测"。
     * 用栈模拟递归过程：当前节点默认挂为栈顶的右子节点，
     * 如果遇到中序数组的匹配，说明需要回溯找到正确的父节点再挂左子节点。
     *
     * 【思考过程】
     * 1. 后序逆序 = 根→右→左。这意味着我们先处理根，然后右子树，最后左子树。
     *    这很像前序（根→左→右）的镜像。
     *
     * 2. 中序逆序 = 右→根→左。中序逆序可以帮助我们检测"右子树是否已经结束"。
     *
     * 3. 从 postorder 末尾开始，第一个是整棵树的根。接下来的节点要么是右子节点，
     *    要么需要回溯后成为某个祖先的左子节点。
     *
     * 4. 用栈维护当前路径。每次取 postorder 的下一个节点：
     *    - 如果栈顶 != inorder 当前指针，说明还在往右走，挂为右子节点。
     *    - 否则不断弹栈匹配 inorder（回溯），最后一个弹出的就是左子节点的父亲。
     *
     * 【举例】inorder = [9,3,15,20,7], postorder = [9,15,7,20,3]
     *   逆后序：3, 20, 7, 15, 9
     *   逆中序：7, 20, 15, 3, 9
     *
     *   step1: node=3，栈空，根节点。栈=[3]
     *   step2: node=20，栈顶3≠7，20是3的右子。栈=[3,20]
     *   step3: node=7，栈顶20≠7? 20≠7，7是20的右子。栈=[3,20,7]
     *   step4: 栈顶7==7 ✓，弹出7。栈顶20==20 ✓，弹出20。栈顶3≠15。
     *          node=15是最后弹出节点20的左子。栈=[3,15]
     *   step5: 栈顶15==15 ✓，弹出。栈顶3==3 ✓，弹出。
     *          node=9是最后弹出节点3的左子。栈=[9]
     *
     * 【时间复杂度】O(n) 每个节点入栈出栈各一次
     * 【空间复杂度】O(n) 栈空间
     */
    public TreeNode buildTreeIterative(int[] inorder, int[] postorder) {
        if (postorder.length == 0) return null;

        TreeNode root = new TreeNode(postorder[postorder.length - 1]);
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);
        int inIdx = inorder.length - 1;

        for (int i = postorder.length - 2; i >= 0; i--) {
            TreeNode node = new TreeNode(postorder[i]);
            if (stack.peek().val != inorder[inIdx]) {
                stack.peek().right = node;
            } else {
                TreeNode parent = null;
                while (!stack.isEmpty() && stack.peek().val == inorder[inIdx]) {
                    parent = stack.pop();
                    inIdx--;
                }
                parent.left = node;
            }
            stack.push(node);
        }

        return root;
    }
}
