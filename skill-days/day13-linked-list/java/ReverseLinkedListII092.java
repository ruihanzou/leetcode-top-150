/**
 * LeetCode 92. Reverse Linked List II
 * 难度: Medium
 *
 * 题目描述：
 * 给你单链表的头指针 head 和两个整数 left 和 right，其中 left <= right。
 * 请你反转从位置 left 到位置 right 的链表节点，返回反转后的链表。
 * 位置从 1 开始计数。
 *
 * 示例 1：head = [1,2,3,4,5], left = 2, right = 4 → 输出 [1,4,3,2,5]
 * 示例 2：head = [5], left = 1, right = 1 → 输出 [5]
 *
 * 【拓展练习】
 * 1. LeetCode 206. Reverse Linked List —— 反转整个链表，本题的简化版
 * 2. LeetCode 25. Reverse Nodes in k-Group —— 每 k 个节点一组进行反转
 * 3. LeetCode 24. Swap Nodes in Pairs —— 两两交换链表节点，可看作 k=2 的反转
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

class ReverseLinkedListII092 {

    /**
     * ==================== 解法一：穿针引线（头插法） ====================
     *
     * 【核心思路】
     * 找到反转区间的前驱节点 pre，然后在 [left, right] 区间内，
     * 不断将当前节点的下一个节点移到 pre 的后面（头插法），
     * 实现原地反转。
     *
     * 【思考过程】
     * 1. 需要反转 [left, right] 之间的节点，不影响前后部分。
     *    → 先找到 left 位置的前驱节点 pre。
     *
     * 2. 如何原地反转？使用"头插法"：
     *    每次把 curr 后面的节点摘下来，插到 pre 的后面。
     *    这样每一步都把一个节点放到反转部分的最前面。
     *
     * 3. 使用虚拟头节点处理 left=1 的边界情况（此时 pre 就是 dummy）。
     *
     * 4. 总共需要做 right - left 次"头插"操作。
     *
     * 【举例】head = [1,2,3,4,5], left = 2, right = 4
     *   dummy → 1 → 2 → 3 → 4 → 5
     *   pre = 节点1, curr = 节点2
     *
     *   第1次头插（把3移到pre后面）：
     *   dummy → 1 → 3 → 2 → 4 → 5
     *
     *   第2次头插（把4移到pre后面）：
     *   dummy → 1 → 4 → 3 → 2 → 5
     *
     *   结果：[1,4,3,2,5]
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public ListNode reverseBetweenInsert(ListNode head, int left, int right) {
        ListNode dummy = new ListNode(0, head);
        ListNode pre = dummy;

        for (int i = 1; i < left; i++) {
            pre = pre.next;
        }

        ListNode curr = pre.next;
        for (int i = 0; i < right - left; i++) {
            ListNode next = curr.next;
            curr.next = next.next;
            next.next = pre.next;
            pre.next = next;
        }

        return dummy.next;
    }

    /**
     * ==================== 解法二：截取反转拼接 ====================
     *
     * 【核心思路】
     * 将链表分为三段：[1, left-1]、[left, right]、[right+1, end]。
     * 把中间段截取出来单独反转，然后重新拼接三段。
     *
     * 【思考过程】
     * 1. 这是最直观的思路：先把要反转的部分"切"出来，反转，再"粘"回去。
     *
     * 2. 需要记录四个关键位置：
     *    - pre: left 的前驱节点
     *    - leftNode: left 位置的节点（反转后变成子链表的尾）
     *    - rightNode: right 位置的节点（反转后变成子链表的头）
     *    - succ: right 的后继节点
     *
     * 3. 切断连接：pre.next = null, rightNode.next = null
     *    反转 [leftNode, rightNode] 这段
     *    重新连接：pre.next = rightNode, leftNode.next = succ
     *
     * 4. 反转单链表用经典的三指针法。
     *
     * 【举例】head = [1,2,3,4,5], left = 2, right = 4
     *   切分前：1 → 2 → 3 → 4 → 5
     *   pre=1, leftNode=2, rightNode=4, succ=5
     *
     *   切断：[1]  [2→3→4]  [5]
     *   反转中间段：[4→3→2]
     *   拼接：1 → 4 → 3 → 2 → 5
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public ListNode reverseBetweenCutReverse(ListNode head, int left, int right) {
        ListNode dummy = new ListNode(0, head);

        ListNode pre = dummy;
        for (int i = 1; i < left; i++) {
            pre = pre.next;
        }
        ListNode leftNode = pre.next;

        ListNode rightNode = pre;
        for (int i = 0; i < right - left + 1; i++) {
            rightNode = rightNode.next;
        }
        ListNode succ = rightNode.next;

        pre.next = null;
        rightNode.next = null;

        reverse(leftNode);

        pre.next = rightNode;
        leftNode.next = succ;

        return dummy.next;
    }

    private void reverse(ListNode head) {
        ListNode prev = null, curr = head;
        while (curr != null) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
    }

    static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }
}
