## LeetCode Top 150 — Skill-Based Roadmap

按技能分类的刷题路径，每天同一技能、≤ 5 题、≤ 1 Hard。

---

## 目录

- [高效刷题指南](#高效刷题指南)
- [双指针](#day-1--two-pointers-基础) · [滑动窗口](#day-3--sliding-window) · [贪心](#day-5--greedy---prefixsuffix)
- [哈希表](#day-8--hashmap-基础) · [区间&矩阵](#day-10--intervals--matrix) · [栈](#day-12--stack-进阶)
- [链表](#day-13--linked-list-基础) · [二叉树](#day-15--binary-tree-基础) · [BST&图](#day-19--bst--graph-入门)
- [Trie&回溯](#day-21--graph--trie) · [分治&二分](#day-24--divide--conquer--kadane) · [堆&位运算](#day-27--heap--bit-manipulation)
- [数学&DP](#day-29--math--dp-入门)
- [总览表](#32-天按技能刷题表--总览)

---

## 高效刷题指南

### 时间规划

| 难度 | 时间 | 备注 |
|-----|------|-----|
| Easy | 10–15 分钟 | 想 + 写 + 测 |
| Medium | 15–25 分钟 | 卡住超 20 分钟看提示 |
| Hard | 25–40 分钟 | 可分段：先暴力再优化 |

### 做题前 30 秒自问

1. 题意和边界清楚了吗？（空、单元素、重复）
2. 能一句话说出用什么套路吗？（双指针/滑动窗口/DFS/DP...）
3. 目标时间/空间复杂度是多少？

### 复习节奏

- **第 2 天**：快速过前一天的「核心思路」
- **第 4 天**：闭眼回忆 2–3 道核心题思路
- **第 8 天**：重写最卡的那道题

### 核心模板速查

#### 双指针

```python
# 快慢指针（原地修改数组）
slow = 0
for fast in range(len(nums)):
    if condition:          # 满足条件才保留
        nums[slow] = nums[fast]
        slow += 1
return slow

# 对撞指针（两端向中间）
l, r = 0, len(nums) - 1
while l < r:
    if nums[l] + nums[r] == target:
        return [l, r]
    elif nums[l] + nums[r] < target:
        l += 1
    else:
        r -= 1
```

#### 滑动窗口

```python
# 可变窗口（求最小）
l = 0
for r in range(len(nums)):
    window.add(nums[r])           # 右扩
    while valid():                # 满足条件时收缩
        update_answer()
        window.remove(nums[l])
        l += 1

# 可变窗口（求最大）
l = 0
for r in range(len(nums)):
    window.add(nums[r])
    while not valid():            # 不满足条件时收缩
        window.remove(nums[l])
        l += 1
    update_answer()

# 字符计数窗口（如 76. 最小覆盖子串）
from collections import Counter
need = Counter(t)                 # 目标字符计数
window = Counter()
valid = 0                         # 有多少字符已满足
l = 0
for r, c in enumerate(s):
    if c in need:
        window[c] += 1
        if window[c] == need[c]:
            valid += 1
    while valid == len(need):     # 全部满足时收缩
        update_answer()
        if s[l] in need:
            window[s[l]] -= 1
            if window[s[l]] < need[s[l]]:
                valid -= 1
        l += 1
```

#### 区间合并

```python
def merge(intervals):
    if not intervals:
        return []
    intervals.sort(key=lambda x: x[0])  # 按起点排序
    res = [intervals[0]]
    for cur in intervals[1:]:
        if cur[0] <= res[-1][1]:        # 重叠，扩展右端点
            res[-1][1] = max(res[-1][1], cur[1])
        else:                           # 不重叠，新增区间
            res.append(cur)
    return res
```

#### 单调栈

```python
# 下一个更大元素
res = [-1] * len(nums)
stack = []                            # 存下标，维护递减栈
for i, x in enumerate(nums):
    while stack and nums[stack[-1]] < x:
        res[stack.pop()] = x
    stack.append(i)

# 接雨水（单调栈版）
stack = []
water = 0
for i, h in enumerate(height):
    while stack and height[stack[-1]] < h:
        mid = stack.pop()
        if stack:
            left = stack[-1]
            water += (min(height[left], h) - height[mid]) * (i - left - 1)
    stack.append(i)
```

#### 二分查找

```python
# 标准 lower_bound（找第一个 >= target 的位置）
l, r = 0, len(nums)
while l < r:
    m = (l + r) // 2
    if nums[m] >= target:
        r = m
    else:
        l = m + 1
return l

# 标准 upper_bound（找第一个 > target 的位置）
l, r = 0, len(nums)
while l < r:
    m = (l + r) // 2
    if nums[m] > target:
        r = m
    else:
        l = m + 1
return l

# 找精确位置（l <= r 写法）
l, r = 0, len(nums) - 1
while l <= r:
    m = (l + r) // 2
    if nums[m] == target:
        return m
    elif nums[m] < target:
        l = m + 1
    else:
        r = m - 1
return -1
```

#### 链表

```python
# 虚拟头节点
dummy = ListNode(0)
dummy.next = head
# ... 操作 ...
return dummy.next

# 快慢指针找中点
slow = fast = head
while fast and fast.next:
    slow = slow.next
    fast = fast.next.next
return slow  # slow 是中点

# 反转链表
prev, cur = None, head
while cur:
    nxt = cur.next
    cur.next = prev
    prev = cur
    cur = nxt
return prev
```

#### 二叉树

```python
# DFS 递归模板
def dfs(root):
    if not root:
        return base_value
    left = dfs(root.left)
    right = dfs(root.right)
    return combine(left, right, root.val)

# BFS 层序遍历
from collections import deque
q = deque([root])
while q:
    level = []
    for _ in range(len(q)):
        node = q.popleft()
        level.append(node.val)
        if node.left:
            q.append(node.left)
        if node.right:
            q.append(node.right)
    res.append(level)

# 前序/中序/后序遍历（迭代）
def preorder(root):  # 前序
    res, stack = [], [root]
    while stack:
        node = stack.pop()
        if node:
            res.append(node.val)
            stack.append(node.right)
            stack.append(node.left)
    return res

def inorder(root):   # 中序
    res, stack = [], []
    cur = root
    while cur or stack:
        while cur:
            stack.append(cur)
            cur = cur.left
        cur = stack.pop()
        res.append(cur.val)
        cur = cur.right
    return res
```

#### 图遍历

```python
# DFS（递归）
def dfs(node, visited):
    visited.add(node)
    for neighbor in graph[node]:
        if neighbor not in visited:
            dfs(neighbor, visited)

# BFS
from collections import deque
def bfs(start):
    q, visited = deque([start]), {start}
    while q:
        node = q.popleft()
        for neighbor in graph[node]:
            if neighbor not in visited:
                visited.add(neighbor)
                q.append(neighbor)

# 拓扑排序（Kahn 算法）
from collections import deque, defaultdict
def topo_sort(n, edges):
    graph = defaultdict(list)
    indeg = [0] * n
    for u, v in edges:
        graph[u].append(v)
        indeg[v] += 1
    q = deque([i for i in range(n) if indeg[i] == 0])
    res = []
    while q:
        u = q.popleft()
        res.append(u)
        for v in graph[u]:
            indeg[v] -= 1
            if indeg[v] == 0:
                q.append(v)
    return res if len(res) == n else []  # 有环返回空

# 网格 DFS（岛屿问题）
def dfs(grid, i, j):
    if i < 0 or i >= len(grid) or j < 0 or j >= len(grid[0]) or grid[i][j] != '1':
        return
    grid[i][j] = '0'  # 标记已访问
    for di, dj in [(1,0),(-1,0),(0,1),(0,-1)]:
        dfs(grid, i+di, j+dj)
```

#### Trie

```python
class TrieNode:
    def __init__(self):
        self.children = {}
        self.is_end = False

class Trie:
    def __init__(self):
        self.root = TrieNode()

    def insert(self, word):
        node = self.root
        for c in word:
            node = node.children.setdefault(c, TrieNode())
        node.is_end = True

    def search(self, word):
        node = self.root
        for c in word:
            if c not in node.children:
                return False
            node = node.children[c]
        return node.is_end

    def startsWith(self, prefix):
        node = self.root
        for c in prefix:
            if c not in node.children:
                return False
            node = node.children[c]
        return True
```

#### 回溯

```python
# 组合（从 n 中选 k 个）
def combine(n, k):
    res, path = [], []
    def dfs(start):
        if len(path) == k:
            res.append(path[:])
            return
        for i in range(start, n + 1):
            path.append(i)
            dfs(i + 1)
            path.pop()
    dfs(1)
    return res

# 排列
def permute(nums):
    res, path, used = [], [], [False] * len(nums)
    def dfs():
        if len(path) == len(nums):
            res.append(path[:])
            return
        for i in range(len(nums)):
            if used[i]:
                continue
            used[i] = True
            path.append(nums[i])
            dfs()
            path.pop()
            used[i] = False
    dfs()
    return res

# 子集
def subsets(nums):
    res, path = [], []
    def dfs(start):
        res.append(path[:])
        for i in range(start, len(nums)):
            path.append(nums[i])
            dfs(i + 1)
            path.pop()
    dfs(0)
    return res
```

#### 动态规划

```python
# 1D DP（打家劫舍）
def rob(nums):
    prev, curr = 0, 0
    for x in nums:
        prev, curr = curr, max(curr, prev + x)
    return curr

# 1D DP（完全背包-最少硬币）
def coinChange(coins, amount):
    dp = [float('inf')] * (amount + 1)
    dp[0] = 0
    for i in range(1, amount + 1):
        for c in coins:
            if i >= c:
                dp[i] = min(dp[i], dp[i - c] + 1)
    return dp[amount] if dp[amount] != float('inf') else -1

# LIS（O(n log n) 二分优化）
import bisect
def lengthOfLIS(nums):
    tails = []
    for x in nums:
        i = bisect.bisect_left(tails, x)
        if i == len(tails):
            tails.append(x)
        else:
            tails[i] = x
    return len(tails)

# 2D DP（网格路径）
def minPathSum(grid):
    m, n = len(grid), len(grid[0])
    dp = [[0] * n for _ in range(m)]
    dp[0][0] = grid[0][0]
    for i in range(1, m):
        dp[i][0] = dp[i-1][0] + grid[i][0]
    for j in range(1, n):
        dp[0][j] = dp[0][j-1] + grid[0][j]
    for i in range(1, m):
        for j in range(1, n):
            dp[i][j] = min(dp[i-1][j], dp[i][j-1]) + grid[i][j]
    return dp[-1][-1]

# 编辑距离
def minDistance(word1, word2):
    m, n = len(word1), len(word2)
    dp = [[0] * (n + 1) for _ in range(m + 1)]
    for i in range(m + 1):
        dp[i][0] = i
    for j in range(n + 1):
        dp[0][j] = j
    for i in range(1, m + 1):
        for j in range(1, n + 1):
            if word1[i-1] == word2[j-1]:
                dp[i][j] = dp[i-1][j-1]
            else:
                dp[i][j] = min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1]) + 1
    return dp[m][n]
```

#### 堆

```python
import heapq

# 最小堆
heap = []
heapq.heappush(heap, x)
heapq.heappop(heap)      # 弹出最小
heap[0]                  # 查看最小

# 最大堆（取负）
heap = []
heapq.heappush(heap, -x)
heapq.heappop(heap)      # 弹出最大的负数
-heap[0]                 # 查看最大

# 对顶堆（数据流中位数）
class MedianFinder:
    def __init__(self):
        self.lo = []   # 大顶堆存较小一半（取负）
        self.hi = []   # 小顶堆存较大一半

    def addNum(self, num):
        heapq.heappush(self.lo, -num)
        heapq.heappush(self.hi, -heapq.heappop(self.lo))
        if len(self.lo) < len(self.hi):
            heapq.heappush(self.lo, -heapq.heappop(self.hi))

    def findMedian(self):
        if len(self.lo) > len(self.hi):
            return -self.lo[0]
        return (-self.lo[0] + self.hi[0]) / 2
```

### 面试高频（★ 时间紧优先）

★1, ★3, ★15, ★20, ★21, ★42, ★76, ★98, ★102, ★121, ★146, ★200, ★207, ★236, ★5, ★72, ★23

---

## Day 1 — Two Pointers 基础

**核心**：快慢指针写入、对撞指针、原地修改

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 88 | Merge Sorted Array | Easy | 逆向双指针归并，避免覆盖 |
| 27 | Remove Element | Easy | 快慢指针，只保留想要的 |
| 26 | Remove Duplicates | Easy | 快慢指针去重 |
| 125 | Valid Palindrome | Easy | 对撞指针 + 字符过滤 |
| 392 | Is Subsequence | Easy | 双指针匹配子序列 |

**拓展**：977（有序数组平方）、986（区间交集）
**坑点**：88 正向写会覆盖；27/26 返回长度 off-by-one；125 忽略非字母数字

---

## Day 2 — Two Pointers 进阶

**核心**：对撞贪心、排序+双指针去重、盛水/接雨

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 80 | Remove Duplicates II | Medium | 快慢指针 + 计数控制 |
| 167 | Two Sum II | Medium | 有序数组对撞 |
| 11 | Container With Most Water | Medium | 对撞指针，移短板 |
| 15 | 3Sum ★ | Medium | 排序 + 固定 + 对撞 + 去重 |
| **42** | Trapping Rain Water | Hard | 双指针维护 lmax/rmax 或单调栈 |

**拓展**：18（4Sum）、75（荷兰国旗）
**坑点**：15 忘记排序或跳过重复导致重复三元组；42 lmax/rmax 更新时机

---

## Day 3 — Sliding Window

**核心**：固定/可变窗口、字符计数、收缩条件

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 219 | Contains Duplicate II | Easy | 固定窗口 + 集合判重 |
| 209 | Minimum Size Subarray Sum | Medium | 可变窗口，满足就收缩 |
| 3 | Longest Substring Without Repeating ★ | Medium | 集合/哈希维护无重复 |
| **76** | Minimum Window Substring | Hard | needs/window 双计数 + valid 收缩 |

**拓展**：424（替换 k 次）、992（恰 k 个不同整数）
**坑点**：209 收缩时更新答案；76 valid 增减时机

---

## Day 4 — Sliding Window + Greedy

**核心**：固定窗口+词频、一次遍历贪心

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| **30** | Substring with Concatenation of All Words | Hard | 固定窗口按词切分 + 词频匹配 |
| 121 | Best Time to Buy and Sell Stock ★ | Easy | 维护前缀最小价格 |
| 122 | Best Time to Buy and Sell Stock II | Medium | 贪心累加上升段 |
| 55 | Jump Game | Medium | 维护可达最远位置 |

**拓展**：406（身高重排）、763（划分字母区间）
**坑点**：30 需要枚举 wordLen 个起点；121 混淆买入/卖出时机

---

## Day 5 — Greedy & Prefix/Suffix

**核心**：前后缀积、跳跃贪心、加油站

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 45 | Jump Game II | Medium | BFS/贪心求最少步数 |
| 189 | Rotate Array | Medium | 三次翻转 |
| 238 | Product of Array Except Self | Medium | 前缀积 × 后缀积 |
| 134 | Gas Station | Medium | 总量判断 + 起点贪心 |
| 274 | H-Index | Medium | 排序/计数后找 h |

**拓展**：560（前缀和+哈希）、406（身高重排）
**坑点**：238 边界初始化为 1；134 误以为要从每个位置都试

---

## Day 6 — Greedy + Array/String

**核心**：左右两遍贪心、字符串基础

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| **135** | Candy | Hard | 左扫一遍 + 右扫一遍取 max |
| 58 | Length of Last Word | Easy | 逆向找最后一个单词 |
| 14 | Longest Common Prefix | Easy | 纵向逐字符比较 |
| 28 | Find the Index of the First Occurrence | Easy | 子串匹配/KMP |
| 13 | Roman to Integer | Easy | 映射表 + 左减规则 |

**拓展**：409（回文构造）、763（贪心划分）
**坑点**：135 第二遍取 max 而非直接赋值；13 IV/IX 特殊组合

---

## Day 7 — Array & String

**核心**：投票算法、字符串构造模拟

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 169 | Majority Element | Easy | Boyer-Moore 投票 |
| 151 | Reverse Words in a String | Medium | split + reverse + join |
| 6 | Zigzag Conversion | Medium | 按行分桶或周期计算下标 |
| 12 | Integer to Roman | Medium | 贪心从大到小匹配 |
| **68** | Text Justification | Hard | 逐行分配单词和空格 |

**拓展**：229（三三抵消）、443（原地压缩）
**坑点**：6 周期长度算错；68 最后一行左对齐

---

## Day 8 — Hashmap 基础

**核心**：字符计数、双向映射、一遍扫描

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 383 | Ransom Note | Easy | 字符计数 |
| 242 | Valid Anagram | Easy | 字符计数 |
| 205 | Isomorphic Strings | Easy | 双向映射 |
| 290 | Word Pattern | Easy | 双向映射（单词级） |
| 1 | Two Sum ★ | Easy | 边查边存，避免自己匹配自己 |

**拓展**：454（4Sum II）、525（前缀和+哈希）
**坑点**：205/290 只检查单向映射会错；1 先存再查会自己匹配自己

---

## Day 9 — Hashmap 进阶

**核心**：分组、集合判连续、O(1) 设计、斜率哈希

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 202 | Happy Number | Easy | 快慢指针/集合判环 |
| 49 | Group Anagrams | Medium | 排序键/计数键分组 |
| 128 | Longest Consecutive Sequence | Medium | 集合 + 只从起点扩展 |
| 380 | Insert Delete GetRandom O(1) | Medium | 哈希 + 动态数组 |
| **149** | Max Points on a Line | Hard | 斜率哈希，用 (dx,dy) 约分 |

**拓展**：460（LFU）、164（桶排序）
**坑点**：128 从每个数都扩展会 O(n²)；380 删除时忘记更新被交换元素下标；149 用浮点斜率有精度问题

---

## Day 10 — Intervals + Matrix

**核心**：区间合并、按端点排序贪心、矩阵校验

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 228 | Summary Ranges | Easy | 扫描连续区间 |
| 56 | Merge Intervals | Medium | 排序 + 合并 |
| 57 | Insert Interval | Medium | 分类讨论插入 |
| 452 | Minimum Number of Arrows | Medium | 按右端点排序贪心 |
| 36 | Valid Sudoku | Medium | 行/列/宫三维检查 |

**拓展**：435（无重叠区间）、417（双源 BFS）
**坑点**：区间边界是否视为重叠；452 按起点排序会错；36 子宫坐标计算

---

## Day 11 — Matrix + Stack 入门

**核心**：矩阵边界模拟、原地标记、栈匹配

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 54 | Spiral Matrix | Medium | 四边界收缩 |
| 48 | Rotate Image | Medium | 转置 + 水平翻转 |
| 73 | Set Matrix Zeroes | Medium | 首行首列做标记 |
| 289 | Game of Life | Medium | 状态编码：低位旧、高位新 |
| 20 | Valid Parentheses ★ | Easy | 栈匹配，映射表简化 |

**拓展**：59（生成螺旋矩阵）、240（Z 字形搜索）
**坑点**：54 边界收缩后判越界；48 转置和翻转顺序；73 首行首列本身含零

---

## Day 12 — Stack 进阶

**核心**：路径栈、辅助栈、逆波兰、表达式解析

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 71 | Simplify Path | Medium | 路径栈简化 |
| 155 | Min Stack | Medium | 辅助栈维护最小值 |
| 150 | Evaluate Reverse Polish Notation | Medium | 栈计算后缀表达式 |
| **224** | Basic Calculator | Hard | 栈 + 递归/符号处理 |
| 141 | Linked List Cycle | Easy | 快慢指针判环 |

**拓展**：84（单调栈）、316（单调栈+去重）
**坑点**：71 连续多个 `/`；224 负号和减号区分、多层括号

---

## Day 13 — Linked List 基础

**核心**：虚拟头、归并、区间反转、快慢指针定位

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 21 | Merge Two Sorted Lists ★ | Easy | dummy + 比较 |
| 2 | Add Two Numbers | Medium | 逐位加法 + 进位 |
| 138 | Copy List with Random Pointer | Medium | 哈希映射或交织复制 |
| 92 | Reverse Linked List II | Medium | 定位区间后逐个反转 |
| 19 | Remove Nth Node From End | Medium | 快指针先走 n 步 |

**拓展**：143（重排链表）、234（回文链表）
**坑点**：2 最后一个进位；92 反转后连接断裂；19 off-by-one

---

## Day 14 — Linked List 进阶

**核心**：虚拟头去重、成环断开、分区、LRU、k 组反转

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 82 | Remove Duplicates from Sorted List II | Medium | 虚拟头 + 跳过所有重复 |
| 61 | Rotate List | Medium | 成环后走到断点断开 |
| 86 | Partition List | Medium | 两个子链表分别收集 |
| 146 | LRU Cache ★ | Medium | 哈希 + 双向链表 |
| **25** | Reverse Nodes in k-Group | Hard | 分组反转 + 递归/迭代 |

**拓展**：24（两两交换）、460（LFU）
**坑点**：82 只删除一个重复节点；61 k 大于长度时取模；146 更新已有 key 忘移到头部

---

## Day 15 — Binary Tree 基础

**核心**：DFS 深度、递归结构比较、翻转、镜像、路径和

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 104 | Maximum Depth of Binary Tree | Easy | max(左, 右) + 1 |
| 100 | Same Tree | Easy | 同时递归比较结构和值 |
| 226 | Invert Binary Tree | Easy | 递归交换左右子树 |
| 101 | Symmetric Tree | Easy | 双指针递归判断镜像 |
| 112 | Path Sum | Easy | target 逐层减，叶子判 0 |

**拓展**：113（输出路径）、437（前缀和+哈希）
**坑点**：112 非叶子节点就返回 true；101 只检查值不检查结构镜像

---

## Day 16 — Binary Tree 构造

**核心**：完全二叉树性质、BFS 层序、前序+中序/中序+后序构造

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 222 | Count Complete Tree Nodes | Easy | 高度性质 + 二分 |
| 637 | Average of Levels in Binary Tree | Easy | BFS 层平均值 |
| 105 | Construct Binary Tree from Preorder and Inorder | Medium | 前序定根 + 中序分割 |
| 106 | Construct Binary Tree from Inorder and Postorder | Medium | 后序定根 + 中序分割 |
| 117 | Populating Next Right Pointers II | Medium | 层序或利用已有 next |

**拓展**：889（前序+后序构造）、110（平衡判断）
**坑点**：105/106 左右子树下标范围；117 非完美二叉树找下一层第一个非空

---

## Day 17 — Binary Tree 高级

**核心**：前序展开、DFS 路径、LCA、右视图、层序

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 114 | Flatten Binary Tree to Linked List | Medium | 前序遍历 + 指针重连 |
| 129 | Sum Root to Leaf Numbers | Medium | DFS 传递 num*10+val |
| 236 | Lowest Common Ancestor ★ | Medium | 递归查找，一左一右即根 |
| 199 | Binary Tree Right Side View | Medium | BFS 每层最后一个 |
| 102 | Binary Tree Level Order Traversal ★ | Medium | BFS 层序遍历 |

**拓展**：116（完美二叉树 next）、513（层序最后一个）
**坑点**：114 递归时右子树被覆盖；236 忘记 p 或 q 本身就是 LCA

---

## Day 18 — Tree + BST

**核心**：之字形层序、最大路径和、BST 中序、分治构造

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 103 | Binary Tree Zigzag Level Order Traversal | Medium | 层序 + 奇偶反转 |
| **124** | Binary Tree Maximum Path Sum | Hard | DFS 返回单边最大，全局更新左+右+根 |
| 530 | Minimum Absolute Difference in BST | Easy | BST 中序相邻差值 |
| 108 | Convert Sorted Array to BST | Easy | 二分取中点为根 |

**拓展**：99（恢复 BST）、450（BST 删除）
**坑点**：124 负数路径和取 0；530 用 BFS 而非中序

---

## Day 19 — BST + Graph 入门

**核心**：BST 中序第 k 小、验证、迭代器、岛屿 DFS、边界 DFS

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 230 | Kth Smallest Element in a BST | Medium | 中序数到第 k 个 |
| 98 | Validate Binary Search Tree ★ | Medium | 递归传范围或中序检查递增 |
| 173 | BST Iterator | Medium | 栈模拟中序暂停/恢复 |
| 200 | Number of Islands ★ | Medium | 网格 DFS/BFS 计数 |
| 130 | Surrounded Regions | Medium | 从边界出发 DFS 标记 O |

**拓展**：417（双源 BFS/DFS）、694（岛屿形状哈希）
**坑点**：98 用 int 边界可能溢出；173 栈初始化压所有左节点；130 从内部出发会错

---

## Day 20 — Graph 遍历

**核心**：图克隆、带权图、拓扑排序、棋盘 BFS

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 133 | Clone Graph | Medium | DFS/BFS + 哈希映射旧→新 |
| 399 | Evaluate Division | Medium | 建带权图 BFS/DFS 或并查集 |
| 207 | Course Schedule ★ | Medium | 拓扑排序判环 |
| 210 | Course Schedule II | Medium | 拓扑排序输出序列 |
| 909 | Snakes and Ladders | Medium | 棋盘编号转坐标 + BFS |

**拓展**：787（带限制最短路）、417（双源 BFS）
**坑点**：133 邻居克隆要递归；399 建双向边；909 蛇梯棋坐标转换

---

## Day 21 — Graph + Trie

**核心**：状态图 BFS、Trie 实现、单词接龙

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 433 | Minimum Genetic Mutation | Medium | 枚举突变 + BFS |
| 208 | Implement Trie | Medium | children 字典 + is_end |
| 211 | Design Add and Search Words | Medium | Trie + DFS 处理 `.` |
| **127** | Word Ladder | Hard | 逐位替换 26 字母 + BFS |

**拓展**：126（输出路径）、720（最长单词）
**坑点**：208 search 和 startsWith 区别；211 `.` 需遍历所有子节点；127 逐词比较会超时

---

## Day 22 — Trie + Backtracking

**核心**：Trie+网格回溯、组合、排列

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| **212** | Word Search II | Hard | Trie 存词表 + 网格 DFS 剪枝 |
| 17 | Letter Combinations of a Phone Number | Medium | 多层字符映射组合回溯 |
| 77 | Combinations | Medium | 组合 + 剪枝 |
| 46 | Permutations | Medium | visited 或 swap |

**拓展**：78（子集）、51（N 皇后）
**坑点**：212 找到词后忘记从 Trie 删除；77 剪枝条件；46 有重复元素需去重

---

## Day 23 — Backtracking

**核心**：组合总和、括号生成、网格回溯、N 皇后

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 39 | Combination Sum | Medium | 可重复选，递归时不 +1 |
| 22 | Generate Parentheses | Medium | 左 ≥ 右约束回溯 |
| 79 | Word Search | Medium | 网格 DFS 回溯 |
| **52** | N-Queens II | Hard | 逐行放置 + 三方向冲突检测 |

**拓展**：51（输出解）、37（数独回溯）
**坑点**：39 忘记排序+剪枝；22 合法性条件搞反；79 忘记标记/取消标记

---

## Day 24 — Divide & Conquer + Kadane

**核心**：四叉树分治、链表归并、Kadane、k 路归并

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 427 | Construct Quad Tree | Medium | 全同则叶子，否则分四块 |
| 148 | Sort List | Medium | 快慢指针找中点 + 归并 |
| 53 | Maximum Subarray | Medium | Kadane: cur=max(x,cur+x) |
| 918 | Maximum Sum Circular Subarray | Medium | Kadane + 环形转化 |
| **23** | Merge k Sorted Lists ★ | Hard | 分治归并或最小堆 |

**拓展**：312（区间 DP）、327（前缀和+归并）
**坑点**：148 找中点后忘记断开；918 全负数时环形公式不适用

---

## Day 25 — Binary Search 基础

**核心**：基础二分、开方、二维转一维、峰值、旋转数组

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 35 | Search Insert Position | Easy | lower_bound 模板 |
| 69 | Sqrt(x) | Easy | 二分答案 |
| 74 | Search a 2D Matrix | Medium | 二维转一维二分 |
| 162 | Find Peak Element | Medium | 比较 mid 和 mid+1 决定方向 |
| 33 | Search in Rotated Sorted Array | Medium | 判断哪半边有序 |

**拓展**：278（First Bad Version）、275（H-Index II）
**坑点**：35 `l<=r` vs `l<r`；69 溢出；33 等号归属

---

## Day 26 — Binary Search + Heap

**核心**：左右边界、旋转数组最小值、两数组 Median、快速选择

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 34 | Find First and Last Position | Medium | 两次二分求起止 |
| 153 | Find Minimum in Rotated Sorted Array | Medium | 比较 mid 和 right |
| **4** | Median of Two Sorted Arrays | Hard | 划分 + 二分 |
| 215 | Kth Largest Element in an Array | Medium | 快速选择或堆 |

**拓展**：378（堆/二分）、719（二分答案）
**坑点**：4 划分点边界值；215 快速选择 pivot 影响

---

## Day 27 — Heap + Bit Manipulation

**核心**：最小堆多路归并、贪心+双堆、二进制加法、位翻转

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 373 | Find K Pairs with Smallest Sums | Medium | 堆维护候选数对 |
| **502** | IPO | Hard | 双堆：按资本的最小堆 + 按利润的最大堆 |
| 67 | Add Binary | Easy | 模拟二进制加法 |
| 190 | Reverse Bits | Easy | 逐位翻转或分治交换 |

**拓展**：378（堆+多路归并）、260（双异或分组）
**坑点**：373 重复入堆；502 每轮更新资本后重新检查可做项目

---

## Day 28 — Bit Manipulation

**核心**：位计数、异或、位状态、区间 AND、对顶堆

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 191 | Number of 1 Bits | Easy | n & (n-1) 消最低位 1 |
| 136 | Single Number | Easy | 全部异或得唯一 |
| 137 | Single Number II | Medium | 逐位统计模 3 或状态机 |
| 201 | Bitwise AND of Numbers Range | Medium | 右移找公共前缀 |
| **295** | Find Median from Data Stream | Hard | 对顶堆维护中位数 |

**拓展**：260（双异或找两数）、371（位运算加法）
**坑点**：137 溢出；201 直接循环 AND 会超时；295 堆 rebalance

---

## Day 29 — Math + DP 入门

**核心**：回文判断、数组进位、因子计数、快速幂、Fibonacci

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 9 | Palindrome Number | Easy | 反转一半数字比较 |
| 66 | Plus One | Easy | 数组模拟进位 |
| 172 | Factorial Trailing Zeroes | Medium | 统计因子 5 的个数 |
| 50 | Pow(x, n) | Medium | 快速幂 |
| 70 | Climbing Stairs | Easy | Fibonacci DP |

**拓展**：263（丑数）、91（Decode Ways）
**坑点**：9 负数、末位 0 但非零；66 全是 9；50 n 为负数取倒数

---

## Day 30 — 1D DP

**核心**：打家劫舍、字典切分、完全背包、LIS、股票多状态

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 198 | House Robber | Medium | dp[i]=max(dp[i-1], dp[i-2]+nums[i]) |
| 139 | Word Break | Medium | 枚举字典词作为最后一段 |
| 322 | Coin Change | Medium | 完全背包最少硬币 |
| 300 | Longest Increasing Subsequence | Medium | DP 或二分优化 |
| **123** | Best Time to Buy and Sell Stock III | Hard | 四状态转移 |

**拓展**：213（环形）、279（完全背包/BFS）
**坑点**：139 用 set 存字典；322 dp[0]=0 其余 inf；300 tails 含义；123 buy 初始化 -inf

---

## Day 31 — 2D / Multi-D DP

**核心**：三角形路径、网格最短路径、障碍路径、回文、交错字符串

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 120 | Triangle | Medium | 自底向上 DP |
| 64 | Minimum Path Sum | Medium | dp[i][j] 由上/左转移 |
| 63 | Unique Paths II | Medium | 障碍格子 dp=0 |
| 5 | Longest Palindromic Substring ★ | Medium | 中心扩展或区间 DP |
| 97 | Interleaving String | Medium | 二维 DP 判断交错 |

**拓展**：131（回文划分）、312（区间 DP）
**坑点**：120 自底向上更简洁；63 障碍格处理；5 DP 遍历顺序；97 dp 大小 (m+1)×(n+1)

---

## Day 32 — 2D DP 冲刺

**核心**：编辑距离、最大正方形、股票 K 次交易

| # | 题目 | 难度 | 一句话思路 |
|---|------|-----|-----------|
| 72 | Edit Distance ★ | Medium | 三种操作对应三个方向转移 |
| 221 | Maximal Square | Medium | dp[i][j]=min(上,左,左上)+1 |
| **188** | Best Time to Buy and Sell Stock IV | Hard | K 次交易状态机 |

**拓展**：10（正则 DP）、44（通配符 DP）
**坑点**：72 初始化首行首列；221 矩阵元素是字符 '1'；188 k≥n/2 退化为无限次交易

---

## 32 天按技能刷题表 — 总览

| Day | Folder | Problems |
|-----|--------|----------|
| 1 | `day01-two-pointers` | 88, 27, 26, 125, 392 |
| 2 | `day02-two-pointers` | 80, 167, 11, ★15, **42** |
| 3 | `day03-sliding-window` | 219, 209, ★3, **76** |
| 4 | `day04-sliding-window-greedy` | **30**, ★121, 122, 55 |
| 5 | `day05-greedy-and-prefix` | 45, 189, 238, 134, 274 |
| 6 | `day06-greedy-array-string` | **135**, 58, 14, 28, 13 |
| 7 | `day07-array-string` | 169, 151, 6, 12, **68** |
| 8 | `day08-hashmap-and-set` | 383, 242, 205, 290, ★1 |
| 9 | `day09-hashmap-and-set` | 202, 49, 128, 380, **149** |
| 10 | `day10-intervals-matrix` | 228, 56, 57, 452, 36 |
| 11 | `day11-matrix-stack` | 54, 48, 73, 289, ★20 |
| 12 | `day12-stack` | 71, 155, 150, **224**, 141 |
| 13 | `day13-linked-list` | ★21, 2, 138, 92, 19 |
| 14 | `day14-linked-list` | 82, 61, 86, ★146, **25** |
| 15 | `day15-binary-tree` | 104, 100, 226, 101, 112 |
| 16 | `day16-binary-tree` | 222, 637, 105, 106, 117 |
| 17 | `day17-binary-tree` | 114, 129, ★236, 199, ★102 |
| 18 | `day18-binary-tree-bst` | 103, **124**, 530, 108 |
| 19 | `day19-bst-graph` | 230, ★98, 173, ★200, 130 |
| 20 | `day20-graph` | 133, 399, ★207, 210, 909 |
| 21 | `day21-graph-trie` | 433, 208, 211, **127** |
| 22 | `day22-trie-backtracking` | **212**, 17, 77, 46 |
| 23 | `day23-backtracking` | 39, 22, 79, **52** |
| 24 | `day24-divide-conquer-kadane` | 427, 148, 53, 918, **★23** |
| 25 | `day25-binary-search` | 35, 69, 74, 162, 33 |
| 26 | `day26-binary-search-heap` | 34, 153, **4**, 215 |
| 27 | `day27-heap-bit-manipulation` | 373, **502**, 67, 190 |
| 28 | `day28-bit-manipulation` | 191, 136, 137, 201, **295** |
| 29 | `day29-math-dp-1d` | 9, 66, 172, 50, 70 |
| 30 | `day30-dp-1d` | 198, 139, 322, 300, **123** |
| 31 | `day31-dp-2d` | 120, 64, 63, ★5, 97 |
| 32 | `day32-dp-2d` | ★72, 221, **188** |

> **加粗** 为 Hard 题。**★** 为面试高频。每天 ≤ 5 题、≤ 1 Hard。
