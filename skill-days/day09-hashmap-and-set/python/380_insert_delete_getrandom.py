"""
LeetCode 380. Insert Delete GetRandom O(1)
难度: Medium

题目描述：
设计一个支持在平均 O(1) 时间复杂度下执行以下操作的数据结构：
  - insert(val): 当元素 val 不存在时，向集合中插入该项，返回 True；否则返回 False
  - remove(val): 当元素 val 存在时，从集合中移除该项，返回 True；否则返回 False
  - getRandom(): 随机返回现有集合中的一项，每个元素被返回的概率相等

示例：
  obj = RandomizedSet()
  obj.insert(1)     # True,  集合 = {1}
  obj.remove(2)     # False, 2 不存在
  obj.insert(2)     # True,  集合 = {1, 2}
  obj.getRandom()   # 随机返回 1 或 2
  obj.remove(1)     # True,  集合 = {2}
  obj.insert(2)     # False, 2 已存在
  obj.getRandom()   # 必定返回 2

【拓展练习】
1. 如果允许元素重复（LeetCode 381），如何修改数据结构？
2. 如果要支持 O(1) 的 getMin/getMax 操作，如何扩展？
"""

import random


class RandomizedSet:
    """
    ==================== 解法一：哈希表 + 动态数组（标准解法）====================

    【核心思路】
    用 list 存元素实现 O(1) 随机访问，用 dict 存 val→index 映射实现
    O(1) 查找。删除时将待删元素与末尾元素交换，然后删除末尾，保证 O(1)。

    【思考过程】
    1. 为什么需要两种数据结构结合？
       - 纯 set：insert/remove O(1)，但无法 O(1) 等概率随机取
         因为 set 内部是哈希结构，不支持按索引访问
       - 纯 list：getRandom O(1)（random.choice），insert O(1)（append），
         但 remove 需要 O(n) 查找
       - 结合 dict + list 可以互补

    2. 删除操作的关键技巧——与末尾交换：
       a. 通过 dict 找到 val 的下标 idx
       b. 将 list 末尾元素移到 idx 位置
       c. 更新 dict 中末尾元素的下标为 idx
       d. pop list 末尾，del dict 中 val 的条目
       这样避免了列表中间删除导致的 O(n) 元素搬移

    【举例】
    insert(1): list=[1], map={1:0}
    insert(2): list=[1,2], map={1:0, 2:1}
    insert(3): list=[1,2,3], map={1:0, 2:1, 3:2}
    remove(2): 2 在 idx=1, 末尾=3
      → list[1]=3, list pop → list=[1,3], map={1:0, 3:1}
    getRandom(): random.choice(list)，即等概率返回 1 或 3

    【时间复杂度】每个操作均摊 O(1)
    【空间复杂度】O(n) — 存储 n 个元素
    """

    def __init__(self):
        self.list = []
        self.map = {}

    def insert(self, val: int) -> bool:
        if val in self.map:
            return False
        self.map[val] = len(self.list)
        self.list.append(val)
        return True

    def remove(self, val: int) -> bool:
        if val not in self.map:
            return False
        idx = self.map[val]
        last_val = self.list[-1]

        self.list[idx] = last_val
        self.map[last_val] = idx

        self.list.pop()
        del self.map[val]
        return True

    def getRandom(self) -> int:
        return random.choice(self.list)


class RandomizedSetDetailed:
    """
    ==================== 解法二：设计分析 + 详细注释版 ====================

    【核心思路】
    与解法一相同的实现，但从"为什么这样设计"的角度详细解释每个决策。

    【为什么其他数据结构不行？】

    1. 纯 set（Python 的 set）：
       - insert/remove 都是 O(1) ✓
       - getRandom：set 不支持按索引访问，要随机取需先转 list 或
         用 random.sample，代价 O(n) ✗

    2. 纯 list：
       - insert（append）: O(1) ✓
       - getRandom: O(1) ✓（random.choice 直接随机下标）
       - remove: 需要先 list.index(val) 查找 O(n)，再 pop(idx) 搬移 O(n) ✗

    3. collections.OrderedDict：
       - insert/remove: O(1) ✓
       - getRandom: 虽然有序，但 Python 的 OrderedDict 不支持
         O(1) 按位置索引访问 ✗

    4. 排序结构（如 SortedList）：
       - 所有操作 O(log n)，不满足 O(1) 要求 ✗

    【最终方案】dict + list
       - dict 提供 O(1) 的"值→下标"查找，解决 list 删除时的查找问题
       - list 提供 O(1) 的随机访问，解决 dict/set 无法随机取元素的问题
       - "交换到末尾再删"的技巧，解决 list 中间删除的搬移问题

    【时间复杂度】每个操作均摊 O(1)
    【空间复杂度】O(n)
    """

    def __init__(self):
        self.list = []
        self.map = {}  # val → index in list

    def insert(self, val: int) -> bool:
        """
        插入：先查 map 判断是否存在（O(1)），不存在则追加到 list 尾部（O(1)），
        同时记录 val 在 list 中的下标到 map 中。
        """
        if val in self.map:
            return False
        self.map[val] = len(self.list)
        self.list.append(val)
        return True

    def remove(self, val: int) -> bool:
        """
        删除核心技巧 —— "交换到末尾再删"：
        直接 pop(idx) 会导致 idx 后面所有元素左移 O(n)。
        解决：把 list 末尾元素搬到 idx 位置"填坑"，然后 pop 末尾 O(1)。
        别忘了更新被搬移元素在 map 中的下标。
        """
        if val not in self.map:
            return False

        idx = self.map[val]
        last_val = self.list[-1]

        self.list[idx] = last_val
        self.map[last_val] = idx

        self.list.pop()
        del self.map[val]
        return True

    def getRandom(self) -> int:
        """
        随机取：list 的元素连续存储，random.choice 内部生成 [0, len) 的
        随机下标直接访问。每个元素被选中的概率 = 1/len，满足等概率要求。
        """
        return random.choice(self.list)
