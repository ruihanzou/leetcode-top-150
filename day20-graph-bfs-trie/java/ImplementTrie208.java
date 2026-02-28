/**
 * LeetCode 208. Implement Trie (Prefix Tree)
 * 难度: Medium
 *
 * 题目描述：
 * 实现一个 Trie（前缀树），包含 insert、search 和 startsWith 三个操作。
 * - insert(word)：向 Trie 中插入字符串 word。
 * - search(word)：如果字符串 word 在 Trie 中，返回 true；否则返回 false。
 * - startsWith(prefix)：如果之前已经插入的字符串中，有以 prefix 为前缀的，返回 true。
 *
 * 示例：
 *   Trie trie = new Trie();
 *   trie.insert("apple");
 *   trie.search("apple");   // true
 *   trie.search("app");     // false
 *   trie.startsWith("app"); // true
 *   trie.insert("app");
 *   trie.search("app");     // true
 *
 * 【拓展练习】
 * 1. LeetCode 211. Design Add and Search Words Data Structure —— Trie+通配符搜索
 * 2. LeetCode 212. Word Search II —— Trie+回溯在棋盘中搜索
 * 3. LeetCode 648. Replace Words —— 利用 Trie 查找最短词根
 */

import java.util.HashMap;
import java.util.Map;

/**
 * ==================== 解法一：数组子节点实现 ====================
 *
 * 【核心思路】
 * 每个节点包含一个长度为 26 的数组 children，children[i] 指向字符 'a'+i 对应的子节点。
 * 另外用一个布尔值 isEnd 标记当前节点是否为某个单词的结尾。
 *
 * 【思考过程】
 * 1. Trie 是一棵多叉树，每条边代表一个字符。
 *    从根到某个节点的路径拼接起来就是一个前缀。
 *    如果该节点标记了 isEnd=true，说明这个前缀本身也是一个完整的单词。
 *
 * 2. 用长度 26 的数组表示子节点，可以 O(1) 访问任意字符的子节点。
 *    对于小写英文字母（仅 26 个），数组非常合适。
 *
 * 3. insert：沿着 word 的每个字符依次往下走，路径不存在则创建新节点。
 *    最后一个字符对应的节点标记 isEnd=true。
 *
 * 4. search：沿着 word 的每个字符往下走，任何一步走不通返回 false。
 *    走完后检查 isEnd 是否为 true。
 *
 * 5. startsWith：和 search 类似，但不需要检查 isEnd，走完就返回 true。
 *
 * 【举例】插入 "apple" 和 "app"
 *   root → a → p → p → l → e (isEnd=true)
 *                    ↑
 *                 isEnd=true (插入"app"后)
 *
 *   search("apple") → 沿 a→p→p→l→e，isEnd=true → true
 *   search("app")   → 沿 a→p→p，isEnd=true → true
 *   search("ap")    → 沿 a→p，isEnd=false → false
 *   startsWith("ap") → 沿 a→p，路径存在 → true
 *
 * 【时间复杂度】insert/search/startsWith 均为 O(L)，L 为单词长度
 * 【空间复杂度】O(T * 26)，T 为 Trie 中节点总数
 */
class Trie {
    private Trie[] children;
    private boolean isEnd;

    public Trie() {
        children = new Trie[26];
        isEnd = false;
    }

    public void insert(String word) {
        Trie node = this;
        for (char ch : word.toCharArray()) {
            int idx = ch - 'a';
            if (node.children[idx] == null) {
                node.children[idx] = new Trie();
            }
            node = node.children[idx];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        Trie node = searchPrefix(word);
        return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
        return searchPrefix(prefix) != null;
    }

    private Trie searchPrefix(String prefix) {
        Trie node = this;
        for (char ch : prefix.toCharArray()) {
            int idx = ch - 'a';
            if (node.children[idx] == null) return null;
            node = node.children[idx];
        }
        return node;
    }
}

/**
 * ==================== 解法二：哈希表子节点实现 ====================
 *
 * 【核心思路】
 * 每个节点用一个 HashMap 存储子节点，键为字符，值为子节点。
 * 相比数组实现，仅为实际存在的字符分配空间，更节省内存。
 *
 * 【思考过程】
 * 1. 数组实现每个节点固定分配 26 个指针，即使大部分为空。
 *    如果字符集很大（如 Unicode），数组实现会非常浪费。
 *    哈希表只存实际使用的字符，空间利用率更高。
 *
 * 2. 代价是每次查找子节点从 O(1) 数组访问变为 O(1) 平均的哈希查找，
 *    常数因子略大，但渐进复杂度相同。
 *
 * 3. 操作逻辑和数组实现完全一致，只是访问子节点的方式不同。
 *
 * 【举例】插入 "apple" 和 "app"
 *   root → {'a': node1}
 *   node1 → {'p': node2}
 *   node2 → {'p': node3}
 *   node3 → {'l': node4}, isEnd=true
 *   node4 → {'e': node5}
 *   node5 → {}, isEnd=true
 *
 * 【时间复杂度】insert/search/startsWith 均为 O(L)
 * 【空间复杂度】O(T)，T 为 Trie 中节点总数，每个节点只存实际存在的子节点
 */
class TrieHashMap {
    private Map<Character, TrieHashMap> children;
    private boolean isEnd;

    public TrieHashMap() {
        children = new HashMap<>();
        isEnd = false;
    }

    public void insert(String word) {
        TrieHashMap node = this;
        for (char ch : word.toCharArray()) {
            node.children.putIfAbsent(ch, new TrieHashMap());
            node = node.children.get(ch);
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        TrieHashMap node = searchPrefix(word);
        return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
        return searchPrefix(prefix) != null;
    }

    private TrieHashMap searchPrefix(String prefix) {
        TrieHashMap node = this;
        for (char ch : prefix.toCharArray()) {
            if (!node.children.containsKey(ch)) return null;
            node = node.children.get(ch);
        }
        return node;
    }
}
