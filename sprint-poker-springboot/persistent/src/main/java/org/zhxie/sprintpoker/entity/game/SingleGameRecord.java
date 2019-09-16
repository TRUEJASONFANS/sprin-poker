package org.zhxie.sprintpoker.entity.game;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Map;


@Data
public class SingleGameRecord {
    private String name;
    private String roomNum;
    private Map<String, SingelPlayerScore> player2Score = Maps.newHashMap();

    public void update(SingelPlayerScore singelPlayerScore) {
        if (!singelPlayerScore.getPlayerName().isEmpty() && !player2Score.containsKey(singelPlayerScore.getPlayerName())) {
            player2Score.put(singelPlayerScore.getPlayerName(), singelPlayerScore);
        } else {
            SingelPlayerScore store = player2Score.get(singelPlayerScore.getPlayerName());
            store.setFibonacciNum(singelPlayerScore.getFibonacciNum());
            store.setClicked(singelPlayerScore.isClicked());
        }
    }

    @Data
    public static class SingelPlayerScore {
        private String fibonacciNum = "??";
        private boolean clicked = false;
        private String playerName;
        private String roomName;
        private boolean shown;
    }

    public void removeScoreRecord(String exitUserId) {
        player2Score.remove(exitUserId);
    }

    public SingelPlayerScore GetScore(String playerName) {
        if (!player2Score.containsKey(playerName)) {
            throw new UnsupportedOperationException("Cannot find the player");
        }
        return player2Score.get(playerName);
    }

    public void addScoreRecord(String playerName, String roomName) {
        SingelPlayerScore score = new SingelPlayerScore();
        score.setPlayerName(playerName);
        score.setRoomName(roomName);
        player2Score.put(playerName, score);
    }

    public boolean isAllPlayerClicked() {
        boolean re = true;
        for (SingelPlayerScore p: player2Score.values()) {
            if (!p.isClicked()) {
                re = false;
            }
        }
        return re;
    }

}