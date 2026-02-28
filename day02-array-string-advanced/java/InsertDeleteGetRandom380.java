/**
 * LeetCode 380. Insert Delete GetRandom O(1)
 * 难度: Medium
 *
 * 题目描述：
 * 设计一个支持在平均 O(1) 时间复杂度下执行以下操作的数据结构：
 *   - insert(val): 当元素 val 不存在时，向集合中插入该项，返回 true；否则返回 false
 *   - remove(val): 当元素 val 存在时，从集合中移除该项，返回 true；否则返回 false
 *   - getRandom(): 随机返回现有集合中的一项，每个元素被返回的概率相等
 *
 * 示例：
 *   RandomizedSet obj = new RandomizedSet();
 *   obj.insert(1);    // true, 集合 = {1}
 *   obj.remove(2);    // false, 2 不存在
 *   obj.insert(2);    // true, 集合 = {1, 2}
 *   obj.getRandom();  // 随机返回 1 或 2
 *   obj.remove(1);    // true, 集合 = {2}
 *   obj.insert(2);    // false, 2 已存在
 *   obj.getRandom();  // 必定返回 2
 *
 * 【拓展练习】
 * 1. 如果允许元素重复（LeetCode 381），如何修改数据结构？
 * 2. 如果要支持 O(1) 的 getMin/getMax 操作，如何扩展？
 */

import java.util.*;

/**
 * ==================== 解法一：哈希表 + 动态数组（标准解法）====================
 *
 * 【核心思路】
 * 用 ArrayList 存元素实现 O(1) 随机访问，用 HashMap 存 val→index 映射实现
 * O(1) 查找。删除时将待删元素与末尾元素交换，然后删除末尾，保证 O(1)。
 *
 * 【思考过程】
 * 1. 为什么需要两种数据结构结合？
 *    - 纯哈希表（HashSet）：insert O(1)、remove O(1)，但无法 O(1) 等概率随机取
 *      因为哈希表内部结构不支持按索引访问
 *    - 纯数组（ArrayList）：getRandom O(1)（随机下标），insert O(1)（尾部添加），
 *      但 remove 需要 O(n) 查找元素位置
 *    - 纯链表：无法 O(1) 随机访问
 *    → 结合 HashMap + ArrayList 可以互补
 *
 * 2. 删除操作的关键技巧——与末尾交换：
 *    a. 通过 map 找到 val 的下标 idx
 *    b. 将 list 末尾元素 lastVal 移到 idx 位置
 *    c. 更新 map 中 lastVal 的下标为 idx
 *    d. 删除 list 末尾元素和 map 中 val 的条目
 *    这样避免了数组中间删除导致的 O(n) 元素搬移
 *
 * 【举例】
 * insert(1): list=[1], map={1:0}
 * insert(2): list=[1,2], map={1:0, 2:1}
 * insert(3): list=[1,2,3], map={1:0, 2:1, 3:2}
 * remove(2): 2 在 idx=1, 末尾=3
 *   → list[1]=3, list 删末尾 → list=[1,3], map={1:0, 3:1}
 * getRandom(): 随机返回 list[rand(0,1)]，即 1 或 3，等概率
 *
 * 【时间复杂度】每个操作均摊 O(1)
 * 【空间复杂度】O(n) — 存储 n 个元素
 */
class InsertDeleteGetRandom380 {

    private List<Integer> list;
    private Map<Integer, Integer> map;
    private Random rand;

    public InsertDeleteGetRandom380() {
        list = new ArrayList<>();
        map = new HashMap<>();
        rand = new Random();
    }

    public boolean insert(int val) {
        if (map.containsKey(val)) {
            return false;
        }
        map.put(val, list.size());
        list.add(val);
        return true;
    }

    public boolean remove(int val) {
        if (!map.containsKey(val)) {
            return false;
        }
        int idx = map.get(val);
        int lastVal = list.get(list.size() - 1);

        list.set(idx, lastVal);
        map.put(lastVal, idx);

        list.remove(list.size() - 1);
        map.remove(val);
        return true;
    }

    public int getRandom() {
        return list.get(rand.nextInt(list.size()));
    }
}

/**
 * ==================== 解法二：设计分析 + 详细注释版 ====================
 *
 * 【核心思路】
 * 与解法一相同的实现，但从"为什么这样设计"的角度详细解释每个决策。
 *
 * 【为什么其他数据结构不行？】
 *
 * 1. 纯 HashSet：
 *    - insert/remove 都是 O(1) ✓
 *    - getRandom：HashSet 基于哈希桶，元素分布不连续，无法通过随机下标
 *      直接访问。要随机取一个元素，只能先转为数组或遍历跳过 k 个，O(n) ✗
 *
 * 2. 纯 ArrayList：
 *    - insert（尾部）: O(1) ✓
 *    - getRandom: O(1) ✓（随机下标直接访问）
 *    - remove: 需要先查找 O(n)，再删除可能触发搬移 O(n) ✗
 *
 * 3. 纯 LinkedList：
 *    - insert: O(1) ✓（头部/尾部插入）
 *    - remove: 如果有节点引用 O(1)，但查找节点 O(n) ✗
 *    - getRandom: 无法按下标随机访问 O(n) ✗
 *
 * 4. TreeSet/TreeMap：
 *    - 所有操作 O(log n)，不满足 O(1) 要求 ✗
 *
 * 【最终方案】HashMap + ArrayList
 *    - HashMap 提供 O(1) 的"值→下标"查找，解决 ArrayList 删除时的查找问题
 *    - ArrayList 提供 O(1) 的随机访问，解决 HashMap 无法随机取元素的问题
 *    - "交换到末尾再删"的技巧，解决 ArrayList 中间删除的搬移问题
 *
 * 【时间复杂度】每个操作均摊 O(1)
 * 【空间复杂度】O(n)
 */
class RandomizedSetDetailed {

    private List<Integer> list;
    private Map<Integer, Integer> map; // val → index in list
    private Random rand;

    public RandomizedSetDetailed() {
        list = new ArrayList<>();
        map = new HashMap<>();
        rand = new Random();
    }

    /**
     * 插入：先查 map 判断是否存在（O(1)），不存在则追加到 list 尾部（O(1)），
     * 同时记录 val 在 list 中的下标到 map 中。
     */
    public boolean insert(int val) {
        if (map.containsKey(val)) {
            return false;
        }
        map.put(val, list.size());
        list.add(val);
        return true;
    }

    /**
     * 删除核心技巧 —— "交换到末尾再删"：
     * 直接删除 list[idx] 会导致 idx 后面所有元素左移 O(n)。
     * 解决：把 list 末尾元素搬到 idx 位置"填坑"，然后删末尾 O(1)。
     * 别忘了更新被搬移元素在 map 中的下标。
     */
    public boolean remove(int val) {
        if (!map.containsKey(val)) {
            return false;
        }

        int idx = map.get(val);
        int lastVal = list.get(list.size() - 1);

        // 用末尾元素覆盖待删位置
        list.set(idx, lastVal);
        map.put(lastVal, idx);

        // 删除末尾和 map 条目
        list.remove(list.size() - 1);
        map.remove(val);
        return true;
    }

    /**
     * 随机取：list 的元素连续存储，直接生成 [0, size) 的随机下标即可。
     * 每个元素被选中的概率 = 1/size，满足等概率要求。
     */
    public int getRandom() {
        return list.get(rand.nextInt(list.size()));
    }
}
