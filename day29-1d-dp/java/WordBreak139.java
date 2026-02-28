/**
 * LeetCode 139. Word Break
 * 难度: Medium
 *
 * 题目描述：
 * 给你一个字符串 s 和一个字符串列表 wordDict 作为字典。
 * 如果可以利用字典中出现的一个或多个单词拼接出 s，则返回 true。
 * 注意：字典中的单词可以重复使用。
 *
 * 示例：s = "leetcode", wordDict = ["leet","code"] → 输出 true
 *      （"leetcode" = "leet" + "code"）
 *
 * 【拓展练习】
 * 1. LeetCode 140. Word Break II —— 返回所有可行的拆分方案（回溯+记忆化）
 * 2. LeetCode 472. Concatenated Words —— 判断数组中哪些词可由其他词拼接而成
 * 3. LeetCode 91. Decode Ways —— 类似的字符串分段DP问题
 */

import java.util.*;

class WordBreak139 {

    /**
     * ==================== 解法一：DP ====================
     *
     * 【核心思路】
     * dp[i] 表示 s[0:i]（前 i 个字符）是否可以被字典中的单词拼接而成。
     * 对于每个位置 i，枚举所有可能的最后一个单词 s[j:i]，
     * 如果 dp[j] 为 true 且 s[j:i] 在字典中，则 dp[i] = true。
     *
     * 【思考过程】
     * 1. 定义状态 dp[i]："s 的前 i 个字符是否可拆分"。
     *    目标是 dp[n]（n 为 s 的长度）。
     *
     * 2. 转移：dp[i] = true 当且仅当存在某个 j (0 <= j < i) 使得：
     *    - dp[j] = true（前 j 个字符已成功拆分）
     *    - s[j:i] 在字典中（从 j 到 i 这段是一个完整单词）
     *
     * 3. 将字典放入 HashSet 中，判断子串是否在字典里就是 O(L) 操作。
     *    可以利用字典中单词最大长度来限制内层循环范围。
     *
     * 【举例】s = "leetcode", wordDict = ["leet", "code"]
     *   dp = [T, F, F, F, F, F, F, F, F]
     *   i=4: j=0, s[0:4]="leet" ∈ dict, dp[0]=T → dp[4]=T
     *   i=8: j=4, s[4:8]="code" ∈ dict, dp[4]=T → dp[8]=T
     *   答案 = dp[8] = true
     *
     * 【时间复杂度】O(n² * L)，n 为字符串长度，L 为字典中最长单词长度
     * 【空间复杂度】O(n)
     */
    public boolean wordBreakDp(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        int maxLen = 0;
        for (String w : wordDict) {
            maxLen = Math.max(maxLen, w.length());
        }

        int n = s.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;

        for (int i = 1; i <= n; i++) {
            for (int j = Math.max(0, i - maxLen); j < i; j++) {
                if (dp[j] && wordSet.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }

        return dp[n];
    }

    /**
     * ==================== 解法二：BFS ====================
     *
     * 【核心思路】
     * 把问题看作图的遍历：每个位置 i 是一个节点，
     * 如果 s[i:i+len(word)] 等于字典中的某个 word，则从 i 连一条边到 i+len(word)。
     * 问题变成：从节点 0 出发，能否到达节点 n。
     *
     * 【思考过程】
     * 1. BFS 天然适合探索"从起点能否到达终点"的问题。
     *
     * 2. 从位置 0 开始，尝试匹配字典中的每个单词。
     *    如果 s[0:len(w)] == w，就把 len(w) 加入队列。
     *    从队列取出位置 i，再尝试从 i 开始匹配……直到到达 n。
     *
     * 3. 用 visited 数组防止重复访问同一位置。
     *
     * 4. 与 DP 解法等价，但思维模型不同：DP 是"填表"，BFS 是"搜索"。
     *
     * 【举例】s = "leetcode", wordDict = ["leet", "code"]
     *   队列: [0]
     *   取出 0: s[0:4]="leet" ∈ dict → 加入 4
     *   取出 4: s[4:8]="code" ∈ dict → 加入 8
     *   8 == n → 返回 true
     *
     * 【时间复杂度】O(n² * L)
     * 【空间复杂度】O(n)
     */
    public boolean wordBreakBfs(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        int n = s.length();
        boolean[] visited = new boolean[n + 1];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(0);

        while (!queue.isEmpty()) {
            int start = queue.poll();
            if (start == n) return true;
            if (visited[start]) continue;
            visited[start] = true;

            for (int end = start + 1; end <= n; end++) {
                if (wordSet.contains(s.substring(start, end))) {
                    queue.offer(end);
                }
            }
        }

        return false;
    }
}
