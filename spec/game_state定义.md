# Game State 定义

本文档定义 Rummikub 后端最小实现所需的 `game_state` 格式。`game_state` 是服务端权威状态，用于 Redis/内存中的实时牌局、MySQL 快照、断线恢复和 WebSocket 状态同步。

## 设计原则

- 牌使用 `short` 编号，范围为 `0-105`。
- 桌面使用 `16 * 8` 逻辑网格，服务端只存格子坐标，不存像素坐标。
- 实时状态以服务端为准，客户端只能提交动作，不能直接修改权威状态。
- MySQL 不存每张牌的实时位置，只存对局记录、动作日志和状态快照。
- 摸牌和出牌/重组互斥：玩家本回合要么摸牌并结束回合，要么出牌/重组后提交。
- `turnDraft` 只保存回合开始时的桌面快照，用于重置本回合。

## Tile 编码

一副牌共 106 张：

```text
0-103   普通牌
104     Joker 1
105     Joker 2
```

普通牌编码规则：

```text
tile = copy * 52 + color * 13 + (number - 1)
```

字段含义：

```text
copy   0-1
color  0-3
number 1-13
```

颜色约定：

```text
0 = RED
1 = BLUE
2 = BLACK
3 = ORANGE
```

反解规则：

```text
copy   = tile / 52
color  = (tile % 52) / 13
number = (tile % 13) + 1
```

Joker：

```text
104, 105
```

Joker 的实际代表牌面不写入 tile 本身，由服务端在校验组合时临时推导。

## 桌面网格

桌面固定为 `16 * 8`：

```text
col: 0-15
row: 0-7
```

每个 set 只保存起始位置：

```json
{
  "setId": 1,
  "tiles": [0, 1, 2],
  "pos": [3, 2]
}
```

该 set 占用：

```text
(3,2), (4,2), (5,2)
```

第一版默认所有 set 横向摆放，不需要 `orientation` 字段。

服务端需要校验：

- `pos` 必须在桌面范围内。
- set 占用格子不能超出 `16 * 8`。
- 不同 set 不能占用同一个格子。
- 操作过程中允许出现长度小于 3 的临时 set。
- `COMMIT_TURN` 时所有 set 必须是合法组合。

## 最小 Game State

```json
{
  "gameId": 1001,
  "roomId": 2001,
  "status": "PLAYING",
  "version": 42,
  "tileSchemaVersion": 1,
  "rules": {
    "initialMeldScore": 30,
    "turnTimeSeconds": 60,
    "tilesPerPlayer": 14,
    "jokerCount": 2,
    "tableCols": 16,
    "tableRows": 8
  },
  "players": [
    {
      "userId": 1,
      "seatNo": 1,
      "status": "PLAYING",
      "online": true,
      "handTiles": [0, 13, 52],
      "hasInitialMelded": false
    }
  ],
  "turn": {
    "turnNo": 8,
    "currentUserId": 1,
    "phase": "WAITING_ACTION",
    "startedAt": "2026-05-16T10:00:00+08:00",
    "deadlineAt": "2026-05-16T10:01:00+08:00",
    "actionSeq": 0
  },
  "tileBag": {
    "remainingTiles": [104, 5, 82, 31]
  },
  "table": {
    "sets": [
      {
        "setId": 1,
        "tiles": [0, 1, 2],
        "pos": [3, 2]
      }
    ]
  },
  "turnDraft": null
}
```

## 字段说明

### 根字段

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `gameId` | long | 是 | 对局 ID |
| `roomId` | long | 是 | 房间 ID |
| `status` | string | 是 | 对局状态 |
| `version` | long | 是 | 状态版本号，每次成功动作后递增 |
| `tileSchemaVersion` | int | 是 | 牌编码规则版本，当前为 `1` |
| `rules` | object | 是 | 本局规则快照 |
| `players` | array | 是 | 对局玩家状态 |
| `turn` | object | 是 | 当前回合状态 |
| `tileBag` | object | 是 | 牌堆状态 |
| `table` | object | 是 | 桌面状态 |
| `turnDraft` | object/null | 是 | 当前回合草稿，未开始操作时为 `null` |

### status

```text
INIT
PLAYING
FINISHED
ABORTED
```

### rules

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `initialMeldScore` | int | 首次出牌最低分，默认 `30` |
| `turnTimeSeconds` | int | 每回合秒数 |
| `tilesPerPlayer` | int | 初始手牌数量，默认 `14` |
| `jokerCount` | int | Joker 数量，默认 `2` |
| `tableCols` | int | 桌面列数，固定 `16` |
| `tableRows` | int | 桌面行数，固定 `8` |

规则需要作为快照存在 `game_state` 中，不能只依赖数据库中的规则配置，避免规则配置后续变更影响历史对局恢复。

### players

```json
{
  "userId": 1,
  "seatNo": 1,
  "status": "PLAYING",
  "online": true,
  "handTiles": [0, 13, 52],
  "hasInitialMelded": false
}
```

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `userId` | long | 用户 ID |
| `seatNo` | int | 座位号，用于确定回合顺序 |
| `status` | string | 玩家对局状态 |
| `online` | boolean | 是否在线 |
| `handTiles` | short[] | 玩家手牌，仅服务端完整保存 |
| `hasInitialMelded` | boolean | 是否已经完成首次出牌 |

玩家状态：

```text
PLAYING
LEFT
WINNER
ELIMINATED
```

### turn

```json
{
  "turnNo": 8,
  "currentUserId": 1,
  "phase": "WAITING_ACTION",
  "startedAt": "2026-05-16T10:00:00+08:00",
  "deadlineAt": "2026-05-16T10:01:00+08:00",
  "actionSeq": 0
}
```

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `turnNo` | int | 当前回合号 |
| `currentUserId` | long | 当前行动玩家 |
| `phase` | string | 当前回合阶段 |
| `startedAt` | string | 回合开始时间 |
| `deadlineAt` | string | 回合截止时间 |
| `actionSeq` | int | 当前回合内动作序号 |

回合阶段：

```text
WAITING_ACTION
MANIPULATING
DRAWN
```

阶段含义：

- `WAITING_ACTION`：玩家还未选择摸牌或出牌/重组。
- `MANIPULATING`：玩家已经开始出牌或重组桌面。
- `DRAWN`：玩家已摸牌，通常立即进入下一位玩家回合。

服务端必须保证：

- `WAITING_ACTION` 可以选择摸牌或开始出牌/重组。
- 进入 `MANIPULATING` 后不能再摸牌。
- 摸牌后进入 `DRAWN`，并立即结束回合。

### tileBag

```json
{
  "remainingTiles": [104, 5, 82, 31]
}
```

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `remainingTiles` | short[] | 剩余牌堆，数组顺序就是后续摸牌顺序 |

`remainingTiles` 只能保存在服务端权威状态中，不能广播给普通客户端。

### table

```json
{
  "sets": [
    {
      "setId": 1,
      "tiles": [0, 1, 2],
      "pos": [3, 2]
    }
  ]
}
```

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `sets` | array | 桌面上的所有牌组 |

set 字段：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `setId` | int | 牌组 ID，单局内唯一 |
| `tiles` | short[] | 牌组内的牌，按从左到右顺序排列 |
| `pos` | int[2] | 起始坐标，格式为 `[col, row]` |

`type` 不作为最小状态字段保存。服务端提交时动态判断该 set 是 `RUN`、`GROUP` 还是非法组合。客户端如需展示类型，可以从服务端校验结果或本地推导得到。

### turnDraft

`turnDraft` 只在当前玩家进入 `MANIPULATING` 后创建。

```json
{
  "baseVersion": 42,
  "tableSnapshot": {
    "sets": [
      {
        "setId": 1,
        "tiles": [0, 1, 2],
        "pos": [3, 2]
      }
    ]
  }
}
```

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `baseVersion` | long | 开始操作时的状态版本 |
| `tableSnapshot` | object | 回合开始时的桌面快照 |

`turnDraft` 不保存动作列表，也不保存回合开始手牌。原因是最小规则下摸牌和出牌互斥，`RESET_TURN` 只需要恢复桌面，并把本回合新增到桌面的牌退回当前玩家手牌。

## RESET_TURN 规则

仅 `MANIPULATING` 阶段允许重置。

处理流程：

```text
1. startedTableTiles = flatten(turnDraft.tableSnapshot.sets)
2. currentTableTiles = flatten(table.sets)
3. addedTiles = currentTableTiles - startedTableTiles
4. 当前玩家 handTiles 追加 addedTiles
5. table = turnDraft.tableSnapshot
6. turnDraft = null
7. turn.phase = WAITING_ACTION
8. version += 1
```

因为每张牌都有唯一 `short` 编号，所以差集可以按普通集合计算。

## COMMIT_TURN 规则

仅 `MANIPULATING` 阶段允许提交。

服务端提交时至少校验：

- 当前操作者必须是 `turn.currentUserId`。
- 桌面不能有重叠和越界 set。
- 每个 set 至少 3 张牌。
- 每个 set 必须是合法 `RUN` 或 `GROUP`。
- 本回合必须至少从手牌向桌面新增 1 张牌。
- 如果玩家尚未完成首次出牌，新增牌组成的合法组合总分必须大于等于 `rules.initialMeldScore`。

本回合新增牌计算：

```text
addedTiles = flatten(currentTable.sets) - flatten(turnDraft.tableSnapshot.sets)
```

提交成功后：

```text
turnDraft = null
turn.phase = WAITING_ACTION
turn.currentUserId = nextPlayer
turn.turnNo += 1
turn.actionSeq = 0
version += 1
```

如果当前玩家手牌为空，则对局结束：

```text
status = FINISHED
winnerUserId = currentUserId
```

## DRAW_TILE 规则

仅 `WAITING_ACTION` 阶段允许摸牌。

处理流程：

```text
1. 从 tileBag.remainingTiles 取出下一张牌
2. 加入当前玩家 handTiles
3. turn.phase = DRAWN
4. 立即切换到下一位玩家
5. turn.phase = WAITING_ACTION
6. turnDraft = null
7. version += 1
```

摸牌后本回合不能再出牌或重组。

## 客户端视图裁剪

服务端完整 `game_state` 不能原样广播给所有客户端。

发给当前玩家：

- 可以包含自己的 `handTiles`。
- 不能包含 `tileBag.remainingTiles`。
- 其他玩家只返回 `handTileCount`，不返回 `handTiles`。

发给其他玩家：

- 不返回当前玩家的 `handTiles`。
- 不返回任何人的完整手牌。
- 不返回 `tileBag.remainingTiles`。

建议后端提供两个转换方法：

```java
PlayerGameView toPlayerView(GameState state, Long viewerUserId);
PublicGameView toPublicView(GameState state);
```

玩家视图片段示例：

```json
{
  "gameId": 1001,
  "roomId": 2001,
  "status": "PLAYING",
  "version": 42,
  "rules": {
    "initialMeldScore": 30,
    "turnTimeSeconds": 60,
    "tableCols": 16,
    "tableRows": 8
  },
  "players": [
    {
      "userId": 1,
      "seatNo": 1,
      "online": true,
      "handTiles": [0, 13, 52],
      "handTileCount": 3,
      "hasInitialMelded": false
    },
    {
      "userId": 2,
      "seatNo": 2,
      "online": true,
      "handTileCount": 14,
      "hasInitialMelded": false
    }
  ],
  "turn": {
    "turnNo": 8,
    "currentUserId": 1,
    "phase": "WAITING_ACTION",
    "deadlineAt": "2026-05-16T10:01:00+08:00"
  },
  "table": {
    "sets": [
      {
        "setId": 1,
        "tiles": [0, 1, 2],
        "pos": [3, 2]
      }
    ]
  },
  "tileBagCount": 42
}
```

## 最小实现可以暂缓的字段

以下字段第一版可以不放入 `game_state`：

- `tiles` 映射表：牌面由 `tileSchemaVersion` 和编码规则推导。
- set `type`：提交时动态计算。
- set `orientation`：第一版固定横向。
- `drawnTiles`：不影响最小规则和恢复。
- 动作列表：由 `game_action_logs` 持久化，不放在 `turnDraft`。
- 每张牌的位置：桌面只记录 set 起点和 set 内顺序。
