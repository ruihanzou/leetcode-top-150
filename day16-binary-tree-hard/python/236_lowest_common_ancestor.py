"""
LeetCode 236. Lowest Common Ancestor of a Binary Tree
难度: Medium

题目描述：
给定一个二叉树，找到该树中两个指定节点的最近公共祖先（LCA）。
最近公共祖先的定义为：对于有根树 T 的两个节点 p、q，最近公共祖先表示为
一个节点 x，满足 x 是 p、q 的祖先且 x 的深度尽可能大（一个节点也可以是它自己的祖先）。

示例 1：root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 1 → 输出 3
示例 2：root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 4 → 输出 5

【拓展练习】
1. LeetCode 235. Lowest Common Ancestor of a BST —— BST 中的 LCA，可利用大小关系
2. LeetCode 1644. Lowest Common Ancestor of a Binary Tree II —— p/q 可能不在树中
3. LeetCode 1676. Lowest Common Ancestor of a Binary Tree IV —— 多个节点的 LCA
"""


class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right


class Solution:
    """
    ==================== 解法一：递归（后序遍历） ====================

    【核心思路】
    后序遍历二叉树，对每个节点判断其左右子树中是否分别包含 p 和 q：
    - 若左右子树各找到一个，当前节点就是 LCA。
    - 若只在一侧找到，则 LCA 在那一侧（返回那一侧的结果）。
    - 若当前节点本身就是 p 或 q，直接返回自身。

    【思考过程】
    1. LCA 的定义：p 和 q 分居在 LCA 的两侧（或 LCA 本身就是 p 或 q）。

    2. 递归函数的含义：在以 node 为根的子树中查找 p 和 q，返回值有三种情况：
       - None：子树中既没有 p 也没有 q
       - p 或 q：子树中找到了其中一个
       - LCA：子树中同时包含 p 和 q，返回它们的最近公共祖先

    3. 递归逻辑：
       - 若 node 为 None，返回 None
       - 若 node == p 或 node == q，直接返回 node（无需继续搜索子树，
         因为即使另一个目标在下方，当前节点就是 LCA）
       - 递归搜索左右子树，得到 left 和 right
       - 若 left 和 right 都非 None：p 和 q 分居两侧，当前 node 是 LCA
       - 否则返回非 None 的那个

    【举例】root = [3,5,1,6,2,0,8,null,null,7,4], p=5, q=4
              3
             / \
            5   1
           / \ / \
          6  2 0  8
            / \
           7   4

      search(3, 5, 4):
        search(5, 5, 4): node==p, 返回 5
        search(1, 5, 4):
          search(0): None, search(8): None → 返回 None
        left=5, right=None → 返回 5

      node==5==p 时直接返回 5，不再往下搜索 4，
      但 LCA 确实是 5（5 是 4 的祖先），答案正确！

    【时间复杂度】O(n)，最坏情况遍历所有节点
    【空间复杂度】O(h)，递归栈深度，h 为树高
    """
    def lowestCommonAncestor(self, root: TreeNode, p: TreeNode, q: TreeNode) -> TreeNode:
        if not root or root == p or root == q:
            return root

        left = self.lowestCommonAncestor(root.left, p, q)
        right = self.lowestCommonAncestor(root.right, p, q)

        if left and right:
            return root
        return left if left else right

    """
    ==================== 解法二：存储父节点 + 路径回溯 ====================

    【核心思路】
    用哈希表记录每个节点的父节点，然后从 p 向上回溯到根，记录路径上所有节点。
    再从 q 向上回溯，第一个出现在 p 的路径中的节点就是 LCA。

    【思考过程】
    1. 如果知道每个节点的父节点，就能从任意节点回溯到根，形成"祖先路径"。

    2. p 和 q 的 LCA 就是它们祖先路径上第一个相交的节点。

    3. 具体步骤：
       a. BFS/DFS 遍历整棵树，用字典记录每个节点的 parent。
       b. 从 p 开始沿 parent 向上走，把经过的节点加入集合 ancestors。
       c. 从 q 开始沿 parent 向上走，第一个出现在 ancestors 中的节点就是 LCA。

    4. 这种方法思路直观，但需要额外的哈希表空间。

    【举例】root = [3,5,1,6,2,0,8,null,null,7,4], p=5, q=4
      parent: {5:3, 1:3, 6:5, 2:5, 0:1, 8:1, 7:2, 4:2}
      p=5 的祖先路径：5 → 3（根）→ 集合 {5, 3}
      q=4 的回溯：4 → 2 → 5 → 5 在集合中！返回 5

    【时间复杂度】O(n)，遍历一次建表 + 两次回溯
    【空间复杂度】O(n)，哈希表存储所有节点的父节点
    """
    def lowestCommonAncestor_parent(self, root: TreeNode, p: TreeNode, q: TreeNode) -> TreeNode:
        parent = {root: None}
        stack = [root]

        while p not in parent or q not in parent:
            node = stack.pop()
            if node.left:
                parent[node.left] = node
                stack.append(node.left)
            if node.right:
                parent[node.right] = node
                stack.append(node.right)

        ancestors = set()
        curr = p
        while curr is not None:
            ancestors.add(curr)
            curr = parent[curr]

        curr = q
        while curr not in ancestors:
            curr = parent[curr]
        return curr
