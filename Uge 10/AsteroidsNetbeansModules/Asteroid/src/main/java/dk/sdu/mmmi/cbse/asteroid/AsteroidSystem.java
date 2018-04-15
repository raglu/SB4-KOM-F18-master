package dk.sdu.mmmi.cbse.asteroid;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.LifePart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.data.entityparts.ProjectilePart;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

@ServiceProviders(value = {
    @ServiceProvider(service = IEntityProcessingService.class)
    ,
    @ServiceProvider(service = IGamePluginService.class)}
)
public class AsteroidSystem implements IEntityProcessingService, IGamePluginService {

    @Override
    public void start(GameData gameData, World world) {
        Entity asteroid = createAsteroid(gameData);
        world.addEntity(asteroid);
    }

    @Override
    public void process(GameData gameData, World world) {

        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            PositionPart positionPart = asteroid.getPart(PositionPart.class);
            ProjectilePart projectilePart = asteroid.getPart(ProjectilePart.class);
            LifePart lifePart = asteroid.getPart(LifePart.class);

            projectilePart.process(gameData, asteroid);
            positionPart.process(gameData, asteroid);

            if (lifePart.isHit()) {
                createSplitAsteroid(asteroid, world);
                world.removeEntity(asteroid);
            }
            updateShape(asteroid);
        }
    }

    @Override
    public void stop(GameData gameData, World world) {
        // Remove entities
        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            world.removeEntity(asteroid);
        }
    }

    private void createSplitAsteroid(Entity e, World world) {
        PositionPart otherPos = e.getPart(PositionPart.class);
        LifePart otherLife = e.getPart(LifePart.class);
        float radians = otherPos.getRadians();
        int radius = 0;
        float speed = 5;
        int life = otherLife.getLife() - 1;
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

        float by1 = (float) sin(radians1) * (e.getRadius() + asteroid1.getRadius());
        float bx1 = (float) cos(radians1) * (e.getRadius() + asteroid1.getRadius());

        PositionPart astPositionPart1 = new PositionPart(otherPos.getX() + bx1, otherPos.getY() + by1, radians1);
        asteroid1.add(new ProjectilePart(speed, radians1));
        asteroid1.add(astPositionPart1);
        asteroid1.add(new LifePart(life));

        world.addEntity(asteroid1);

        Entity asteroid2 = new Asteroid();

        asteroid2.setRadius(radius);
        float radians2 = radians + 0.5f;

        float by2 = (float) sin(radians2) * (e.getRadius() + asteroid2.getRadius());
        float bx2 = (float) cos(radians2) * (e.getRadius() + asteroid2.getRadius());

        PositionPart astPositionPart2 = new PositionPart(otherPos.getX() + bx2, otherPos.getY() + by2, radians2);
        asteroid2.add(new ProjectilePart(speed, radians2));
        asteroid2.add(astPositionPart2);
        asteroid2.add(new LifePart(life));

        world.addEntity(asteroid2);

        world.removeEntity(e);
    }

    private Entity createAsteroid(GameData gameData) {
        Entity asteroid = new Asteroid();
        float x = gameData.getDisplayWidth() / 4 * 3;
        float y = gameData.getDisplayHeight() / 2;
        float radians = (float) Math.random() * 2 * 3.1415f;
        float speed = (float) Math.random() * 10f + 20f;

        asteroid.setRadius(20);
        asteroid.add(new ProjectilePart(speed, radians));
        asteroid.add(new PositionPart(x, y, radians));
        asteroid.add(new LifePart(3));

        return asteroid;
    }

    private void updateShape(Entity entity) {
        PositionPart position = entity.getPart(PositionPart.class);
        float[] shapex = new float[5];
        float[] shapey = new float[5];
        float radians = position.getRadians();
        float x = position.getX();
        float y = position.getY();
        float radius = entity.getRadius();

        float angle = 0;

        for (int i = 0; i < 5; i++) {
            shapex[i] = x + (float) Math.cos(angle + radians) * radius;
            shapey[i] = y + (float) Math.sin(angle + radians) * radius;
            angle += 2 * 3.1415f / 5;
        }

        entity.setShapeX(shapex);
        entity.setShapeY(shapey);
    }
}
