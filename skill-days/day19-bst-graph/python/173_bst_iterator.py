"""
LeetCode 173. Binary Search Tree Iterator
难度: Medium

题目大意：
实现一个二叉搜索树（BST）迭代器 `BSTIterator`，支持：
- `next()`：返回下一个最小的数
- `hasNext()`：是否还有下一个数

要求：
- `next` 和 `hasNext` 的均摊时间复杂度为 O(1)
- 额外空间复杂度 O(h)，其中 h 是树的高度

【核心思路】
利用 BST 的中序遍历有序这一性质。
我们不用一次性中序遍历整棵树（那样会占用 O(n) 空间），
而是用栈模拟「受控的中序遍历」：

1. 初始化时，将从 root 出发的「最左路径」全部压栈。
2. `next()`：
   - 栈顶元素就是当前最小元素，弹出记为 node。
   - 如果 node 有右子树，则对 node.right 再次走一遍「最左路径压栈」。
   - 返回 node.val。
3. `hasNext()`：栈非空即可。

这样栈中最多只会保存从根到当前节点的一条路径（外加若干右子树的左链），空间复杂度 O(h)。
每个节点最多入栈、出栈一次，均摊时间复杂度 O(1)。
"""

from typing import Optional, List


class TreeNode:
    def __init__(self, val: int = 0,
                 left: Optional["TreeNode"] = None,
                 right: Optional["TreeNode"] = None) -> None:
        self.val = val
        self.left = left
        self.right = right


class BSTIterator:
    """
    栈模拟中序遍历的迭代器实现。
    """

    def __init__(self, root: Optional[TreeNode]) -> None:
        self.stack: List[TreeNode] = []
        self._push_left_branch(root)

    def _push_left_branch(self, node: Optional[TreeNode]) -> None:
        while node:
            self.stack.append(node)
            node = node.left

    def next(self) -> int:
        """
        返回下一个最小的数。
        前置条件：hasNext() 为 True。
        """
        node = self.stack.pop()
        if node.right:
            self._push_left_branch(node.right)
        return node.val

    def hasNext(self) -> bool:
        """
        是否还有下一个最小值。
        """
        return len(self.stack) > 0


__all__ = ["TreeNode", "BSTIterator"]

