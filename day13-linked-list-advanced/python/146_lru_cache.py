"""
LeetCode 146. LRU Cache
难度: Medium

题目描述：
请你设计并实现一个满足 LRU（最近最少使用）缓存约束的数据结构。
实现 LRUCache 类：
  - LRUCache(int capacity)：以正整数 capacity 作为容量初始化 LRU 缓存。
  - int get(int key)：如果关键字 key 存在于缓存中，则返回关键字的值，否则返回 -1。
  - void put(int key, int value)：如果关键字 key 已存在，则变更其数据值 value；
    如果不存在，则向缓存中插入该组 key-value。
    如果插入操作导致关键字数量超过 capacity，则应该逐出最久未使用的关键字。
函数 get 和 put 必须以 O(1) 的平均时间复杂度运行。

示例：
  输入：["LRUCache","put","put","get","put","get","put","get","get","get"]
       [[2],[1,1],[2,2],[1],[3,3],[2],[4,4],[1],[3],[4]]
  输出：[null,null,null,1,null,-1,null,-1,3,4]
  解释：
    LRUCache lRUCache = new LRUCache(2);
    lRUCache.put(1, 1); // 缓存是 {1=1}
    lRUCache.put(2, 2); // 缓存是 {1=1, 2=2}
    lRUCache.get(1);    // 返回 1
    lRUCache.put(3, 3); // 该操作会使得关键字 2 作废，缓存是 {1=1, 3=3}
    lRUCache.get(2);    // 返回 -1（未找到）
    lRUCache.put(4, 4); // 该操作会使得关键字 1 作废，缓存是 {4=4, 3=3}
    lRUCache.get(1);    // 返回 -1（未找到）
    lRUCache.get(3);    // 返回 3
    lRUCache.get(4);    // 返回 4

【拓展练习】
1. LeetCode 460. LFU Cache —— 最不经常使用缓存，淘汰使用频率最低的
2. LeetCode 432. All O`one Data Structure —— 全 O(1) 数据结构
3. LeetCode 588. Design In-Memory File System —— 设计内存文件系统
"""

from collections import OrderedDict


class DLinkedNode:
    def __init__(self, key=0, val=0):
        self.key = key
        self.val = val
        self.prev = None
        self.next = None


class LRUCache:
    """
    ==================== 解法一：哈希表 + 手动实现双向链表 ====================

    【核心思路】
    用哈希表实现 O(1) 查找，用双向链表维护访问顺序。
    最近使用的节点放在链表头部，最久未使用的在尾部。
    get/put 时将被访问的节点移到头部；容量满时删除尾部节点。

    【思考过程】
    1. get 需要 O(1) → 哈希表（key → 链表节点）。
    2. 需要维护"最近使用"的顺序，且需要 O(1) 地：
       - 将某节点移到最前面（move_to_head）
       - 删除最后一个节点（remove_tail）
       - 插入新节点到最前面（add_to_head）
       → 双向链表可以在已知节点引用的情况下 O(1) 删除和插入。
    3. 使用 dummy head 和 dummy tail 简化边界处理。
    4. put 操作：
       - key 已存在：更新 value，移到头部。
       - key 不存在：创建新节点，加入哈希表和链表头部。
         如果超容量，删除尾部节点，同时从哈希表中移除。

    【举例】capacity = 2
      put(1,1): head ↔ [1:1] ↔ tail, map={1:node1}
      put(2,2): head ↔ [2:2] ↔ [1:1] ↔ tail, map={1:node1, 2:node2}
      get(1):   访问key=1，移到头部 → head ↔ [1:1] ↔ [2:2] ↔ tail，返回 1
      put(3,3): 容量已满，删除尾部[2:2]，加入[3:3]
                head ↔ [3:3] ↔ [1:1] ↔ tail, map={1:node1, 3:node3}
      get(2):   key=2不存在，返回 -1

    【时间复杂度】get 和 put 均为 O(1)
    【空间复杂度】O(capacity)
    """
    def __init__(self, capacity: int):
        self.capacity = capacity
        self.map = {}
        self.head = DLinkedNode()
        self.tail = DLinkedNode()
        self.head.next = self.tail
        self.tail.prev = self.head

    def get(self, key: int) -> int:
        if key not in self.map:
            return -1
        node = self.map[key]
        self._move_to_head(node)
        return node.val

    def put(self, key: int, value: int) -> None:
        if key in self.map:
            node = self.map[key]
            node.val = value
            self._move_to_head(node)
        else:
            node = DLinkedNode(key, value)
            self.map[key] = node
            self._add_to_head(node)
            if len(self.map) > self.capacity:
                removed = self._remove_tail()
                del self.map[removed.key]

    def _add_to_head(self, node: DLinkedNode) -> None:
        node.prev = self.head
        node.next = self.head.next
        self.head.next.prev = node
        self.head.next = node

    def _remove_node(self, node: DLinkedNode) -> None:
        node.prev.next = node.next
        node.next.prev = node.prev

    def _move_to_head(self, node: DLinkedNode) -> None:
        self._remove_node(node)
        self._add_to_head(node)

    def _remove_tail(self) -> DLinkedNode:
        node = self.tail.prev
        self._remove_node(node)
        return node


class LRUCacheOrderedDict:
    """
    ==================== 解法二：使用 OrderedDict ====================

    【核心思路】
    Python 的 OrderedDict 内部就是哈希表 + 双向链表的实现，
    提供了 move_to_end() 和 popitem() 方法，天然适合实现 LRU 缓存。

    【思考过程】
    1. OrderedDict 记住插入顺序，且支持 O(1) 地将某个 key 移到末尾。
    2. 约定：末尾是最近使用的，头部是最久未使用的。
    3. get 时：如果 key 存在，调用 move_to_end(key) 标记为最近使用。
    4. put 时：
       - key 已存在：更新值，move_to_end。
       - key 不存在：插入（自动在末尾），如果超容量，popitem(last=False) 弹出头部。
    5. 代码极简，但面试中通常期望手写双向链表。

    【举例】capacity = 2
      put(1,1): OrderedDict({1:1})
      put(2,2): OrderedDict({1:1, 2:2})
      get(1):   move_to_end(1) → OrderedDict({2:2, 1:1})，返回 1
      put(3,3): 超容量，popitem(last=False) 弹出 (2,2)
                OrderedDict({1:1, 3:3})
      get(2):   不存在，返回 -1

    【时间复杂度】get 和 put 均为 O(1)
    【空间复杂度】O(capacity)
    """
    def __init__(self, capacity: int):
        self.capacity = capacity
        self.cache = OrderedDict()

    def get(self, key: int) -> int:
        if key not in self.cache:
            return -1
        self.cache.move_to_end(key)
        return self.cache[key]

    def put(self, key: int, value: int) -> None:
        if key in self.cache:
            self.cache.move_to_end(key)
        self.cache[key] = value
        if len(self.cache) > self.capacity:
            self.cache.popitem(last=False)
