# SecGM (Secret GameMode)
![Forks](https://img.shields.io/github/forks/KnoxTheDev/SecGM)
![Stars](https://img.shields.io/github/stars/KnoxTheDev/SecGM)
![GitHub Actions](https://github.com/KnoxTheDev/SecGM/workflows/main/badge.svg)
![Latest Release](https://img.shields.io/github/v/release/KnoxTheDev/SecGM)

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
