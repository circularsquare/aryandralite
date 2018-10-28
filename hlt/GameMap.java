package hlt;

import java.util.ArrayList;
import java.util.Random;

public class GameMap {
    public final int width;
    public final int height;
    public final MapCell[][] cells;
	final Random rng;

    public GameMap(final int width, final int height) {
        this.width = width;
        this.height = height;

        cells = new MapCell[height][];
        for (int y = 0; y < height; ++y) {
            cells[y] = new MapCell[width];
        }
		
		
		final long rngSeed;  //copied and pasted from mybot
        rngSeed = System.nanoTime();
        rng = new Random(rngSeed);
    }

    public MapCell at(final Position position) {
        final Position normalized = normalize(position);
        return cells[normalized.y][normalized.x];
    }

    public MapCell at(final Entity entity) {
        return at(entity.position);
    }

    public int calculateDistance(final Position source, final Position target) {
        final Position normalizedSource = normalize(source);
        final Position normalizedTarget = normalize(target);

        final int dx = Math.abs(normalizedSource.x - normalizedTarget.x);
        final int dy = Math.abs(normalizedSource.y - normalizedTarget.y);

        final int toroidal_dx = Math.min(dx, width - dx);
        final int toroidal_dy = Math.min(dy, height - dy);

        return toroidal_dx + toroidal_dy;
    }

    public Position normalize(final Position position) {
        final int x = ((position.x % width) + width) % width;
        final int y = ((position.y % height) + height) % height;
        return new Position(x, y);
    }

    public ArrayList<Direction> getUnsafeMoves(final Position source, final Position destination) { //this function says which way to go to get to dest
        final ArrayList<Direction> possibleMoves = new ArrayList<>();

        final Position normalizedSource = normalize(source);
        final Position normalizedDestination = normalize(destination);

        final int dx = Math.abs(normalizedSource.x - normalizedDestination.x);
        final int dy = Math.abs(normalizedSource.y - normalizedDestination.y);
        final int wrapped_dx = width - dx;
        final int wrapped_dy = height - dy;
		
        if (normalizedSource.x < normalizedDestination.x) {
            possibleMoves.add(dx > wrapped_dx ? Direction.WEST : Direction.EAST);
        } else if (normalizedSource.x > normalizedDestination.x) {
            possibleMoves.add(dx < wrapped_dx ? Direction.WEST : Direction.EAST);
        }

        if (normalizedSource.y < normalizedDestination.y) {
            possibleMoves.add(dy > wrapped_dy ? Direction.NORTH : Direction.SOUTH);
        } else if (normalizedSource.y > normalizedDestination.y) {
            possibleMoves.add(dy < wrapped_dy ? Direction.NORTH : Direction.SOUTH);
        }
        return possibleMoves;
    }

    public Direction naiveNavigate(final Ship ship, final Position destination) {
        // getUnsafeMoves normalizes for us
        for (final Direction direction : getUnsafeMoves(ship.position, destination)) {
            final Position targetPos = ship.position.directionalOffset(direction);
            if (!at(targetPos).isOccupied()) {
                at(targetPos).markUnsafe(ship);
				//at(ship).markSafe();
				return direction;
            }
        }
        return Direction.STILL;
    }
	
	public Direction randomNavigate(final Ship ship){
		//for (final Direction direction : Direction.ALL_CARDINALS.get(rng.nextInt(4))){}
		int tryLast = rng.nextInt(4);
		int i = (tryLast+1)%4;
		while (i != tryLast){
			Direction direction = Direction.ALL_CARDINALS.get(i);
			final Position targetPos = ship.position.directionalOffset(direction);
			if (!(at(targetPos).isOccupied())){
				at(targetPos).markUnsafe(ship);
				//at(ship).markSafe();
				return direction;
			}
			i = (i+1)%4;
		}
		return Direction.STILL;
		//this returns a direction to go into at random
	}
	public Direction randomNavigate(final Ship ship, Direction d1, Direction d2){
		//for (final Direction direction : Direction.ALL_CARDINALS.get(rng.nextInt(4))){}
		if (rng.nextInt(2) == 0) {
			final Position targetPos = ship.position.directionalOffset(d1);
			if(!(at(targetPos).isOccupied())){
				at(targetPos).markUnsafe(ship);
				return d1;
			}
		}
		else {
			final Position targetPos = ship.position.directionalOffset(d2);
			if(!(at(targetPos).isOccupied())){
				at(targetPos).markUnsafe(ship);
				return d2;
			}
		}
		int tryLast = rng.nextInt(4);
		int i = (tryLast+1)%4;
		while (i != tryLast){
			Direction direction = Direction.ALL_CARDINALS.get(i);
			final Position targetPos = ship.position.directionalOffset(direction);
			if (!(at(targetPos).isOccupied())){
				at(targetPos).markUnsafe(ship);
				//at(ship).markSafe();
				return direction;
			}
			i = (i+1)%4;
		}
		return Direction.STILL;
		//this returns a direction to go into at random
	}
	

    void _update() {
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                cells[y][x].ship = null;
            }
        }

        final int updateCount = Input.readInput().getInt();

        for (int i = 0; i < updateCount; ++i) {
            final Input input = Input.readInput();
            final int x = input.getInt();
            final int y = input.getInt();

            cells[y][x].halite = input.getInt();
        }
    }

    static GameMap _generate() {
        final Input mapInput = Input.readInput();
        final int width = mapInput.getInt();
        final int height = mapInput.getInt();

        final GameMap map = new GameMap(width, height);

        for (int y = 0; y < height; ++y) {
            final Input rowInput = Input.readInput();

            for (int x = 0; x < width; ++x) {
                final int halite = rowInput.getInt();
                map.cells[y][x] = new MapCell(new Position(x, y), halite);
            }
        }

        return map;
    }
}
