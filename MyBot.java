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
        // This is a good place to do computationally expensive start-up pre-processing.
        game.ready("MyJavaBot");

        Log.log("Successfully created bot! My Player ID is " + game.myId + ". Bot rng seed is " + rngSeed + ".");

        for (;;) {
            game.updateFrame();
            final Player me = game.me;
            final GameMap gameMap = game.gameMap;

            final ArrayList<Command> commandQueue = new ArrayList<>();

            for (final Ship ship : me.ships.values()) {
				Log.log("Ship " + ship.id + " has " + ship.halite + " halite!");//idk how logs work??? this doesnt do anything afaik
				if(!shipStatus.containsKey(ship.id)){
					shipStatus.put(ship.id, "exploring"); //if new ship, set exploring
				}
				Log.log("Ship " + ship.id + " is " + shipStatus.get(ship.id));
				if(ship.halite >= Constants.MAX_HALITE/2){
					shipStatus.put(ship.id, "returning"); //if fullish, set returning
				}
				
				
				if(shipStatus.get(ship.id)=="returning"){ 
					if(ship.position.equals( me.shipyard.position)){
						shipStatus.put(ship.id, "exploring"); //if returned, set exploring and go into next if block
						//commandQueue.add(ship.move(Direction.WEST));
					}
					else{
						Direction move = gameMap.naiveNavigate(ship, me.shipyard.position); //if returning, go to shipyard 
						commandQueue.add(ship.move(move));
					}
				}
				
				if(shipStatus.get(ship.id)=="exploring"){
					if (gameMap.at(ship).halite < Constants.MAX_HALITE/10) { //if ur spot is empty, randommove
						Direction move = gameMap.randomNavigate(ship);
						commandQueue.add(ship.move(move));
					} else { //if ur spot has halite, collect
						commandQueue.add(ship.stayStill());
					}
				}
				//shit to do:
				//if exploring, stay in place or move safely....
            }

            if (
                game.turnNumber % 20 == 1 && 
				game.turnNumber <= 200 &&
                me.halite >= 3000 &&
                !gameMap.at(me.shipyard).isOccupied())
            {
                commandQueue.add(me.shipyard.spawn());
            }

            game.endTurn(commandQueue);
        }
    }
}
