package dk.sdu.mmmi.cbse.collision;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.LifePart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

public class CollisionDetectionSystem implements IEntityProcessingService {

    @Override
    public void process(GameData gameData, World world) {

        boolean collision;

        for (Entity entity : world.getEntities()) {
            for (Entity otherEntity : world.getEntities()) {
                if (!entity.getClass().equals(otherEntity.getClass())) {

                    LifePart lifePart = entity.getPart(LifePart.class);
                    LifePart otherLifePart = otherEntity.getPart(LifePart.class);

                    collision = checkCollision(entity, otherEntity);

                    lifePart.setIsHit(collision);
                    otherLifePart.setIsHit(collision);

                }
            }
        }
    }

    private boolean checkCollision(Entity entity, Entity otherEntity) {

        PositionPart positionPart = entity.getPart(PositionPart.class);
        PositionPart otherPositionPart = otherEntity.getPart(PositionPart.class);

        float x = positionPart.getX() - otherPositionPart.getX();
        float y = positionPart.getY() - otherPositionPart.getY();

        float distance = (float) Math.sqrt(x * x + y * y);

        return distance <= entity.getRadius() + entity.getRadius();
    }
}
