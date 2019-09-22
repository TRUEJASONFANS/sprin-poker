package org.zhxie.sprintpoker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zhxie.sprintpoker.entity.Room;
import org.zhxie.sprintpoker.entity.game.SingleGameRecord;
import org.zhxie.sprintpoker.entity.dto.GameDTO;
import org.zhxie.sprintpoker.exception.CommandException;
import org.zhxie.sprintpoker.repository.SocketSessionRegistry;

import java.security.Principal;
import java.util.List;

@Controller
public class RoomController {

  @Autowired
  SocketSessionRegistry webAgentSessionRegistry;

  @MessageMapping("/rooms")
  @SendTo("/pocker/rooms")
  public List<Room> getRoomList(Principal user) {
    return webAgentSessionRegistry.getRooms();
  }

  @MessageMapping("/addRoom")
  @SendTo("/pocker/rooms")
  public List<Room> addRoom(Room room) {
    webAgentSessionRegistry.createRoom(room);
    return webAgentSessionRegistry.getRooms();
  }

  @MessageMapping("/joinPockerBoard/{roomName}/{curPage}")
  @SendTo("/pocker/pockerBoard/{roomName}")
  public GameDTO joinPockerBoardByRoomId(Principal user, @DestinationVariable String roomName, @DestinationVariable Integer curPage) throws
          CommandException {
    //TODO: join the room and remember websocket session id
    System.out.println(user.getName());
    if (!webAgentSessionRegistry.isInRoom(roomName, user.getName())) {
      webAgentSessionRegistry.joinRoom(roomName, user.getName());
    }
    return webAgentSessionRegistry.getSingleGameRecord(roomName, curPage);
  }

  @MessageMapping("/onClickPocker/{roomName}/{curPage}")
  @SendTo("/pocker/pockerBoard/{roomName}")
  public GameDTO onClickPocker(Principal user, SingleGameRecord.SingelPlayerScore singelPlayerScore, @DestinationVariable String
          roomName, @DestinationVariable Integer curPage) {
    System.out.println(user.getName());
    singelPlayerScore.setPlayerName(user.getName());
    webAgentSessionRegistry.updateRoomScoreByPlayer(singelPlayerScore, roomName, curPage);
    return webAgentSessionRegistry.getSingleGameRecord(roomName, curPage);
  }

  @MessageMapping("/onNextGame/{roomName}/{curPage}")
  @SendTo("/pocker/pockerBoard/{roomName}")
  public GameDTO onNextGame(Principal user, @DestinationVariable String roomName, @DestinationVariable Integer curPage) {
    webAgentSessionRegistry.onNextGame(user.getName(), roomName, curPage);
    final GameDTO singleGameRecord = webAgentSessionRegistry.getSingleGameRecord(roomName, curPage);
    singleGameRecord.setReset(true);
    return singleGameRecord;
  }

  @MessageMapping("/onAddStory/{roomName}")
  @SendTo("/pocker/pockerBoard/{roomName}")
  public GameDTO onAddStory(@RequestBody String roomName, @RequestBody Integer curPage, @RequestBody Integer totalPage) {
      webAgentSessionRegistry.onAddStory(roomName, totalPage);
      return webAgentSessionRegistry.getSingleGameRecord(roomName, curPage);
  }


}
