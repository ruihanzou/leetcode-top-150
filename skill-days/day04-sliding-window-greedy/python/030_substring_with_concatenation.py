"""
LeetCode 30. Substring with Concatenation of All Words
难度: Hard

题目描述：
给定一个字符串 s 和一个字符串数组 words。words 中所有字符串长度相同。
s 中的串联子串是指一个包含 words 中所有字符串以任意顺序排列连接起来的子串。
返回所有串联子串在 s 中的起始索引。你可以以任意顺序返回答案。

示例 1：
  输入：s = "barfoothefoobarman", words = ["foo","bar"]
  输出：[0,9]
  解释：
    从索引 0 开始的子串是 "barfoo"，它是 ["bar","foo"] 的串联。
    从索引 9 开始的子串是 "foobar"，它是 ["foo","bar"] 的串联。

示例 2：
  输入：s = "wordgoodgoodgoodbestword", words = ["word","good","best","word"]
  输出：[8]
  解释：从索引 8 开始的子串是 "able goodbestword"，
    它是 ["word","good","best","word"] 的串联。

示例 3：
  输入：s = "barfoofoobarthefoobarman", words = ["bar","foo","the"]
  输出：[6,9,12]

【拓展练习】
1. LeetCode 76. Minimum Window Substring —— 滑动窗口 + 字符计数
2. LeetCode 438. Find All Anagrams in a String —— 固定窗口找变位词
3. LeetCode 567. Permutation in String —— 判断是否包含排列
"""

from typing import List
from collections import Counter
from collections import defaultdict
 

class Solution:
    """
    ==================== 解法一：暴力逐位检查 ====================

    【变量说明】
    - word_len: 每个单词的长度（words 里所有词等长）
    - word_count: 单词个数，即 len(words)
    - total_len: 串联子串的总长度，等于 word_len * word_count
    - word_freq: words 中每个单词出现的次数（目标计数）
    - i: 当前枚举的「子串起始位置」（在 s 中的下标）
    - seen: 从位置 i 开始已截取到的单词及其出现次数（临时计数）
    - j: 从 i 开始已经成功截取的「段」的个数（0 到 word_count）
    - start: 当前这一段在 s 中的起始下标，即 i + j * word_len
    - word: 当前这一段截出来的字符串，即 s[start:start+word_len]
    - result: 所有合法起始位置的列表

    【核心思路】
    枚举 s 中每一个可能的起始位置 i，从 i 开始每次截取 word_len 长度的子串，
    连续截取 word_count 个，检查这些子串是否恰好构成 words 的一个排列。

    【思考过程】
    1. 串联子串的总长度 = total_len，所以起始位置 i 的范围是 [0, len(s) - total_len] 
    or [i, i + total_len]。

    2. 对每个 i，用临时计数器 seen，逐个截取长度 word_len 的子串（即 word），
       如果 word 不在 word_freq 中或者 seen[word] 超过 word_freq[word]，就提前终止。

    3. 如果成功截取了 word_count 个段（j == word_count），说明 i 是合法起始位置，加入 result。

    【举例】s = "barfoothefoobarman", words = ["foo","bar"]
      word_len=3, word_count=2, total_len=6, word_freq = {"foo":1, "bar":1}
      i 的范围是 0 到 len(s)-total_len = 12，即 0,1,...,12 都会枚举。

      i=0: 截取 "bar"(✓), "foo"(✓), 共2个 → 合法，加入 result
      i=1: 截取 "arf" → 不在 words 中，跳过
      ...
      i=9: 截取 "foo"(✓), "bar"(✓), 共2个 → 合法，加入 result
      i=10, 11, 12: 同样会检查，但截取的段要么不在 words 中、要么次数超，均不合法。
      result = [0, 9]

    【时间复杂度】O(n * m * k) —— n=len(s), m=word_count, k=word_len
    【空间复杂度】O(m * k) —— 计数器空间
    """
    def findSubstring_brute(self, s: str, words: List[str]) -> List[int]:
        if not s or not words:
            return []

        word_len = len(words[0])
        word_count = len(words)
        total_len = word_len * word_count
        # Counter is built-in data structure in collections module, 
        # it is a dictionary that counts the frequency of each element 
        # in the iterable.
        # word_freq looks like this: {"foo": 1, "bar": 1}

        # In java, it is a Map<String, Integer> and using merge method to count the frequency.
        # For example:
        # Map<String, Integer> wordFreq = new HashMap<>();
        # for (String w : words) {
        #     wordFreq.merge(w, 1, Integer::sum);
        # }
        word_freq = Counter(words)
        result = []

        # the range is from 0 to len(s)-total_len, 
        # because the total length of the substring is total_len.
        for i in range(len(s) - total_len + 1):
            seen = Counter()
            # pointer j is used to count the number of words in the substring.
            j = 0
            # while j is less than word_count, 
            # we can continue to count the number of words in the substring.
            while j < word_count:
                # how to calculate the start index of the substring?
                # start = i + j * word_len
                # i is the starting index of the substring, j is the number of words in the substring.
                # word_len is the length of the word.
                # why we need to multiply j by word_len?
                # because we need to skip the words in the substring.
                # for example, if the word is "foo" and the word_len is 3,
                # and we want to skip the first word, we need to skip 3 characters.
                # so we need to multiply j by word_len.
                # if j is 1, we need to skip 3 characters.
                # if j is 2, we need to skip 6 characters.
                # if j is 3, we need to skip 9 characters.
                # so on and so forth.
                start = i + j * word_len
                # word is the substring of the s.
                word = s[start:start + word_len]
                if word not in word_freq:
                    break
                seen[word] += 1
                if seen[word] > word_freq[word]:
                    break
                j += 1
            if j == word_count:
                result.append(i)

        return result

    """
    ==================== 解法二：滑动窗口（按单词长度分组） ====================

    【变量说明】
    - word_len, word_count, total_len: 同解法一。
    - start: 当前桶号（0 到 word_len-1）。桶内段的起点为 start, start+word_len, start+2*word_len, ...
    - left: 窗口左边界（s 的下标）。right: 当前要加入的段起点；每次 right += word_len。
    - target: words 中每个单词出现次数（目标）。window: 窗口内各单词出现次数。
    - count: 窗口内单词个数。ans: 合法起点列表。

    【一眼看懂】
    这题可以理解成两步：
    1. 先把所有可能的起点分到 word_len 个桶里。
    2. 再在每个桶里做一次滑动窗口。

    【为什么要分桶】
    因为每个单词长度都是 word_len，所以合法答案一定是「一段一段」拼出来的。
    例如 word_len = 3 时：
    - 桶 0 里的起点是 0, 3, 6, 9, ...
    - 桶 1 里的起点是 1, 4, 7, 10, ...
    - 桶 2 里的起点是 2, 5, 8, 11, ...
    同一个桶里的起点，切分方式是对齐的，适合用同一个滑动窗口来处理。

    【每个桶里怎么做】
    1. 固定桶号 start。
    2. 让 right 每次往右跳 word_len，表示右端加入一个完整单词。
    3. 用 window 统计当前窗口里每个单词的出现次数。
    4. 如果某个单词出现次数超了，就让 left 也往右跳 word_len，弹出左边一个单词。
    5. 这样窗口里始终只保留“可能合法”的一段。

    【什么时候记答案】
    当 count == word_count 时，窗口里刚好有 word_count 个单词且次数都合法，此时 left 是合法起点。

    【为什么不会漏】
    因为每个起点 i 都会进入唯一一个桶：第 i % word_len 号桶。
    所以所有起点都会被检查到，而且不会重复检查。

    【举例】s = "barfoothefoobarman", words = ["foo","bar"], word_len = 3
    - 桶 0：检查起点 0, 3, 6, 9, 12, ...
      先扫到 "bar","foo" → 合法，记录 0
      再扫到 "the" → 不在 words 中，清空窗口
      再扫到 "foo","bar" → 合法，记录 9
    - 桶 1、桶 2：都找不到合法答案
    最终 ans = [0, 9]

    【时间复杂度】O(n * k)  【空间复杂度】O(m)
    """
    
    def findSubstring(self, s: str, words: List[str]) -> List[int]:
        # sanity check
        if not s or not words or not words[0]:
            return []

        # get the length of the word and the number of words.
        word_len = len(words[0])
        word_count = len(words)
        total_len = word_len * word_count
        n = len(s)

        # if the length of the string is less than the total length of the words, 
        # return empty list.
        if n < total_len:
            return []
        # target is the frequency of the words.
        # for example, if the words are ["foo","bar"], 
        # target will be {"foo":1, "bar":1}.
        # in java, it is a Map<String, Integer> and using merge method to count the frequency.
        # For example:
        # Map<String, Integer> target = new HashMap<>();
        # for (String w : words) {
        #     target.merge(w, 1, Integer::sum);
        # }
        target = Counter(words)
        ans = []

        # 做 word_len 轮
        for start in range(word_len):
            left, right = start, start
            window = defaultdict(int)
            count = 0

            while right + word_len <= n:
                word = s[right:right + word_len]
                right += word_len

                # 情况1：这个词不在 target 里，整段作废，重新开始
                if word not in target:
                    window.clear()
                    count = 0
                    left = right
                    continue

                # 加入窗口
                window[word] += 1
                count += 1

                while window[word] > target[word]:
                    left_word = s[left:left + word_len]
                    window[left_word] -= 1
                    count -= 1
                    left += word_len

                if count == word_count:
                    ans.append(left)
                    left_word = s[left:left + word_len]
                    window[left_word] -= 1
                    count -= 1
                    left += word_len

        return ans
