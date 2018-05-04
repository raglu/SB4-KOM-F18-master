package dk.sdu.mmmi.cbse.collision;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.LifePart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

@ServiceProviders(value = {
    @ServiceProvider(service = IPostEntityProcessingService.class)
})

public class CollisionSystem implements IPostEntityProcessingService {

    @Override
    public void process(GameData gameData, World world) {

        boolean collision;

        for (Entity entity : world.getEntities()) {
            for (Entity otherEntity : world.getEntities()) {

                if (!entity.equals(otherEntity)) {

                    LifePart lifePart = entity.getPart(LifePart.class);
                    LifePart otherLifePart = otherEntity.getPart(LifePart.class);

                    collision = checkCollision(entity, otherEntity);

                    lifePart.setIsHit(collision);
                    otherLifePart.setIsHit(collision);

                }
            }
        }
    }

    int collisionChecks = 0;

    private boolean checkCollision(Entity entity, Entity otherEntity) {

        PositionPart positionPart = entity.getPart(PositionPart.class);
        PositionPart otherPositionPart = otherEntity.getPart(PositionPart.class);

        float x = positionPart.getX() - otherPositionPart.getX();
        float y = positionPart.getY() - otherPositionPart.getY();

        float distance = (float) Math.sqrt(x * x + y * y);

        return distance <= (entity.getRadius() + otherEntity.getRadius());
    }
}
