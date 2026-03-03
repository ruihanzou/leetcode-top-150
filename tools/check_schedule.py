import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
readme_path = ROOT / "README.md"
plan_path = ROOT / "PLAN_BY_SKILL.md"


def parse_readme_problems():
    text = readme_path.read_text(encoding="utf-8")
    lines = text.splitlines()
    problems = {}
    current_day = None
    for line in lines:
        if line.startswith("### 📅 Day"):
            current_day = line
            continue
        if not line.startswith("|"):
            continue
        # skip header separator rows
        if set(line.strip()) <= {"|", "-", " "}:
            continue
        cols = [c.strip() for c in line.strip().split("|")[1:-1]]
        if len(cols) < 4:
            continue
        num_str, title, difficulty, _tech = cols[:4]
        if not num_str.isdigit():
            continue
        num = int(num_str)
        problems[num] = {
            "title": title,
            "difficulty": difficulty,
            "day_header": current_day,
        }
    return problems


def parse_skill_schedule():
    text = plan_path.read_text(encoding="utf-8")
    lines = text.splitlines()
    schedule = {}
    in_table = False
    for line in lines:
        if "按技能刷题表" in line and line.startswith("##"):
            in_table = False
        if line.startswith("| Day ") and "Problems" in line:
            in_table = True
            continue
        if not in_table:
            continue
        if not line.startswith("|"):
            continue
        # skip header/separator
        if set(line.strip()) <= {"|", "-", " "}:
            continue
        cols = [c.strip() for c in line.strip().split("|")[1:-1]]
        if len(cols) < 3:
            continue
        day_str, _focus, probs_str = cols[0], cols[1], cols[2]
        if not day_str.isdigit():
            continue
        day = int(day_str)
        probs = []
        if probs_str:
            for part in probs_str.split(","):
                part = part.strip().strip("*")  # handle **42** for Hard
                if not part:
                    continue
                if part.isdigit():
                    probs.append(int(part))
        schedule[day] = probs
    return schedule


def main():
    problems = parse_readme_problems()
    schedule = parse_skill_schedule()

    all_ids = set(problems.keys())
    scheduled_ids = {pid for plist in schedule.values() for pid in plist}

    missing = sorted(all_ids - scheduled_ids)
    extra = sorted(scheduled_ids - all_ids)

    print(f"Total problems in README: {len(all_ids)}")
    print(f"Total scheduled entries (with duplicates): {sum(len(v) for v in schedule.values())}")
    print(f"Unique scheduled problems: {len(scheduled_ids)}")
    print()
    print(f"Missing from skill schedule ({len(missing)}): {missing}")
    print(f"Extra (not in README) ({len(extra)}): {extra}")
    print()

    # duplicates
    counts = {}
    for d, plist in schedule.items():
        for pid in plist:
            counts[pid] = counts.get(pid, 0) + 1
    dups = sorted(pid for pid, c in counts.items() if c > 1)
    print(f"Problems appearing more than once in schedule ({len(dups)}): {dups}")
    print()

    # global difficulty distribution
    diff_counts = {"Easy": 0, "Medium": 0, "Hard": 0}
    for meta in problems.values():
        d = meta["difficulty"]
        if d in diff_counts:
            diff_counts[d] += 1
    print("Difficulty distribution in README:", diff_counts)
    print()

    # print details for missing and duplicates
    def describe(pid):
        meta = problems.get(pid, {})
        return f"{pid} ({meta.get('difficulty','?')} - {meta.get('title','?')})"

    print("Missing detail:")
    for pid in missing:
        print("  ", describe(pid))
    print("\nDuplicates detail:")
    for pid in dups:
        print("  ", describe(pid))

    # per-day stats including hard count
    print("Per-day stats:")
    for day in sorted(schedule.keys()):
        plist = schedule[day]
        hard = sum(1 for pid in plist if problems.get(pid, {}).get("difficulty") == "Hard")
        print(f"Day {day:2d}: count={len(plist)}, hard={hard}, problems={plist}")


if __name__ == "__main__":
    main()

