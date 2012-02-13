package org.glen_h.games.wolfnsheep;

/**
 * An enum representing the game type: <i>SINGLEPLAYER</i> (One player), <i>MULTIPLAYER</i> (Multiple players), <i>MULTIPLAYER_2P</i> (2 players), <i>MULTIPLAYER_3P</i> (3 players), or <i>MULTIPLAYER_4P</i> (4 players).
 * It is preferred to be as specific as possible (Example: If there are going to be 3 players, use <i>MULTIPLAYER_3P</i>, not <i>MULTIPLAYER</i>.
 * In this case, "players" mean human players.
 */
public enum PlayerMode {
		SINGLEPLAYER, MULTIPLAYER, MULTIPLAYER_2P, MULTIPLAYER_3P, MULTIPLAYER_4P
}
