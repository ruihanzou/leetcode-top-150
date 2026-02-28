"""
LeetCode 208. Implement Trie (Prefix Tree)
难度: Medium

题目描述：
实现一个 Trie（前缀树），包含 insert、search 和 startsWith 三个操作。
- insert(word)：向 Trie 中插入字符串 word。
- search(word)：如果字符串 word 在 Trie 中，返回 true；否则返回 false。
- startsWith(prefix)：如果之前已经插入的字符串中，有以 prefix 为前缀的，返回 true。

示例：
  trie = Trie()
  trie.insert("apple")
  trie.search("apple")   → true
  trie.search("app")     → false
  trie.startsWith("app") → true
  trie.insert("app")
  trie.search("app")     → true

【拓展练习】
1. LeetCode 211. Design Add and Search Words Data Structure —— Trie+通配符搜索
2. LeetCode 212. Word Search II —— Trie+回溯在棋盘中搜索
3. LeetCode 648. Replace Words —— 利用 Trie 查找最短词根
"""


class Trie:
    """
    ==================== 解法一：数组子节点实现 ====================

    【核心思路】
    每个节点包含一个长度为 26 的数组 children，children[i] 指向字符 'a'+i 对应的子节点。
    另外用一个布尔值 is_end 标记当前节点是否为某个单词的结尾。

    【思考过程】
    1. Trie 是一棵多叉树，每条边代表一个字符。
       从根到某个节点的路径拼接起来就是一个前缀。
       如果该节点标记了 is_end=True，说明这个前缀本身也是一个完整的单词。

    2. 用长度 26 的数组表示子节点，可以 O(1) 访问任意字符的子节点。
       缺点是如果字符集大、实际使用的字符少，会浪费空间。
       但对于小写英文字母（仅 26 个），数组非常合适。

    3. insert：沿着 word 的每个字符依次往下走，路径不存在则创建新节点。
       最后一个字符对应的节点标记 is_end=True。

    4. search：沿着 word 的每个字符往下走，任何一步走不通返回 False。
       走完后检查 is_end 是否为 True。

    5. startsWith：和 search 类似，但不需要检查 is_end，走完就返回 True。

    【举例】插入 "apple" 和 "app"
      root → a → p → p → l → e (is_end=True)
                       ↑
                    is_end=True (插入"app"后)

      search("apple") → 沿 a→p→p→l→e，is_end=True → True
      search("app")   → 沿 a→p→p，is_end=True → True
      search("ap")    → 沿 a→p，is_end=False → False
      startsWith("ap") → 沿 a→p，路径存在 → True

    【时间复杂度】insert/search/startsWith 均为 O(L)，L 为单词长度
    【空间复杂度】O(T * 26)，T 为 Trie 中节点总数
    """
    def __init__(self):
        self.children = [None] * 26
        self.is_end = False

    def insert(self, word: str) -> None:
        node = self
        for ch in word:
            idx = ord(ch) - ord('a')
            if node.children[idx] is None:
                node.children[idx] = Trie()
            node = node.children[idx]
        node.is_end = True

    def search(self, word: str) -> bool:
        node = self._search_prefix(word)
        return node is not None and node.is_end

    def startsWith(self, prefix: str) -> bool:
        return self._search_prefix(prefix) is not None

    def _search_prefix(self, prefix: str):
        node = self
        for ch in prefix:
            idx = ord(ch) - ord('a')
            if node.children[idx] is None:
                return None
            node = node.children[idx]
        return node


class TrieHashMap:
    """
    ==================== 解法二：哈希表子节点实现 ====================

    【核心思路】
    每个节点用一个字典（HashMap）存储子节点，键为字符，值为子节点。
    相比数组实现，仅为实际存在的字符分配空间，更节省内存。

    【思考过程】
    1. 数组实现每个节点固定分配 26 个指针，即使大部分为空。
       如果字符集很大（如 Unicode），数组实现会非常浪费。
       哈希表只存实际使用的字符，空间利用率更高。

    2. 代价是每次查找子节点从 O(1) 数组访问变为 O(1) 平均的哈希查找，
       常数因子略大，但渐进复杂度相同。

    3. 操作逻辑和数组实现完全一致，只是访问子节点的方式不同。

    【举例】插入 "apple" 和 "app"
      root → {'a': node1}
      node1 → {'p': node2}
      node2 → {'p': node3}
      node3 → {'l': node4}, is_end=True
      node4 → {'e': node5}
      node5 → {}, is_end=True

    【时间复杂度】insert/search/startsWith 均为 O(L)
    【空间复杂度】O(T)，T 为 Trie 中节点总数，每个节点只存实际存在的子节点
    """
    def __init__(self):
        self.children = {}
        self.is_end = False

    def insert(self, word: str) -> None:
        node = self
        for ch in word:
            if ch not in node.children:
                node.children[ch] = TrieHashMap()
            node = node.children[ch]
        node.is_end = True

    def search(self, word: str) -> bool:
        node = self._search_prefix(word)
        return node is not None and node.is_end

    def startsWith(self, prefix: str) -> bool:
        return self._search_prefix(prefix) is not None

    def _search_prefix(self, prefix: str):
        node = self
        for ch in prefix:
            if ch not in node.children:
                return None
            node = node.children[ch]
        return node
