/**
 * LeetCode 148. Sort List
 * 难度: Medium
 *
 * 题目描述：
 * 给你链表的头结点 head，请将其按升序排列并返回排序后的链表。
 * 要求在 O(n log n) 时间复杂度和常数级空间复杂度下完成。
 *
 * 示例 1：head = [4,2,1,3] → [1,2,3,4]
 * 示例 2：head = [-1,5,3,4,0] → [-1,0,3,4,5]
 * 示例 3：head = [] → []
 *
 * 【拓展练习】
 * 1. LeetCode 21. Merge Two Sorted Lists —— 合并两个有序链表，本题的子操作
 * 2. LeetCode 147. Insertion Sort List —— 链表插入排序，O(n^2)
 * 3. LeetCode 23. Merge k Sorted Lists —— 合并k个有序链表，分治/堆
 */

class SortList148 {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /**
     * ==================== 解法一：自顶向下归并排序 ====================
     *
     * 【核心思路】
     * 用快慢指针找到链表中点，将链表拆分为两半，分别递归排序后再合并。
     * 这是经典的归并排序在链表上的应用。
     *
     * 【思考过程】
     * 1. 链表排序不能像数组那样随机访问，快排的 partition 不方便。
     *    但归并排序只需要顺序扫描和合并，非常适合链表。
     *
     * 2. 找中点：用快慢指针，slow 一次走一步，fast 一次走两步。
     *    fast 到末尾时，slow 恰好在中点。
     *
     * 3. 拆分：在 slow 处断开，得到两个子链表。
     *    分别递归排序后，用双指针合并两个有序链表。
     *
     * 4. 递归基：链表为空或只有一个节点时，直接返回。
     *
     * 【举例】head = [4, 2, 1, 3]
     *   找中点：slow=2, fast到末尾 → 拆分为 [4,2] 和 [1,3]
     *   递归 [4,2]：拆分为 [4] 和 [2]，合并得 [2,4]
     *   递归 [1,3]：拆分为 [1] 和 [3]，合并得 [1,3]
     *   合并 [2,4] 和 [1,3]：→ [1,2,3,4]
     *
     * 【时间复杂度】O(n log n)
     * 【空间复杂度】O(log n) 递归栈
     */
    public ListNode sortList(ListNode head) {
        if (head == null || head.next == null) return head;

        ListNode slow = head, fast = head.next;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        ListNode mid = slow.next;
        slow.next = null;

        ListNode left = sortList(head);
        ListNode right = sortList(mid);
        return merge(left, right);
    }

    private ListNode merge(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                cur.next = l1;
                l1 = l1.next;
            } else {
                cur.next = l2;
                l2 = l2.next;
            }
            cur = cur.next;
        }
        cur.next = (l1 != null) ? l1 : l2;
        return dummy.next;
    }

    /**
     * ==================== 解法二：自底向上归并排序 ====================
     *
     * 【核心思路】
     * 迭代地按步长 1, 2, 4, 8, ... 进行归并。
     * 每轮将相邻的两段长度为 step 的子链表合并为一段长度为 2*step 的有序链表。
     * 不使用递归，空间复杂度 O(1)。
     *
     * 【思考过程】
     * 1. 自顶向下的递归会占用 O(log n) 栈空间，题目要求常数空间。
     *    → 改用自底向上的迭代方式。
     *
     * 2. 第一轮：每两个相邻节点（step=1）合并为一个长度2的有序段。
     *    第二轮：每两个相邻的长度2段（step=2）合并为长度4。
     *    ...直到 step >= n，整条链表有序。
     *
     * 3. 每轮需要：
     *    a) 从当前位置切下长度为 step 的段
     *    b) 再切下一段长度为 step 的段
     *    c) 合并这两段，接到结果链表尾部
     *    d) 继续处理后续的段
     *
     * 【举例】head = [4, 2, 1, 3]，n=4
     *   step=1: 合并(4)(2)→[2,4], 合并(1)(3)→[1,3] → [2,4,1,3]
     *   step=2: 合并(2,4)(1,3)→[1,2,3,4]
     *   step=4 >= n，结束
     *
     * 【时间复杂度】O(n log n)
     * 【空间复杂度】O(1)
     */
    public ListNode sortListBottomUp(ListNode head) {
        if (head == null || head.next == null) return head;

        int length = 0;
        ListNode node = head;
        while (node != null) {
            length++;
            node = node.next;
        }

        ListNode dummy = new ListNode(0);
        dummy.next = head;

        for (int step = 1; step < length; step <<= 1) {
            ListNode cur = dummy.next;
            ListNode tail = dummy;

            while (cur != null) {
                ListNode left = cur;
                ListNode right = split(left, step);
                cur = split(right, step);

                ListNode[] merged = mergeWithTail(left, right);
                tail.next = merged[0];
                tail = merged[1];
            }
        }

        return dummy.next;
    }

    private ListNode split(ListNode head, int step) {
        if (head == null) return null;
        for (int i = 1; i < step && head.next != null; i++) {
            head = head.next;
        }
        ListNode next = head.next;
        head.next = null;
        return next;
    }

    private ListNode[] mergeWithTail(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                cur.next = l1;
                l1 = l1.next;
            } else {
                cur.next = l2;
                l2 = l2.next;
            }
            cur = cur.next;
        }
        cur.next = (l1 != null) ? l1 : l2;
        while (cur.next != null) cur = cur.next;
        return new ListNode[]{dummy.next, cur};
    }
}
