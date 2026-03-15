"""
LeetCode 202. Happy Number
难度: Easy

题目描述：
编写一个算法来判断一个数 n 是不是快乐数。
「快乐数」定义为：
- 对于一个正整数，每一次将该数替换为它每个位置上的数字的平方和。
- 然后重复这个过程直到这个数变为 1，也可能是无限循环但始终变不到 1。
- 如果这个过程结果为 1，那么这个数就是快乐数。
如果 n 是快乐数就返回 true；不是，则返回 false。

示例：
  输入: n = 19
  输出: true
  解释: 1² + 9² = 82
        8² + 2² = 68
        6² + 8² = 100
        1² + 0² + 0² = 1

  输入: n = 2
  输出: false
  解释: 会进入循环 2→4→16→37→58→89→145→42→20→4→...

【拓展练习】
1. LeetCode 141. Linked List Cycle —— 链表环检测，快慢指针经典题
2. LeetCode 258. Add Digits —— 各位数字反复相加直到一位数
3. LeetCode 263. Ugly Number —— 判断一个数是否只包含质因数2,3,5
"""


class Solution:
    """
    ==================== 解法一：哈希集合检测循环 ====================

    【核心思路】
    不断对 n 求各位数字平方和，把出现过的数存入哈希集合。
    如果某次结果为 1，返回 true；如果某次结果已在集合中，说明进入循环，返回 false。

    【思考过程】
    1. 快乐数的过程要么最终到 1，要么进入一个不包含 1 的循环。
       不可能无限增长（因为位数 d 的最大平方和是 81d，增长速度远慢于数本身）。
    2. 如何检测循环？最简单的方法：记住所有出现过的数。
       如果某个数第二次出现，说明已经进入循环。
    3. 用 set 存储，查找和插入都是 O(1)。

    【举例】n = 19
      seen = {}
      19 → 1²+9²=82, seen={19}
      82 → 8²+2²=68, seen={19,82}
      68 → 6²+8²=100, seen={19,82,68}
      100 → 1²+0²+0²=1 → 返回 True

    【时间复杂度】O(log n) 每步计算平方和 O(log n)，循环次数有限（最多约几十步）
    【空间复杂度】O(log n) 集合中存储的数的个数
    """
    def isHappy_hashset(self, n: int) -> bool:
        def get_next(num):
            total = 0
            while num > 0:
                num, digit = divmod(num, 10)
                total += digit * digit
            return total

        seen = set()
        while n != 1 and n not in seen:
            seen.add(n)
            n = get_next(n)
        return n == 1

    """
    ==================== 解法二：快慢指针（Floyd 循环检测） ====================

    【核心思路】
    把"对 n 反复求各位平方和"看作一条隐式链表，每个数指向下一个数。
    如果是快乐数，链表最终到达 1（1→1 自环）；如果不是，链表进入某个循环。
    用快慢指针检测：快指针每次走两步，慢指针每次走一步，它们一定会相遇。
    如果相遇在 1，则是快乐数。

    【思考过程】
    1. 哈希集合需要 O(log n) 额外空间。能否做到 O(1) 空间？
       → 联想链表环检测的 Floyd 算法。
    2. 本题的数值序列等价于链表：n → next(n) → next(next(n)) → ...
       如果存在循环，快慢指针必然在循环内相遇。
    3. 无论循环经过 1 还是其他数，快慢指针都会相遇。
       相遇后只需检查相遇点是否为 1。

    【举例】n = 19（注意 fast 初始为 get_next(n)=82，不是 n）
      slow: 19 → 82 → 68 → ...
      fast: 82 → 100 → 1 → ...
      当 fast 到达 1 时退出循环 → 返回 True

      n = 2（fast 初始为 get_next(2)=4）
      slow: 2 → 4 → 16 → 37 → 58 → 89 → 145 → 42 → 20 → 4
      fast: 4 → 16 → 58 → 145 → 20 → 16 → 58 → ...
      最终相遇于循环中某个非 1 的数 → 返回 False

    【时间复杂度】O(log n)
    【空间复杂度】O(1) 只用两个指针
    """
    def isHappy(self, n: int) -> bool:
        def get_next(num):
            total = 0
            while num > 0:
                # divmod 返回两个值，一个是商，一个是余数
                num, digit = divmod(num, 10)
                total += digit * digit
            return total

        slow = n
        fast = get_next(n)
        while fast != 1 and slow != fast:
            slow = get_next(slow)
            fast = get_next(get_next(fast))
        return fast == 1
