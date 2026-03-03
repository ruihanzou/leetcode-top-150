/**
 * LeetCode 211. Design Add and Search Words Data Structure
 * 难度: Medium
 *
 * 题目描述：
 * 设计一个数据结构，支持添加新单词和查找字符串是否与任何先前添加的字符串匹配。
 * - addWord(word)：将 word 添加到数据结构中。
 * - search(word)：如果数据结构中存在与 word 匹配的字符串，返回 true；否则返回 false。
 *   word 中可能包含 '.'，'.' 可以匹配任意一个字母。
 *
 * 示例：
 *   WordDictionary wd = new WordDictionary();
 *   wd.addWord("bad");
 *   wd.addWord("dad");
 *   wd.addWord("mad");
 *   wd.search("pad");  // false
 *   wd.search("bad");  // true
 *   wd.search(".ad");  // true
 *   wd.search("b..");  // true
 *
 * 【拓展练习】
 * 1. LeetCode 208. Implement Trie (Prefix Tree) —— 标准 Trie 实现
 * 2. LeetCode 212. Word Search II —— Trie+回溯在棋盘中搜索
 * 3. LeetCode 10. Regular Expression Matching —— 更复杂的模式匹配
 */

import java.util.*;

/**
 * ==================== 解法一：Trie + DFS 搜索 ====================
 *
 * 【核心思路】
 * 使用 Trie 存储所有单词。搜索时，遇到普通字符按正常 Trie 查找；
 * 遇到 '.' 则递归搜索当前节点的所有子节点（因为 '.' 可匹配任意字母）。
 *
 * 【思考过程】
 * 1. addWord 和普通 Trie 的 insert 完全一样。
 * 2. search 的难点在于处理 '.'：
 *    - 如果当前字符是普通字母，直接走对应的子节点。
 *    - 如果当前字符是 '.'，需要尝试所有 26 个子节点，
 *      只要任意一个能匹配剩余部分就返回 true。
 *    这天然是一个 DFS 过程。
 *
 * 3. 递归函数 dfs(node, word, index)：
 *    从 node 出发，匹配 word[index:] 部分。
 *    base case：index == word.length() 时检查 node.isEnd。
 *
 * 【举例】
 *   插入 "bad", "dad", "mad"
 *   Trie 结构：
 *     root → b → a → d (isEnd=true)
 *          → d → a → d (isEnd=true)
 *          → m → a → d (isEnd=true)
 *
 *   search(".ad")：
 *     '.' → 尝试 b, d, m 三个子节点
 *       b → 'a' → a → 'd' → d, isEnd=true → 返回 true
 *
 *   search("b..")：
 *     'b' → b → '.' → 尝试 a → '.' → 尝试 d, isEnd=true → true
 *
 * 【时间复杂度】addWord: O(L)，search: 最坏 O(26^L)（全是'.'），平均远小于此
 * 【空间复杂度】O(T * 26)，T 为 Trie 中节点总数
 */
class WordDictionary {
    private WordDictionary[] children;
    private boolean isEnd;

    public WordDictionary() {
        children = new WordDictionary[26];
        isEnd = false;
    }

    public void addWord(String word) {
        WordDictionary node = this;
        for (char ch : word.toCharArray()) {
            int idx = ch - 'a';
            if (node.children[idx] == null) {
                node.children[idx] = new WordDictionary();
            }
            node = node.children[idx];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        return dfs(this, word, 0);
    }

    private boolean dfs(WordDictionary node, String word, int index) {
        if (index == word.length()) {
            return node.isEnd;
        }

        char ch = word.charAt(index);
        if (ch == '.') {
            for (WordDictionary child : node.children) {
                if (child != null && dfs(child, word, index + 1)) {
                    return true;
                }
            }
            return false;
        } else {
            int idx = ch - 'a';
            if (node.children[idx] == null) return false;
            return dfs(node.children[idx], word, index + 1);
        }
    }
}

/**
 * ==================== 解法二：按长度分组的哈希表 + 逐字符匹配 ====================
 *
 * 【核心思路】
 * 将所有单词按长度分组存储在 HashMap 中。
 * 搜索时，先按长度筛选候选单词，再逐字符比较（'.' 匹配任意字符）。
 *
 * 【思考过程】
 * 1. 不使用 Trie，而是用更简单的数据结构。
 * 2. 搜索时长度不同的单词不可能匹配，先按长度过滤减少比较次数。
 * 3. 对于候选单词，逐字符比较：
 *    - 搜索字符是 '.' → 跳过该位，继续比较后续
 *    - 搜索字符是普通字母 → 必须完全匹配
 * 4. 缺点：当同一长度的单词很多时，搜索效率低于 Trie+DFS。
 *    优点：实现简单，无需维护复杂的树结构。
 *
 * 【举例】
 *   addWord("bad"), addWord("dad"), addWord("mad")
 *   groups = {3: ["bad", "dad", "mad"]}
 *
 *   search(".ad")：
 *     len = 3 → 候选 ["bad", "dad", "mad"]
 *     "bad": '.'->'b' ✓, 'a'=='a' ✓, 'd'=='d' ✓ → 匹配！返回 true
 *
 * 【时间复杂度】addWord: O(1)，search: O(n * L)，n 为同长度单词数
 * 【空间复杂度】O(N * L)，N 为总单词数
 */
class WordDictionaryHashGroup {
    private Map<Integer, List<String>> groups;

    public WordDictionaryHashGroup() {
        groups = new HashMap<>();
    }

    public void addWord(String word) {
        groups.computeIfAbsent(word.length(), k -> new ArrayList<>()).add(word);
    }

    public boolean search(String word) {
        List<String> candidates = groups.get(word.length());
        if (candidates == null) return false;

        for (String candidate : candidates) {
            if (match(word, candidate)) return true;
        }
        return false;
    }

    private boolean match(String pattern, String word) {
        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) != '.' && pattern.charAt(i) != word.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
