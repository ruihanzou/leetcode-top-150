# LeetCode Top Interview 150 - 30天刷题计划

> 开始日期: 2026-02-28 | 目标: 30天内完成 LeetCode Top Interview 150 全部题目
> 每道题提供 Python + Java 双语解法 | 包含解题思路 + 时间复杂度 + 拓展练习

---

## 刷题模式选择

- **按时间线刷题**: 建议第一次刷题严格按照下面的 `Day 1 → Day 30` 顺序完成，循序渐进覆盖所有题型。
- **按技能刷题**: 如果你想集中训练某一类技能（例如 Sliding Window、Two Pointers、DP），可以参考仓库中的 `PLAN_BY_SKILL.md`，其中按算法/思维模式重新整理了同一批 Top 150 题目，并提供「32 天按技能刷题表」及高效刷题指南。

---

## 进度总览

| 天数 | 主题 | 题数 | 状态 |
|------|------|------|------|
| Day 1 | Array/String 基础 | 10 | ✅ 已完成 |
| Day 2 | Array/String 进阶 | 5 | ⬜ |
| Day 3 | String 操作 | 5 | ⬜ |
| Day 4 | String 困难题 | 4 | ⬜ |
| Day 5 | Two Pointers 双指针 | 5 | ⬜ |
| Day 6 | Sliding Window 滑动窗口 | 4 | ⬜ |
| Day 7 | Matrix 矩阵 | 5 | ⬜ |
| Day 8 | Hashmap 基础 | 5 | ⬜ |
| Day 9 | Hashmap 进阶 | 4 | ⬜ |
| Day 10 | Intervals 区间 | 4 | ⬜ |
| Day 11 | Stack 栈 | 5 | ⬜ |
| Day 12 | Linked List 基础 | 5 | ⬜ |
| Day 13 | Linked List 进阶 | 6 | ⬜ |
| Day 14 | Binary Tree 基础 | 5 | ⬜ |
| Day 15 | Binary Tree 构造 | 5 | ⬜ |
| Day 16 | Binary Tree 难题 | 4 | ⬜ |
| Day 17 | Binary Tree BFS | 4 | ⬜ |
| Day 18 | BST + Graph 入门 | 5 | ⬜ |
| Day 19 | Graph 遍历 | 5 | ⬜ |
| Day 20 | Graph BFS + Trie | 4 | ⬜ |
| Day 21 | Backtracking 回溯 | 5 | ⬜ |
| Day 22 | 回溯 + 分治 | 4 | ⬜ |
| Day 23 | 分治 + Kadane | 4 | ⬜ |
| Day 24 | Binary Search 二分搜索 | 5 | ⬜ |
| Day 25 | 二分 + Heap 堆 | 4 | ⬜ |
| Day 26 | Heap + Bit Manipulation | 4 | ⬜ |
| Day 27 | Bit Manipulation 位运算 | 4 | ⬜ |
| Day 28 | Math 数学 | 5 | ⬜ |
| Day 29 | 1D DP 一维动态规划 | 6 | ⬜ |
| Day 30 | Multi-DP 多维动态规划 | 10 | ⬜ |

---

## 详细每日计划

### 📅 Day 1 — Array/String 基础 ✅ 已完成
> 核心技巧: 双指针原地操作、贪心策略

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 88 | Merge Sorted Array | Easy | 逆向双指针 |
| 27 | Remove Element | Easy | 快慢指针 |
| 26 | Remove Duplicates from Sorted Array | Easy | 快慢指针 |
| 80 | Remove Duplicates from Sorted Array II | Medium | 快慢指针 + 计数 |
| 169 | Majority Element | Easy | Boyer-Moore投票 |
| 189 | Rotate Array | Medium | 三次翻转 |
| 121 | Best Time to Buy and Sell Stock | Easy | 维护最小值 |
| 122 | Best Time to Buy and Sell Stock II | Medium | 贪心 |
| 55 | Jump Game | Medium | 贪心 |
| 45 | Jump Game II | Medium | 贪心/BFS |

---

### 📅 Day 2 — Array/String 进阶
> 核心技巧: 排序、哈希表辅助、前缀积、贪心

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 274 | H-Index | Medium | 排序/计数排序 |
| 380 | Insert Delete GetRandom O(1) | Medium | 哈希表 + 动态数组 |
| 238 | Product of Array Except Self | Medium | 前缀积 + 后缀积 |
| 134 | Gas Station | Medium | 贪心 + 总量判断 |
| 135 | Candy | Hard | 两次遍历贪心 |

---

### 📅 Day 3 — String 操作
> 核心技巧: 字符串遍历、映射转换

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 42 | Trapping Rain Water | Hard | 双指针/单调栈 |
| 13 | Roman to Integer | Easy | 映射表 + 左减规则 |
| 12 | Integer to Roman | Medium | 贪心 + 映射表 |
| 58 | Length of Last Word | Easy | 逆向遍历 |
| 14 | Longest Common Prefix | Easy | 纵向比较 |

---

### 📅 Day 4 — String 困难题
> 核心技巧: 模拟、字符串构造

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 151 | Reverse Words in a String | Medium | 分割 + 翻转 |
| 6 | Zigzag Conversion | Medium | 模拟行变化 |
| 28 | Find the Index of the First Occurrence in a String | Easy | KMP/暴力匹配 |
| 68 | Text Justification | Hard | 模拟 + 字符串格式化 |

---

### 📅 Day 5 — Two Pointers 双指针
> 核心技巧: 对撞指针、快慢指针

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 125 | Valid Palindrome | Easy | 对撞双指针 |
| 392 | Is Subsequence | Easy | 双指针 |
| 167 | Two Sum II - Input Array Is Sorted | Medium | 对撞双指针 |
| 11 | Container With Most Water | Medium | 对撞双指针 + 贪心 |
| 15 | 3Sum | Medium | 排序 + 双指针 |

---

### 📅 Day 6 — Sliding Window 滑动窗口
> 核心技巧: 可变窗口、固定窗口、哈希表辅助

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 209 | Minimum Size Subarray Sum | Medium | 可变滑动窗口 |
| 3 | Longest Substring Without Repeating Characters | Medium | 滑动窗口 + 哈希集合 |
| 30 | Substring with Concatenation of All Words | Hard | 固定窗口 + 哈希表 |
| 76 | Minimum Window Substring | Hard | 可变窗口 + 字符计数 |

---

### 📅 Day 7 — Matrix 矩阵
> 核心技巧: 边界模拟、原地标记

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 36 | Valid Sudoku | Medium | 哈希集合 |
| 54 | Spiral Matrix | Medium | 边界模拟 |
| 48 | Rotate Image | Medium | 转置 + 翻转 |
| 73 | Set Matrix Zeroes | Medium | 原地标记 |
| 289 | Game of Life | Medium | 状态编码 |

---

### 📅 Day 8 — Hashmap 基础
> 核心技巧: 字符映射、分组

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 383 | Ransom Note | Easy | 字符计数 |
| 205 | Isomorphic Strings | Easy | 双向映射 |
| 290 | Word Pattern | Easy | 双向映射 |
| 242 | Valid Anagram | Easy | 字符计数 |
| 49 | Group Anagrams | Medium | 排序键分组 |

---

### 📅 Day 9 — Hashmap 进阶
> 核心技巧: 哈希查找优化、连续序列

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 1 | Two Sum | Easy | 哈希表一遍扫描 |
| 202 | Happy Number | Easy | 快慢指针/哈希集合 |
| 219 | Contains Duplicate II | Easy | 滑动窗口哈希集合 |
| 128 | Longest Consecutive Sequence | Medium | 哈希集合 + 起点判断 |

---

### 📅 Day 10 — Intervals 区间
> 核心技巧: 排序、合并、贪心

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 228 | Summary Ranges | Easy | 线性扫描 |
| 56 | Merge Intervals | Medium | 排序 + 合并 |
| 57 | Insert Interval | Medium | 分类讨论 |
| 452 | Minimum Number of Arrows to Burst Balloons | Medium | 排序 + 贪心 |

---

### 📅 Day 11 — Stack 栈
> 核心技巧: 括号匹配、单调栈、辅助栈

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 20 | Valid Parentheses | Easy | 栈匹配 |
| 71 | Simplify Path | Medium | 栈模拟 |
| 155 | Min Stack | Medium | 辅助栈 |
| 150 | Evaluate Reverse Polish Notation | Medium | 栈计算 |
| 224 | Basic Calculator | Hard | 栈 + 递归 |

---

### 📅 Day 12 — Linked List 基础
> 核心技巧: 虚拟头节点、快慢指针

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 141 | Linked List Cycle | Easy | 快慢指针 |
| 2 | Add Two Numbers | Medium | 模拟进位 |
| 21 | Merge Two Sorted Lists | Easy | 递归/迭代 |
| 138 | Copy List with Random Pointer | Medium | 哈希表/交织复制 |
| 92 | Reverse Linked List II | Medium | 穿针引线 |

---

### 📅 Day 13 — Linked List 进阶
> 核心技巧: 反转、分区、LRU设计

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 25 | Reverse Nodes in k-Group | Hard | 分组反转 |
| 19 | Remove Nth Node From End of List | Medium | 快慢指针 |
| 82 | Remove Duplicates from Sorted List II | Medium | 虚拟头节点 |
| 61 | Rotate List | Medium | 成环断开 |
| 86 | Partition List | Medium | 双链表分区 |
| 146 | LRU Cache | Medium | 哈希表 + 双向链表 |

---

### 📅 Day 14 — Binary Tree 基础
> 核心技巧: DFS递归、树的性质

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 104 | Maximum Depth of Binary Tree | Easy | DFS |
| 100 | Same Tree | Easy | 递归比较 |
| 226 | Invert Binary Tree | Easy | 递归/BFS |
| 101 | Symmetric Tree | Easy | 镜像递归 |
| 105 | Construct Binary Tree from Preorder and Inorder | Medium | 递归 + 哈希表 |

---

### 📅 Day 15 — Binary Tree 构造
> 核心技巧: 树的构造、展平、路径

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 106 | Construct Binary Tree from Inorder and Postorder | Medium | 递归构造 |
| 117 | Populating Next Right Pointers in Each Node II | Medium | BFS/常量空间 |
| 114 | Flatten Binary Tree to Linked List | Medium | 前序遍历 |
| 112 | Path Sum | Easy | DFS |
| 129 | Sum Root to Leaf Numbers | Medium | DFS路径 |

---

### 📅 Day 16 — Binary Tree 难题
> 核心技巧: 路径和、LCA、完全二叉树

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 124 | Binary Tree Maximum Path Sum | Hard | DFS + 全局最大值 |
| 222 | Count Complete Tree Nodes | Easy | 二分 + 完全二叉树性质 |
| 236 | Lowest Common Ancestor of a Binary Tree | Medium | 递归 |
| 199 | Binary Tree Right Side View | Medium | BFS/DFS |

---

### 📅 Day 17 — Binary Tree BFS
> 核心技巧: 层序遍历变体

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 637 | Average of Levels in Binary Tree | Easy | BFS |
| 102 | Binary Tree Level Order Traversal | Medium | BFS |
| 103 | Binary Tree Zigzag Level Order Traversal | Medium | BFS + 翻转 |
| 530 | Minimum Absolute Difference in BST | Easy | 中序遍历 |

---

### 📅 Day 18 — BST + Graph 入门
> 核心技巧: BST中序性质、图的DFS/BFS

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 173 | Binary Search Tree Iterator | Medium | 中序遍历 + 迭代器设计 |
| 230 | Kth Smallest Element in a BST | Medium | 中序遍历 |
| 98 | Validate Binary Search Tree | Medium | 中序/递归 + 范围 |
| 200 | Number of Islands | Medium | DFS/BFS |
| 130 | Surrounded Regions | Medium | 边界DFS |
| 133 | Clone Graph | Medium | BFS/DFS + 哈希表 |

---

### 📅 Day 19 — Graph 遍历
> 核心技巧: 拓扑排序、带权图

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 399 | Evaluate Division | Medium | 带权图BFS/并查集 |
| 207 | Course Schedule | Medium | 拓扑排序 |
| 210 | Course Schedule II | Medium | 拓扑排序 |
| 909 | Snakes and Ladders | Medium | BFS |
| 433 | Minimum Genetic Mutation | Medium | BFS |

---

### 📅 Day 20 — Graph BFS + Trie
> 核心技巧: 最短路径BFS、前缀树

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 127 | Word Ladder | Hard | BFS + 双向BFS |
| 208 | Implement Trie (Prefix Tree) | Medium | 前缀树实现 |
| 211 | Design Add and Search Words Data Structure | Medium | Trie + DFS |
| 212 | Word Search II | Hard | Trie + 回溯 |

---

### 📅 Day 21 — Backtracking 回溯
> 核心技巧: 选择-探索-撤销

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 17 | Letter Combinations of a Phone Number | Medium | 回溯 |
| 77 | Combinations | Medium | 回溯 + 剪枝 |
| 46 | Permutations | Medium | 回溯 |
| 39 | Combination Sum | Medium | 回溯 + 剪枝 |
| 52 | N-Queens II | Hard | 回溯 |

---

### 📅 Day 22 — 回溯 + 分治
> 核心技巧: 二维回溯、递归分治

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 22 | Generate Parentheses | Medium | 回溯 |
| 79 | Word Search | Medium | DFS回溯 |
| 108 | Convert Sorted Array to Binary Search Tree | Easy | 递归分治 |
| 148 | Sort List | Medium | 归并排序 |

---

### 📅 Day 23 — 分治 + Kadane
> 核心技巧: 分治思想、最大子数组

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 427 | Construct Quad Tree | Medium | 递归分治 |
| 23 | Merge k Sorted Lists | Hard | 分治/优先队列 |
| 53 | Maximum Subarray | Medium | Kadane算法 |
| 918 | Maximum Sum Circular Subarray | Medium | Kadane变体 |

---

### 📅 Day 24 — Binary Search 二分搜索
> 核心技巧: 标准二分、变体二分

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 35 | Search Insert Position | Easy | 标准二分 |
| 74 | Search a 2D Matrix | Medium | 二维二分 |
| 162 | Find Peak Element | Medium | 二分 |
| 33 | Search in Rotated Sorted Array | Medium | 二分变体 |
| 34 | Find First and Last Position of Element in Sorted Array | Medium | 左右边界二分 |

---

### 📅 Day 25 — 二分 + Heap 堆
> 核心技巧: 旋转数组二分、堆排序

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 153 | Find Minimum in Rotated Sorted Array | Medium | 二分 |
| 4 | Median of Two Sorted Arrays | Hard | 二分 |
| 215 | Kth Largest Element in an Array | Medium | 快速选择/堆 |
| 502 | IPO | Hard | 贪心 + 双堆 |

---

### 📅 Day 26 — Heap + Bit Manipulation
> 核心技巧: 优先队列、位运算基础

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 373 | Find K Pairs with Smallest Sums | Medium | 最小堆 |
| 295 | Find Median from Data Stream | Hard | 对顶堆 |
| 67 | Add Binary | Easy | 模拟进位 |
| 190 | Reverse Bits | Easy | 位操作 |

---

### 📅 Day 27 — Bit Manipulation 位运算
> 核心技巧: 异或、位计数、位区间

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 191 | Number of 1 Bits | Easy | n & (n-1) |
| 136 | Single Number | Easy | 异或 |
| 137 | Single Number II | Medium | 位计数/状态机 |
| 201 | Bitwise AND of Numbers Range | Medium | 公共前缀 |

---

### 📅 Day 28 — Math 数学
> 核心技巧: 数学规律、快速幂

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 9 | Palindrome Number | Easy | 反转一半 |
| 66 | Plus One | Easy | 进位模拟 |
| 172 | Factorial Trailing Zeroes | Medium | 因子5计数 |
| 69 | Sqrt(x) | Easy | 二分/牛顿法 |
| 50 | Pow(x, n) | Medium | 快速幂 |

---

### 📅 Day 29 — 1D DP 一维动态规划
> 核心技巧: 状态定义、转移方程

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 149 | Max Points on a Line | Hard | 斜率哈希 |
| 70 | Climbing Stairs | Easy | dp[i] = dp[i-1] + dp[i-2] |
| 198 | House Robber | Medium | dp[i] = max(dp[i-1], dp[i-2]+nums[i]) |
| 139 | Word Break | Medium | dp + 字典判断 |
| 322 | Coin Change | Medium | 完全背包 |
| 300 | Longest Increasing Subsequence | Medium | dp/二分优化 |

---

### 📅 Day 30 — Multi-DP 多维动态规划 (冲刺日)
> 核心技巧: 二维dp、区间dp、状态压缩

| # | 题目 | 难度 | 关键技巧 |
|---|------|------|----------|
| 120 | Triangle | Medium | 自底向上dp |
| 64 | Minimum Path Sum | Medium | 网格dp |
| 63 | Unique Paths II | Medium | 网格dp + 障碍 |
| 5 | Longest Palindromic Substring | Medium | 中心扩展/dp |
| 97 | Interleaving String | Medium | 二维dp |
| 72 | Edit Distance | Medium | 经典二维dp |
| 123 | Best Time to Buy and Sell Stock III | Hard | 状态机dp |
| 188 | Best Time to Buy and Sell Stock IV | Hard | 通用状态机dp |
| 221 | Maximal Square | Medium | 二维dp |

---

## 学习建议

### 每日流程
1. **看题 (5min)** — 理解题意，想清楚边界情况
2. **独立思考 (15-20min)** — 尝试自己写出解法
3. **参考解题思路** — 对比最优解，理解核心技巧
4. **用两种语言实现** — Python 和 Java 各写一遍，加深理解
5. **复习拓展练习** — 巩固同类型题目

### 难度分配策略
- **Easy 题**: 15分钟内完成，重点掌握基础模板
- **Medium 题**: 25-30分钟，理解核心思路后编码
- **Hard 题**: 40-45分钟，先理解解法再实现

### 文件夹结构
```
leetcode-top-150/
├── README.md                          # 本计划文件
├── day01-array-string-basics/         # ✅ 已完成
│   ├── python/                        # Python 解法
│   └── java/                          # Java 解法
├── day02-array-string-advanced/
│   ├── python/
│   │   ├── 274_h_index.py
│   │   ├── 380_insert_delete_getrandom.py
│   │   └── ...
│   └── java/
│       ├── HIndex274.java
│       ├── InsertDeleteGetRandom380.java
│       └── ...
├── day03-string-operations/
│   ├── python/
│   └── java/
...
└── day30-multidimensional-dp/
    ├── python/
    └── java/
```
