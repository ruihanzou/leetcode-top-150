/**
 * LeetCode 105. Construct Binary Tree from Preorder and Inorder Traversal
 * 难度: Medium
 *
 * 题目描述：
 * 给定两个整数数组 preorder 和 inorder，其中 preorder 是二叉树的前序遍历，
 * inorder 是同一棵树的中序遍历，请构造二叉树并返回其根节点。
 * 假设树中没有重复元素。
 *
 * 示例 1：preorder = [3,9,20,15,7], inorder = [9,3,15,20,7] → 输出 [3,9,20,null,null,15,7]
 *   解释：
 *       3
 *      / \
 *     9  20
 *       /  \
 *      15   7
 * 示例 2：preorder = [-1], inorder = [-1] → 输出 [-1]
 *
 * 【拓展练习】
 * 1. LeetCode 106. Construct Binary Tree from Inorder and Postorder Traversal —— 中序+后序建树
 * 2. LeetCode 889. Construct Binary Tree from Preorder and Postorder Traversal —— 前序+后序建树
 * 3. LeetCode 297. Serialize and Deserialize Binary Tree —— 二叉树的序列化与反序列化
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

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayDeque;
import java.util.Deque;

class ConstructFromPreorderInorder105 {

    /**
     * ==================== 解法一：递归 + 哈希表 ====================
     *
     * 【核心思路】
     * 前序遍历的第一个元素是根节点。在中序遍历中找到根节点的位置，
     * 位置左边就是左子树的中序遍历，右边就是右子树的中序遍历。
     * 根据左子树的节点个数，可以在前序遍历中划分出左子树和右子树的前序遍历。
     * 递归构建左右子树。用哈希表加速中序遍历中根节点位置的查找。
     *
     * 【思考过程】
     * 1. 前序遍历特征：[根, 左子树..., 右子树...]
     *    中序遍历特征：[左子树..., 根, 右子树...]
     *
     * 2. 通过前序的第一个元素确定根节点值，在中序中找到这个值的位置 idx，
     *    就知道了左子树有 idx - inStart 个节点。
     *
     * 3. 前序中左子树范围：[preStart+1, preStart+leftSize]
     *    前序中右子树范围：[preStart+leftSize+1, preEnd]
     *    中序中左子树范围：[inStart, idx-1]
     *    中序中右子树范围：[idx+1, inEnd]
     *
     * 4. 在中序中查找根节点位置，暴力需要 O(n)，用哈希表预处理可以 O(1) 查找。
     *
     * 【举例】preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]
     *   根 = 3，在 inorder 中 idx = 1
     *   左子树 leftSize = 1：pre=[9], in=[9] → 节点 9
     *   右子树：pre=[20,15,7], in=[15,20,7]
     *     根 = 20，在 inorder 中 idx = 3
     *     左子树：pre=[15], in=[15] → 节点 15
     *     右子树：pre=[7], in=[7] → 节点 7
     *   结果：3(9, 20(15, 7))
     *
     * 【时间复杂度】O(n)，哈希表查找 O(1)，每个节点处理一次
     * 【空间复杂度】O(n)，哈希表 + 递归栈
     */
    private Map<Integer, Integer> inorderMap;
    private int[] preorder;

    public TreeNode buildTreeRecursive(int[] preorder, int[] inorder) {
        this.preorder = preorder;
        inorderMap = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            inorderMap.put(inorder[i], i);
        }
        return build(0, preorder.length - 1, 0, inorder.length - 1);
    }

    private TreeNode build(int preStart, int preEnd, int inStart, int inEnd) {
        if (preStart > preEnd) return null;

        int rootVal = preorder[preStart];
        TreeNode root = new TreeNode(rootVal);

        int idx = inorderMap.get(rootVal);
        int leftSize = idx - inStart;

        root.left = build(preStart + 1, preStart + leftSize, inStart, idx - 1);
        root.right = build(preStart + leftSize + 1, preEnd, idx + 1, inEnd);

        return root;
    }

    /**
     * ==================== 解法二：迭代（栈） ====================
     *
     * 【核心思路】
     * 用栈模拟建树过程。遍历 preorder 数组，利用 inorder 来判断
     * 当前节点应该作为左子节点还是右子节点插入。
     *
     * 【思考过程】
     * 1. 前序遍历中，连续出现的节点总是"一路向左"或"回溯后向右"。
     *
     * 2. 维护一个栈，栈顶是当前最深的"等待分配右子树"的节点。
     *    用 inorder 的指针 inIdx 跟踪"下一个应该出现在中序遍历中的节点"。
     *
     * 3. 对于 preorder 中的每个值：
     *    - 如果栈顶 != inorder[inIdx]，说明还没到左子树的尽头，
     *      当前节点是栈顶的左子节点。
     *    - 如果栈顶 == inorder[inIdx]，说明左子树已遍历完，
     *      需要不断弹栈（同时 inIdx 前进），直到栈顶 != inorder[inIdx]，
     *      当前节点是最后弹出节点的右子节点。
     *
     * 4. 这本质上模拟了递归在系统栈上的行为。
     *
     * 【举例】preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]
     *   处理 3：栈空，3 是根。stack=[3], inIdx=0
     *   处理 9：栈顶 3 != in[0]=9 → 9 是 3 的左孩子。stack=[3,9], inIdx=0
     *   处理 20：栈顶 9 == in[0]=9 → 弹出 9, inIdx=1
     *           栈顶 3 == in[1]=3 → 弹出 3, inIdx=2
     *           栈空 → 20 是最后弹出节点 3 的右孩子。stack=[20], inIdx=2
     *   处理 15：栈顶 20 != in[2]=15 → 15 是 20 的左孩子。stack=[20,15], inIdx=2
     *   处理 7：栈顶 15 == in[2]=15 → 弹出 15, inIdx=3
     *          栈顶 20 == in[3]=20 → 弹出 20, inIdx=4
     *          栈空 → 7 是最后弹出节点 20 的右孩子。stack=[7]
     *
     * 【时间复杂度】O(n)，每个节点入栈出栈各一次
     * 【空间复杂度】O(n)，栈空间
     */
    public TreeNode buildTreeIterative(int[] preorder, int[] inorder) {
        if (preorder == null || preorder.length == 0) return null;

        TreeNode root = new TreeNode(preorder[0]);
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);
        int inIdx = 0;

        for (int i = 1; i < preorder.length; i++) {
            TreeNode node = new TreeNode(preorder[i]);

            if (stack.peek().val != inorder[inIdx]) {
                stack.peek().left = node;
            } else {
                TreeNode parent = null;
                while (!stack.isEmpty() && stack.peek().val == inorder[inIdx]) {
                    parent = stack.pop();
                    inIdx++;
                }
                parent.right = node;
            }

            stack.push(node);
        }

        return root;
    }
}
