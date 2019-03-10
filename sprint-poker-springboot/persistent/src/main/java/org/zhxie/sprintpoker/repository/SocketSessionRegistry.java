package org.zhxie.sprintpoker.repository;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.zhxie.sprintpoker.entity.Player;
import org.zhxie.sprintpoker.entity.Room;
import org.zhxie.sprintpoker.exception.CommandException;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class SocketSessionRegistry {

  //this map save every session
  //这个集合存储session
  private final ConcurrentMap<String, Set<String>> userSessionIds = new ConcurrentHashMap();
  private final Object lock = new Object();
  private static final Map<String, Room> roomId2Room = Maps.newHashMap();

  public SocketSessionRegistry() {
  }

  /**
   * 获取所有session
   *
   * @return
   */
  public ConcurrentMap<String, Set<String>> getAllSessionIds() {
    return this.userSessionIds;
  }

  /**
   * register session
   *
   * @param user
   * @param sessionId
   */
  public void registerSessionId(String user, String sessionId) {
    Assert.notNull(user, "User must not be null");
    Assert.notNull(sessionId, "Session ID must not be null");
    Object var3 = this.lock;
    synchronized (this.lock) {
      Object set = (Set) this.userSessionIds.get(user);
      if (set == null) {
        set = new CopyOnWriteArraySet();
        this.userSessionIds.put(user, (Set<String>) set);
      }

      ((Set) set).add(sessionId);
    }
  }

  public void unregisterSessionId(String userName, String sessionId) {
    Assert.notNull(userName, "User Name must not be null");
    Assert.notNull(sessionId, "Session ID must not be null");
    Object var3 = this.lock;
    synchronized (this.lock) {
      Set set = (Set) this.userSessionIds.get(userName);
      if (set != null && set.remove(sessionId) && set.isEmpty()) {
        this.userSessionIds.remove(userName);
      }

    }
  }

  public List<Room> getRooms() {
    return Lists.newArrayList(roomId2Room.values());
  }

  public List<Player> getPlayersByRoomID(String roomID) {
    return roomId2Room.get(roomID).getPlayer();
  }

  public void joinRoom(String roomID, String userId) {
    Player player = new Player(userId);

    if (roomId2Room.containsKey(roomID)) {
      roomId2Room.get(roomID).add(player);
    } else {
      Room room = new Room();
      room.add(player);
      roomId2Room.put(roomID, room);
    }
  }

  public String getUserIdBySessionId(String sessionID) throws CommandException {
    String userId = "";
    for (Map.Entry<String, Set<String>> user2SessionID : getAllSessionIds().entrySet()) {
      if (user2SessionID.getValue().contains(sessionID)) {
        userId = user2SessionID.getKey();
      }
    }
    if (!Strings.isNullOrEmpty(userId)) {
      return userId;
    }
    throw new CommandException();
  }

  public boolean isInRoom(String roomName, String userId) {
    return roomId2Room.get(roomName).hasPlayer(userId);
  }

  public void createRoom(Room room) {
    room.setRoomNum(String.valueOf(roomId2Room.size()));
    roomId2Room.put(room.getName(), room);
  }

  public void updateRoomScoreByPlayer(Player player, String roomName) {
    List<Player> players = roomId2Room.get(roomName).getPlayer();
    for (Player p : players) {
      if (p.getName().equals(player.getName())) {
        p.setFibonacciNum(player.getFibonacciNum());
      }
    }
  }

  public List<Room> removeUser(String userName) {
    synchronized (this.lock) {
      List<Room> updateRooms = Lists.newArrayList();
      userSessionIds.remove(userName);
      Room emtpyRoom = null;
      for (Room room : roomId2Room.values()) {
        if (room.hasPlayer(userName)) {
          updateRooms.add(room);
        }
        room.removePlayer(userName);
      }
      Iterator<Map.Entry<String, Room>> iterator = roomId2Room.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry<String, Room> next = iterator.next();
        if (next.getValue().isEmpty()) {
          iterator.remove();
        }
      }
      return updateRooms;
    }
  }

  public boolean hasRoom(String roomName) {
    return roomId2Room.values().stream().filter(r-> r.getName().equalsIgnoreCase(roomName)).findAny().isPresent();
  }
}
