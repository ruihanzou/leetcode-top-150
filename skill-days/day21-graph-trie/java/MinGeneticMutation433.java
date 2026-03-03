/**
 * LeetCode 433. Minimum Genetic Mutation
 * 难度: Medium
 *
 * 题目描述：
 * 基因序列由 8 个字符组成，每个字符是 'A'、'C'、'G'、'T' 之一。
 * 一次基因变化意味着序列中一个字符发生了变化。变化后的序列必须在基因库 bank 中。
 * 给你 start 和 end 两个基因序列以及基因库 bank，
 * 返回从 start 变化为 end 所需的最少变化次数，无法完成返回 -1。
 *
 * 示例：start = "AACCGGTT", end = "AAACGGTA",
 *       bank = ["AACCGGTA","AACCGCTA","AAACGGTA"]
 *   → 输出 2  ("AACCGGTT" → "AACCGGTA" → "AAACGGTA")
 *
 * 【拓展练习】
 * 1. LeetCode 127. Word Ladder —— 类似的 BFS 最短转换问题
 * 2. LeetCode 126. Word Ladder II —— 找所有最短转换序列
 * 3. LeetCode 752. Open the Lock —— BFS 求最短操作次数
 */

import java.util.*;

class MinGeneticMutation433 {

    /**
     * ==================== 解法一：BFS ====================
     *
     * 【核心思路】
     * 将每个合法基因序列看作图中的节点，恰好相差一个字符的序列之间连边。
     * 从 start 出发 BFS 找到 end 的最短路径。
     *
     * 【思考过程】
     * 1. "最少变化次数" = 无权图最短路径 → BFS。
     *
     * 2. 枚举当前序列每个位置×4种字符，得到最多 8×3=24 个候选邻居。
     *    如果候选在 bank 中且未访问过，加入队列。
     *
     * 3. bank 用 HashSet 存储，O(1) 查找。
     *
     * 【举例】"AACCGGTT" → 变第8位为A → "AACCGGTA"(在bank中)
     *       → 变第3位为A → "AAACGGTA" = end → 返回2
     *
     * 【时间复杂度】O(n * L * 4)，n=bank大小，L=8
     * 【空间复杂度】O(n * L)
     */
    public int minMutationBfs(String startGene, String endGene, String[] bank) {
        Set<String> bankSet = new HashSet<>(Arrays.asList(bank));
        if (!bankSet.contains(endGene)) return -1;

        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.offer(startGene);
        visited.add(startGene);
        char[] genes = {'A', 'C', 'G', 'T'};
        int steps = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int q = 0; q < size; q++) {
                String current = queue.poll();
                if (current.equals(endGene)) return steps;

                char[] arr = current.toCharArray();
                for (int i = 0; i < arr.length; i++) {
                    char original = arr[i];
                    for (char g : genes) {
                        if (g == original) continue;
                        arr[i] = g;
                        String mutation = new String(arr);
                        if (bankSet.contains(mutation) && !visited.contains(mutation)) {
                            visited.add(mutation);
                            queue.offer(mutation);
                        }
                    }
                    arr[i] = original;
                }
            }
            steps++;
        }

        return -1;
    }

    /**
     * ==================== 解法二：双向 BFS ====================
     *
     * 【核心思路】
     * 同时从 start 和 end 两端 BFS，每次扩展较小的一端。
     * 两端相遇时，总步数即为答案。
     *
     * 【思考过程】
     * 1. 单向 BFS 搜索空间随深度指数增长。
     *    双向 BFS 从两端同时搜索，搜索空间约为单向的平方根。
     *
     * 2. 每轮选择较小集合扩展，保持搜索树平衡。
     *
     * 3. 当一端生成的新节点出现在另一端的集合中 → 相遇 → 返回步数。
     *
     * 【时间复杂度】O(n * L * 4)，实际搜索空间更小
     * 【空间复杂度】O(n * L)
     */
    public int minMutationBidirectional(String startGene, String endGene, String[] bank) {
        Set<String> bankSet = new HashSet<>(Arrays.asList(bank));
        if (!bankSet.contains(endGene)) return -1;

        Set<String> front = new HashSet<>();
        Set<String> back = new HashSet<>();
        Set<String> visited = new HashSet<>();
        front.add(startGene);
        back.add(endGene);
        visited.add(startGene);
        visited.add(endGene);
        char[] genes = {'A', 'C', 'G', 'T'};
        int steps = 0;

        while (!front.isEmpty() && !back.isEmpty()) {
            if (front.size() > back.size()) {
                Set<String> temp = front;
                front = back;
                back = temp;
            }

            Set<String> newFront = new HashSet<>();
            for (String current : front) {
                char[] arr = current.toCharArray();
                for (int i = 0; i < arr.length; i++) {
                    char original = arr[i];
                    for (char g : genes) {
                        if (g == original) continue;
                        arr[i] = g;
                        String mutation = new String(arr);
                        if (back.contains(mutation)) return steps + 1;
                        if (bankSet.contains(mutation) && !visited.contains(mutation)) {
                            visited.add(mutation);
                            newFront.add(mutation);
                        }
                    }
                    arr[i] = original;
                }
            }

            front = newFront;
            steps++;
        }

        return -1;
    }
}
