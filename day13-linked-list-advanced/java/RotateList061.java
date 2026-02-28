/**
 * LeetCode 61. Rotate List
 * 难度: Medium
 *
 * 题目描述：
 * 给你一个链表的头节点 head，旋转链表，将链表每个节点向右移动 k 个位置。
 *
 * 示例 1：head = [1,2,3,4,5], k = 2 → 输出 [4,5,1,2,3]
 * 示例 2：head = [0,1,2], k = 4 → 输出 [2,0,1]
 *
 * 【拓展练习】
 * 1. LeetCode 189. Rotate Array —— 数组版本的旋转问题
 * 2. LeetCode 725. Split Linked List in Parts —— 将链表拆分为 k 个部分
 * 3. LeetCode 143. Reorder List —— 链表重排（涉及找中点、反转、合并）
 */

// ListNode 定义（LeetCode 已提供，无需重复定义）：
// public class ListNode {
//     int val;
//     ListNode next;
//     ListNode() {}
//     ListNode(int val) { this.val = val; }
//     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
// }

class RotateList061 {

    /**
     * ==================== 解法一：成环断开 ====================
     *
     * 【核心思路】
     * 将链表首尾相连形成环，然后在正确的位置断开。
     * 向右旋转 k 个位置等价于：新头是原链表的倒数第 k 个节点，
     * 在倒数第 k+1 个节点处断开。
     *
     * 【思考过程】
     * 1. 右旋 k 位：最后 k 个节点移到前面。
     *    如果 k >= n，实际旋转 k % n 位（旋转 n 位等于不动）。
     * 2. 倒数第 k 个节点 = 正数第 n - k 个节点（0-indexed）。
     *    新的断开点在正数第 n - k - 1 个节点之后。
     * 3. 实现方式：先遍历到尾节点算出 n，同时把尾节点连接到头节点形成环。
     *    然后从尾节点继续走 n - k % n 步到达新尾节点，断开即可。
     *
     * 【举例】head = [1,2,3,4,5], k = 2
     *   n = 5, k % n = 2
     *   成环：1 → 2 → 3 → 4 → 5 → 1 → ...
     *   从尾(5)走 n - k = 3 步：5 → 1 → 2 → 3
     *   新尾 = 3，新头 = 4
     *   断开：3.next = null
     *   结果：4 → 5 → 1 → 2 → 3
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public ListNode rotateRightCycle(ListNode head, int k) {
        if (head == null || head.next == null || k == 0) return head;

        ListNode tail = head;
        int n = 1;
        while (tail.next != null) {
            tail = tail.next;
            n++;
        }

        k %= n;
        if (k == 0) return head;

        tail.next = head;

        ListNode newTail = tail;
        for (int i = 0; i < n - k; i++) {
            newTail = newTail.next;
        }

        ListNode newHead = newTail.next;
        newTail.next = null;
        return newHead;
    }

    /**
     * ==================== 解法二：快慢指针 ====================
     *
     * 【核心思路】
     * 利用快慢指针找到倒数第 k 个节点的前驱，然后在该处断开链表，
     * 将后半部分拼接到前半部分的前面。
     *
     * 【思考过程】
     * 1. 先遍历一遍得到链表长度 n，计算有效旋转量 k % n。
     * 2. 用快指针先走 k 步，然后快慢同时走，快指针到尾部时慢指针在断点。
     * 3. 这种方式和成环断开本质相同，但概念上是"找到断点后拼接"而非"成环后断开"。
     *
     * 【举例】head = [1,2,3,4,5], k = 2
     *   n = 5, k % n = 2
     *   fast 先走2步：fast 指向 3
     *   同步走：slow=1,fast=3 → slow=2,fast=4 → slow=3,fast=5
     *   fast.next=null 停止
     *   newHead = slow.next = 4
     *   fast.next = head → 5.next = 1
     *   slow.next = null → 3.next = null
     *   结果：4 → 5 → 1 → 2 → 3
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public ListNode rotateRightTwoPointer(ListNode head, int k) {
        if (head == null || head.next == null || k == 0) return head;

        int n = 0;
        ListNode curr = head;
        while (curr != null) {
            n++;
            curr = curr.next;
        }

        k %= n;
        if (k == 0) return head;

        ListNode fast = head, slow = head;
        for (int i = 0; i < k; i++) {
            fast = fast.next;
        }

        while (fast.next != null) {
            fast = fast.next;
            slow = slow.next;
        }

        ListNode newHead = slow.next;
        slow.next = null;
        fast.next = head;
        return newHead;
    }
}
