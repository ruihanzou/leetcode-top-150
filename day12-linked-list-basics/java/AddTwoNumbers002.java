/**
 * LeetCode 2. Add Two Numbers
 * 难度: Medium
 *
 * 题目描述：
 * 给你两个非空的链表，表示两个非负的整数。它们每位数字都是按照逆序的方式存储的，
 * 并且每个节点只能存储一位数字。请你将两个数相加，并以相同形式返回一个表示和的链表。
 * 你可以假设除了数字 0 之外，这两个数都不会以 0 开头。
 *
 * 示例 1：l1 = [2,4,3], l2 = [5,6,4] → 输出 [7,0,8]
 *   解释：342 + 465 = 807
 * 示例 2：l1 = [0], l2 = [0] → 输出 [0]
 * 示例 3：l1 = [9,9,9,9,9,9,9], l2 = [9,9,9,9] → 输出 [8,9,9,9,0,0,0,1]
 *
 * 【拓展练习】
 * 1. LeetCode 445. Add Two Numbers II —— 链表数字正序存储，需要反转或用栈
 * 2. LeetCode 67. Add Binary —— 二进制字符串相加，同样的进位逻辑
 * 3. LeetCode 43. Multiply Strings —— 字符串表示的大数乘法
 */

/**
 * 链表节点定义：
 * class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */

class AddTwoNumbers002 {

    /**
     * ==================== 解法一：迭代模拟 ====================
     *
     * 【核心思路】
     * 同时遍历两个链表，逐位相加并处理进位，用虚拟头节点简化边界处理。
     *
     * 【思考过程】
     * 1. 链表是逆序存储的，最低位在链表头部，这恰好方便我们从低位到高位逐位相加。
     *
     * 2. 每一位的计算：sum = l1.val + l2.val + carry
     *    当前位的值 = sum % 10
     *    进位 carry = sum / 10
     *
     * 3. 两个链表长度可能不同，短链表遍历完后，缺失的位视为 0。
     *
     * 4. 遍历结束后如果还有进位（carry > 0），需要额外创建一个节点。
     *
     * 5. 使用虚拟头节点（dummy），可以避免对第一个节点的特殊处理。
     *
     * 【举例】l1 = [2,4,3], l2 = [5,6,4]
     *   第1位：2+5+0=7, carry=0, 结果 7
     *   第2位：4+6+0=10, carry=1, 结果 0
     *   第3位：3+4+1=8, carry=0, 结果 8
     *   最终链表：[7,0,8]，即 807
     *
     * 【时间复杂度】O(max(m, n))，m 和 n 分别为两个链表的长度
     * 【空间复杂度】O(1)，不计输出链表的空间
     */
    public ListNode addTwoNumbersIterative(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode curr = dummy;
        int carry = 0;

        while (l1 != null || l2 != null || carry > 0) {
            int sum = carry;
            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }
            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }
            carry = sum / 10;
            curr.next = new ListNode(sum % 10);
            curr = curr.next;
        }

        return dummy.next;
    }

    /**
     * ==================== 解法二：递归 ====================
     *
     * 【核心思路】
     * 将"两个链表对应位相加并处理进位"的过程用递归表达。
     * 每层递归处理当前位，将进位传递给下一层。
     *
     * 【思考过程】
     * 1. 递归的终止条件：l1 == null && l2 == null && carry == 0。
     *
     * 2. 每层递归：
     *    - 取出 l1 和 l2 当前位的值（如果为 null 则取 0）
     *    - 计算 sum = val1 + val2 + carry
     *    - 创建当前节点，值为 sum % 10
     *    - 递归处理 next，传入新的 carry = sum / 10
     *
     * 3. 递归写法更简洁，但递归深度为 O(max(m,n))，
     *    对于非常长的链表可能导致栈溢出。
     *
     * 【举例】l1 = [2,4,3], l2 = [5,6,4]
     *   递归第1层：2+5+0=7, 创建节点7, carry=0 → 递归(l1.next, l2.next, 0)
     *   递归第2层：4+6+0=10, 创建节点0, carry=1 → 递归(l1.next, l2.next, 1)
     *   递归第3层：3+4+1=8, 创建节点8, carry=0 → 递归(null, null, 0)
     *   递归第4层：终止条件满足，返回 null
     *   回溯拼接：7 → 0 → 8
     *
     * 【时间复杂度】O(max(m, n))
     * 【空间复杂度】O(max(m, n))，递归调用栈深度
     */
    public ListNode addTwoNumbersRecursive(ListNode l1, ListNode l2) {
        return addHelper(l1, l2, 0);
    }

    private ListNode addHelper(ListNode l1, ListNode l2, int carry) {
        if (l1 == null && l2 == null && carry == 0) {
            return null;
        }

        int sum = carry;
        if (l1 != null) sum += l1.val;
        if (l2 != null) sum += l2.val;

        ListNode node = new ListNode(sum % 10);
        node.next = addHelper(
            l1 != null ? l1.next : null,
            l2 != null ? l2.next : null,
            sum / 10
        );

        return node;
    }

    static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }
}
