package dk.sdu.mmmi.cbse.asteroid;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.LifePart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.ProjectilePart;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class AsteroidProcessor implements IEntityProcessingService {

    @Override
    public void process(GameData gameData, World world) {

        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            PositionPart positionPart = asteroid.getPart(PositionPart.class);
            ProjectilePart projectilePart = asteroid.getPart(ProjectilePart.class);
            LifePart lifePart = asteroid.getPart(LifePart.class);

            int numPoints = 5;

            projectilePart.process(gameData, asteroid);
            positionPart.process(gameData, asteroid);

            if (lifePart.isIsHit()) {
                createSplitAsteroid(asteroid, world);
            }
            setShape(asteroid, numPoints);
        }
    }

    private void setShape(Entity entity, int numPoints) {
        PositionPart position = entity.getPart(PositionPart.class);
        float[] shapex = new float[numPoints];
        float[] shapey = new float[numPoints];
        float radians = position.getRadians();
        float x = position.getX();
        float y = position.getY();
        float radius = entity.getRadius();

        float angle = 0;

        for (int i = 0; i < numPoints; i++) {
            shapex[i] = x + (float) Math.cos(angle + radians) * radius;
            shapey[i] = y + (float) Math.sin(angle + radians) * radius;
            angle += 2 * 3.1415f / numPoints;
        }

        entity.setShapeX(shapex);
        entity.setShapeY(shapey);
    }

    public void createSplitAsteroid(Entity e, World world) {
        PositionPart positionPart = e.getPart(PositionPart.class);
        LifePart lifePart = e.getPart(LifePart.class);
        float radians = positionPart.getRadians();
        int radius = 0;
        float speed = 5;
        int life = lifePart.getLife() - 1;
        if (life == 1) {
            radius = 6;
            speed = (float) Math.random() * 30f + 70f;
        } else if (life == 2) {
            radius = 10;
            speed = (float) Math.random() * 10f + 50f;
        } else if (life <= 0) {
            world.removeEntity(e);
            return;
        }

        Entity asteroid1 = new Asteroid();

        asteroid1.setRadius(radius);
        float radians1 = radians - 0.5f;

        float by1 = (float) sin(radians1) * e.getRadius();
        float bx1 = (float) cos(radians1) * e.getRadius();

        PositionPart astPositionPart1 = new PositionPart(positionPart.getX() + bx1, positionPart.getY() + by1, radians1);
        asteroid1.add(new ProjectilePart(speed, radians1));
        asteroid1.add(astPositionPart1);
        asteroid1.add(new LifePart(life, 0));

        world.addEntity(asteroid1);

        Entity asteroid2 = new Asteroid();

        asteroid2.setRadius(radius);
        float radians2 = radians + 0.5f;

        float by2 = (float) sin(radians2) * e.getRadius();
        float bx2 = (float) cos(radians2) * e.getRadius();
        PositionPart astPositionPart2 = new PositionPart(positionPart.getX() + bx2, positionPart.getY() + by2, radians2);

        asteroid2.add(new ProjectilePart(speed, radians2));
        asteroid2.add(astPositionPart2);
        asteroid2.add(new LifePart(life, 0));

        world.addEntity(asteroid2);

        world.removeEntity(e);
    }
}
