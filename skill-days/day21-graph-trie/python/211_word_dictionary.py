"""
LeetCode 211. Design Add and Search Words Data Structure
难度: Medium

题目描述：
设计一个数据结构，支持添加新单词和查找字符串是否与任何先前添加的字符串匹配。
- addWord(word)：将 word 添加到数据结构中。
- search(word)：如果数据结构中存在与 word 匹配的字符串，返回 true；否则返回 false。
  word 中可能包含 '.'，'.' 可以匹配任意一个字母。

示例：
  wd = WordDictionary()
  wd.addWord("bad")
  wd.addWord("dad")
  wd.addWord("mad")
  wd.search("pad")  → false
  wd.search("bad")  → true
  wd.search(".ad")  → true
  wd.search("b..")  → true

【拓展练习】
1. LeetCode 208. Implement Trie (Prefix Tree) —— 标准 Trie 实现
2. LeetCode 212. Word Search II —— Trie+回溯在棋盘中搜索
3. LeetCode 10. Regular Expression Matching —— 更复杂的模式匹配
"""


class WordDictionary:
    """
    ==================== 解法一：Trie + DFS 搜索 ====================

    【核心思路】
    使用 Trie 存储所有单词。搜索时，遇到普通字符按正常 Trie 查找；
    遇到 '.' 则递归搜索当前节点的所有子节点（因为 '.' 可匹配任意字母）。

    【思考过程】
    1. addWord 和普通 Trie 的 insert 完全一样。
    2. search 的难点在于处理 '.'：
       - 如果当前字符是普通字母，直接走对应的子节点。
       - 如果当前字符是 '.'，需要尝试所有 26 个子节点，
         只要任意一个能匹配剩余部分就返回 True。
       这天然是一个 DFS 过程。

    3. 递归函数 dfs(node, word, index)：
       从 node 出发，匹配 word[index:] 部分。
       base case：index == len(word) 时检查 node.is_end。

    【举例】
      插入 "bad", "dad", "mad"
      Trie 结构：
        root → b → a → d (is_end=True)
             → d → a → d (is_end=True)
             → m → a → d (is_end=True)

      search(".ad")：
        '.' → 尝试 b, d, m 三个子节点
          b → 'a' → a → 'd' → d, is_end=True → 返回 True

      search("b..")：
        'b' → b → '.' → 尝试 a → '.' → 尝试 d, is_end=True → True

    【时间复杂度】addWord: O(L)，search: 最坏 O(26^L)（全是'.'），平均远小于此
    【空间复杂度】O(T * 26)，T 为 Trie 中节点总数
    """
    def __init__(self):
        self.children = [None] * 26
        self.is_end = False

    def addWord(self, word: str) -> None:
        node = self
        for ch in word:
            idx = ord(ch) - ord('a')
            if node.children[idx] is None:
                node.children[idx] = WordDictionary()
            node = node.children[idx]
        node.is_end = True

    def search(self, word: str) -> bool:
        return self._dfs(self, word, 0)

    def _dfs(self, node, word: str, index: int) -> bool:
        if index == len(word):
            return node.is_end

        ch = word[index]
        if ch == '.':
            for child in node.children:
                if child is not None and self._dfs(child, word, index + 1):
                    return True
            return False
        else:
            idx = ord(ch) - ord('a')
            if node.children[idx] is None:
                return False
            return self._dfs(node.children[idx], word, index + 1)


class WordDictionaryHashGroup:
    """
    ==================== 解法二：按长度分组的哈希表 + 逐字符匹配 ====================

    【核心思路】
    将所有单词按长度分组存储在字典中。
    搜索时，先按长度筛选候选单词，再逐字符比较（'.' 匹配任意字符）。

    【思考过程】
    1. 不使用 Trie，而是用更简单的数据结构。
    2. 搜索时长度不同的单词不可能匹配，先按长度过滤减少比较次数。
    3. 对于候选单词，逐字符比较：
       - 搜索字符是 '.' → 跳过该位，继续比较后续
       - 搜索字符是普通字母 → 必须完全匹配
    4. 缺点：当同一长度的单词很多时，搜索效率低于 Trie+DFS。
       优点：实现简单，无需维护复杂的树结构。

    【举例】
      addWord("bad"), addWord("dad"), addWord("mad")
      groups = {3: ["bad", "dad", "mad"]}

      search(".ad")：
        len = 3 → 候选 ["bad", "dad", "mad"]
        "bad": '.'->'b' ✓, 'a'=='a' ✓, 'd'=='d' ✓ → 匹配！返回 True

      search("b.d")：
        len = 3 → 候选 ["bad", "dad", "mad"]
        "bad": 'b'=='b' ✓, '.'->'a' ✓, 'd'=='d' ✓ → 匹配！返回 True

    【时间复杂度】addWord: O(1)，search: O(n * L)，n 为同长度单词数
    【空间复杂度】O(N * L)，N 为总单词数
    """
    def __init__(self):
        self.groups = {}

    def addWord(self, word: str) -> None:
        length = len(word)
        if length not in self.groups:
            self.groups[length] = []
        self.groups[length].append(word)

    def search(self, word: str) -> bool:
        length = len(word)
        if length not in self.groups:
            return False

        for candidate in self.groups[length]:
            if self._match(word, candidate):
                return True
        return False

    def _match(self, pattern: str, word: str) -> bool:
        for p, w in zip(pattern, word):
            if p != '.' and p != w:
                return False
        return True
