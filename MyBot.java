// This Java API uses camelCase instead of the snake_case as documented in the API docs.
//     Otherwise the names of methods are consistent.

import hlt.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

public class MyBot {
    public static void main(final String[] args) {
        final long rngSeed;
        if (args.length > 1) {
            rngSeed = Integer.parseInt(args[1]);
        } else {
            rngSeed = System.nanoTime();
        }
        final Random rng = new Random(rngSeed);

        Game game = new Game();
		Map<EntityId, String> shipStatus = new HashMap<EntityId, String>();
		
        // At this point "game" variable is populated with initial map data.
        // This is a good place to do computationally expensive start-up pre-processing.
        // As soon as you call "ready" function below, the 2 second per turn timer will start.
        game.ready("MyJavaBot");

        Log.log("Successfully created bot! My Player ID is " + game.myId + ". Bot rng seed is " + rngSeed + ".");

        for (;;) {
            game.updateFrame();
            final Player me = game.me;
            final GameMap gameMap = game.gameMap;

            final ArrayList<Command> commandQueue = new ArrayList<>();

            for (final Ship ship : me.ships.values()) {
				
				if(!shipStatus.containsKey(ship.id)){
					shipStatus.put(ship.id, "exploring");
				}
				if(ship.halite >= Constants.MAX_HALITE/4){
					shipStatus.put(ship.id, "returning");
				}
				else{if(shipStatus.get(ship.id)=="returning"){
					if(ship.position == me.shipyard.position){
						shipStatus.put(ship.id, "exploring");
					}
					else{
						Direction move = gameMap.naiveNavigate(ship, me.shipyard.position);
						commandQueue.add(ship.move(move));
					}
				}}
				
				if(shipStatus.get(ship.id)=="exploring"){
					if ((gameMap.at(ship).halite < Constants.MAX_HALITE/10)) {
						final Direction randomDirection = Direction.ALL_CARDINALS.get(rng.nextInt(4));
						commandQueue.add(ship.move(randomDirection));
						
						//u wanna check all the positions around u to see if they're occupied
						
						
					} else {
						commandQueue.add(ship.stayStill());
					}
				}
				//shit to do:
				//if exploring, stay in place or move safely....
            }

            if (
                game.turnNumber <= 200 &&
                me.halite >= Constants.SHIP_COST &&
                !gameMap.at(me.shipyard).isOccupied())
            {
                commandQueue.add(me.shipyard.spawn());
            }

            game.endTurn(commandQueue);
        }
    }
}
