# 🎣 Fishing Event Plugin

A lightweight Minecraft plugin for running fishing competitions with a live leaderboard, PlaceholderAPI support, and customizable scoring.

---

## 📜 Features
- Start and stop timed fishing events.
- Tracks each player's total fish count and score.
- Live boss bar countdown.
- **Top 5 leaderboard placeholders** for use in holograms, scoreboards, etc.
- Compatible with **Paper/Spigot** servers.

---

## ⌨ Commands

| Command                | Description                             | Permission        |
|------------------------|-----------------------------------------|-------------------|
| `/fishingstart <time>` | Start a fishing event for `<time>` seconds | `fishingevent.start` |
| `/fishingstop`         | Stop the current fishing event          | `fishingevent.stop`  |

---

## 🏷 Placeholders

Requires **[PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)**.

| Placeholder                       | Description                          |
|-----------------------------------|--------------------------------------|
| `%fishingevent_top_name_1%`       | Name of player in 1st place           |
| `%fishingevent_top_score_1%`      | Score of player in 1st place          |
| `%fishingevent_top_fish_1%`       | Fish count of player in 1st place     |
| `%fishingevent_top_name_2%`       | Name of player in 2nd place           |
| `%fishingevent_top_score_2%`      | Score of player in 2nd place          |
| `%fishingevent_top_fish_2%`       | Fish count of player in 2nd place     |
| `%fishingevent_top_name_3%`       | Name of player in 3rd place           |
| `%fishingevent_top_score_3%`      | Score of player in 3rd place          |
| `%fishingevent_top_fish_3%`       | Fish count of player in 3rd place     |
| `%fishingevent_top_name_4%`       | Name of player in 4th place           |
| `%fishingevent_top_score_4%`      | Score of player in 4th place          |
| `%fishingevent_top_fish_4%`       | Fish count of player in 4th place     |
| `%fishingevent_top_name_5%`       | Name of player in 5th place           |
| `%fishingevent_top_score_5%`      | Score of player in 5th place          |
| `%fishingevent_top_fish_5%`       | Fish count of player in 5th place     |

> **Note:** If there is no player in that rank, the placeholder will return `---`.

---

## 🖥 Supported Server Types
- **Paper** (recommended)
- **Spigot**
- **Purpur** (untested but should work)

---

## 📦 Supported Versions
- **Minecraft 1.21.7**
- Should work on **1.20+** (not tested on earlier versions)

---

## ⚙ Installation
1. Download the `.jar` and place it in your `plugins` folder.
2. Install **PlaceholderAPI** if you want placeholders.
3. Restart your server.
4. Use `/fishingstart` to start an event and enjoy!

---

## 📜 License
This project is free to use and modify for your server.
