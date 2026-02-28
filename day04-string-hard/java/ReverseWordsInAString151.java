/**
 * LeetCode 151. Reverse Words in a String
 * 难度: Medium
 *
 * 题目描述：
 * 给定一个字符串 s，将字符串中的单词顺序反转。单词是由非空格字符组成的字符串。
 * s 中至少存在一个单词。输入字符串可能在前面和后面包含多余的空格。
 * 两个单词之间可能有多余的空格。返回的字符串中单词之间应该只有一个空格。
 *
 * 示例：s = "  hello world  " → 输出 "world hello"
 *
 * 【拓展练习】
 * 1. LeetCode 186. Reverse Words in a String II —— 原地翻转（输入为 char[]）
 * 2. LeetCode 557. Reverse Words in a String III —— 翻转每个单词中的字符，但保持单词顺序
 * 3. LeetCode 58. Length of Last Word —— 从后往前扫描提取最后一个单词
 */

class ReverseWordsInAString151 {

    /**
     * ==================== 解法一：split + reverse ====================
     *
     * 【核心思路】
     * 利用语言内置的 split 按空格分割字符串得到单词数组，
     * 反转数组后用单个空格拼接即可。
     *
     * 【思考过程】
     * 1. 题目要求反转单词顺序，最直观的做法就是先提取所有单词，再反向拼接。
     * 2. Java 的 String.split("\\s+") 可以按一个或多个空格分割，
     *    但需注意前导空格会导致分割结果开头产生一个空字符串，需要先 trim()。
     * 3. 得到单词数组后，从后往前遍历拼接，或者用 Collections.reverse()
     *    再 String.join()，效果一样。
     *
     * 【举例】s = "  hello world  "
     *   trim → "hello world"
     *   split("\\s+") → ["hello", "world"]
     *   反转 → ["world", "hello"]
     *   join → "world hello"
     *
     * 【时间复杂度】O(n) 分割和拼接各遍历一次
     * 【空间复杂度】O(n) 存储分割后的单词数组和结果字符串
     */
    public String reverseWordsSplit(String s) {
        String[] words = s.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = words.length - 1; i >= 0; i--) {
            sb.append(words[i]);
            if (i > 0) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

    /**
     * ==================== 解法二：双指针从后往前提取单词 ====================
     *
     * 【核心思路】
     * 不使用 split，而是用双指针从字符串末尾开始扫描，
     * 逐个定位单词的右边界和左边界，提取后追加到结果中。
     *
     * 【思考过程】
     * 1. 从右往左扫描，跳过空格，找到当前单词的右边界 right。
     * 2. 继续往左走直到遇到空格或到达开头，此时左边界 left 确定。
     * 3. 截取 s[left+1 .. right+1) 就是一个完整单词，追加到结果。
     * 4. 重复直到扫描完整个字符串。
     * 5. 这种方法直接控制了空格的处理，不依赖正则或 split 函数。
     *
     * 【举例】s = "  hello world  "
     *   i=14: 空格，跳过
     *   i=13: 空格，跳过
     *   i=12: 'd', right=12
     *   i=11..8: 'l','r','o','w', 继续
     *   i=7: 空格, 提取 s[8..13] = "world"
     *   i=6: 空格，跳过
     *   i=5..1: 'o','l','l','e','h', 继续
     *   i=0: 空格, 提取 s[1..6] = "hello" (实际 i=-1 时提取 s[0..5] = "hello")
     *   结果: "world hello"
     *
     * 【时间复杂度】O(n) 一次遍历
     * 【空间复杂度】O(n) 结果字符串
     */
    public String reverseWordsTwoPointer(String s) {
        StringBuilder sb = new StringBuilder();
        int n = s.length();
        int i = n - 1;

        while (i >= 0) {
            while (i >= 0 && s.charAt(i) == ' ') {
                i--;
            }
            if (i < 0) break;

            int right = i;
            while (i >= 0 && s.charAt(i) != ' ') {
                i--;
            }

            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(s, i + 1, right + 1);
        }

        return sb.toString();
    }

    /**
     * ==================== 解法三：整体翻转 + 逐词翻转 + 清理空格 ====================
     *
     * 【核心思路】
     * 经典的"三步翻转法"：
     * 1. 翻转整个字符串 → 单词顺序反转了，但每个单词内部字符也反了。
     * 2. 翻转每个单词 → 单词内部恢复正常。
     * 3. 清理多余空格 → 去除前后空格，单词间仅保留一个空格。
     *
     * 在 C/C++ 中可以真正原地操作。Java 中 String 不可变，
     * 需要转成 char[] 模拟原地操作，最终仍需 O(n) 空间构造新 String。
     *
     * 【思考过程】
     * 1. "反转单词顺序" 可以拆解为两步操作的叠加：
     *    整体翻转把最后一个单词移到最前面，但单词本身也被翻了；
     *    再逐个单词翻转，就把单词内部恢复正确。
     *
     * 2. 这是一种经典技巧，常用于矩阵旋转、数组循环移位等场景。
     *
     * 3. 多余空格的处理：用一个写指针 write 和读指针 read 做原地去重，
     *    类似于"移除多余空格"的双指针技巧。
     *
     * 【举例】s = "  hello world  "
     *   char[] → ['  hello world  ']
     *   步骤1 整体翻转 → "  dlrow olleh  "
     *   步骤2 逐词翻转 → "  world hello  "
     *   步骤3 清理空格 → "world hello"
     *
     * 【时间复杂度】O(n) 翻转和清理都是线性扫描
     * 【空间复杂度】O(n) Java 中 char[] 和最终 String 各占 O(n)
     */
    public String reverseWordsInPlace(String s) {
        char[] arr = s.toCharArray();
        int n = arr.length;

        reverse(arr, 0, n - 1);

        int start = 0;
        for (int j = 0; j <= n; j++) {
            if (j == n || arr[j] == ' ') {
                reverse(arr, start, j - 1);
                start = j + 1;
            }
        }

        int write = 0;
        for (int j = 0; j < n; j++) {
            if (arr[j] != ' ') {
                if (write > 0) {
                    arr[write++] = ' ';
                }
                while (j < n && arr[j] != ' ') {
                    arr[write++] = arr[j++];
                }
            }
        }

        return new String(arr, 0, write);
    }

    private void reverse(char[] arr, int left, int right) {
        while (left < right) {
            char tmp = arr[left];
            arr[left++] = arr[right];
            arr[right--] = tmp;
        }
    }
}
