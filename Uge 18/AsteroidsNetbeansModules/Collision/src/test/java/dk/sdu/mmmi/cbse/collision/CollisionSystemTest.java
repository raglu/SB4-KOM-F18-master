/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.sdu.mmmi.cbse.collision;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.data.entityparts.LifePart;
import dk.sdu.mmmi.cbse.common.data.entityparts.PositionPart;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rasmu
 */
public class CollisionSystemTest {

    static World world = new World();

    public CollisionSystemTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        Entity entity1 = new Entity();
        entity1.add(new PositionPart(0, 0, 10));
        entity1.setRadius(10);
        entity1.add(new LifePart(1));
        world.addEntity(entity1);

        Entity entity2 = new Entity();
        entity2.add(new PositionPart(10, 10, 10));
        entity2.setRadius(10);
        entity2.add(new LifePart(1));
        world.addEntity(entity2);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of process method, of class CollisionSystem.
     */
    @Test
    public void testProcess() {
        System.out.println("process");
        GameData gameData = new GameData();
        CollisionSystem instance = new CollisionSystem();
        instance.process(gameData, world);
        // TODO review the generated test code and remove the default call to fail.
        for (Entity entity : world.getEntities()) {
            LifePart lifePart = entity.getPart(LifePart.class);
            assertTrue(lifePart.isHit());
        }
    }

}
