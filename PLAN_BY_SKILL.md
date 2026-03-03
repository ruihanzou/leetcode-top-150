## LeetCode Top 150 — Skill-Based Roadmap

This file gives an **alternative view** of the same LeetCode Top Interview 150 set:

- **时间线模式**: 按照 `Day 1 → Day 30` 跟着 `README.md` 刷题。
- **技能模式**: 按 **`skill-days/`** 的 32 天顺序刷题，每天同一技能、≤ 5 题、≤ 1 Hard。

下面的 **32 个小节** 与 `skill-days/day01-two-pointers` ~ `skill-days/day32-dp-2d` 一一对应，代码目录与本文档完全一致。

> 每个 Day 下的 **拓展** 为 Top 150 之外的推荐题目，用于巩固当天技能、拓展同类型题感。

---

## 高效刷题指南

### 时间规划
- **每道 Easy**：10–15 分钟（含想 + 写 + 对拍）
- **每道 Medium**：15–25 分钟；卡住超 20 分钟就先看提示
- **每道 Hard**：25–40 分钟；可分段做（先暴力再优化）
- **每天 5 题**：建议 1–1.5 小时专注块，早/晚各一块更易坚持

### 做题前 30 秒自问
1. 题意和示例都清楚了吗？边界（空、单元素、重复）考虑了吗？
2. 能一句话说出「用什么套路」吗？（双指针 / 滑动窗口 / DFS / DP...）
3. 目标时间/空间复杂度大概是多少？

### 复习节奏（艾宾浩斯简化版）
- **第 1 天**：做 Day N
- **第 2 天**：做 Day N+1 前，花 5 分钟快速过一遍 Day N 的「当天重点」
- **第 4 天**：做 Day N+3 前，闭眼回忆 Day N 的 2–3 道核心题的思路
- **第 8 天**：做 Day N+7 前，挑 Day N 里最卡的一道重写一遍

### 每日完成后自检
- [ ] 今天 5 题的「核心思路」能脱口而出吗？
- [ ] 有 1 道以上完全没想出来、直接看答案的吗？若有，标记「需二刷」
- [ ] 明天的技能跟今天有关吗？有的话提前扫一眼模板

### 核心模板速查（卡住时看一眼）
| 套路 | 一句话 | 关键代码 |
|------|--------|----------|
| 快慢指针 | slow 写、fast 扫，满足条件才写 | `if cond: arr[slow]=arr[fast]; slow+=1` |
| 对撞指针 | l,r 两端向中间，根据比较移动 | `while l<r: if arr[l]+arr[r]...</` |
| 滑动窗口 | r 扩、不满足时 l 收 | `while valid: update; r++; while !valid: l++` |
| 二分 | 找满足条件的边界，注意 l,r 的取等 | `while l<=r: m=(l+r)//2; if cond: r=m-1 else l=m+1` |
| BFS 层序遍历 | queue，每层 for _ in range(len(q)) | `while q: for _ in range(len(q)): ...` |
| DFS 递归 | 选/不选 or 枚举下一个，记得恢复 | `path.append(x); dfs(); path.pop()` |
| Kadane | 以 i 结尾的最大和 = max(nums[i], dp[i-1]+nums[i]) | `cur=max(x, cur+x); best=max(best,cur)` |
| 1D DP | 定义 dp[i]，找与 dp[0..i-1] 的转移 | 先想「最后一步做了什么」 |

### 面试高频（★ 时间紧可优先）
★1, ★3, ★15, ★20, ★21, ★42, ★76, ★98, ★102, ★121, ★146, ★200, ★207, ★236, ★5, ★72, ★23

### 卡题急救（卡住时看突破点）
| 题号 | 突破点 |
|------|--------|
| 42 | 双指针：min(lmax,rmax)-h；单调栈：维护递减，出栈时算水 |
| 76 | needs 记录目标字符数，window 记录当前；valid 满足时才收缩 l |
| 30 | 固定窗口 len(words[0])*len(words)，按词切分后滑 |
| 135 | 左扫一遍（比左大则+1），右扫一遍（比右大则取 max） |
| 68 | 最后一行左对齐；中间行均匀插空格，整除不了的前几个多插 1 |
| 149 | 斜率用 dx,dy 最大公约数约分，dx*MAX+dy 当 key |
| 224 | 遇到 ( 递归，遇到 ) 返回；+- 用 sign=+1/-1 |
| 25 | 先 reverseK 一组，返回新头；递归处理下一组，接在尾上 |
| 124 | DFS 返回「单边最大路径」；用 左+右+根 更新全局 ans |
| 127 | BFS 层数即步数；每个位置枚举 26 字母替换 |
| 212 | Trie 存词表；网格 DFS 时用 Trie 剪枝，找到就剪掉该分支 |
| 52 | 同 51，只计数；位运算：col/ dia1/ dia2 用 int 表示 |
| 23 | 分治：mergeTwo(merge(l,m), merge(m+1,r))；或堆维护 k 路 |
| 4 | 划分：保证 left≤right，用二分找划分点 |
| 188 | 复用 123 思路，k 次交易 = 2k 个状态，k 大时退化成 122 |

### 快捷入口
- **LeetCode 搜题**：站内搜索框输入题号（如 `88`）可直达
- **代码目录**：`skill-days/dayXX-xxx/python/` 和 `java/`

---

## Day 1 — day01-two-pointers（Two Pointers 基础）

**核心能力**：快慢指针、对撞指针、原地修改数组。  
**当天重点**：88/27/26 是「快慢指针」三板斧（归并、删除、去重）；125/392 是对撞指针和双指针遍历，建立左右指针同时移动的直觉。  
**拓展**：977 Squares of Sorted Array（有序数组平方）、986 Interval List Intersections（区间列表交集）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 88 | Merge Sorted Array | Easy | 逆向双指针归并 |
| 27 | Remove Element | Easy | 快慢指针删除元素 |
| 26 | Remove Duplicates from Sorted Array | Easy | 快慢指针去重 |
| 125 | Valid Palindrome | Easy | 对撞指针 |
| 392 | Is Subsequence | Easy | 双指针遍历 |

---

## Day 2 — day02-two-pointers（Two Pointers 进阶）

**核心能力**：对撞指针进阶、排序后双指针、盛水/接雨类问题。  
**当天重点**：80 在 26 基础上加「最多保留 2 个」的计数；167/11/15 是「有序数组 + 对撞」的经典组合；42 接雨水既可双指针也可单调栈，建议两种写法都过一遍。  
**拓展**：18 4Sum（四数之和）、75 Sort Colors（荷兰国旗/三路 partition）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 80 | Remove Duplicates from Sorted Array II | Medium | 去重 + 计数控制 |
| 167 | Two Sum II - Input Array Is Sorted | Medium | 有序数组对撞指针 |
| 11 | Container With Most Water | Medium | 对撞指针 + 贪心 |
| 15 | 3Sum | Medium | 排序 + 双指针 |
| **42** | Trapping Rain Water | Hard | 双指针/单调栈 |

---

## Day 3 — day03-sliding-window（Sliding Window）

**核心能力**：固定/可变窗口、左右指针收缩、字符计数。  
**当天重点**：219 是简单窗口入门；209 是最小可变窗口模板；3 是无重复子串的经典题；76 是最小覆盖子串，需要 needs/window 双计数 + 收缩条件判断，是滑动窗口 Hard 标杆。  
**拓展**：424 Longest Repeating Character Replacement（最多替换 k 次）、992 Subarrays with K Different Integers（恰含 k 个不同整数）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 219 | Contains Duplicate II | Easy | 滑动窗口 + 哈希集合 |
| 209 | Minimum Size Subarray Sum | Medium | 最基础可变窗口 |
| 3 | Longest Substring Without Repeating Characters | Medium | 集合去重窗口 |
| **76** | Minimum Window Substring | Hard | 字符需求计数窗口 |

---

## Day 4 — day04-sliding-window-greedy（Sliding Window + Greedy）

**核心能力**：固定窗口 + 词频、股票贪心、跳跃可达性。  
**当天重点**：30 是固定窗口 + 词频哈希，边界较多；121/122/55 是经典贪心：维护最小值、累加上升段、维护可达最远位置。  
**拓展**：406 Queue Reconstruction by Height（身高重排）、763 Partition Labels（划分字母区间）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| **30** | Substring with Concatenation of All Words | Hard | 固定窗口 + 词频哈希 |
| 121 | Best Time to Buy and Sell Stock | Easy | 一次遍历维护最小价 |
| 122 | Best Time to Buy and Sell Stock II | Medium | 贪心累加所有上升段 |
| 55 | Jump Game | Medium | 可达最远位置贪心 |

---

## Day 5 — day05-greedy-and-prefix（Greedy & Prefix/Suffix）

**核心能力**：前缀/后缀积、跳跃贪心、区间贪心、计数类题。  
**当天重点**：238 前缀积×后缀积是 O(1) 空间的经典技巧；134 加油站的「总量判断 + 起点贪心」；45 跳跃 II 可视为 BFS 或贪心；274 H-Index 理解「引用数 ≥ 篇数」的定义。  
**拓展**：560 Subarray Sum Equals K（前缀和 + 哈希）、406 Queue Reconstruction by Height（身高重排贪心）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 45 | Jump Game II | Medium | 区间贪心/BFS 思路 |
| 189 | Rotate Array | Medium | 三次翻转 |
| 238 | Product of Array Except Self | Medium | 前缀积 + 后缀积 |
| 134 | Gas Station | Medium | 总量判断 + 起点贪心 |
| 274 | H-Index | Medium | 排序/计数排序 |

---

## Day 6 — day06-greedy-array-string（Greedy + Array/String）

**核心能力**：左右遍历贪心、字符串遍历与比较、映射转换。  
**当天重点**：135  Candy 是「左右各扫一遍」的典型；58/14/28 是字符串基础（逆向、纵向比较、子串匹配）；13 罗马数字掌握「左小减、左大加」规则。  
**拓展**：409 Longest Palindrome（回文构造）、763 Partition Labels（贪心划分）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| **135** | Candy | Hard | 左右两遍贪心分配 |
| 58 | Length of Last Word | Easy | 基础字符串遍历 |
| 14 | Longest Common Prefix | Easy | 纵向比较前缀 |
| 28 | Find the Index of the First Occurrence in a String | Easy | 子串匹配/KMP 入门 |
| 13 | Roman to Integer | Easy | 映射表 + 左减规则 |

---

## Day 7 — day07-array-string（Array & String）

**核心能力**：投票算法、字符串分割与构造、模拟。  
**当天重点**：169 Boyer-Moore 投票是 O(1) 空间求多数；151 分割+翻转；6 Z 字形是下标映射；12 罗马数字输出用贪心+映射；68 文本对齐是细节较多的模拟题。  
**拓展**：229 Majority Element II（三三抵消）、443 String Compression（原地压缩）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 169 | Majority Element | Easy | Boyer-Moore 投票 |
| 151 | Reverse Words in a String | Medium | 分割 + 翻转构造 |
| 6 | Zigzag Conversion | Medium | 字符串构造模拟 |
| 12 | Integer to Roman | Medium | 贪心 + 映射表 |
| **68** | Text Justification | Hard | 字符串格式化模拟 |

---

## Day 8 — day08-hashmap-and-set（Hashmap 基础）

**核心能力**：字符计数、双向映射、哈希一遍扫描。  
**当天重点**：383/242 是字符计数入门；205/290 是「字符→字符」或「单词→模式」的双向映射；1 Two Sum 是哈希表最经典用法，务必秒写。  
**拓展**：454 4Sum II（四数组、哈希分组）、525 Contiguous Array（前缀和 + 哈希 0/1 平衡）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 383 | Ransom Note | Easy | 字符计数 |
| 242 | Valid Anagram | Easy | 字符计数 |
| 205 | Isomorphic Strings | Easy | 双向映射 |
| 290 | Word Pattern | Easy | 双向映射 |
| 1 | Two Sum | Easy | 哈希表一遍扫描 |

---

## Day 9 — day09-hashmap-and-set（Hashmap 进阶）

**核心能力**：分组、集合判连续、设计 O(1) 结构、几何哈希。  
**当天重点**：202 可快慢指针也可 set 判环；49 分组 key 用排序或计数；128 只在「序列起点」扩展；380 哈希表+动态数组实现 O(1) 随机；149 斜率/向量哈希防精度问题。  
**拓展**：460 LFU Cache（LFU 设计，比 LRU 更难）、164 Maximum Gap（桶排序 + 相邻桶）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 202 | Happy Number | Easy | 快慢指针/哈希集合 |
| 49 | Group Anagrams | Medium | 排序键/计数键分组 |
| 128 | Longest Consecutive Sequence | Medium | 集合 + 起点判断 |
| 380 | Insert Delete GetRandom O(1) | Medium | 哈希表 + 动态数组 |
| **149** | Max Points on a Line | Hard | 斜率哈希 |

---

## Day 10 — day10-intervals-matrix（Intervals + Matrix）

**核心能力**：区间排序合并、调度贪心、矩阵校验与模拟。  
**当天重点**：228 线性扫描连续区间；56/57 区间合并与插入的模板；452 射箭问题是「按右端点排序」的贪心；36 数独是行列宫三维检查。  
**拓展**：435 Non-overlapping Intervals（无重叠区间）、417 Pacific Atlantic Water Flow（双源 BFS）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 228 | Summary Ranges | Easy | 扫描连续区间 |
| 56 | Merge Intervals | Medium | 排序 + 合并 |
| 57 | Insert Interval | Medium | 分类讨论插入 |
| 452 | Minimum Number of Arrows to Burst Balloons | Medium | 区间排序 + 射箭贪心 |
| 36 | Valid Sudoku | Medium | 行列宫哈希检查 |

---

## Day 11 — day11-matrix-stack（Matrix + Stack 入门）

**核心能力**：矩阵边界与原地、状态编码、栈匹配。  
**当天重点**：54 螺旋矩阵按层处理边界；48 转置+翻转；73 首行首列做标记；289 生命游戏用位编码原地；20 括号匹配是栈最基础题。  
**拓展**：59 Spiral Matrix II（生成螺旋矩阵）、240 Search a 2D Matrix II（Z 字形搜索）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 54 | Spiral Matrix | Medium | 边界模拟 |
| 48 | Rotate Image | Medium | 转置 + 翻转 |
| 73 | Set Matrix Zeroes | Medium | 原地标记首行首列 |
| 289 | Game of Life | Medium | 状态编码原地更新 |
| 20 | Valid Parentheses | Easy | 括号栈匹配 |

---

## Day 12 — day12-stack（Stack 进阶）

**核心能力**：路径栈、辅助栈、逆波兰、表达式解析、快慢指针判环。  
**当天重点**：71 用栈处理路径；155 辅助栈维护最小值；150 逆波兰是栈计算模板；224 计算器需要处理括号和符号；141 虽然放这，本质是快慢指针判环。  
**拓展**：84 Largest Rectangle in Histogram（单调栈）、316 Remove Duplicate Letters（单调栈 + 去重）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 71 | Simplify Path | Medium | 路径栈简化 |
| 155 | Min Stack | Medium | 辅助栈维护最小值 |
| 150 | Evaluate Reverse Polish Notation | Medium | 逆波兰表达式栈计算 |
| **224** | Basic Calculator | Hard | 栈 + 递归/符号处理 |
| 141 | Linked List Cycle | Easy | 快慢指针判环 |

---

## Day 13 — day13-linked-list（Linked List 基础）

**核心能力**：虚拟头、归并、区间反转、随机指针、快慢指针定位。  
**当天重点**：21 归并链表是基础；2 链表加法注意进位；138 随机指针可哈希或交织复制；92 区间反转要处理好前后连接；19 快慢指针找倒数第 k 个。  
**拓展**：143 Reorder List（找中点 + 反转 + 合并）、234 Palindrome Linked List（快慢指针找中点 + 反转比较）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 21 | Merge Two Sorted Lists | Easy | 归并链表 |
| 2 | Add Two Numbers | Medium | 链表模拟加法 |
| 138 | Copy List with Random Pointer | Medium | 哈希/交织复制 |
| 92 | Reverse Linked List II | Medium | 区间反转 |
| 19 | Remove Nth Node From End of List | Medium | 快慢指针定位 |

---

## Day 14 — day14-linked-list（Linked List 进阶）

**核心能力**：虚拟头去重、成环断开、分区、LRU 设计、k 组反转。  
**当天重点**：82 虚拟头+去重逻辑；61 成环后旋转再断开；86 分区用两个子链表；146 LRU 是哈希+双向链表的经典设计；25 k 组反转要递归或迭代写好边界。  
**拓展**：24 Swap Nodes in Pairs（两两交换）、460 LFU Cache（比 LRU 多一层频率）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 82 | Remove Duplicates from Sorted List II | Medium | 虚拟头 + 去重 |
| 61 | Rotate List | Medium | 成环再断开 |
| 86 | Partition List | Medium | 两个子链表分区 |
| 146 | LRU Cache | Medium | 哈希 + 双向链表 |
| **25** | Reverse Nodes in k-Group | Hard | 分组反转 |

---

## Day 15 — day15-binary-tree（Binary Tree 基础）

**核心能力**：DFS 深度、结构比较、翻转、镜像判断、路径和。  
**当天重点**：104/100/226/101 是树递归的入门四件套；112 路径和是「target - val」向下传递的典型写法。  
**拓展**：113 Path Sum II（输出路径）、437 Path Sum III（前缀和 + 哈希）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 104 | Maximum Depth of Binary Tree | Easy | DFS 深度 |
| 100 | Same Tree | Easy | 递归比较结构 |
| 226 | Invert Binary Tree | Easy | 递归/BFS 交换左右 |
| 101 | Symmetric Tree | Easy | 镜像递归判断 |
| 112 | Path Sum | Easy | DFS 路径和 |

---

## Day 16 — day16-binary-tree（Binary Tree 构造）

**核心能力**：完全二叉树性质、BFS 层序、前序+中序/中序+后序构造。  
**当天重点**：222 完全二叉树用高度性质+二分；105/106 是构造树的经典：前序/后序定根，中序分左右；117 next 指针可层序遍历或利用已建立的 next。  
**拓展**：889 Construct Binary Tree from Preorder and Postorder（前序+后序构造）、110 Balanced Binary Tree（高度平衡判断）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 222 | Count Complete Tree Nodes | Easy | 完全二叉树高度性质 + 二分 |
| 637 | Average of Levels in Binary Tree | Easy | BFS 层平均值 |
| 105 | Construct Binary Tree from Preorder and Inorder | Medium | 前序 + 中序构造 |
| 106 | Construct Binary Tree from Inorder and Postorder | Medium | 中序 + 后序构造 |
| 117 | Populating Next Right Pointers in Each Node II | Medium | 层序/BFS 连接 next |

---

## Day 17 — day17-binary-tree（Binary Tree 高级）

**核心能力**：前序展开、DFS 路径、LCA、右视图、层序遍历。  
**当天重点**：114 展开为链表用 Morris 或递归前序；129 路径转数字；236 LCA 理解「一个在左一个在右」即根；199 右视图用 BFS 或 DFS 层高；102 层序遍历是 BFS 模板。  
**拓展**：116 Populating Next Right Pointers（完美二叉树 next）、513 Find Bottom Left Tree Value（层序最后一个）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 114 | Flatten Binary Tree to Linked List | Medium | 前序展开为链表 |
| 129 | Sum Root to Leaf Numbers | Medium | DFS 数字路径 |
| 236 | Lowest Common Ancestor of a Binary Tree | Medium | 递归 LCA |
| 199 | Binary Tree Right Side View | Medium | BFS/DFS 右视图 |
| 102 | Binary Tree Level Order Traversal | Medium | BFS 层序遍历 |

---

## Day 18 — day18-binary-tree-bst（Tree + BST）

**核心能力**：之字形层序、最大路径和、BST 性质、递归分治构造。  
**当天重点**：103 之字形层序用层数奇偶反转；124 最大路径和是 DFS 返回「单边最大」+ 更新全局；530 BST 中序相邻最小差；108 有序数组构造平衡 BST 是分治入门。  
**拓展**：98 思路延伸的 99 Recover BST（中序找逆序对）、450 Delete Node in a BST（BST 删除）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 103 | Binary Tree Zigzag Level Order Traversal | Medium | 之字形层序 |
| **124** | Binary Tree Maximum Path Sum | Hard | DFS + 全局最大路径 |
| 530 | Minimum Absolute Difference in BST | Easy | BST 中序相邻差值 |
| 108 | Convert Sorted Array to Binary Search Tree | Easy | 递归分治构造 |

---

## Day 19 — day19-bst-graph（BST + Graph 入门）

**核心能力**：BST 中序、验证、迭代器、岛屿 DFS、边界处理。  
**当天重点**：230 中序第 k 小；98 范围递归验证 BST；173 迭代器用栈模拟中序；200/130 是网格 DFS 入门，130 注意从边界出发。  
**拓展**：417 Pacific Atlantic Water Flow（双源 BFS/DFS）、694 Number of Distinct Islands（岛屿形状哈希）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 230 | Kth Smallest Element in a BST | Medium | 中序第 k 小 |
| 98 | Validate Binary Search Tree | Medium | 范围限制/中序检查 |
| 173 | BST Iterator | Medium | 中序迭代器 |
| 200 | Number of Islands | Medium | 网格 DFS/BFS |
| 130 | Surrounded Regions | Medium | 边界 DFS |

---

## Day 20 — day20-graph（Graph 遍历）

**核心能力**： clone 图、带权图、拓扑排序、BFS 棋盘。  
**当天重点**：133 clone 用 HashMap 存旧→新映射；399 带权图 BFS 或并查集；207/210 拓扑排序判断环与输出序列；909 棋盘 BFS 注意格子和编号转换。  
**拓展**：787 Cheapest Flights Within K Stops（带限制最短路）、417 Pacific Atlantic Water Flow（双源 BFS）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 133 | Clone Graph | Medium | 图 DFS/BFS + 哈希表 |
| 399 | Evaluate Division | Medium | 带权图 + BFS/并查集 |
| 207 | Course Schedule | Medium | 拓扑排序可行性 |
| 210 | Course Schedule II | Medium | 拓扑排序序列 |
| 909 | Snakes and Ladders | Medium | 棋盘 BFS |

---

## Day 21 — day21-graph-trie（Graph + Trie）

**核心能力**：状态图 BFS、Trie 实现、Trie+DFS、单词接龙。  
**当天重点**：433 基因突变是 8 方向 BFS；208/211 Trie 基础与带通配符搜索；127 单词接龙是 BFS 或双向 BFS 的经典题。  
**拓展**：126 Word Ladder II（输出路径）、720 Longest Word in Dictionary（Trie 前缀判断）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 433 | Minimum Genetic Mutation | Medium | 状态图 BFS |
| 208 | Implement Trie (Prefix Tree) | Medium | Trie 基础实现 |
| 211 | Design Add and Search Words Data Structure | Medium | Trie + DFS |
| **127** | Word Ladder | Hard | BFS / 双向 BFS |

---

## Day 22 — day22-trie-backtracking（Trie + Backtracking）

**核心能力**：Trie+回溯搜索、组合、排列。  
**当天重点**：212 在网格上用 Trie 回溯，找到词就剪枝；17/77/46 是回溯入门：电话字母组合、组合、全排列，掌握「选择-递归-撤销」模板。  
**拓展**：78 Subsets（子集）、51 N-Queens（输出棋盘）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| **212** | Word Search II | Hard | Trie + 回溯 |
| 17 | Letter Combinations of a Phone Number | Medium | 字符串组合回溯 |
| 77 | Combinations | Medium | 组合 + 剪枝 |
| 46 | Permutations | Medium | 全排列 |

---

## Day 23 — day23-backtracking（Backtracking）

**核心能力**：组合总和、括号生成、网格回溯、N 皇后。  
**当天重点**：39 可重复选；22 括号合法条件「左≥右」；79 网格 DFS 回溯；52 N 皇后 II 只计数，可位运算优化。  
**拓展**：51 N-Queens（输出解）、37 Sudoku Solver（数独回溯）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 39 | Combination Sum | Medium | 组合总和 |
| 22 | Generate Parentheses | Medium | 合法括号回溯 |
| 79 | Word Search | Medium | 网格 DFS 回溯 |
| **52** | N-Queens II | Hard | 棋盘回溯 |

---

## Day 24 — day24-divide-conquer-kadane（Divide & Conquer + Kadane）

**核心能力**：四叉树分治、链表归并、Kadane、分治合并。  
**当天重点**：427 四叉树理解「全同则叶子」；148 链表排序用归并；53/918 Kadane 及其环状变体；23 合并 k 链表用分治或堆。  
**拓展**：312 Burst Balloons（区间 DP）、327 Count of Range Sum（前缀和 + 归并）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 427 | Construct Quad Tree | Medium | 递归分治构建 |
| 148 | Sort List | Medium | 链表归并排序 |
| 53 | Maximum Subarray | Medium | Kadane 算法 |
| 918 | Maximum Sum Circular Subarray | Medium | Kadane 变体 |
| **23** | Merge k Sorted Lists | Hard | 分治/优先队列 |

---

## Day 25 — day25-binary-search（Binary Search 基础）

**核心能力**：基础二分、开方、二维转一维、峰值、旋转数组。  
**当天重点**：35 插入位置是 lower_bound；69 开方；74 二维矩阵当一维二分；162 峰值用「比较 mid 与 mid+1」；33 旋转数组判断左右哪边有序。  
**拓展**：278 First Bad Version（二分边界）、275 H-Index II（有序数组 H-Index）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 35 | Search Insert Position | Easy | 基础二分模板 |
| 69 | Sqrt(x) | Easy | 二分/牛顿法 |
| 74 | Search a 2D Matrix | Medium | 二维转一维二分 |
| 162 | Find Peak Element | Medium | 峰值二分 |
| 33 | Search in Rotated Sorted Array | Medium | 旋转数组二分 |

---

## Day 26 — day26-binary-search-heap（Binary Search + Heap）

**核心能力**：左右边界二分、旋转数组最值、两个有序数组 Median、快速选择。  
**当天重点**：34 左右边界两次二分；153 旋转数组最小值；4 两数组 Median 是划分+二分的难题；215 第 k 大用快排思想或堆。  
**拓展**：378 Kth Smallest in Sorted Matrix（堆/二分）、719 Find K-th Smallest Pair Distance（二分答案）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 34 | Find First and Last Position of Element in Sorted Array | Medium | 左右边界二分 |
| 153 | Find Minimum in Rotated Sorted Array | Medium | 旋转数组最小值二分 |
| **4** | Median of Two Sorted Arrays | Hard | 划分 + 二分答案 |
| 215 | Kth Largest Element in an Array | Medium | 快速选择/堆 |

---

## Day 27 — day27-heap-bit-manipulation（Heap + Bit Manipulation）

**核心能力**：最小堆 k 组、IPO 贪心+堆、二进制加法、位翻转。  
**当天重点**：373 k 对小和用堆；502 IPO 是贪心+双堆；67/190 二进制模拟与位运算基础。  
**拓展**：378 Kth Smallest in Sorted Matrix（堆+多路归并）、260 Single Number III（双异或分组）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 373 | Find K Pairs with Smallest Sums | Medium | 最小堆 K 组数对 |
| **502** | IPO | Hard | 贪心 + 双堆 |
| 67 | Add Binary | Easy | 二进制加法模拟 |
| 190 | Reverse Bits | Easy | 位翻转 |

---

## Day 28 — day28-bit-manipulation（Bit Manipulation）

**核心能力**：位计数、异或、位状态、区间公共前缀、对顶堆。  
**当天重点**：191 n&(n-1) 消最低位；136 异或找唯一；137 位计数/状态机；201 区间 AND 是公共前缀；295 对顶堆维护中位数。  
**拓展**：260 Single Number III（双异或找两数）、371 Sum of Two Integers（位运算加法）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 191 | Number of 1 Bits | Easy | n & (n-1) 计数 |
| 136 | Single Number | Easy | 异或找唯一 |
| 137 | Single Number II | Medium | 位计数/状态机 |
| 201 | Bitwise AND of Numbers Range | Medium | 公共前缀 |
| **295** | Find Median from Data Stream | Hard | 对顶堆维护中位数 |

---

## Day 29 — day29-math-dp-1d（Math + DP 入门）

**核心能力**：回文判断、数组进位、因子计数、快速幂、Fibonacci DP。  
**当天重点**：9 反转一半比回文；66 数组进位；172 统计因子 5；50 快速幂；70 爬楼梯是 DP 最入门题。  
**拓展**：263 Ugly Number（因子判断）、91 Decode Ways（简单 1D DP）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 9 | Palindrome Number | Easy | 反转一半数字 |
| 66 | Plus One | Easy | 数组模拟进位 |
| 172 | Factorial Trailing Zeroes | Medium | 统计因子 5 |
| 50 | Pow(x, n) | Medium | 快速幂 |
| 70 | Climbing Stairs | Easy | 经典 Fibonacci DP |

---

## Day 30 — day30-dp-1d（1D DP）

**核心能力**：打家劫舍、字典切分、完全背包、LIS、股票多状态。  
**当天重点**：198 相邻不取；139 字典切分 DP；322 完全背包最少硬币；300 LIS 可二分优化；123 股票 III 是两笔交易的状态机。  
**拓展**：213 House Robber II（环形）、279 Perfect Squares（完全背包 / BFS）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 198 | House Robber | Medium | 打家劫舍基础 |
| 139 | Word Break | Medium | 字典切分 DP |
| 322 | Coin Change | Medium | 完全背包最少硬币 |
| 300 | Longest Increasing Subsequence | Medium | DP/二分优化 |
| **123** | Best Time to Buy and Sell Stock III | Hard | 多状态机 DP |

---

## Day 31 — day31-dp-2d（2D / Multi-D DP）

**核心能力**：三角形路径、网格最短路径、障碍路径、回文、交错字符串。  
**当天重点**：120 自底向上；64/63 网格 DP；5 中心扩展或二维 DP；97 交错字符串是 2D 经典。  
**拓展**：131 Palindrome Partitioning（回文划分）、312 Burst Balloons（区间 DP）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 120 | Triangle | Medium | 自底向上三角形 DP |
| 64 | Minimum Path Sum | Medium | 网格最短路径 DP |
| 63 | Unique Paths II | Medium | 障碍网格 DP |
| 5 | Longest Palindromic Substring | Medium | 中心扩展/二维 DP |
| 97 | Interleaving String | Medium | 2D DP 交错字符串 |

---

## Day 32 — day32-dp-2d（2D DP 冲刺）

**核心能力**：编辑距离、最大正方形、股票 K 次交易。  
**当天重点**：72 编辑距离是经典 2D DP；221 最大正方形以右下为顶点的边长；188 股票 IV 是 K 次交易的状态机，可复用 123 思路。  
**拓展**：10 Regular Expression Matching（正则 DP）、44 Wildcard Matching（通配符 DP）

| # | 题目 | 难度 | Notes |
|---|------|------|-------|
| 72 | Edit Distance | Medium | 经典编辑距离 DP |
| 221 | Maximal Square | Medium | 网格最大正方形 DP |
| **188** | Best Time to Buy and Sell Stock IV | Hard | K 次交易 DP |

---

## 覆盖性检查

- 以上 32 个小节与 **`skill-days/`** 目录一一对应，覆盖全部 **150 题**。

---

## 32 天按技能刷题表 — 总览

上方 32 个小节与 `skill-days/` 一一对应，以下是速查表：

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

> **加粗** 为 Hard 题。**★** 为面试高频，时间紧可优先。每天 ≤ 5 题、≤ 1 Hard。

> **使用建议**: 第一轮可跟 `README.md` 按 topic 刷；第二轮用本表按技能集中刷，形成模式记忆。
