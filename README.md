# SecGM (Secret GameMode)
### General Badges

- ![License](https://badgen.net/github/license/KnoxTheDev/SecGM)
- ![Watchers](https://badgen.net/github/watchers/KnoxTheDev/SecGM)
- ![Forks](https://badgen.net/github/forks/KnoxTheDev/SecGM)
- ![Stars](https://badgen.net/github/stars/KnoxTheDev/SecGM)
- ![Contributors](https://badgen.net/github/contributors/KnoxTheDev/SecGM)
- ![Last Commit](https://badgen.net/github/last-commit/KnoxTheDev/SecGM)

### Branches and Releases

- ![Branches](https://badgen.net/github/branches/KnoxTheDev/SecGM)
- ![Releases](https://badgen.net/github/releases/KnoxTheDev/SecGM)
- ![Tags](https://badgen.net/github/tags/KnoxTheDev/SecGM)
- ![Latest Tag](https://badgen.net/github/tag/KnoxTheDev/SecGM)
- ![Latest Release](https://badgen.net/github/release/KnoxTheDev/SecGM)
- ![Stable Release](https://badgen.net/github/release/KnoxTheDev/SecGM/stable)

### Commits and Checks

- ![Commits](https://badgen.net/github/commits/KnoxTheDev/SecGM)
- ![Checks](https://badgen.net/github/checks/KnoxTheDev/SecGM)
- ![Build Status](https://badgen.net/github/checks/KnoxTheDev/SecGM/main/build)

### Issues and Pull Requests

- ![Issues](https://badgen.net/github/issues/KnoxTheDev/SecGM)
- ![Open Issues](https://badgen.net/github/open-issues/KnoxTheDev/SecGM)
- ![Closed Issues](https://badgen.net/github/closed-issues/KnoxTheDev/SecGM)
- ![Pull Requests](https://badgen.net/github/prs/KnoxTheDev/SecGM)
- ![Open PRs](https://badgen.net/github/open-prs/KnoxTheDev/SecGM)
- ![Closed PRs](https://badgen.net/github/closed-prs/KnoxTheDev/SecGM)
- ![Merged PRs](https://badgen.net/github/merged-prs/KnoxTheDev/SecGM)

### Milestones and Labels

- ![Milestones](https://badgen.net/github/milestones/KnoxTheDev/SecGM/1)
- ![Label Issues](https://badgen.net/github/label-issues/KnoxTheDev/SecGM/help-wanted/open)

### Downloads and Dependencies

- ![Assets Downloads](https://badgen.net/github/assets-dl/KnoxTheDev/SecGM)
- ![Dependents](https://badgen.net/github/dependents-repo/KnoxTheDev/SecGM)
- ![Dependabot Status](https://badgen.net/github/dependabot/KnoxTheDev/SecGM)

SecGM is a Minecraft Fabric mod that adds a private `/secgm` command, allowing players to switch between game modes (Survival, Creative, Adventure, Spectator) using numbers (`0`, `1`, `2`, `3`) without broadcasting their actions to the server or other players. This mod is ideal for players who want to change game modes discreetly.

## Features

- Switch between game modes using numbers:
  - `0` - Survival
  - `1` - Creative
  - `2` - Adventure
  - `3` - Spectator
- Commands are executed without broadcasting to the server.
- Lightweight and easy to install.

## Supported Minecraft Versions

- **1.18.x**
- **1.19.x**
- **1.20.x**
- **1.21.x**

## Requirements

- Fabric Loader (version compatible with the Minecraft versions listed)
- Fabric API (version compatible with the Minecraft versions listed)
- Java 17 or higher

## Installation

1. Ensure you have the Fabric Loader and Fabric API installed.
2. Download the latest version of the SecGM mod from the [releases page](https://github.com/KnoxTheDev/SecGM/releases).
3. Place the downloaded `.jar` file in the `mods` folder of your Minecraft directory.

## Usage

### Commands

The mod introduces the `/secgm` command, which allows players to switch between game modes using the corresponding numbers:

- `/secgm 0` - Switch to Survival mode.
- `/secgm 1` - Switch to Creative mode.
- `/secgm 2` - Switch to Adventure mode.
- `/secgm 3` - Switch to Spectator mode.

### Example

To switch to Creative mode, use:

```plaintext
/secgm 1
