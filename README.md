# MineCodeCraft-Mod

Minecodecraft服务端专用MOD

当前分支`1.21`适配版本：**Minecraft 1.21**

## 功能

- `/minecodecraft creeperExplosion`苦力怕防爆(与`mobGriefing`不冲突)
- `/home` `/back`传送功能
- `/here` `/where`位置共享功能
- `/save`自定义存档
- 自定义服务器登录欢迎信息，开服时间统计
- 定时服务器轮播消息
- 同原版数据一同存储扩展用户数据，支持用户在线时间，累计挖掘和历史上线时间统计
- 接入分布式消息系统[NATS](https://nats.io/)，可供透传服务器运行信息与事件

## 指令

### 苦力怕防爆

- **需要OP权限**

单独打开/关闭苦力怕破坏方块能力，默认为关闭破坏`false`，无需再调整`mobGriefing`规则

`/minecodecraft creeperExplosion [true|false]`

- 与`mobGriefing`游戏规则共同作用，在`mobGriefing`为`false`时，所有除玩家外的生物均无法交互

### 传送能力

传送回家

`/home`

`/minecodecraft home`

- 首次使用需要在配置文件中配置`tpPlayer.homePos`

返回上一个位置

`/back`

`/minecodecraft back`

- 上一位置包括每次传送前的位置，以及死亡重生前的位置
- 重新登录也会导致历史记录被覆盖
- 服务端在每次重启后记录会被清除

### 位置共享

向全服玩家共享你的位置

`/here`

`/minecodecraft here`

- 你会被带上`高亮`的药水效果一段时间，并向全服玩家发送你的坐标

向一个玩家请求共享位置

`/where [player]`

`/minecodecraft where [player]`

- 被邀请的玩家会收到消息，可以选择是否在一定时间内通过`/here`共享位置，超时双方会受到拒绝消息
- OP无需对方玩家同意即可立即生效

### 玩家数据

- 需要OP权限

可以查看其他在线玩家的用户数据，其格式与MOTD展示内容相同

`/minecodecraft player [player] info`

### 自定义存档

- **需要OP权限**

当配置项`worldAutoSaveInterval`为非`0`值时，将会关闭所有世界的游戏内自动存档，由MOD接管存档时机，否则，指令仍能执行但是可能不会有效果

`/minecodecraft save` 手动存档

## NATS

事件主题格式为：`[nats.prefix].[event_type]`，数据格式为JSON，事件类型`event_type`见下文

- 每个类型的事件必含`time`字段，为事件生成的时间，格式为RFC3339，形如`2024-06-24T18:07:16+08:00`，下文表格将不再包含

### 服务器生命周期事件(serverAction)

| 属性   | 值类型 | 说明                                            |
| ------ | ------ | ----------------------------------------------- |
| action | string | 服务器启动状态，`lunch`为启动中，`stop`为停止中 |

### 玩家事件(playerAction)

| 属性       | 值类型 | 说明                                                         |
| ---------- | ------ | ------------------------------------------------------------ |
| action     | string | 玩家事件类型，`join`为玩家加入，`disconnect`为玩家退出，`saving`为每次保存玩家数据时触发 |
| name       | string | 玩家显示名称                                                 |
| uuid       | string | 玩家UUID                                                     |
| ip         | string | 玩家的客户端IP                                               |
| onlineTime | int    | 玩家的累计在线时间                                           |
| blockBreak | int    | 玩家的累计方块破坏数                                         |

- 对于`saving`玩家事件，并不意味着玩家此刻离线，服务端会每隔一段时间自动保存一次在线玩家的数据。特别地，因为玩家在触发`disconnect`时会进行保存，因此也会同时触发`saving`事件

## 配置

所有配置项在`config/minecodecraft.json`

`/minecodecraft config [save|reload]` 保存运行时配置至文件/从配置文件重载配置，**需要OP权限**

| 配置项                        | 值类型        | 说明                                                    |
| ----------------------------- | ------------- | ------------------------------------------------------- |
| `gameRule.creeperExplosion`   | bool          | 允许苦力怕破坏方块                                      |
| `tpPlayer.interval`           | int           | 玩家传送等待时间，单位为秒                              |
| `tpPlayer.homePos`            | Object(x,y,z) | 家的坐标                                                |
| `serverName`                  | string        | 服务器名称                                              |
| `lunchTime`                   | string        | 开服时间，格式为yyyy-mm-dd                              |
| `nats.server`                 | string        | 消息队列系统NATS的连接地址，格式为`nats://address:port` |
| `nats.prefix`                 | string        | NATS消息主题前缀，默认为`minecodecraft`                 |
| `tips.interval`               | int           | 全服轮播消息间隔                                        |
| `tips.tips`                   | []string      | 全服轮播消息                                            |
| `notice`                      | []string      | 登录欢迎消息                                            |
| `worldAutoSaveInterval`       | int           | 自动保存间隔，非`0`时生效，单位为秒                     |
| `playerHereGlowingTime`       | int           | 玩家位置共享高亮时间，单位为秒                          |
| `playerWhereRequestExpire`    | int           | 位置共享请求超时时间，单位为秒                          |
| `playerLatencyUpdateInterval` | int           | 玩家延迟检测间隔，单位为秒                              |
| `copyRight`                   | bool          | 展示MOD信息                                             |

