/**
 * LeetCode 68. Text Justification
 * 难度: Hard
 *
 * 题目描述：
 * 给定一个单词数组 words 和一个最大宽度 maxWidth，按照以下规则进行文本对齐：
 * 每行恰好有 maxWidth 个字符且两端对齐。尽可能多地往每一行中放单词。
 * 必要时在单词间填充额外空格，使得每行恰好 maxWidth 个字符。
 * 单词间的空格应尽量均匀分布。如果某一行的空格不能均匀分布，左侧的空格应多于右侧。
 * 最后一行应为左对齐，单词间不插入额外空格。
 *
 * 示例：words = ["This","is","an","example","of","text","justification."], maxWidth = 16
 * 输出：
 *   "This    is    an"
 *   "example  of text"
 *   "justification.  "
 *
 * 【拓展练习】
 * 1. LeetCode 1592. Rearrange Spaces Between Words —— 简化版的空格重排
 * 2. LeetCode 2138. Divide a String Into Groups of Size k —— 字符串分组填充
 * 3. LeetCode 412. Fizz Buzz —— 简单的格式化输出练习
 */

import java.util.ArrayList;
import java.util.List;

class TextJustification068 {

    /**
     * ==================== 解法一：贪心模拟（一体化） ====================
     *
     * 【核心思路】
     * 逐行贪心地放入尽可能多的单词，然后根据是否为最后一行来分配空格：
     * - 非最后一行：计算总空格数，均匀分配到单词间隙中，余数从左到右多分一个。
     * - 最后一行：单词间只放一个空格，末尾用空格补齐到 maxWidth。
     * - 只有一个单词的行：单词后补空格到 maxWidth。
     *
     * 【思考过程】
     * 1. 贪心放单词：从当前单词开始，不断往后加单词，直到加入下一个单词
     *    会超过 maxWidth（需考虑单词间至少一个空格）。
     *
     * 2. 确定一行的单词后，计算总空格数 = maxWidth - 所有单词字符总长度。
     *    如果有 k 个间隙（k = 单词数 - 1），每个间隙至少分 totalSpaces / k 个空格，
     *    前 totalSpaces % k 个间隙多分一个。
     *
     * 3. 特殊处理：
     *    - 一行只有一个单词 → 后面全补空格
     *    - 最后一行 → 单词间一个空格，末尾补空格
     *
     * 4. 这题没有算法上的巧妙优化，核心在于正确处理各种边界条件。
     *
     * 【举例】words = ["This","is","an","example","of","text","justification."], maxWidth = 16
     *   第1行: ["This","is","an"], 字符长=4+2+2=8, 总空格=16-8=8, 2个间隙
     *          每个间隙=8/2=4, 余数=8%2=0 → "This····is····an" (·表示空格)
     *   第2行: ["example","of","text"], 字符长=7+2+4=13, 总空格=3, 2个间隙
     *          每个间隙=3/2=1, 余数=3%2=1 → "example··of·text"
     *   第3行: ["justification."], 最后一行，左对齐 → "justification.··"
     *
     * 【时间复杂度】O(n) n 是所有单词字符总数，每个字符最多被处理常数次
     * 【空间复杂度】O(n) 存储结果
     */
    public List<String> fullJustifyGreedy(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        int i = 0;

        while (i < words.length) {
            int lineLen = words[i].length();
            int j = i + 1;
            while (j < words.length && lineLen + 1 + words[j].length() <= maxWidth) {
                lineLen += 1 + words[j].length();
                j++;
            }

            int gaps = j - i - 1;
            StringBuilder line = new StringBuilder();

            if (j == words.length || gaps == 0) {
                for (int k = i; k < j; k++) {
                    if (k > i) line.append(' ');
                    line.append(words[k]);
                }
                while (line.length() < maxWidth) {
                    line.append(' ');
                }
            } else {
                int totalChars = 0;
                for (int k = i; k < j; k++) {
                    totalChars += words[k].length();
                }
                int totalSpaces = maxWidth - totalChars;
                int spacePerGap = totalSpaces / gaps;
                int extraSpaces = totalSpaces % gaps;

                for (int k = i; k < j; k++) {
                    line.append(words[k]);
                    if (k < j - 1) {
                        int spaces = spacePerGap + (k - i < extraSpaces ? 1 : 0);
                        for (int s = 0; s < spaces; s++) {
                            line.append(' ');
                        }
                    }
                }
            }

            result.add(line.toString());
            i = j;
        }

        return result;
    }

    /**
     * ==================== 解法二：分步处理版 ====================
     *
     * 【核心思路】
     * 将问题拆分为两个清晰的阶段：
     * 阶段一：分行 —— 贪心确定每行包含哪些单词（记录起止索引）。
     * 阶段二：格式化 —— 对每行独立处理空格分配。
     *
     * 算法逻辑与解法一完全相同，但代码组织更加模块化，可读性更强。
     *
     * 【思考过程】
     * 1. 解法一把"分行"和"格式化"混在一个循环里，逻辑耦合度高。
     *    如果将它们拆开，每一步的逻辑更容易理解和调试。
     *
     * 2. 阶段一用一个循环确定每行的起止索引对 [start, end)。
     *    阶段二遍历每个行，调用辅助方法格式化。
     *
     * 3. 辅助方法 formatLine 专注于一行的空格分配逻辑，
     *    入参是 words 数组、起止索引、maxWidth 和是否最后一行。
     *
     * 4. 这种写法在工程中更受推崇：关注点分离，便于单元测试。
     *
     * 【举例】同解法一
     *
     * 【时间复杂度】O(n)
     * 【空间复杂度】O(n)
     */
    public List<String> fullJustifyStepwise(String[] words, int maxWidth) {
        List<int[]> lines = splitIntoLines(words, maxWidth);
        List<String> result = new ArrayList<>();

        for (int idx = 0; idx < lines.size(); idx++) {
            int[] line = lines.get(idx);
            boolean isLastLine = (idx == lines.size() - 1);
            result.add(formatLine(words, line[0], line[1], maxWidth, isLastLine));
        }

        return result;
    }

    private List<int[]> splitIntoLines(String[] words, int maxWidth) {
        List<int[]> lines = new ArrayList<>();
        int i = 0;

        while (i < words.length) {
            int lineLen = words[i].length();
            int j = i + 1;
            while (j < words.length && lineLen + 1 + words[j].length() <= maxWidth) {
                lineLen += 1 + words[j].length();
                j++;
            }
            lines.add(new int[]{i, j});
            i = j;
        }

        return lines;
    }

    private String formatLine(String[] words, int start, int end, int maxWidth, boolean isLastLine) {
        int wordCount = end - start;
        StringBuilder line = new StringBuilder();

        if (wordCount == 1 || isLastLine) {
            for (int k = start; k < end; k++) {
                if (k > start) line.append(' ');
                line.append(words[k]);
            }
            while (line.length() < maxWidth) {
                line.append(' ');
            }
            return line.toString();
        }

        int totalChars = 0;
        for (int k = start; k < end; k++) {
            totalChars += words[k].length();
        }

        int gaps = wordCount - 1;
        int totalSpaces = maxWidth - totalChars;
        int spacePerGap = totalSpaces / gaps;
        int extraSpaces = totalSpaces % gaps;

        for (int k = start; k < end; k++) {
            line.append(words[k]);
            if (k < end - 1) {
                int spaces = spacePerGap + (k - start < extraSpaces ? 1 : 0);
                for (int s = 0; s < spaces; s++) {
                    line.append(' ');
                }
            }
        }

        return line.toString();
    }
}
