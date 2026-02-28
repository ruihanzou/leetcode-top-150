"""
LeetCode 189. Rotate Array
难度: Medium

题目描述：
给定一个整数数组 nums，将数组中的元素向右轮转 k 个位置，其中 k 是非负数。
要求用尽可能多的方法来实现，至少三种不同的方法。

示例：nums = [1,2,3,4,5,6,7], k = 3 → 输出 [5,6,7,1,2,3,4]
      nums = [-1,-100,3,99], k = 2 → 输出 [3,99,-1,-100]

约束：1 <= nums.length <= 10^5, -2^31 <= nums[i] <= 2^31-1, 0 <= k <= 10^5

【拓展练习】
1. LeetCode 186. Reverse Words in a String II —— 同样用到"三次翻转"技巧
2. LeetCode 61. Rotate List —— 链表版本的旋转
3. LeetCode 396. Rotate Function —— 旋转数组后求函数最大值
"""

from typing import List
from math import gcd


class Solution:
    """
    ==================== 解法一：额外数组 ====================

    【核心思路】
    新建一个数组，将原数组每个元素直接放到旋转后的正确位置 (i+k)%n。

    【思考过程】
    1. 旋转 k 位 = 原来位置 i 的元素移动到 (i+k) % n。
       这是最直观的映射关系。

    2. 由于原地赋值会覆盖还没移走的元素，所以用一个新数组暂存结果，
       最后整体拷贝回来。

    【举例】nums = [1,2,3,4,5,6,7], k = 3
      temp = [_, _, _, _, _, _, _]
      i=0: temp[(0+3)%7]=temp[3]=1 → [_,_,_,1,_,_,_]
      i=1: temp[(1+3)%7]=temp[4]=2 → [_,_,_,1,2,_,_]
      i=2: temp[5]=3, i=3: temp[6]=4
      i=4: temp[0]=5, i=5: temp[1]=6, i=6: temp[2]=7
      temp = [5,6,7,1,2,3,4] → 拷贝回 nums

    【时间复杂度】O(n)
    【空间复杂度】O(n)
    """
    def rotate_extra_array(self, nums: List[int], k: int) -> None:
        n = len(nums)
        temp = [0] * n
        for i in range(n):
            temp[(i + k) % n] = nums[i]
        nums[:] = temp

    """
    ==================== 解法二：三次翻转法 ====================

    【核心思路】
    先整体翻转数组，再翻转前 k 个元素，最后翻转后 n-k 个元素。
    三步操作恰好等价于右旋 k 位。

    【思考过程】
    1. 右旋 k 位 = 把尾部 k 个元素搬到头部。
       原数组可以看作 [A | B]，其中 A 长 n-k，B 长 k。
       目标是 [B | A]。

    2. 整体翻转得到 [A|B]^R = B^R | A^R。
       再分别翻转两段：B^R → B，A^R → A，最终 [B | A]。

    3. 这就是经典的"三次翻转法"，空间 O(1)，只用交换操作。

    【举例】nums = [1,2,3,4,5,6,7], k = 3
      整体翻转 → [7,6,5,4,3,2,1]
      翻转前3个 → [5,6,7,4,3,2,1]
      翻转后4个 → [5,6,7,1,2,3,4] ✓

    【时间复杂度】O(n)
    【空间复杂度】O(1)
    """
    def rotate_reverse(self, nums: List[int], k: int) -> None:
        n = len(nums)
        k %= n

        def reverse(left: int, right: int) -> None:
            while left < right:
                nums[left], nums[right] = nums[right], nums[left]
                left += 1
                right -= 1

        reverse(0, n - 1)
        reverse(0, k - 1)
        reverse(k, n - 1)

    """
    ==================== 解法三：环状替换 ====================

    【核心思路】
    从位置 0 开始，把元素一个个搬到目标位置 (i+k)%n，被占位的元素再搬到它的目标位置，
    形成一个"环"。所有环走完，每个元素恰好被移动一次。

    【思考过程】
    1. 元素 i → (i+k)%n → (i+2k)%n → ... 形成一个循环。
       当回到起点 i 时，一个环结束。

    2. 如果 n 和 k 的最大公约数 g = gcd(n,k)，则一共有 g 个独立的环，
       每个环包含 n/g 个元素。所以需要启动 g 个起点（0, 1, ..., g-1）。

    3. 实现上可以用计数器 moved 记录已移动的元素数，
       当 moved == n 时所有元素都到位了。

    【举例】nums = [1,2,3,4,5,6,7], k = 3, n = 7, gcd(7,3) = 1
      只有 1 个环，从 start=0 开始：
      0→3→6→2→5→1→4→0（回到起点，7个元素全部移动完毕）
      结果 → [5,6,7,1,2,3,4] ✓

    【时间复杂度】O(n)
    【空间复杂度】O(1)
    """
    def rotate_cyclic(self, nums: List[int], k: int) -> None:
        n = len(nums)
        k %= n
        moved = 0

        for start in range(gcd(n, k)):
            current = start
            prev = nums[start]

            while True:
                next_pos = (current + k) % n
                nums[next_pos], prev = prev, nums[next_pos]
                current = next_pos
                moved += 1
                if current == start:
                    break
