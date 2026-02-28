"""
LeetCode 155. Min Stack
难度: Medium

题目描述：
设计一个支持 push、pop、top 操作，并能在常数时间内检索到最小元素的栈。

实现 MinStack 类：
- MinStack() 初始化堆栈对象。
- void push(int val) 将元素 val 推入堆栈。
- void pop() 删除堆栈顶部的元素。
- int top() 获取堆栈顶部的元素。
- int getMin() 获取堆栈中的最小元素。

示例：
  MinStack minStack = new MinStack();
  minStack.push(-2);   // 栈: [-2]
  minStack.push(0);    // 栈: [-2, 0]
  minStack.push(-3);   // 栈: [-2, 0, -3]
  minStack.getMin();   // 返回 -3
  minStack.pop();      // 弹出 -3，栈: [-2, 0]
  minStack.top();      // 返回 0
  minStack.getMin();   // 返回 -2

【拓展练习】
1. LeetCode 716. Max Stack —— 支持 peekMax 和 popMax 的栈
2. LeetCode 232. Implement Queue using Stacks —— 用栈实现队列
3. LeetCode 895. Maximum Frequency Stack —— 按频率弹出的栈
"""

import math


class MinStack:
    """
    ==================== 解法一：辅助栈 ====================

    【核心思路】
    使用两个栈：主栈存所有元素，辅助栈同步记录每一层的最小值。
    push 时辅助栈压入 min(val, 当前最小值)，pop 时两个栈同步弹出。
    getMin 直接返回辅助栈栈顶。

    【思考过程】
    1. 普通栈的 push/pop/top 都是 O(1)，难点在于 O(1) 的 getMin。
    2. 如果每次 getMin 都遍历栈找最小值 → O(n)，不符合要求。
    3. 关键洞察：栈的元素是按顺序弹出的，当前栈的最小值只会在 push/pop 时变化。
       → 可以用另一个栈"同步"记录每个状态下的最小值。
    4. push(val) 时：辅助栈压入 min(val, 辅助栈栈顶)。
       pop() 时：两栈同步弹出。
       getMin()：辅助栈栈顶就是当前状态的最小值。

    【举例】
      push(-2): 主栈[-2], 辅助栈[-2]         getMin=-2
      push(0):  主栈[-2,0], 辅助栈[-2,-2]     getMin=-2
      push(-3): 主栈[-2,0,-3], 辅助栈[-2,-2,-3] getMin=-3
      pop():    主栈[-2,0], 辅助栈[-2,-2]     getMin=-2

    【时间复杂度】所有操作 O(1)
    【空间复杂度】O(n)，辅助栈与主栈同大小
    """
    def __init__(self):
        self.stack = []
        self.min_stack = []

    def push(self, val: int) -> None:
        self.stack.append(val)
        min_val = min(val, self.min_stack[-1]) if self.min_stack else val
        self.min_stack.append(min_val)

    def pop(self) -> None:
        self.stack.pop()
        self.min_stack.pop()

    def top(self) -> int:
        return self.stack[-1]

    def getMin(self) -> int:
        return self.min_stack[-1]


class MinStackDiff:
    """
    ==================== 解法二：单栈存差值 ====================

    【核心思路】
    栈中不直接存值，而是存储 val - current_min（差值）。
    用一个变量 min_val 维护当前最小值。
    通过差值的正负来判断最小值是否需要更新。

    【思考过程】
    1. 解法一用两个栈，能否只用一个栈？
    2. 核心困难：pop 掉当前最小值后，如何恢复上一个最小值？
    3. 差值法：令 diff = val - min_val，把 diff 压栈。
       - diff >= 0：val 不是新最小值，min_val 不变。
       - diff < 0：val 是新最小值，更新 min_val = val。
    4. pop 时：
       - 栈顶 diff >= 0：原始值 = min_val + diff，min_val 不变。
       - 栈顶 diff < 0：当前 min_val 就是被弹出的值，
         恢复上一个 min_val = min_val - diff。
    5. 注意：差值可能溢出 int 范围，Python 不存在这个问题。

    【举例】
      push(-2): diff=-2-(-inf)? 不对，初始 min_val=-2, 栈[0]
      让我们用正确的流程：
      push(-2): 栈空，min_val=-2, 压入0 → 栈[0]
      push(0):  diff=0-(-2)=2>=0, 压入2 → 栈[0,2], min_val=-2
      push(-3): diff=-3-(-2)=-1<0, 压入-1, min_val更新=-3 → 栈[0,2,-1]
      getMin(): 返回 min_val=-3
      pop():    栈顶-1<0, 恢复 min_val=-3-(-1)=-2, 弹出 → 栈[0,2]
      getMin(): 返回 min_val=-2 ✓

    【时间复杂度】所有操作 O(1)
    【空间复杂度】O(n)，但只用一个栈
    """
    def __init__(self):
        self.stack = []
        self.min_val = math.inf

    def push(self, val: int) -> None:
        if not self.stack:
            self.min_val = val
            self.stack.append(0)
        else:
            diff = val - self.min_val
            self.stack.append(diff)
            if diff < 0:
                self.min_val = val

    def pop(self) -> None:
        diff = self.stack.pop()
        if diff < 0:
            self.min_val = self.min_val - diff
        if not self.stack:
            self.min_val = math.inf

    def top(self) -> int:
        diff = self.stack[-1]
        if diff >= 0:
            return self.min_val + diff
        else:
            return self.min_val

    def getMin(self) -> int:
        return self.min_val


class MinStackTuple:
    """
    ==================== 解法三：栈中存 (val, currentMin) 对 ====================

    【核心思路】
    每个栈元素不只存值，还附带"截至当前层的最小值"。
    相当于把辅助栈的信息合并到主栈中，只用一个栈。

    【思考过程】
    1. 解法一用两个栈是因为需要同时记录值和最小值。
       → 能否把这两个信息打包到一个栈里？
    2. 每次 push 时存入元组 (val, currentMin)：
       currentMin = min(val, 前一个元素的 currentMin)
    3. pop 时直接弹出元组，top 取元组第一个元素，getMin 取第二个。
    4. 空间上和解法一相同（两个栈 vs 一个栈存元组），
       但代码更紧凑，不需要维护两个独立的栈。

    【举例】
      push(-2): 栈 [(-2, -2)]
      push(0):  min(0, -2)=-2, 栈 [(-2,-2), (0,-2)]
      push(-3): min(-3, -2)=-3, 栈 [(-2,-2), (0,-2), (-3,-3)]
      getMin(): 栈顶元组的第二个元素 = -3
      pop():    弹出 (-3,-3), 栈 [(-2,-2), (0,-2)]
      top():    栈顶元组的第一个元素 = 0
      getMin(): 栈顶元组的第二个元素 = -2

    【时间复杂度】所有操作 O(1)
    【空间复杂度】O(n)
    """
    def __init__(self):
        self.stack = []

    def push(self, val: int) -> None:
        current_min = min(val, self.stack[-1][1]) if self.stack else val
        self.stack.append((val, current_min))

    def pop(self) -> None:
        self.stack.pop()

    def top(self) -> int:
        return self.stack[-1][0]

    def getMin(self) -> int:
        return self.stack[-1][1]
