"""
LeetCode 23. Merge k Sorted Lists
难度: Hard

题目描述：
给你一个链表数组，每个链表都已经按升序排列。
请你将所有链表合并到一个升序链表中，返回合并后的链表。

示例：lists = [[1,4,5],[1,3,4],[2,6]] → 输出 [1,1,2,3,4,4,5,6]

【拓展练习】
1. LeetCode 21. Merge Two Sorted Lists —— 合并两个有序链表，本题的基础操作
2. LeetCode 378. Kth Smallest Element in a Sorted Matrix —— 多路归并思想的矩阵应用
3. LeetCode 264. Ugly Number II —— 多指针归并生成序列
"""

import heapq
from typing import List, Optional


class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next


class Solution:
    """
    ==================== 解法一：优先队列/最小堆 ====================

    【核心思路】
    用一个大小为 k 的最小堆维护每个链表的当前头节点，
    每次弹出最小值接到结果链表，然后将该节点的 next 入堆。

    【思考过程】
    1. k 个链表各自有序，我们需要在 k 个"候选最小值"中挑全局最小。
       → 用最小堆可以 O(log k) 完成一次取最小操作。

    2. 初始时把每个链表的头节点放入堆（最多 k 个元素）。

    3. 每次弹出堆顶（全局最小），接到结果链表末尾。
       如果弹出节点有 next，把 next 压入堆。

    4. 重复直到堆为空，此时所有节点都已经按序串好。

    5. Python 的 heapq 比较元组时按元素顺序比较，加入序号 idx 避免
       val 相同时比较 ListNode 对象。

    【举例】lists = [[1,4,5],[1,3,4],[2,6]]
      初始堆：[(1,0,node), (1,1,node), (2,2,node)]
      弹 (1,0,node) → 结果[1]，压入 (4,0,node.next)
      弹 (1,1,node) → 结果[1,1]，压入 (3,1,node.next)
      弹 (2,2,node) → 结果[1,1,2]，压入 (6,2,node.next)
      ... 最终 [1,1,2,3,4,4,5,6]

    【时间复杂度】O(N log k)，N 为所有节点总数，每个节点入堆出堆各一次
    【空间复杂度】O(k) 堆大小
    """
    def mergeKLists_heap(self, lists: List[Optional[ListNode]]) -> Optional[ListNode]:
        heap = []
        for idx, head in enumerate(lists):
            if head:
                heapq.heappush(heap, (head.val, idx, head))

        dummy = ListNode(0)
        cur = dummy
        while heap:
            val, idx, node = heapq.heappop(heap)
            cur.next = node
            cur = cur.next
            if node.next:
                heapq.heappush(heap, (node.next.val, idx, node.next))

        return dummy.next

    """
    ==================== 解法二：分治合并 ====================

    【核心思路】
    将 k 个链表两两配对合并，每轮合并后链表数量减半，
    递归 log k 轮后得到一条链表。类似归并排序的合并阶段。

    【思考过程】
    1. 合并两个有序链表是 O(n+m) 的简单问题（LeetCode 21）。
       如果能把 k 路合并拆解成若干次两路合并，就能复用这个操作。

    2. 最朴素的方式是逐一合并（1和2合，结果和3合...），
       但这样前面的链表被反复遍历，总复杂度 O(Nk)。

    3. 分治：两两配对合并（1和2合，3和4合...），得到 k/2 条链表；
       再两两合并...直到只剩一条。
       每轮遍历所有 N 个节点，共 log k 轮，总 O(N log k)。

    4. 这个方法不需要额外的堆结构，空间开销只有递归栈 O(log k)。

    【举例】lists = [L1, L2, L3, L4]
      第一轮：merge(L1,L2)→M1, merge(L3,L4)→M2 → [M1, M2]
      第二轮：merge(M1,M2)→结果

    【时间复杂度】O(N log k)，每轮合并涉及所有 N 个节点，共 log k 轮
    【空间复杂度】O(log k) 递归栈深度
    """
    def mergeKLists_divide(self, lists: List[Optional[ListNode]]) -> Optional[ListNode]:
        if not lists:
            return None

        def merge_two(l1, l2):
            dummy = ListNode(0)
            cur = dummy
            while l1 and l2:
                if l1.val <= l2.val:
                    cur.next = l1
                    l1 = l1.next
                else:
                    cur.next = l2
                    l2 = l2.next
                cur = cur.next
            cur.next = l1 if l1 else l2
            return dummy.next

        def divide(lo, hi):
            if lo == hi:
                return lists[lo]
            mid = (lo + hi) // 2
            left = divide(lo, mid)
            right = divide(mid + 1, hi)
            return merge_two(left, right)

        return divide(0, len(lists) - 1)

    """
    ==================== 解法三：逐一合并 ====================

    【核心思路】
    从第一个链表开始，依次与后续链表逐一合并。
    每次合并两个有序链表，结果作为下一次合并的输入。

    【思考过程】
    1. 这是最直觉的做法：把问题拆成 k-1 次"合并两个有序链表"。

    2. 第 i 次合并时，当前结果链表长度可能已经很长（前 i 个链表的所有节点），
       每次合并都要遍历整个当前结果 → 靠前的节点被反复遍历。

    3. 假设每个链表平均长度 N/k，第 i 次合并的代价约 i*(N/k)，
       总代价 ≈ Σ i*(N/k) ≈ O(Nk)。比分治法慢。

    4. 但实现最简单，适合 k 较小或面试中作为基准解。

    【举例】lists = [L1, L2, L3]
      result = L1
      result = merge(result, L2)  ← 遍历 L1+L2 的节点
      result = merge(result, L3)  ← 遍历 L1+L2+L3 的节点

    【时间复杂度】O(Nk)，N 为所有节点总数，k 为链表数量
    【空间复杂度】O(1) 不计递归栈
    """
    def mergeKLists_one_by_one(self, lists: List[Optional[ListNode]]) -> Optional[ListNode]:
        if not lists:
            return None

        def merge_two(l1, l2):
            dummy = ListNode(0)
            cur = dummy
            while l1 and l2:
                if l1.val <= l2.val:
                    cur.next = l1
                    l1 = l1.next
                else:
                    cur.next = l2
                    l2 = l2.next
                cur = cur.next
            cur.next = l1 if l1 else l2
            return dummy.next

        result = lists[0]
        for i in range(1, len(lists)):
            result = merge_two(result, lists[i])
        return result
