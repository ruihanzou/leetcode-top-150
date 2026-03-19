"""
LeetCode 71. Simplify Path
难度: Medium

题目描述：
给你一个字符串 path，表示指向某一文件或目录的 Unix 风格绝对路径（以 '/' 开头），
请你将其转化为更加简洁的规范路径。

规则：
- 多个连续斜杠 '//' 视为一个 '/'
- '.' 表示当前目录（忽略）
- '..' 表示上一级目录（弹出上一级）
- 任何其他格式的句点（如 '...' 或 'a..b'）视为文件/目录名
- 规范路径不以 '/' 结尾（根目录除外）

示例 1：path = "/home/" → 输出 "/home"
示例 2：path = "/home//foo/" → 输出 "/home/foo"
示例 3：path = "/home/user/Documents/../Pictures" → 输出 "/home/user/Pictures"
示例 4：path = "/../" → 输出 "/"
示例 5：path = "/.../a/../b/c/../d/./" → 输出 "/.../b/d"

【拓展练习】
1. LeetCode 1166. Design File System —— 设计文件系统（Trie/HashMap）
2. LeetCode 609. Find Duplicate File in System —— 在文件系统中查找重复文件
3. LeetCode 388. Longest Absolute File Path —— 文件路径中最长的绝对路径
"""

from collections import deque


class Solution:
    """
    ==================== 解法一：栈模拟 ====================

    【核心思路】
    按 '/' 分割路径得到各组件，用栈模拟目录层级：
    - 空字符串或 '.'：跳过（多余的斜杠或当前目录）
    - '..'：弹栈（返回上一级，若栈空则忽略）
    - 其他：压栈（进入子目录）
    最后将栈中元素用 '/' 拼接即为规范路径。

    【思考过程】
    1. Unix 路径的核心操作就是"进入子目录"和"返回上级"。
       → 栈天然适合模拟这种嵌套层级结构。

    2. 用 split('/') 把路径打碎成组件列表。
       例如 "/a//b/../c" → ['', 'a', '', 'b', '..', 'c']
       空字符串来自连续的 '/' 或首尾的 '/'，直接跳过即可。

    3. 对每个组件：
       - '' 或 '.'：无意义，跳过
       - '..'：回上级 → 弹栈（如果栈非空）
       - 其他：进入目录 → 压栈

    4. 最后 '/' + '/'.join(stack) 得到规范路径。

    【举例】path = "/home/user/Documents/../Pictures"
      分割 → ['', 'home', 'user', 'Documents', '..', 'Pictures']
      '' → 跳过
      'home' → 压栈: ['home']
      'user' → 压栈: ['home', 'user']
      'Documents' → 压栈: ['home', 'user', 'Documents']
      '..' → 弹栈: ['home', 'user']
      'Pictures' → 压栈: ['home', 'user', 'Pictures']
      拼接 → "/home/user/Pictures"

    【时间复杂度】O(n)，n 为路径长度
    【空间复杂度】O(n)
    """
    def simplifyPath_stack(self, path: str) -> str:
        stack = []
        for part in path.split('/'):
            if part == '..':
                if stack:
                    stack.pop()
            elif part and part != '.':
                stack.append(part)
                # '/'.join(stack) 是将stack中的元素用'/'连接起来
        return '/' + '/'.join(stack)

    """
    ==================== 解法二：deque 双端队列 ====================

    【核心思路】
    与栈解法思路相同，但使用 deque 代替列表。
    deque 在头部和尾部操作都是 O(1)，虽然此题只需要尾部操作，
    但 deque 更明确地表达了"栈"的语义，且在某些变体题中
    （如需要从头部输出时）更灵活。

    【思考过程】
    1. 本质上和栈解法完全一样，只是数据结构换成了 deque。
    2. 在 Python 中 list 的 append/pop 已经是 O(1)，
       所以性能上没有差异，更多是代码风格的不同。
    3. 如果需要从左侧操作（如某些变体题），deque 会更高效。

    【举例】path = "/../a/b/../c/"
      分割 → ['', '..', 'a', 'b', '..', 'c', '']
      '' → 跳过
      '..' → deque空，跳过
      'a' → 加入: deque(['a'])
      'b' → 加入: deque(['a', 'b'])
      '..' → 弹出: deque(['a'])
      'c' → 加入: deque(['a', 'c'])
      '' → 跳过
      拼接 → "/a/c"

    【时间复杂度】O(n)
    【空间复杂度】O(n)
    """
    def simplifyPath_deque(self, path: str) -> str:
        dq = deque()
        for part in path.split('/'):
            if part == '..':
                if dq:
                    dq.pop()
            elif part and part != '.':
                dq.append(part)
        return '/' + '/'.join(dq)
