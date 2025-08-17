# 🎣 FishingEvent Plugin

A lightweight and customizable **Fishing Event** plugin for Minecraft (Spigot/Paper).  
Supports **custom scoring per fish type**, leaderboard placeholders, and easy commands to control events.

---

## 📌 Features
- Start and stop timed fishing events.
- BossBar countdown for active events.
- Leaderboard system with **PlaceholderAPI** support.
- Configurable **fish scoring per type** (`config.yml`).
- Reset leaderboard with a simple command.
- Works with **DecentHolograms** (via placeholders).

---

## ⚙️ Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/fishingstart <seconds>` | Starts a fishing event for the given time. | `fishingevent.start` |
| `/fishingstop` | Stops the current fishing event. | `fishingevent.stop` |
| `/fishingreset` | Resets the leaderboard stats. | `fishingevent.reset` |

---

## 🪝 Placeholders
Available via **PlaceholderAPI**:

| Placeholder | Description |
|-------------|-------------|
| `%fishingevent_top_name_<n>%` | Name of the player in rank `<n>` (1–3 by default). |
| `%fishingevent_top_score_<n>%` | Score of the player in rank `<n>`. |
| `%fishingevent_top_fish_<n>%` | Total fish caught by the player in rank `<n>`. |

> If a rank does not exist, the placeholder returns `§7---`.

---

## 🐟 Config (config.yml)
When first run, the plugin generates `plugins/FishingEvent/config.yml`.  
Here you can define **custom points per fish type**:

```yaml
fish-scores:
  COD: 1
  SALMON: 2
  TROPICAL_FISH: 3
  PUFFERFISH: 4
  # Add more fish types with custom scores
