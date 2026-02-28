/**
 * LeetCode 86. Partition List
 * 难度: Medium
 *
 * 题目描述：
 * 给你一个链表的头节点 head 和一个特定值 x，请你对链表进行分隔，
 * 使得所有小于 x 的节点都出现在大于或等于 x 的节点之前。
 * 你应当保留两个分区中每个节点的初始相对顺序。
 *
 * 示例 1：head = [1,4,3,2,5,2], x = 3 → 输出 [1,2,2,4,3,5]
 * 示例 2：head = [2,1], x = 2 → 输出 [1,2]
 *
 * 【拓展练习】
 * 1. LeetCode 328. Odd Even Linked List —— 按奇偶位置分隔链表
 * 2. LeetCode 725. Split Linked List in Parts —— 将链表拆分为 k 个部分
 * 3. LeetCode 2161. Partition Array According to Given Pivot —— 数组版本的分区问题
 */

// ListNode 定义（LeetCode 已提供，无需重复定义）：
// public class ListNode {
//     int val;
//     ListNode next;
//     ListNode() {}
//     ListNode(int val) { this.val = val; }
//     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
// }

class PartitionList086 {

    /**
     * ==================== 解法一：双链表分区 ====================
     *
     * 【核心思路】
     * 创建两个虚拟头节点 smallDummy 和 largeDummy，分别用于收集值小于 x 的节点
     * 和值大于等于 x 的节点。遍历完成后，将 small 链表的尾部连接 large 链表的头部。
     *
     * 【思考过程】
     * 1. 题目要求保持原始相对顺序 → 不能用交换，需要按顺序收集。
     * 2. 用两个独立的链表分别收集 < x 和 >= x 的节点：
     *    - 遍历原链表，根据节点值决定追加到哪个链表。
     *    - 最后把 small 链表尾部连接到 large 链表头部。
     * 3. 注意：large 链表的尾部必须置 null，否则可能形成环。
     *
     * 【举例】head = [1,4,3,2,5,2], x = 3
     *   遍历：
     *     1 < 3 → small: [1]
     *     4 >= 3 → large: [4]
     *     3 >= 3 → large: [4,3]
     *     2 < 3 → small: [1,2]
     *     5 >= 3 → large: [4,3,5]
     *     2 < 3 → small: [1,2,2]
     *   拼接：[1,2,2] → [4,3,5] → null
     *   结果：[1,2,2,4,3,5]
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)，只用了常数个指针（节点是原链表的，没有新建）
     */
    public ListNode partitionTwoList(ListNode head, int x) {
        ListNode smallDummy = new ListNode(0);
        ListNode largeDummy = new ListNode(0);
        ListNode small = smallDummy, large = largeDummy;

        while (head != null) {
            if (head.val < x) {
                small.next = head;
                small = small.next;
            } else {
                large.next = head;
                large = large.next;
            }
            head = head.next;
        }

        large.next = null;
        small.next = largeDummy.next;
        return smallDummy.next;
    }

    /**
     * ==================== 解法二：原地操作（双指针） ====================
     *
     * 【核心思路】
     * 维护一个 "插入位置" 指针 insertPrev，它指向小于 x 的部分的最后一个节点。
     * 遍历链表，当发现一个值小于 x 的节点不在正确位置时，将它从原位置摘出，
     * 插入到 insertPrev 之后。
     *
     * 【思考过程】
     * 1. 我们可以不创建新链表，而是在原链表上调整节点顺序。
     * 2. 使用 dummy 节点简化头部操作。
     * 3. prev 指针用于遍历，当 prev.next.val < x 时：
     *    - 如果 prev == insertPrev，说明这个小节点已经在正确区域，两者同时前移。
     *    - 否则，把 prev.next 从当前位置摘出，插入到 insertPrev 之后。
     * 4. 这种方式不需要额外链表，但代码逻辑较复杂。
     *
     * 【举例】head = [1,4,3,2,5,2], x = 3
     *   dummy → 1 → 4 → 3 → 2 → 5 → 2
     *   insertPrev=dummy, prev=dummy
     *   节点1: 1<3, prev==insertPrev → 同步前移，insertPrev=1, prev=1
     *   节点4: 4>=3 → prev=4
     *   节点3: 3>=3 → prev=3
     *   节点2: 2<3, prev≠insertPrev → 摘出2，插入insertPrev(1)后
     *     → dummy → 1 → 2 → 4 → 3 → 5 → 2，insertPrev=2
     *   节点5: 5>=3 → prev=5（注意prev仍在3，3.next=5）
     *   节点2: 2<3 → 摘出2，插入insertPrev(2)后
     *     → dummy → 1 → 2 → 2 → 4 → 3 → 5
     *   结果：[1,2,2,4,3,5]
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(1)
     */
    public ListNode partitionInPlace(ListNode head, int x) {
        ListNode dummy = new ListNode(0, head);
        ListNode insertPrev = dummy;
        ListNode prev = dummy;

        while (prev.next != null) {
            if (prev.next.val < x) {
                if (prev == insertPrev) {
                    insertPrev = insertPrev.next;
                    prev = prev.next;
                } else {
                    ListNode node = prev.next;
                    prev.next = node.next;
                    node.next = insertPrev.next;
                    insertPrev.next = node;
                    insertPrev = node;
                }
            } else {
                prev = prev.next;
            }
        }

        return dummy.next;
    }
}
