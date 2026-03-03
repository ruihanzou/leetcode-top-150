/**
 * LeetCode 146. LRU Cache
 * 难度: Medium
 *
 * 题目描述：
 * 请你设计并实现一个满足 LRU（最近最少使用）缓存约束的数据结构。
 * 实现 LRUCache 类：
 *   - LRUCache(int capacity)：以正整数 capacity 作为容量初始化 LRU 缓存。
 *   - int get(int key)：如果关键字 key 存在于缓存中，则返回关键字的值，否则返回 -1。
 *   - void put(int key, int value)：如果关键字 key 已存在，则变更其数据值 value；
 *     如果不存在，则向缓存中插入该组 key-value。
 *     如果插入操作导致关键字数量超过 capacity，则应该逐出最久未使用的关键字。
 * 函数 get 和 put 必须以 O(1) 的平均时间复杂度运行。
 *
 * 示例：
 *   输入：["LRUCache","put","put","get","put","get","put","get","get","get"]
 *        [[2],[1,1],[2,2],[1],[3,3],[2],[4,4],[1],[3],[4]]
 *   输出：[null,null,null,1,null,-1,null,-1,3,4]
 *   解释：
 *     LRUCache lRUCache = new LRUCache(2);
 *     lRUCache.put(1, 1); // 缓存是 {1=1}
 *     lRUCache.put(2, 2); // 缓存是 {1=1, 2=2}
 *     lRUCache.get(1);    // 返回 1
 *     lRUCache.put(3, 3); // 该操作会使得关键字 2 作废，缓存是 {1=1, 3=3}
 *     lRUCache.get(2);    // 返回 -1（未找到）
 *     lRUCache.put(4, 4); // 该操作会使得关键字 1 作废，缓存是 {4=4, 3=3}
 *     lRUCache.get(1);    // 返回 -1（未找到）
 *     lRUCache.get(3);    // 返回 3
 *     lRUCache.get(4);    // 返回 4
 *
 * 【拓展练习】
 * 1. LeetCode 460. LFU Cache —— 最不经常使用缓存，淘汰使用频率最低的
 * 2. LeetCode 432. All O`one Data Structure —— 全 O(1) 数据结构
 * 3. LeetCode 588. Design In-Memory File System —— 设计内存文件系统
 */

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ==================== 解法一：哈希表 + 手动实现双向链表 ====================
 *
 * 【核心思路】
 * 用哈希表实现 O(1) 查找，用双向链表维护访问顺序。
 * 最近使用的节点放在链表头部，最久未使用的在尾部。
 * get/put 时将被访问的节点移到头部；容量满时删除尾部节点。
 *
 * 【思考过程】
 * 1. get 需要 O(1) → 哈希表（key → 链表节点）。
 * 2. 需要维护"最近使用"的顺序，且需要 O(1) 地：
 *    - 将某节点移到最前面（moveToHead）
 *    - 删除最后一个节点（removeTail）
 *    - 插入新节点到最前面（addToHead）
 *    → 双向链表可以在已知节点引用的情况下 O(1) 删除和插入。
 * 3. 使用 dummy head 和 dummy tail 简化边界处理。
 * 4. put 操作：
 *    - key 已存在：更新 value，移到头部。
 *    - key 不存在：创建新节点，加入哈希表和链表头部。
 *      如果超容量，删除尾部节点，同时从哈希表中移除。
 *
 * 【举例】capacity = 2
 *   put(1,1): head ↔ [1:1] ↔ tail, map={1:node1}
 *   put(2,2): head ↔ [2:2] ↔ [1:1] ↔ tail, map={1:node1, 2:node2}
 *   get(1):   访问key=1，移到头部 → head ↔ [1:1] ↔ [2:2] ↔ tail，返回 1
 *   put(3,3): 容量已满，删除尾部[2:2]，加入[3:3]
 *             head ↔ [3:3] ↔ [1:1] ↔ tail, map={1:node1, 3:node3}
 *   get(2):   key=2不存在，返回 -1
 *
 * 【时间复杂度】get 和 put 均为 O(1)
 * 【空间复杂度】O(capacity)
 */
class LRUCache146 {

    private static class DLinkedNode {
        int key, val;
        DLinkedNode prev, next;

        DLinkedNode() {}

        DLinkedNode(int key, int val) {
            this.key = key;
            this.val = val;
        }
    }

    private final int capacity;
    private final Map<Integer, DLinkedNode> map;
    private final DLinkedNode head;
    private final DLinkedNode tail;

    public LRUCache146(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = new DLinkedNode();
        this.tail = new DLinkedNode();
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        DLinkedNode node = map.get(key);
        if (node == null) return -1;
        moveToHead(node);
        return node.val;
    }

    public void put(int key, int value) {
        DLinkedNode node = map.get(key);
        if (node != null) {
            node.val = value;
            moveToHead(node);
        } else {
            DLinkedNode newNode = new DLinkedNode(key, value);
            map.put(key, newNode);
            addToHead(newNode);
            if (map.size() > capacity) {
                DLinkedNode removed = removeTail();
                map.remove(removed.key);
            }
        }
    }

    private void addToHead(DLinkedNode node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(DLinkedNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(DLinkedNode node) {
        removeNode(node);
        addToHead(node);
    }

    private DLinkedNode removeTail() {
        DLinkedNode node = tail.prev;
        removeNode(node);
        return node;
    }
}

/**
 * ==================== 解法二：使用 LinkedHashMap ====================
 *
 * 【核心思路】
 * Java 的 LinkedHashMap 内部就是哈希表 + 双向链表的实现。
 * 通过设置 accessOrder=true，每次 get/put 会自动将条目移到链表末尾。
 * 重写 removeEldestEntry 方法即可在超容量时自动淘汰最旧的条目。
 *
 * 【思考过程】
 * 1. LinkedHashMap(capacity, loadFactor, accessOrder=true) 会按访问顺序排列。
 * 2. 最久未访问的在头部，最近访问的在尾部。
 * 3. 重写 removeEldestEntry：当 size > capacity 时返回 true，自动删除最老条目。
 * 4. 代码极其简洁，但面试中通常期望手写双向链表版本。
 *
 * 【举例】capacity = 2
 *   put(1,1): LinkedHashMap{1=1}
 *   put(2,2): LinkedHashMap{1=1, 2=2}
 *   get(1):   访问顺序调整 → LinkedHashMap{2=2, 1=1}，返回 1
 *   put(3,3): 超容量，自动移除最老的 2=2 → LinkedHashMap{1=1, 3=3}
 *   get(2):   不存在，返回 -1
 *
 * 【时间复杂度】get 和 put 均为 O(1)
 * 【空间复杂度】O(capacity)
 */
class LRUCacheLinkedHashMap {

    private final LinkedHashMap<Integer, Integer> cache;

    public LRUCacheLinkedHashMap(int capacity) {
        cache = new LinkedHashMap<Integer, Integer>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > capacity;
            }
        };
    }

    public int get(int key) {
        return cache.getOrDefault(key, -1);
    }

    public void put(int key, int value) {
        cache.put(key, value);
    }
}
