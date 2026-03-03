/**
 * LeetCode 127. Word Ladder
 * 难度: Hard
 *
 * 题目描述：
 * 给定两个单词 beginWord 和 endWord，以及一个字典 wordList，
 * 找到从 beginWord 到 endWord 的最短转换序列的长度。转换规则如下：
 * 1. 每次只能改变一个字母。
 * 2. 转换过程中的中间单词必须在字典 wordList 中。
 * 如果不存在这样的转换序列，返回 0。
 *
 * 示例 1：beginWord = "hit", endWord = "cog", wordList = ["hot","dot","dog","lot","log","cog"]
 *   输出 5，转换序列为 "hit" -> "hot" -> "dot" -> "dog" -> "cog"
 *
 * 示例 2：beginWord = "hit", endWord = "cog", wordList = ["hot","dot","dog","lot","log"]
 *   输出 0，endWord "cog" 不在 wordList 中
 *
 * 【拓展练习】
 * 1. LeetCode 126. Word Ladder II —— 返回所有最短转换序列
 * 2. LeetCode 433. Minimum Genetic Mutation —— 基因突变版本，8位字符串、4种字符
 * 3. LeetCode 752. Open the Lock —— BFS求最短路径的类似模型
 */

import java.util.*;

class WordLadder127 {

    /**
     * ==================== 解法一：BFS ====================
     *
     * 【核心思路】
     * 将每个单词视为图中的节点，两个单词之间如果只差一个字母则连一条边。
     * 从 beginWord 出发做 BFS，第一次到达 endWord 时的层数就是最短路径长度。
     *
     * 【思考过程】
     * 1. "最短转换序列"→ 无权图最短路径 → BFS。
     * 2. 关键在于如何高效找到当前单词的所有邻居（只差一个字母的单词）。
     *    对当前单词的每个位置尝试替换为 a-z，检查是否在字典中（O(26*L) 每步）。
     *    在 wordList 很大时效率远高于逐一比较。
     *
     * 3. 用 HashSet 存储 wordList 以支持 O(1) 查找。
     *    访问过的单词从 set 中删除（等价于 visited 标记），避免重复访问。
     *
     * 4. BFS 逐层扩展，层数 = 转换序列长度。
     *
     * 【举例】beginWord = "hit", endWord = "cog"
     *   wordList = {"hot","dot","dog","lot","log","cog"}
     *   层1: {"hit"}, steps=1
     *   层2: {"hot"} (hit→hot), steps=2
     *   层3: {"dot","lot"} (hot→dot, hot→lot), steps=3
     *   层4: {"dog","log"} (dot→dog, lot→log), steps=4
     *   层5: {"cog"} (dog→cog, log→cog), steps=5 → 找到endWord，返回5
     *
     * 【时间复杂度】O(n * L²)，n 为字典大小，L 为单词长度
     * 【空间复杂度】O(n * L)
     */
    public int ladderLengthBFS(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord)) return 0;

        Queue<String> queue = new LinkedList<>();
        queue.offer(beginWord);
        wordSet.remove(beginWord);
        int steps = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int q = 0; q < size; q++) {
                char[] word = queue.poll().toCharArray();
                for (int i = 0; i < word.length; i++) {
                    char original = word[i];
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == original) continue;
                        word[i] = c;
                        String next = new String(word);
                        if (next.equals(endWord)) return steps + 1;
                        if (wordSet.contains(next)) {
                            wordSet.remove(next);
                            queue.offer(next);
                        }
                    }
                    word[i] = original;
                }
            }
            steps++;
        }

        return 0;
    }

    /**
     * ==================== 解法二：双向 BFS ====================
     *
     * 【核心思路】
     * 同时从 beginWord 和 endWord 两端开始做 BFS，每次扩展较小的一端。
     * 当两端的搜索相遇时，路径就找到了。
     *
     * 【思考过程】
     * 1. 单向 BFS 的搜索空间像一棵扇形展开的树，越远层节点越多。
     *    假设分支因子为 b，路径长度为 d，搜索空间约 O(b^d)。
     *
     * 2. 双向 BFS 从两端同时搜索，每端只需搜索 d/2 层，
     *    搜索空间约 O(2 * b^(d/2))，远小于 O(b^d)。
     *
     * 3. 每次选择扩展较小的一端（贪心策略），可以进一步减少搜索空间。
     *
     * 4. 实现上使用两个 Set 分别存储两端当前层的节点。
     *    每轮扩展较小的 Set，生成下一层节点。
     *    如果下一层节点出现在另一端的 Set 中，说明相遇了。
     *
     * 【举例】beginWord = "hit", endWord = "cog"
     *   front = {"hit"}, back = {"cog"}, steps=1
     *   扩展 front: hit → {"hot"}, steps=2
     *   扩展 back: cog → {"dog","log"}, steps=3
     *   扩展 front: hot → {"dot","lot"}, steps=4
     *   扩展 back: dot 在 front 中！相遇，返回 steps=5
     *
     * 【时间复杂度】O(n * L²)，最坏情况同单向 BFS，但实际远快
     * 【空间复杂度】O(n * L)
     */
    public int ladderLengthBidirectional(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord)) return 0;

        Set<String> front = new HashSet<>();
        Set<String> back = new HashSet<>();
        front.add(beginWord);
        back.add(endWord);
        wordSet.remove(beginWord);
        wordSet.remove(endWord);
        int steps = 1;

        while (!front.isEmpty() && !back.isEmpty()) {
            if (front.size() > back.size()) {
                Set<String> temp = front;
                front = back;
                back = temp;
            }

            Set<String> nextLevel = new HashSet<>();
            for (String w : front) {
                char[] word = w.toCharArray();
                for (int i = 0; i < word.length; i++) {
                    char original = word[i];
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == original) continue;
                        word[i] = c;
                        String next = new String(word);
                        if (back.contains(next)) return steps + 1;
                        if (wordSet.contains(next)) {
                            wordSet.remove(next);
                            nextLevel.add(next);
                        }
                    }
                    word[i] = original;
                }
            }

            front = nextLevel;
            steps++;
        }

        return 0;
    }
}
